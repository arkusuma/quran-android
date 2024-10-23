package com.grafian.quran.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.io.File;

public class QuranWord {

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

	public String[][] get(int sura, int aya) {
		if (mDB == null) {
			return new String[][] {};
		}

		try {
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
			cursor.close();
			return result;
		} catch (SQLiteException e) {
			e.printStackTrace();
			close();
			return new String[][] {};
		}
	}

	public String getPath() {
		return mPath;
	}

	public boolean isLoaded() {
		return mDB != null;
	}

}
