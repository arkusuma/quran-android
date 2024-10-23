package com.grafian.quran;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

	private int mTheme;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mTheme = App.app.config.theme;
		setTheme(App.getThemeID());
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
		if (mTheme != App.app.config.theme) {
			restart();
		} else if (!App.app.loadAllData()) {
			Extractor.extractAll(this, () -> {
                if (!App.app.loadAllData()) {
                    DialogInterface.OnClickListener onQuit = (dialog, which) -> finish();
                    new AlertDialog.Builder(BaseActivity.this)
                            .setMessage(R.string.sdcard_required)
                            .setCancelable(false)
                            .setPositiveButton(R.string.quit, onQuit)
                            .show();
                }
            });
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.base, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.settings) {
			App.app.config.save(this);
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		} else if (id == R.id.about) {
			doAbout();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void restart() {
		recreate();
	}

	private void doAbout() {
		View about = getLayoutInflater().inflate(R.layout.about, null);
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			TextView name = about.findViewById(R.id.app_name);
			name.setText(name.getText() + " v" + info.versionName);
		} catch (NameNotFoundException e) {
		}
		new AlertDialog.Builder(this)
				.setCustomTitle(about)
				.setPositiveButton(R.string.more_apps, (dialog, which) -> {
					startActivity(new Intent(Intent.ACTION_VIEW,
							Uri.parse("https://grafian.com")));
					dialog.dismiss();
				} )
				.setNegativeButton(R.string.okay, (dialog, which) -> dialog.dismiss())
				.setCancelable(true)
				.show();
	}

	private final DialogInterface.OnClickListener onAboutDialog = (dialog, which) -> {
        Intent intent;
        if (which == DialogInterface.BUTTON_POSITIVE) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/search?q=pub:Grafian"));
            startActivity(intent);
        }
        dialog.dismiss();
    };

}
