package com.grafian.quran.model;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class QuranWord {

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

	public String[][] get(int sura, int aya) {
		if (mDB == null) {
			return new String[][] {};
		}

		Cursor cursor = mDB.query("quran", new String[] { "ar", "tr" },
				"sura=? and aya=?", new String[] { "" + sura, "" + aya },
				null, null, "word");
		final int rows = cursor.getCount();
		String[][] result = new String[rows][];
		for (int i = 0; i < rows; i++) {
			cursor.moveToNext();
			String ar = cursor.getString(0);
			String tr = cursor.getString(1);
			result[i] = new String[] { ar, tr };
		}
		return result;
	}

	public String getPath() {
		return mPath;
	}

	public boolean isLoaded() {
		return mDB != null;
	}

}
