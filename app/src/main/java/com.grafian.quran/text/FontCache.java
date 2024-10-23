package com.grafian.quran.text;

import android.graphics.Bitmap;
import androidx.collection.LruCache;

public class FontCache {
	final private static int BITMAP_CACHE_SIZE = 256;
	final private static int EXTENT_CACHE_SIZE = 1024;

	private final LruCache<String, Bitmap> mBitmapCache;
	private final LruCache<String, int[]> mExtentCache;

	private static FontCache mInstance;

	private FontCache() {
		mBitmapCache = new LruCache<>(BITMAP_CACHE_SIZE);
		mExtentCache = new LruCache<>(EXTENT_CACHE_SIZE);
	}

	public static FontCache getInstance() {
		if (mInstance == null) {
			mInstance = new FontCache();
		}
		return mInstance;
	}

	public void putBitmap(String text, Bitmap bitmap) {
		mBitmapCache.put(text, bitmap);
	}

	public Bitmap getBitmap(String text) {
		return mBitmapCache.get(text);
	}

	public void putExtent(String text, int[] extent) {
		mExtentCache.put(text, extent);
	}

	public int[] getExtent(String text) {
		return mExtentCache.get(text);
	}

	public void clearCache() {
		mBitmapCache.evictAll();
		mExtentCache.evictAll();
	}
}
