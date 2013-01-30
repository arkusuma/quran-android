package com.grafian.quran;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class BaseActivity extends SherlockFragmentActivity {

	private boolean mDarkTheme;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mDarkTheme = App.app.config.darkTheme;
		if (App.app.config.darkTheme) {
			setTheme(R.style.Theme_Sherlock);
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onPause() {
		super.onPause();
		App.app.config.save(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		App.app.config.load(this);
		if (mDarkTheme != App.app.config.darkTheme) {
			restart();
		}

		if (mDarkTheme) {
			findViewById(R.id.pager_title_strip).setBackgroundColor(Color.BLACK);
			findViewById(R.id.pager).setBackgroundColor(Color.BLACK);
		} else {
			findViewById(R.id.pager).setBackgroundColor(Color.WHITE);
		}

		App.app.loadFont();
		if (App.app.loaded) {
			if (App.app.needDataReload()) {
				reloadData();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.base, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			App.app.config.save(this);
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		case R.id.about:
			doAbout();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void restart() {
		if (Build.VERSION.SDK_INT >= 11) {
			recreate();
		} else {
			finish();
			startActivity(getIntent());
		}
	}

	private void reloadData() {
		App.app.loadAllData(BaseActivity.this, new ProgressListener() {
			@Override
			public void onProgress() {
			}

			@Override
			public void onFinish() {
				restart();
			}

		});
	}

	private void doAbout() {
		View about = getLayoutInflater().inflate(R.layout.about, null);
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			TextView name = (TextView) about.findViewById(R.id.app_name);
			name.setText(name.getText() + " v" + info.versionName);
		} catch (NameNotFoundException e) {
		}
		new AlertDialog.Builder(this)
				.setCustomTitle(about)
				.setPositiveButton(R.string.visit_web, onAboutDialog)
				.setNeutralButton(R.string.more_apps, onAboutDialog)
				.setNegativeButton(R.string.okay, onAboutDialog)
				.setCancelable(true)
				.show();
	}

	private final DialogInterface.OnClickListener onAboutDialog = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			Intent intent;
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://grafian.com"));
				startActivity(intent);
				break;
			case DialogInterface.BUTTON_NEUTRAL:
				intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/search?q=pub:Grafian%20Software%20Crafter"));
				startActivity(intent);
				break;
			}
			dialog.dismiss();
		}
	};

}
