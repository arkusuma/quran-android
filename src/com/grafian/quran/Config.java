package com.grafian.quran;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Config {

	final public static int MODE_SURA = 0;
	final public static int MODE_PAGE = 1;
	final public static int MODE_JUZ = 2;
	final public static int MODE_HIZB = 3;

	final private static String PAGING_MODE = "pagingMode";
	final private static String LANG = "lang";
	final private static String RTL = "rtl";
	final private static String SHOW_ARABIC = "showArabic";
	final private static String SHOW_TRANSLATION = "showTranslation";
	final private static String FONT_SIZE_ARABIC = "fontSizeArabic";
	final private static String FONT_SIZE_TRANSLATION = "fontSizeTranslation";

	public int pagingMode;
	public String lang;
	public boolean rtl;
	public boolean showArabic;
	public boolean showTranslation;
	public int fontSizeArabic;
	public int fontSizeTranslation;

	public void load(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		try {
			pagingMode = sp.getInt(PAGING_MODE, 0);
			lang = sp.getString(LANG, "en");
			rtl = sp.getBoolean(RTL, true);
			showArabic = sp.getBoolean(SHOW_ARABIC, true);
			showTranslation = sp.getBoolean(SHOW_TRANSLATION, true);
			fontSizeArabic = Integer.parseInt(sp.getString(FONT_SIZE_ARABIC, "20"));
			fontSizeTranslation = Integer.parseInt(sp.getString(FONT_SIZE_TRANSLATION, "16"));
		} catch (Exception e) {
			pagingMode = 0;
			lang = "en";
			rtl = true;
			showArabic = true;
			showTranslation = true;
			fontSizeArabic = 20;
			fontSizeTranslation = 16;
		}

		if (!showArabic && !showTranslation) {
			showArabic = true;
			showTranslation = true;
		}
	}

	public void save(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor ed = sp.edit();
		ed.putInt(PAGING_MODE, pagingMode);
		ed.putString(LANG, lang);
		ed.putBoolean(RTL, rtl);
		ed.putBoolean(SHOW_ARABIC, showArabic);
		ed.putBoolean(SHOW_TRANSLATION, showTranslation);
		ed.putString(FONT_SIZE_ARABIC, "" + fontSizeArabic);
		ed.putString(FONT_SIZE_TRANSLATION, "" + fontSizeTranslation);
		ed.commit();
	}

}
