package com.grafian.quran.parser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import com.grafian.quran.ProgressListener;

import android.content.Context;

public class Quran {
	final private ArrayList<ArrayList<String>> mSuras = new ArrayList<ArrayList<String>>(114);

	public void load(Context context, int resid, MetaData metaData, boolean strip, ProgressListener listener) {
		try {
			mSuras.clear();
			InputStream in = context.getResources().openRawResource(resid);
			GZIPInputStream gz = new GZIPInputStream(new BufferedInputStream(in));
			BufferedReader reader = new BufferedReader(new InputStreamReader(gz));
			ArrayList<String> list = null;
			String line;
			String bismillah = null;
			while ((line = reader.readLine()) != null) {
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
					if (listener != null) {
						listener.onProgress();
					}
				}
			}
			if (listener != null) {
				listener.onFinish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String get(int sura, int aya) {
		return mSuras.get(sura - 1).get(aya - 1);
	}

}
