package com.grafian.quran;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class BaseFragmentActivity extends SherlockFragmentActivity {

	protected App app;
	private Intent starterIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = (App) getApplication();
		starterIntent = getIntent();
	}

	@Override
	protected void onPause() {
		super.onPause();
		app.config.save();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu, menu);
		MenuItem item = menu.findItem(R.id.translation);
		if (app.config.getLang().equals("id")) {
			item.setTitle(R.string.indonesia);
		} else {
			item.setTitle(R.string.english);
		}
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.translation:
			doTranslation();
			break;
		case R.id.about:
			doAbout();
			break;
		default:
			return false;
		}
		return true;
	}

	private void doAbout() {
		View about = getLayoutInflater().inflate(R.layout.about, null);
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
			if (!app.config.getLang().equals(lang)) {
				app.config.setLang(lang);
				app.loadTranslation(BaseFragmentActivity.this, new ProgressListener() {
					@Override
					public void onProgress() {
					}

					@Override
					public void onFinish() {
						if (Build.VERSION.SDK_INT >= 11) {
							recreate();
						} else {
							startActivity(starterIntent);
							finish();
						}
					}
				});
			}
		}
	};
}
