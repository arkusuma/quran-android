package com.grafian.quran;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class BaseActivity extends SherlockFragmentActivity {

	protected App mApp;
	private Menu mMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mApp = (App) getApplication();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mApp.config.save(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mMenu != null) {
			updateMenu();
		}
		mApp.config.load(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.base, menu);
		mMenu = menu;
		updateMenu();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.translation:
			doTranslation();
			return true;
		case R.id.settings:
			mApp.config.save(this);
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		case R.id.about:
			doAbout();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateMenu() {
		MenuItem item = mMenu.findItem(R.id.translation);
		if (mApp.config.lang.equals("id")) {
			item.setTitle(R.string.indonesia);
		} else {
			item.setTitle(R.string.english);
		}
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

	private void doTranslation() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.select_translation)
				.setItems(R.array.lang_names, onTranslate)
				.show();
	}

	private DialogInterface.OnClickListener onTranslate = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			String codes[] = getResources().getStringArray(R.array.lang_codes);
			String lang = codes[which];
			if (!mApp.config.lang.equals(lang)) {
				mApp.config.lang = lang;
				mApp.loadTranslation(BaseActivity.this, new ProgressListener() {
					@Override
					public void onProgress() {
					}

					@Override
					public void onFinish() {
						if (Build.VERSION.SDK_INT >= 11) {
							recreate();
						} else {
							finish();
							startActivity(getIntent());
						}
					}
				});
			}
		}
	};
}
