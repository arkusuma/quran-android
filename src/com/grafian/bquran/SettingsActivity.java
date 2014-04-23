package com.grafian.bquran;

import android.annotation.TargetApi;
import android.app.backup.BackupManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(App.getThemeID());
		super.onCreate(savedInstanceState);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressWarnings("deprecation")
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		    getActionBar().setDisplayHomeAsUpEnabled(true);
		}
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
	protected void onPause() {
		super.onPause();
		BackupManager.dataChanged(App.PACKAGE_NAME);
	}
}
