package com.grafian.quran.parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

public class MetaData {

	public static class Sura {
		final public static int MECCAN = 0;
		final public static int MEDINAN = 1;
		public int index;
		public int ayas;
		public int start;
		public String name;
		public String tname;
		public String ename;
		public int type;
		public int order;
		public int rukus;
	}

	public static class Mark {
		public int sura;
		public int aya;

		public Mark() {
		}

		public Mark(Mark m) {
			this.sura = m.sura;
			this.aya = m.aya;
		}

		public Mark(int sura, int aya) {
			this.sura = sura;
			this.aya = aya;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Sajda) {
				Sajda m = (Sajda) o;
				return m.sura == sura && m.aya == aya;
			}
			return false;
		}
	}

	public static class Sajda extends Mark {
		final public static int RECOMMENDED = 0;
		final public static int OBLIGATORY = 1;
		public int type;

		public Sajda() {
		}

		public Sajda(Sajda mark) {
			super(mark.sura, mark.aya);
		}
	}

	final private ArrayList<Sura> mSuras = new ArrayList<Sura>();
	final private ArrayList<Mark> mJuzs = new ArrayList<Mark>();
	final private ArrayList<Mark> mHizbs = new ArrayList<Mark>();
	final private ArrayList<Mark> mPages = new ArrayList<Mark>();
	final private ArrayList<Sajda> mSajdas = new ArrayList<Sajda>();

	public void load(Context context, int resid) {
		try {
			InputStream in = context.getResources().openRawResource(resid);
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new InputStreamReader(in));
			int event = xpp.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				if (event == XmlPullParser.START_TAG) {
					String localName = xpp.getName();
					if ("sura".equals(localName)) {
						mSuras.add(parseSura(xpp));
					} else if ("juz".equals(localName)) {
						mJuzs.add(parseMark(xpp));
					} else if ("quarter".equals(localName)) {
						mHizbs.add(parseMark(xpp));
					} else if ("page".equals(localName)) {
						mPages.add(parseMark(xpp));
					} else if ("sajda".equals(localName)) {
						mSajdas.add(parseSajda(xpp));
					}
				}
				event = xpp.next();
			}
		} catch (Exception e) {
		}
	}

	private Sura parseSura(XmlPullParser xpp) {
		Sura sura = new Sura();
		for (int i = 0; i < xpp.getAttributeCount(); i++) {
			String k = xpp.getAttributeName(i);
			String v = xpp.getAttributeValue(i);
			if ("index".equals(k)) {
				sura.index = Integer.parseInt(v);
			} else if ("ayas".equals(k)) {
				sura.ayas = Integer.parseInt(v);
			} else if ("start".equals(k)) {
				sura.start = Integer.parseInt(v);
			} else if ("name".equals(k)) {
				sura.name = v;
			} else if ("tname".equals(k)) {
				sura.tname = v;
			} else if ("ename".equals(k)) {
				sura.ename = v;
			} else if ("type".equals(k)) {
				sura.type = "Meccan".equals(v) ? Sura.MECCAN : Sura.MEDINAN;
			} else if ("order".equals(k)) {
				sura.order = Integer.parseInt(v);
			} else if ("rukus".equals(k)) {
				sura.rukus = Integer.parseInt(v);
			}
		}
		return sura;
	}

	private Sajda parseMark(XmlPullParser xpp) {
		Sajda mark = new Sajda();
		for (int i = 0; i < xpp.getAttributeCount(); i++) {
			String k = xpp.getAttributeName(i);
			String v = xpp.getAttributeValue(i);
			if ("sura".equals(k)) {
				mark.sura = Integer.parseInt(v);
			} else if ("aya".equals(k)) {
				mark.aya = Integer.parseInt(v);
			}
		}
		return mark;
	}

	private Sajda parseSajda(XmlPullParser xpp) {
		Sajda sajda = new Sajda();
		for (int i = 0; i < xpp.getAttributeCount(); i++) {
			String k = xpp.getAttributeName(i);
			String v = xpp.getAttributeValue(i);
			if ("sura".equals(k)) {
				sajda.sura = Integer.parseInt(v);
			} else if ("aya".equals(k)) {
				sajda.aya = Integer.parseInt(v);
			} else if ("type".equals(k)) {
				sajda.type = "recommended".equals(v) ? Sajda.RECOMMENDED : Sajda.OBLIGATORY;
			}
		}
		return sajda;
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
