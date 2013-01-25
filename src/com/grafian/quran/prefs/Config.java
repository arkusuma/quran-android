package com.grafian.quran.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Config {

	final public static int QURAN_TEXT_SIMPLE = 0;
	final public static int QURAN_TEXT_MAX = 0;

	final public static int FONT_QALAM_MAJEED = 0;
	final public static int FONT_NASKH = 1;
	final public static int FONT_NOOREHUDA = 2;
	final public static int FONT_MAX = 2;

	final private static String LANG = "lang";
	final private static String RTL = "rtl";
	final private static String SHOW_TRANSLATION = "showTranslation";
	final private static String FULL_WIDTH = "fullWidth";
	final private static String QURAN_TEXT = "quranText";
	final private static String FONT_ARABIC = "fontArabic";
	final private static String FONT_SIZE_ARABIC = "fontSizeArabic";
	final private static String FONT_SIZE_TRANSLATION = "fontSizeTranslation";

	public String lang;
	public boolean rtl;
	public boolean showTranslation;
	public boolean fullWidth;
	public int quranText;
	public int fontArabic;
	public int fontSizeArabic;
	public int fontSizeTranslation;

	public void loadDefaults() {
		lang = "en";
		rtl = true;
		showTranslation = true;
		fullWidth = false;
		quranText = QURAN_TEXT_SIMPLE;
		fontArabic = FONT_QALAM_MAJEED;
		fontSizeArabic = 26;
		fontSizeTranslation = 14;
	}

	private int validate(int val, int min, int max, int def) {
		return (val < min || val > max) ? def : val;
	}

	public void load(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		try {
			loadDefaults();
			lang = sp.getString(LANG, lang);
			rtl = sp.getBoolean(RTL, rtl);
			showTranslation = sp.getBoolean(SHOW_TRANSLATION, showTranslation);
			fullWidth = sp.getBoolean(FULL_WIDTH, fullWidth);
			quranText = Integer.parseInt(sp.getString(QURAN_TEXT, Integer.toString(quranText)));
			fontArabic = Integer.parseInt(sp.getString(FONT_ARABIC, Integer.toString(fontArabic)));
			fontSizeArabic = Integer.parseInt(sp.getString(FONT_SIZE_ARABIC, Integer.toString(fontSizeArabic)));
			fontSizeTranslation = Integer.parseInt(sp.getString(FONT_SIZE_TRANSLATION, Integer.toString(fontSizeTranslation)));
		} catch (Exception e) {
			loadDefaults();
		}

		quranText = validate(quranText, 0, QURAN_TEXT_MAX, QURAN_TEXT_SIMPLE);
		fontArabic = validate(fontArabic, 0, FONT_MAX, FONT_QALAM_MAJEED);
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
		ed.putString(QURAN_TEXT, "" + quranText);
		ed.putString(FONT_ARABIC, "" + fontArabic);
		ed.putString(FONT_SIZE_ARABIC, "" + fontSizeArabic);
		ed.putString(FONT_SIZE_TRANSLATION, "" + fontSizeTranslation);
		ed.commit();
	}

}
