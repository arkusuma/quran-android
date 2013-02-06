package com.grafian.quran;

import java.io.File;
import java.io.IOException;

import android.app.Application;

import com.grafian.quran.model.MetaData;
import com.grafian.quran.model.QuranText;
import com.grafian.quran.model.QuranWord;
import com.grafian.quran.prefs.Bookmark;
import com.grafian.quran.prefs.Config;
import com.grafian.quran.text.NativeRenderer;

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

	@Override
	public void onCreate() {
		super.onCreate();

		PACKAGE_NAME = getPackageName();
		app = this;

		config.load(this);
		bookmark.load(this);
	}

	private String getQuranTextPath() {
		return new File(getExternalFilesDir(null), "quran-uthmani").toString();
	}

	private String getTranslationPath() {
		return new File(getExternalFilesDir(null), config.lang).toString();
	}

	private String getQuranWordPath() {
		return new File(getExternalFilesDir(null), "words_en").toString();
	}

	public void loadAllData() {
		if (loadedFont != config.fontArabic) {
			loadedFont = config.fontArabic;
			String name = "qalam.ttf";
			switch (loadedFont) {
			case Config.FONT_NASKH:
				name = "naskh.otf";
				break;
			case Config.FONT_NOOREHUDA:
				name = "noorehuda.ttf";
				break;
			case Config.FONT_ME_QURAN:
				name = "me_quran.ttf";
				break;
			}
			try {
				NativeRenderer.loadFont(getAssets().open(name));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		quranText.load(this, getQuranTextPath());
		translation.load(this, getTranslationPath());
		quranWord.load(this, getQuranWordPath());
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
			return R.style.White;
		case Config.THEME_BLACK:
			return R.style.Black;
		default:
			return R.style.Paper;
		}
	}

}
