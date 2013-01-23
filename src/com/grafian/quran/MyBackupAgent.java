package com.grafian.quran;

import com.grafian.quran.prefs.Bookmark;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class MyBackupAgent extends BackupAgentHelper {
	static final String BACKUP_KEY = "prefs";

	@Override
	public void onCreate() {
		String settings = App.PACKAGE_NAME + "_preferences";
		SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, settings, Bookmark.BOOKMARK_NAME);
		addHelper(BACKUP_KEY, helper);
	}

}
