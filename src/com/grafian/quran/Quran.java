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
			ArrayList<String> sura = null;
			String line;
			String bismillah = null;
			while ((line = reader.readLine()) != null) {
				String[] fields = line.split("\\|");
				if (fields.length == 3) {
					if (fields[1].equals("1")) {
						if (fields[0].equals("1")) {
							bismillah = fields[2];
						} else if (strip && !fields[0].equals("9")) {
							fields[2] = fields[2].substring(bismillah.length() + 1);
						}
						sura = new ArrayList<String>(metaData.getSura(mSuras.size() + 1).ayas);
						mSuras.add(sura);
					}
					sura.add(fields[2]);
					if (mListener != null) {
						mListener.onProgress();
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
