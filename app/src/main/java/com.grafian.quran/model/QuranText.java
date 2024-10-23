package com.grafian.quran.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.io.File;

public class QuranText {

	private SQLiteDatabase mDB;
	private String mPath;

	private void close() {
		if (mDB != null) {
			mDB.close();
			mDB = null;
			mPath = null;
		}
	}

	public boolean load(Context context, String path) {
		if (!new File(path).exists()) {
			return false;
		}
		if (!path.equals(mPath)) {
			try {
				close();
				mDB = SQLiteDatabase.openDatabase(path, null,
						SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
				mPath = path;
			} catch (SQLiteException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public String get(int sura, int aya) {
		if (mDB == null) {
			return "";
		}

		try {
			Cursor cursor = mDB.query("quran", new String[] { "text" },
					"sura=? and aya=?", new String[] { "" + sura, "" + aya },
					null, null, null);
			cursor.moveToFirst();
			String s = cursor.getString(0);
			cursor.close();
			return s;
		} catch (SQLiteException e) {
			e.printStackTrace();
			close();
			return "";
		}
	}

	public String getPath() {
		return mPath;
	}

	public boolean isLoaded() {
		return mDB != null;
	}

}
