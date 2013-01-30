package com.grafian.quran;

import android.app.backup.BackupManager;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

public class SettingsActivity extends SherlockPreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (App.app.config.darkTheme) {
			setTheme(R.style.Theme_Sherlock);
		}
		super.onCreate(savedInstanceState);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (App.app.config.enableAnalytics) {
			EasyTracker.getInstance().activityStart(this);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (App.app.config.enableAnalytics) {
			EasyTracker.getInstance().activityStop(this);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		BackupManager.dataChanged(App.PACKAGE_NAME);
	}
}
