#include <android/log.h>
#include <android/bitmap.h>
#include <freetype/ftglyph.h>
#include <hb-ft.h>

#include "render.h"

static char LOG_TAG[] = "NativeRenderer";

#define CACHE_SIZE 256

typedef struct _cache {
	int when;
	int codepoint;
	int rendered;
	FT_Glyph glyph;
	FT_BBox bbox;
} cache_t;

typedef struct _context {
	FT_Library lib;
	FT_Face face;

	unsigned char *blob;
	hb_font_t *font;
	int font_size;
	hb_buffer_t *buf;

	cache_t cache[CACHE_SIZE];
	int when;
} context_t;

static context_t *ctx;

extern hb_unicode_funcs_t *hb_ucdn_get_unicode_funcs(void);

#if 0
#define assert_zero(x)
#else
#define assert_zero(x) do { \
	int ret = (x); \
	if (ret != 0) { \
		char s[128], *p = 0; \
		sprintf(s, "%s:%d (ret=%d)", __func__, __LINE__, ret); \
		__android_log_print(6, LOG_TAG, s); \
		*p = 0; \
	} } while (0)
#endif

#define assert(x) assert_zero(!(x))

#define round(x) (((x) + 32) >> 6)

static void clear_cache()
{
	int i;
	ctx->when = 1;
	for (i = 0; i < CACHE_SIZE; i++) {
		if (ctx->cache[i].when) {
			ctx->cache[i].when = 0;
			FT_Done_Glyph(ctx->cache[i].glyph);
		}
	}
}

static void init_cache(int font_size)
{
	if (!ctx->font || ctx->font_size != font_size) {
		if (ctx->font) hb_font_destroy(ctx->font);
		assert_zero(FT_Set_Pixel_Sizes(ctx->face, 0, font_size));
		ctx->font = hb_ft_font_create(ctx->face, NULL);
		ctx->font_size = font_size;
		clear_cache();
	}

	if (!ctx->buf) {
		ctx->buf = hb_buffer_create();
		hb_buffer_set_unicode_funcs(ctx->buf, hb_ucdn_get_unicode_funcs());
	}
}

static int get_cached_glyph(int codepoint, FT_Glyph *glyph, FT_BBox *bbox)
{
	int i, oldest = 0;
	for (i = 0; i < CACHE_SIZE; i++) {
		if (ctx->cache[i].when && ctx->cache[i].codepoint == codepoint) {
			ctx->cache[i].when = ctx->when++;
			if (glyph) *glyph = ctx->cache[i].glyph;
			if (bbox) *bbox = ctx->cache[i].bbox;
			return i;
		}
		if (ctx->cache[oldest].when > ctx->cache[i].when)
			oldest = i;
	}
	i = oldest;

	if (ctx->cache[i].when)
		FT_Done_Glyph(ctx->cache[i].glyph);

	assert_zero(FT_Load_Glyph(ctx->face, codepoint, FT_LOAD_DEFAULT));
	assert_zero(FT_Get_Glyph(ctx->face->glyph, &ctx->cache[i].glyph));
	FT_Glyph_Get_CBox(ctx->cache[i].glyph, FT_GLYPH_BBOX_UNSCALED, &ctx->cache[i].bbox);

	ctx->cache[i].when = ctx->when++;
	ctx->cache[i].codepoint = codepoint;
	ctx->cache[i].rendered = 0;
	if (glyph) *glyph = ctx->cache[i].glyph;
	if (bbox) *bbox = ctx->cache[i].bbox;
	return i;
}

static int get_cached_bitmap(int codepoint, FT_BitmapGlyph *bitmap)
{
	FT_Glyph glyph;
	int i = get_cached_glyph(codepoint, &glyph, 0);
	if (!ctx->cache[i].rendered) {
		assert_zero(FT_Glyph_To_Bitmap(&glyph, FT_RENDER_MODE_NORMAL, 0, 1));
		ctx->cache[i].rendered = 1;
		ctx->cache[i].glyph = glyph;
	}
	*bitmap = (FT_BitmapGlyph) glyph;
	return i;
}

static void render_bitmap(FT_Bitmap *bitmap, void *pixels, AndroidBitmapInfo *abi, int x, int y)
{
	unsigned int ix, iy, ox, oy;
	for (iy = 0; (int) iy < bitmap->rows; iy++) {
		oy = y + iy;
		if (oy >= abi->height) continue;
		unsigned char *ipix = bitmap->buffer + iy * bitmap->width;
		unsigned char *opix = (unsigned char *) pixels + oy * abi->stride;
		for (ix = 0; (int) ix < bitmap->width; ix++) {
			ox = x + ix;
			if (ox >= abi->width) continue;
			if (opix[ox] < ipix[ix]) opix[ox] = ipix[ix];
		}
	}
}

static void render_glyphs(hb_buffer_t *buf, void *pixels, AndroidBitmapInfo *abi, int x, int y)
{
	int len = hb_buffer_get_length(buf);
	hb_glyph_info_t *info = hb_buffer_get_glyph_infos(buf, 0);
	hb_glyph_position_t *pos = hb_buffer_get_glyph_positions(buf, 0);
	int i;

	x <<= 6;
	y <<= 6;
	for (i = 0; i < len; i++) {
		FT_BitmapGlyph bitmap;

		get_cached_bitmap(info->codepoint, &bitmap);
		render_bitmap(&bitmap->bitmap, pixels, abi,
				round(x + pos->x_offset) + bitmap->left,
				round(y - pos->y_offset) - bitmap->top);

		x += pos->x_advance;
		y -= pos->y_advance;

		info++;
		pos++;
   	}
}

static void compute_bbox(hb_buffer_t *buf, FT_BBox *bbox)
{
	int len = hb_buffer_get_length(buf);
	hb_glyph_info_t *info = hb_buffer_get_glyph_infos(buf, 0);
	hb_glyph_position_t *pos = hb_buffer_get_glyph_positions(buf, 0);
	int i;

	bbox->xMin = bbox->yMin = 32000;
	bbox->xMax = bbox->yMax = -32000;
	int x = 0;
	int y = 0;
	for (i = 0; i < len; i++) {
		FT_Glyph glyph;
		FT_BBox gbox;

		get_cached_glyph(info->codepoint, &glyph, &gbox);
		gbox.xMin += x + pos->x_offset;
		gbox.xMax += x + pos->x_offset;
		gbox.yMin += y + pos->y_offset;
		gbox.yMax += y + pos->y_offset;

		if (bbox->xMin > gbox.xMin)
			bbox->xMin = gbox.xMin;
		if (bbox->yMin > gbox.yMin)
			bbox->yMin = gbox.yMin;
		if (bbox->xMax < gbox.xMax)
			bbox->xMax = gbox.xMax;
		if (bbox->yMax < gbox.yMax)
			bbox->yMax = gbox.yMax;

		x += pos->x_advance;
		y += pos->y_advance;

		info++;
		pos++;
   	}

	if (bbox->xMin > bbox->xMax) {
		bbox->xMin = 0;
		bbox->xMax = 0;
		bbox->yMin = 0;
		bbox->yMax = 0;
	} else {
		bbox->xMin = round(bbox->xMin);
		bbox->xMax = round(bbox->xMax);
		bbox->yMin = round(bbox->yMin);
		bbox->yMax = round(bbox->yMax);
	}
}

static void shape(const jchar *text, int text_length, int offset, int length, hb_font_t *font, hb_buffer_t *buf)
{
	hb_buffer_clear_contents(buf);
	hb_buffer_set_direction(buf, HB_DIRECTION_RTL);
	hb_buffer_set_script(buf, HB_SCRIPT_ARABIC);
	hb_buffer_set_language(buf, hb_language_from_string("ar", 2));
	hb_buffer_add_utf16(buf, text, text_length, offset, length);
	hb_shape(font, buf, NULL, 0);
}

static void cleanup(void)
{
	if (ctx->buf) {
		hb_buffer_destroy(ctx->buf);
		ctx->buf = 0;
	}

	if (ctx->font) {
		hb_font_destroy(ctx->font);
		ctx->font = 0;
	}

	if (ctx->blob) {
		FT_Done_Face(ctx->face);
		free(ctx->blob);
		ctx->blob = 0;
	}

	clear_cache();
}

static void clear_rect(void *pixels, AndroidBitmapInfo *abi, int width, int height) {
	unsigned char *row = (unsigned char *) pixels;
	int i;
	if (width > (int) abi->width) width = abi->width;
	if (height > (int) abi->height) height = abi->height;
	for (i = 0; i < height; i++) {
		memset(row, 0, width);
		row += abi->stride;
	}
}

#define UNUSED(x) (void)(x)

JNIEXPORT void JNICALL Java_com_grafian_quran_text_NativeRenderer_loadFont
  (JNIEnv *env, jclass cls, jbyteArray blob)
{
	UNUSED(cls);

	// Initialize FreeType library for the first time
	if (!ctx) {
		assert(ctx = (context_t *) calloc(1, sizeof(*ctx)));
		assert_zero(FT_Init_FreeType(&ctx->lib));
	}

	cleanup();

	// Clone blob
	int size = (*env)->GetArrayLength(env, blob);
	jbyte *bytes = (*env)->GetByteArrayElements(env, blob, 0);
	ctx->blob = (unsigned char *) malloc(size);
	memcpy(ctx->blob, bytes, size);
	(*env)->ReleaseByteArrayElements(env, blob, bytes, JNI_ABORT);

	// Actual font loading
	assert_zero(FT_New_Memory_Face(ctx->lib, ctx->blob, size, 0, &ctx->face));
}

JNIEXPORT jintArray JNICALL Java_com_grafian_quran_text_NativeRenderer_getTextExtent
  (JNIEnv *env, jclass cls, jstring text, jint font_size)
{
	const jchar *ctext;
	int len;
	jint array[6];
	jintArray result;
	jboolean iscopy;
	FT_BBox bbox;

	UNUSED(cls);

	init_cache(font_size);

	ctext = (*env)->GetStringChars(env, text, &iscopy);
	len = (*env)->GetStringLength(env, text);

	shape(ctext, len, 0, len, ctx->font, ctx->buf);
	compute_bbox(ctx->buf, &bbox);

	(*env)->ReleaseStringChars(env, text, ctext);

	array[0] = bbox.xMax - bbox.xMin;
	array[1] = bbox.yMax - bbox.yMin;
	array[2] = bbox.yMax;
	array[3] = round(ctx->face->size->metrics.height);
	array[4] = round(ctx->face->size->metrics.ascender);
	array[5] = round(ctx->face->size->metrics.descender);
	result = (*env)->NewIntArray(env, 6);
	(*env)->SetIntArrayRegion(env, result, 0, 6, array);
	return result;
}

JNIEXPORT void JNICALL Java_com_grafian_quran_text_NativeRenderer_renderText
  (JNIEnv *env, jclass cls, jstring text, jint font_size, jobject bitmap)
{
	AndroidBitmapInfo abi;
	void *pixels;
	const jchar *ctext;
	int len;
	jboolean iscopy;
	FT_BBox bbox;

	UNUSED(cls);

	init_cache(font_size);

	AndroidBitmap_getInfo(env, bitmap, &abi);
	AndroidBitmap_lockPixels(env, bitmap, &pixels);

	ctext = (*env)->GetStringChars(env, text, &iscopy);
	len = (*env)->GetStringLength(env, text);

	shape(ctext, len, 0, len, ctx->font, ctx->buf);
	compute_bbox(ctx->buf, &bbox);
	clear_rect(pixels, &abi, bbox.xMax - bbox.xMin, bbox.yMax - bbox.yMin);
	render_glyphs(ctx->buf, pixels, &abi, -bbox.xMin, bbox.yMax);

	(*env)->ReleaseStringChars(env, text, ctext);

	AndroidBitmap_unlockPixels(env, bitmap);
}
