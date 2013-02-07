package com.grafian.quran;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.AsyncTask;

public class Extractor {

	public interface ProgressListener {
		void onProgress(long progress, long total);
	}

	private static class MyGZIPInputStream extends GZIPInputStream {

		public MyGZIPInputStream(InputStream stream) throws IOException {
			super(stream);
		}

		public long getBytesRead() {
			return inf.getBytesRead();
		}
	}

	private static boolean extractFromAsset(Context context, String[] srcs, File dest, final ProgressListener listener) {
		MyGZIPInputStream gzip = null;
		OutputStream out = null;
		try {
			long totalSize = 0;
			ArrayList<InputStream> ins = new ArrayList<InputStream>();
			for (String src : srcs) {
				AssetFileDescriptor afd = context.getAssets().openFd(src);
				totalSize += afd.getLength();
				ins.add(context.getAssets().open(src));
			}

			Enumeration<InputStream> en = Collections.enumeration(ins);
			gzip = new MyGZIPInputStream(new SequenceInputStream(en));
			out = new FileOutputStream(dest);
			byte[] buf = new byte[128 * 1024];
			int len;
			while ((len = gzip.read(buf)) > 0) {
				out.write(buf, 0, len);
				if (listener != null) {
					listener.onProgress(gzip.getBytesRead(), totalSize);
				}
			}
			out.close();
			gzip.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			try {
				if (out != null) {
					out.close();
				}
				if (gzip != null) {
					gzip.close();
				}
			} catch (IOException e1) {
			}
			dest.delete();
			return false;
		}
	}

	public static void extractAll(final Context context, final Runnable onFinished) {
		new AsyncTask<String, String, Boolean>() {

			private long mProgress;
			private long mTotal;
			private ProgressDialog mDialog;

			final private ProgressListener onProgress = new ProgressListener() {
				@Override
				public void onProgress(long progress, long total) {
					mProgress = progress;
					mTotal = total;
					publishProgress();
				}
			};

			@Override
			protected void onProgressUpdate(String... values) {
				if (values.length > 0) {
					String message = "Extracting " + values[0] + "\u2026";
					if (mDialog == null) {
						mDialog = new ProgressDialog(context);
						mDialog.setCancelable(false);
						mDialog.setMessage(message);
						mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
						mDialog.show();
					} else {
						mDialog.setMessage(message);
					}
				} else {
					mDialog.setMax((int) mTotal);
					mDialog.setProgress((int) mProgress);
				}
			};

			@Override
			protected Boolean doInBackground(String... params) {
				final AssetManager assets = context.getAssets();
				String[] files = {};
				try {
					files = assets.list("");
				} catch (IOException e) {
					e.printStackTrace();
				}

				File dir = context.getExternalFilesDir(null);
				if (dir == null) {
					return false;
				}

				for (String file : files) {
					if (!file.endsWith(".png")) {
						continue;
					}
					if (Character.isDigit(file.charAt(file.length() - 5))) {
						continue;
					}
					String base = file.substring(0, file.lastIndexOf('.'));
					File dest = new File(dir, base);
					if (!dest.exists()) {
						publishProgress(dest.getName());
						if (!extractFromAsset(context, new String[] { file }, dest, onProgress)) {
							return false;
						}
					}
				}

				File dest = new File(dir, "words_en");
				if (!dest.exists()) {
					publishProgress(dest.getName());
					if (!extractFromAsset(context, new String[] { "words_en1.png", "words_en2.png" }, dest, onProgress)) {
						return false;
					}
				}

				return true;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (mDialog != null) {
					mDialog.dismiss();
				}
				if (onFinished != null) {
					onFinished.run();
				}
			}
		}.execute();
	}
}
