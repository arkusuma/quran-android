package com.grafian.quran;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;

public class Quran {
	final private ArrayList<ArrayList<String>> mSuras = new ArrayList<ArrayList<String>>(114);

	private ProgressListener mListener;

	public Quran(ProgressListener listener) {
		mListener = listener;
	}

	public void load(Context context, int resid, MetaData metaData, boolean strip) {
		try {
			mSuras.clear();
			InputStream in = context.getResources().openRawResource(resid);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			ArrayList<String> list = null;
			String line;
			String bismillah = null;
			while ((line = reader.readLine()) != null) {
				if (Character.isDigit(line.charAt(0))) {
					int a = line.indexOf("|", 0);
					int b = line.indexOf("|", a + 1);
					if (b != -1) {
						int sura = Integer.parseInt(line.substring(0, a));
						int aya = Integer.parseInt(line.substring(a + 1, b));
						String text = line.substring(b + 1);
						if (aya == 1) {
							if (sura == 1) {
								bismillah = text;
							} else if (strip && sura != 9) {
								text = text.substring(bismillah.length() + 1);
							}
							list = new ArrayList<String>(metaData.getSura(mSuras.size() + 1).ayas);
							mSuras.add(list);
						}
						list.add(text);
						if (mListener != null) {
							mListener.onProgress();
						}
					}
				}
			}
			if (mListener != null) {
				mListener.onFinish();
			}
		} catch (Exception e) {
		}
	}

	public String get(int sura, int aya) {
		return mSuras.get(sura - 1).get(aya - 1);
	}

}
