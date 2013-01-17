package com.grafian.quran;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.actionbarsherlock.app.SherlockListFragment;
import com.grafian.quran.MetaData.Mark;
import com.grafian.quran.MetaData.Sura;

public class QuranFragment extends SherlockListFragment {
	final public static String MODE = "MODE";
	final public static String SURA = "SURA";
	final public static String AYA = "AYA";

	final private QuranAdapter mAdapter = new QuranAdapter();
	private App app;

	private boolean mVisible;
	private int mMode;
	private Mark mFrom;
	private Mark mTo;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		app = (App) getActivity().getApplication();

		Bundle args = getArguments();
		mMode = args.getInt(MODE);
		int sura = args.getInt(SURA);
		int aya = args.getInt(AYA);
		int pos;
		switch (mMode) {
		case Config.MODE_SURA:
			mFrom = new Mark(sura, 1);
			mTo = new Mark(sura, app.metaData.getSura(sura).ayas);
			break;
		case Config.MODE_PAGE:
			pos = app.metaData.searchPage(sura, aya);
			mFrom = new Mark(app.metaData.getPage(pos));
			if (pos < app.metaData.getPageCount()) {
				mTo = new Mark(app.metaData.getPage(pos + 1));
				app.metaData.makeBefore(mTo);
			} else {
				mTo = app.metaData.lastAya();
			}
			break;
		case Config.MODE_JUZ:
			pos = app.metaData.searchJuz(sura, aya);
			mFrom = new Mark(app.metaData.getJuz(pos));
			if (pos < app.metaData.getJuzCount()) {
				mTo = new Mark(app.metaData.getJuz(pos + 1));
				app.metaData.makeBefore(mTo);
			} else {
				mTo = app.metaData.lastAya();
			}
			break;
		case Config.MODE_HIZB:
			pos = app.metaData.searchHizb(sura, aya);
			mFrom = new Mark(app.metaData.getHizb(pos));
			if (pos < app.metaData.getHizbCount()) {
				mTo = new Mark(app.metaData.getHizb(pos + 1));
				app.metaData.makeBefore(mTo);
			} else {
				mTo = app.metaData.lastAya();
			}
			break;
		}

		setListAdapter(mAdapter);
		getListView().setOnScrollListener(onScroll);
		setListShownNoAnimation(true);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		mVisible = isVisibleToUser;
	}

	final private OnScrollListener onScroll = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (mVisible) {
				updateTitle(firstVisibleItem);
			}
		}
	};

	private void updateTitle(int position) {
		Mark mark = (Mark) mAdapter.getItem(position);
		Sura sura = app.metaData.getSura(mark.sura);
		String title = "" + sura.index + ". " + App.getSuraName(sura.index);
		getActivity().setTitle(title);

	}

	private void setArabic(TextView tv, String s) {
		Spannable span = new SpannableString("\n" + s);
		span.setSpan(new RelativeSizeSpan(0.25f), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		tv.setText(span, BufferType.SPANNABLE);
	}

	private class QuranAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			int count = 0;
			int start = mFrom.aya == 1 ? 0 : mFrom.aya;
			for (int i = mFrom.sura; i < mTo.sura; i++) {
				count += app.metaData.getSura(i).ayas + 1 - start;
				start = 0;
			}
			count += mTo.aya - start + 1;
			return count;
		}

		@Override
		public Object getItem(int position) {
			int sura = mFrom.sura;
			int aya = mFrom.aya == 1 ? 0 : mFrom.aya;
			while (position > app.metaData.getSura(sura).ayas - aya) {
				position -= app.metaData.getSura(sura).ayas - aya + 1;
				sura++;
				aya = 0;
			}
			return new Mark(sura, aya + position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Mark mark = (Mark) getItem(position);
			if (mark.aya == 0) {
				if (convertView == null) {
					convertView = getActivity().getLayoutInflater().inflate(R.layout.viewer_sura_row, null);
				}

				TextView suraName = (TextView) convertView.findViewById(R.id.sura_name);
				TextView suraTranslation = (TextView) convertView.findViewById(R.id.sura_traslation);
				TextView bismillah = (TextView) convertView.findViewById(R.id.bismillah);
				View pageSeparator = convertView.findViewById(R.id.page_separator);

				suraName.setText("" + mark.sura + ". " + App.getSuraName(mark.sura));
				suraTranslation.setText("(" + App.getSuraTranslation(mark.sura) + ")");
				if (mark.sura == 1 || mark.sura == 9) {
					bismillah.setVisibility(View.GONE);
				} else {
					bismillah.setVisibility(View.VISIBLE);
					setArabic(bismillah, app.quran.get(1, 1));
				}

				mark.aya = 1;
				int index = app.metaData.searchPage(mark.sura, mark.aya);
				Mark page = app.metaData.getPage(index);
				if (mark.equals(page)) {
					pageSeparator.setVisibility(View.VISIBLE);
				} else {
					pageSeparator.setVisibility(View.INVISIBLE);
				}
			} else {
				if (convertView == null) {
					convertView = getActivity().getLayoutInflater().inflate(R.layout.viewer_aya_row, null);
				}

				TextView ayaNumber = (TextView) convertView.findViewById(R.id.aya_number);
				TextView arabic = (TextView) convertView.findViewById(R.id.arabic);
				TextView translation = (TextView) convertView.findViewById(R.id.translation);
				TextView juzNumber = (TextView) convertView.findViewById(R.id.juz_number);
				TextView pageNumber = (TextView) convertView.findViewById(R.id.page_number);
				View pageSeparator = convertView.findViewById(R.id.page_separator);

				ayaNumber.setText("(" + mark.aya + ")");
				setArabic(arabic, app.quran.get(mark.sura, mark.aya));
				translation.setText(app.translation.get(mark.sura, mark.aya));

				int index = app.metaData.searchHizb(mark.sura, mark.aya);
				Mark hizb = app.metaData.getHizb(index);
				if (mark.equals(hizb)) {
					String s[] = { "", "⅛", "¼", "⅜", "½", "⅝", "¾", "⅞" };
					index--;
					int juz = (index / 8) + 1;
					index %= 8;
					juzNumber.setText("Juz\n" + juz + s[index]);
					juzNumber.setVisibility(View.VISIBLE);
				} else {
					juzNumber.setVisibility(View.INVISIBLE);
				}

				index = app.metaData.searchPage(mark.sura, mark.aya);
				Mark page = app.metaData.getPage(index);
				if (mark.equals(page)) {
					pageNumber.setText("Page\n" + index);
					pageNumber.setVisibility(View.VISIBLE);
				} else {
					pageNumber.setVisibility(View.INVISIBLE);
				}
				if (mark.equals(page) && mark.aya != 1) {
					pageSeparator.setVisibility(View.VISIBLE);
				} else {
					pageSeparator.setVisibility(View.INVISIBLE);
				}
			}
			return convertView;
		}

		@Override
		public int getItemViewType(int position) {
			return ((Mark) getItem(position)).aya == 0 ? 0 : 1;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

	}
}
