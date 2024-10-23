package com.grafian.quran;

import android.app.backup.BackupManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(App.getThemeID());
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		if (getActionBar() != null) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	protected void onPause() {
		super.onPause();
		BackupManager.dataChanged(App.PACKAGE_NAME);
	}
}
