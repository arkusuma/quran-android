package com.grafian.bquran;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import android.app.Application;
import android.view.ViewConfiguration;

import com.grafian.bquran.R;
import com.grafian.bquran.model.MetaData;
import com.grafian.bquran.model.QuranText;
import com.grafian.bquran.model.QuranWord;
import com.grafian.bquran.prefs.Bookmark;
import com.grafian.bquran.prefs.Config;
import com.grafian.bquran.text.FontCache;
import com.grafian.bquran.text.NativeRenderer;

public class App extends Application {

	public static String PACKAGE_NAME;

	final public Config config = new Config();
	final public Bookmark bookmark = new Bookmark();
	final public MetaData metaData = new MetaData();

	final public QuranText quranText = new QuranText();
	final public QuranText translation = new QuranText();
	final public QuranWord quranWord = new QuranWord();

	public static App app;

	private int loadedFont = -1;
	private int loadedFontSize = -1;

	@Override
	public void onCreate() {
		super.onCreate();

		PACKAGE_NAME = getPackageName();
		app = this;

		config.load(this);
		bookmark.load(this);

		forceOverflowMenu();
		loadFont();
	}

	private void forceOverflowMenu() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ex) {
		}
	}

	private String getQuranTextPath() {
		return new File(getExternalFilesDir(null), "quran-uthmani").toString();
	}

	private String getTranslationPath() {
		return new File(getExternalFilesDir(null), config.lang).toString();
	}

	private String getQuranWordPath() {
		if (config.lang.startsWith("id.")) {
			return new File(getExternalFilesDir(null), "words_id").toString();
		} else {
			return new File(getExternalFilesDir(null), "words_en").toString();
		}
	}

	private String getFontName(int fontCode) {
		switch (fontCode) {
		case Config.FONT_HAFS:
			return "hafs.otf";
		case Config.FONT_NOOREHUDA:
			return "noorehuda.ttf";
		case Config.FONT_ME_QURAN:
			return "me_quran.ttf";
		case Config.FONT_QALAM_MAJEED:
		default:
			return "qalam.ttf";
		}
	}

	public boolean loadFont() {
		if (loadedFont != config.fontArabic || loadedFontSize != config.fontSizeArabic) {
			String name = getFontName(config.fontArabic);
			try {
				NativeRenderer.loadFont(getAssets().open(name));
				FontCache.getInstance().clearCache();
				loadedFont = config.fontArabic;
				loadedFontSize = config.fontSizeArabic;
			} catch (IOException e) {
				e.printStackTrace();
				loadedFont = -1;
				loadedFontSize = -1;
				return false;
			}
		}
		return true;
	}

	public boolean loadAllData() {
		return loadFont()
				&& quranText.load(this, getQuranTextPath())
				&& translation.load(this, getTranslationPath())
				&& quranWord.load(this, getQuranWordPath());
	}

	public static String getSuraName(int i) {
		String items[];
		if (app.config.lang.startsWith("id")) {
			items = app.getResources().getStringArray(R.array.sura_name_id);
		} else {
			items = app.getResources().getStringArray(R.array.sura_name_en);
		}
		return items[i - 1];
	}

	public static String getSuraTranslation(int i) {
		String items[];
		if (app.config.lang.startsWith("id")) {
			items = app.getResources().getStringArray(R.array.sura_translation_id);
		} else {
			items = app.getResources().getStringArray(R.array.sura_translation_en);
		}
		return items[i - 1];
	}

	public static int getThemeID() {
		switch (app.config.theme) {
		case Config.THEME_WHITE:
			return R.style.Theme_White;
		case Config.THEME_BLACK:
			return R.style.Theme_Black;
		default:
			return R.style.Theme_Mushaf;
		}
	}

}
