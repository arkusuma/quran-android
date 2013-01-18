package com.grafian.quran;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

public class MetaData {

	public static class Sura {
		final public static int MECCAN = 0;
		final public static int MEDINAN = 1;
		int index;
		int ayas;
		int start;
		String name;
		String tname;
		String ename;
		int type;
		int order;
		int rukus;
	}

	public static class Mark {
		int sura;
		int aya;

		Mark() {
		}

		Mark(Mark m) {
			this.sura = m.sura;
			this.aya = m.aya;
		}

		Mark(int sura, int aya) {
			this.sura = sura;
			this.aya = aya;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Mark) {
				Mark m = (Mark) o;
				return m.sura == sura && m.aya == aya;
			}
			return false;
		}
	}

	public static class Sajda extends Mark {
		final public static int RECOMMENDED = 0;
		final public static int OBLIGATORY = 1;
		int type;

		Sajda() {
		}

		Sajda(Mark mark) {
			super(mark.sura, mark.aya);
		}
	}

	final private ArrayList<Sura> mSuras = new ArrayList<Sura>();
	final private ArrayList<Mark> mJuzs = new ArrayList<Mark>();
	final private ArrayList<Mark> mHizbs = new ArrayList<Mark>();
	final private ArrayList<Mark> mManzils = new ArrayList<Mark>();
	final private ArrayList<Mark> mRukus = new ArrayList<Mark>();
	final private ArrayList<Mark> mPages = new ArrayList<Mark>();
	final private ArrayList<Sajda> mSajdas = new ArrayList<Sajda>();

	private ProgressListener mProgressListener;

	public MetaData(ProgressListener listener) {
		mProgressListener = listener;
	}

	public void load(Context context, int resid) {
		try {
			InputStream in = context.getResources().openRawResource(resid);
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			xr.setContentHandler(new XMLHandler());
			xr.parse(new InputSource(in));
		} catch (Exception e) {
		}
	}

	private Mark getMark(Attributes attributes) {
		Mark mark = new Mark();
		mark.sura = Integer.parseInt(attributes.getValue("sura"));
		mark.aya = Integer.parseInt(attributes.getValue("aya"));
		return mark;
	}

	class XMLHandler extends DefaultHandler {
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if ("sura".equals(localName)) {
				Sura sura = new Sura();
				sura.index = Integer.parseInt(attributes.getValue("index"));
				sura.ayas = Integer.parseInt(attributes.getValue("ayas"));
				sura.start = Integer.parseInt(attributes.getValue("start"));
				sura.name = attributes.getValue("name");
				sura.tname = attributes.getValue("tname");
				sura.ename = attributes.getValue("ename");
				sura.type = "Meccan".equals(attributes.getValue("type")) ?
						Sura.MECCAN : Sura.MEDINAN;
				sura.order = Integer.parseInt(attributes.getValue("", "order"));
				sura.rukus = Integer.parseInt(attributes.getValue("", "rukus"));
				mSuras.add(sura);
				updateProgress();
			} else if ("juz".equals(localName)) {
				mJuzs.add(getMark(attributes));
				updateProgress();
			} else if ("quarter".equals(localName)) {
				mHizbs.add(getMark(attributes));
				updateProgress();
			} else if ("manzil".equals(localName)) {
				mManzils.add(getMark(attributes));
				updateProgress();
			} else if ("ruku".equals(localName)) {
				mRukus.add(getMark(attributes));
				updateProgress();
			} else if ("page".equals(localName)) {
				mPages.add(getMark(attributes));
				updateProgress();
			} else if ("sajda".equals(localName)) {
				Sajda sajda = new Sajda(getMark(attributes));
				sajda.type = "recommended".equals(attributes.getValue("", "type")) ?
						Sajda.RECOMMENDED : Sajda.OBLIGATORY;
				mSajdas.add(sajda);
				updateProgress();
			}
		}
	}

	private void updateProgress() {
		if (mProgressListener != null) {
			mProgressListener.onProgress();
		}
	}

	public int getSuraCount() {
		return mSuras.size();
	}

	public Sura getSura(int sura) {
		return mSuras.get(sura - 1);
	}

	public int getJuzCount() {
		return mJuzs.size();
	}

	public Mark getJuz(int juz) {
		return mJuzs.get(juz - 1);
	}

	public int getHizbCount() {
		return mHizbs.size();
	}

	public Mark getHizb(int hizb) {
		return mHizbs.get(hizb - 1);
	}

	public int getManzilCount() {
		return mManzils.size();
	}

	public Mark getManzil(int manzil) {
		return mManzils.get(manzil - 1);
	}

	public int getRukuCount() {
		return mRukus.size();
	}

	public Mark getRuku(int ruku) {
		return mRukus.get(ruku - 1);
	}

	public int getPageCount() {
		return mPages.size();
	}

	public Mark getPage(int page) {
		return mPages.get(page - 1);
	}

	public int getSajdaCount() {
		return mSajdas.size();
	}

	public Sajda getSajdas(int sajda) {
		return mSajdas.get(sajda - 1);
	}

	public Mark lastAya() {
		Sura sura = mSuras.get(mSuras.size() - 1);
		return new Mark(sura.index, sura.ayas);
	}

	public void makeBefore(Mark mark) {
		if (mark.aya > 1) {
			mark.aya--;
		} else {
			mark.sura--;
			mark.aya = getSura(mark.sura).ayas;
		}
	}

	private int find(ArrayList<Mark> list, int sura, int aya) {
		for (int i = 1; i < list.size(); i++) {
			Mark m = list.get(i);
			if (sura < m.sura || (sura == m.sura && aya < m.aya))
				return i;
		}
		return list.size();
	}

	public int findJuz(int sura, int aya) {
		return find(mJuzs, sura, aya);
	}

	public int findHizb(int sura, int aya) {
		return find(mHizbs, sura, aya);
	}

	public int findPage(int sura, int aya) {
		return find(mPages, sura, aya);
	}
}
