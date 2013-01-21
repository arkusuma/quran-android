package com.grafian.quran;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;

public class Config {

	final public static int PAGING_MODE_SURA = 0;
	final public static int PAGING_MODE_PAGE = 1;
	final public static int PAGING_MODE_JUZ = 2;
	final public static int PAGING_MODE_HIZB = 3;

	final public static int FONT_DEFAULT = 0;
	final public static int FONT_UTHMAN = 1;
	final public static int FONT_ME_QURAN = 2;

	final private static String PAGING_MODE = "pagingMode";
	final private static String LANG = "lang";
	final private static String RTL = "rtl";
	final private static String SHOW_ARABIC = "showArabic";
	final private static String SHOW_TRANSLATION = "showTranslation";
	final private static String FONT_ARABIC = "fontArabic";
	final private static String FONT_SIZE_ARABIC = "fontSizeArabic";
	final private static String FONT_SIZE_TRANSLATION = "fontSizeTranslation";
	final private static String INTERNAL_RESHAPER = "internalReshaper";

	public int pagingMode;
	public String lang;
	public boolean rtl;
	public boolean showArabic;
	public boolean showTranslation;
	public int fontArabic;
	public int fontSizeArabic;
	public int fontSizeTranslation;
	public boolean internalReshaper;

	public void load(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		try {
			loadDefaults();
			pagingMode = sp.getInt(PAGING_MODE, pagingMode);
			lang = sp.getString(LANG, lang);
			rtl = sp.getBoolean(RTL, rtl);
			showArabic = sp.getBoolean(SHOW_ARABIC, showArabic);
			showTranslation = sp.getBoolean(SHOW_TRANSLATION, showTranslation);
			fontArabic = Integer.parseInt(sp.getString(FONT_ARABIC, Integer.toString(fontArabic)));
			fontSizeArabic = Integer.parseInt(sp.getString(FONT_SIZE_ARABIC, Integer.toString(fontSizeArabic)));
			fontSizeTranslation = Integer.parseInt(sp.getString(FONT_SIZE_TRANSLATION, Integer.toString(fontSizeTranslation)));
			internalReshaper = sp.getBoolean(INTERNAL_RESHAPER, internalReshaper);
		} catch (Exception e) {
			loadDefaults();
		}

		if (!showArabic && !showTranslation) {
			showArabic = true;
			showTranslation = true;
		}
	}

	public void loadDefaults() {
		pagingMode = PAGING_MODE_SURA;
		lang = "en";
		rtl = true;
		showArabic = true;
		showTranslation = true;
		fontArabic = FONT_ME_QURAN;
		fontSizeArabic = 20;
		fontSizeTranslation = 16;
		internalReshaper = Build.VERSION.SDK_INT < 14 ? true : false;
	}

	public void save(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor ed = sp.edit();
		ed.putInt(PAGING_MODE, pagingMode);
		ed.putString(LANG, lang);
		ed.putBoolean(RTL, rtl);
		ed.putBoolean(SHOW_ARABIC, showArabic);
		ed.putBoolean(SHOW_TRANSLATION, showTranslation);
		ed.putString(FONT_ARABIC, "" + fontArabic);
		ed.putString(FONT_SIZE_ARABIC, "" + fontSizeArabic);
		ed.putString(FONT_SIZE_TRANSLATION, "" + fontSizeTranslation);
		ed.putBoolean(INTERNAL_RESHAPER, internalReshaper);
		ed.commit();
	}

}
