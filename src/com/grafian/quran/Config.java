package com.grafian.quran;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Config {

	final public static int MODE_SURA = 0;
	final public static int MODE_PAGE = 1;
	final public static int MODE_JUZ = 2;
	final public static int MODE_HIZB = 3;

	final private static String CONFIG_NAME = "config";
	final private static String MODE = "mode";
	final private static String LANG = "lang";

	private int mMode;
	private String mLang;

	public void load(Context context) {
		SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
		mMode = sp.getInt(MODE, 0);
		mLang = sp.getString(LANG, "en");
	}

	public void save(Context context) {
		SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
		Editor ed = sp.edit();
		ed.putInt(MODE, mMode);
		ed.putString(LANG, mLang);
		ed.commit();
	}

	public int getMode() {
		return mMode;
	}

	public void setMode(int mode) {
		mMode = mode;
	}

	public String getLang() {
		return mLang;
	}

	public void setLang(String lang) {
		mLang = lang;
	}

}
