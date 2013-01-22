package com.grafian.quran;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

public class App extends Application {

	public static String PACKAGE_NAME;

	public MetaData metaData;
	public Quran quran;
	public Quran translation;
	public boolean loaded = false;
	public Config config;
	public Bookmark bookmark;
	public static App app;

	@Override
	public void onCreate() {
		super.onCreate();

		PACKAGE_NAME = getPackageName();
		app = this;

		config = new Config();
		config.load(this);

		bookmark = new Bookmark();
		bookmark.load(this);

	}

	private int getTranslationID() {
		if (config.lang.equals("id")) {
			return R.raw.id_indonesian;
		}
		return R.raw.en_sahih;
	}

	public void loadAllData(final Context context, final ProgressListener listener) {
		new AsyncTask<Void, Integer, Void>() {
			int tick;
			ProgressDialog dialog;

			final private ProgressListener onProgress = new ProgressListener() {
				@Override
				public void onProgress() {
					publishProgress(tick++);
				}

				@Override
				public void onFinish() {
				}
			};

			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			protected void onPreExecute() {
				dialog = new ProgressDialog(context);
				dialog.setCancelable(true);
				dialog.setMessage(getString(R.string.loading));
				dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				dialog.setProgress(0);
				dialog.setMax(14038);
				if (Build.VERSION.SDK_INT >= 11) {
					dialog.setProgressNumberFormat(null);
				}
				dialog.show();
			}

			@Override
			protected Void doInBackground(Void... params) {
				metaData = new MetaData(onProgress);
				metaData.load(App.this, R.raw.quran_data);

				quran = new Quran(onProgress);
				quran.load(App.this, R.raw.quran_simple, metaData, true);

				translation = new Quran(onProgress);
				translation.load(App.this, getTranslationID(), metaData, false);
				return null;
			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				int val = values[0];
				if (val % 200 == 0) {
					dialog.setProgress(val);
				}
			}

			@Override
			protected void onPostExecute(Void result) {
				dialog.dismiss();
				loaded = true;
				listener.onFinish();
				if (tick != dialog.getMax()) {
					Toast.makeText(context, "" + tick, Toast.LENGTH_LONG).show();
				}
			}
		}.execute();
	}

	public void loadTranslation(final Context context, final ProgressListener listener) {
		new AsyncTask<Void, Integer, Quran>() {
			int tick;
			ProgressDialog dialog;

			final private ProgressListener onProgress = new ProgressListener() {
				@Override
				public void onProgress() {
					publishProgress(tick++);
					if (listener != null) {
						listener.onProgress();
					}
				}

				@Override
				public void onFinish() {
				}
			};

			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			protected void onPreExecute() {
				dialog = new ProgressDialog(context);
				dialog.setCancelable(true);
				dialog.setMessage(getString(R.string.loading));
				dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				dialog.setProgress(0);
				dialog.setMax(6236);
				if (Build.VERSION.SDK_INT >= 11) {
					dialog.setProgressNumberFormat(null);
				}
				dialog.show();
			}

			@Override
			protected Quran doInBackground(Void... params) {
				Quran translation = new Quran(onProgress);
				translation.load(App.this, getTranslationID(), metaData, false);
				return translation;
			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				int val = values[0];
				if (val % 200 == 0) {
					dialog.setProgress(val);
				}
			}

			@Override
			protected void onPostExecute(Quran result) {
				translation = result;
				dialog.dismiss();
				if (listener != null) {
					listener.onFinish();
				}
				if (tick != dialog.getMax()) {
					Toast.makeText(context, "" + tick, Toast.LENGTH_LONG).show();
				}
			}
		}.execute();
	}

	public static String getSuraName(int i) {
		String items[];
		if ("en".equals(app.config.lang)) {
			items = app.getResources().getStringArray(R.array.sura_name_en);
		} else {
			items = app.getResources().getStringArray(R.array.sura_name_id);
		}
		return items[i - 1];
	}

	public static String getSuraTranslation(int i) {
		String items[];
		if ("en".equals(app.config.lang)) {
			items = app.getResources().getStringArray(R.array.sura_translation_en);
		} else {
			items = app.getResources().getStringArray(R.array.sura_translation_id);
		}
		return items[i - 1];
	}

}
