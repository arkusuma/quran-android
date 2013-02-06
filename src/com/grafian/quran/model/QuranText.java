package com.grafian.quran.model;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class QuranText {

	private SQLiteDatabase mDB;
	private String mPath;

	public void load(Context context, String path) {
		if (!path.equals(mPath) && new File(path).exists()) {
			if (mDB != null) {
				mDB.close();
				mDB = null;
			}
			mPath = path;
			mDB = SQLiteDatabase.openDatabase(path, null,
					SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		}
	}

	public String get(int sura, int aya) {
		if (mDB == null) {
			return "";
		}

		Cursor cursor = mDB.query("quran", new String[] { "text" },
				"sura=? and aya=?", new String[] { "" + sura, "" + aya },
				null, null, null);
		cursor.moveToFirst();
		return cursor.getString(0);
	}

	public String getPath() {
		return mPath;
	}

	public boolean isLoaded() {
		return mDB != null;
	}

}
