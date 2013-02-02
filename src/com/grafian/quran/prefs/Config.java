package com.grafian.quran.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Config {

	final public static int QURAN_TEXT_SIMPLE = 0;
	final public static int QURAN_TEXT_UTHMANI = 1;
	final public static int QURAN_TEXT_MAX = 1;

	final public static int FONT_QALAM_MAJEED = 0;
	final public static int FONT_NASKH = 1;
	final public static int FONT_NOOREHUDA = 2;
	final public static int FONT_ME_QURAN = 3;
	final public static int FONT_MAX = 3;

	final public static int THEME_WHITE = 0;
	final public static int THEME_BLACK = 1;
	final public static int THEME_MUSHAF = 2;
	final public static int THEME_MAX = 2;

	final private static String LANG = "lang";
	final private static String RTL = "rtl";
	final private static String SHOW_TRANSLATION = "showTranslation";
	final private static String FULL_WIDTH = "fullWidth";
	final private static String THEME = "theme";
	final private static String ENABLE_ANALYTICS = "enableAnalytics";
	final private static String QURAN_TEXT = "quranText";
	final private static String FONT_ARABIC = "fontArabic";
	final private static String FONT_SIZE_ARABIC = "fontSizeArabic";
	final private static String FONT_SIZE_TRANSLATION = "fontSizeTranslation";

	public String lang;
	public boolean rtl;
	public boolean showTranslation;
	public boolean fullWidth;
	public boolean enableAnalytics;
	public int quranText;
	public int fontArabic;
	public int fontSizeArabic;
	public int fontSizeTranslation;
	public int theme;

	public void loadDefaults() {
		lang = "en";
		rtl = true;
		showTranslation = true;
		fullWidth = false;
		enableAnalytics = true;
		quranText = QURAN_TEXT_SIMPLE;
		fontArabic = FONT_QALAM_MAJEED;
		fontSizeArabic = 26;
		fontSizeTranslation = 14;
		theme = THEME_MUSHAF;
	}

	private int validate(int val, int min, int max, int def) {
		return (val < min || val > max) ? def : val;
	}

	private int getStringInt(SharedPreferences sp, String key, int defValue) {
		return Integer.parseInt(sp.getString(key, Integer.toString(defValue)));
	}

	public void load(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		try {
			loadDefaults();
			lang = sp.getString(LANG, lang);
			rtl = sp.getBoolean(RTL, rtl);
			showTranslation = sp.getBoolean(SHOW_TRANSLATION, showTranslation);
			fullWidth = sp.getBoolean(FULL_WIDTH, fullWidth);
			enableAnalytics = sp.getBoolean(ENABLE_ANALYTICS, enableAnalytics);
			quranText = getStringInt(sp, QURAN_TEXT, quranText);
			fontArabic = getStringInt(sp, FONT_ARABIC, fontArabic);
			fontSizeArabic = getStringInt(sp, FONT_SIZE_ARABIC, fontSizeArabic);
			fontSizeTranslation = getStringInt(sp, FONT_SIZE_TRANSLATION, fontSizeTranslation);
			theme = getStringInt(sp, THEME, theme);
		} catch (Exception e) {
			loadDefaults();
		}

		quranText = validate(quranText, 0, QURAN_TEXT_MAX, QURAN_TEXT_SIMPLE);
		fontArabic = validate(fontArabic, 0, FONT_MAX, FONT_QALAM_MAJEED);
		theme = validate(theme, 0, THEME_MAX, THEME_MUSHAF);
		if (!lang.equals("en") && !lang.equals("id")) {
			lang = "en";
		}
	}

	public void save(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor ed = sp.edit();
		ed.clear();
		ed.putString(LANG, lang);
		ed.putBoolean(RTL, rtl);
		ed.putBoolean(SHOW_TRANSLATION, showTranslation);
		ed.putBoolean(FULL_WIDTH, fullWidth);
		ed.putBoolean(ENABLE_ANALYTICS, enableAnalytics);
		ed.putString(QURAN_TEXT, "" + quranText);
		ed.putString(FONT_ARABIC, "" + fontArabic);
		ed.putString(FONT_SIZE_ARABIC, "" + fontSizeArabic);
		ed.putString(FONT_SIZE_TRANSLATION, "" + fontSizeTranslation);
		ed.putString(THEME, "" + theme);
		ed.commit();
	}

}
