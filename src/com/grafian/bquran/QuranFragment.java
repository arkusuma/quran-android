package com.grafian.bquran;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.text.ClipboardManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.grafian.bquran.layout.FlowLayout;
import com.grafian.bquran.model.MetaData.Mark;
import com.grafian.bquran.model.MetaData.Sura;
import com.grafian.bquran.model.Paging;
import com.grafian.bquran.prefs.Bookmark;
import com.grafian.bquran.prefs.Bookmark.Folder;
import com.grafian.bquran.prefs.Bookmark.Item;
import com.grafian.bquran.prefs.Config;

@SuppressWarnings("deprecation")
public class QuranFragment extends ListFragment {
	final public static String PAGING = "PAGING";
	final public static String SURA = "SURA";
	final public static String AYA = "AYA";

	final private QuranAdapter mAdapter = new QuranAdapter();
	private App app;

	private int mPaging;
	private Mark mStart;
	private Mark mEnd;
	private int mSura;
	private int mAya;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		app = (App) getActivity().getApplication();

		Bundle args = getArguments();
		mPaging = args.getInt(PAGING);
		mSura = args.getInt(SURA);
		mAya = args.getInt(AYA);

		int pos = app.metaData.find(mPaging, mSura, mAya);
		mStart = app.metaData.getMarkStart(mPaging, pos);
		mEnd = app.metaData.getMarkEnd(mPaging, pos);

		setListAdapter(mAdapter);
		getListView().setOnScrollListener(onScroll);
		getListView().setFastScrollEnabled(true);
		getListView().setSelection(findPosition(mSura, mAya));
		getListView().setOnItemLongClickListener(onItemLongClick);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getListView().setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
		}

		setHasOptionsMenu(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.viewer, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int paging = 0;
		switch (item.getItemId()) {
		case R.id.view_as_sura:
			paging = Paging.SURA;
			break;
		case R.id.view_as_page:
			paging = Paging.PAGE;
			break;
		case R.id.view_as_juz:
			paging = Paging.JUZ;
			break;
		case R.id.view_as_hizb:
			paging = Paging.HIZB;
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		if (paging != mPaging) {
			Mark m = getCurrentPosition();
			((ViewerActivity) getActivity()).showPage(paging, m.sura, m.aya);
		}
		return true;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		final Mark mark = (Mark) l.getItemAtPosition(position);
		if (mark.aya > 0) {
			String folders[] = new String[app.bookmark.getFolderCount() + 1];
			for (int i = 0; i < app.bookmark.getFolderCount(); i++) {
				folders[i] = app.bookmark.getFolder(i).getName();
			}
			folders[folders.length - 1] = getString(R.string.create_new);

			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (which < app.bookmark.getFolderCount()) {
						Folder folder = app.bookmark.getFolder(which);
						if (folder.getType() == Bookmark.TYPE_SINGLE && folder.size() > 0) {
							doConfirmSingleBookmark(mark, folder);
						} else {
							addBookmark(mark, folder);
						}
					} else {
						doCreateFolder(mark);
					}
				}
			};

			new AlertDialog.Builder(getActivity())
					.setTitle(R.string.select_folder)
					.setItems(folders, listener)
					.show();
		}
	}

	public Mark getCurrentPosition() {
		int pos = getListView().getFirstVisiblePosition();
		Mark mark = (Mark) mAdapter.getItem(pos);
		if (mark.aya == 0) {
			mark.aya = 1;
		}
		return mark;
	}

	final private OnScrollListener onScroll = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (getUserVisibleHint()) {
				updateTitle(firstVisibleItem);
			}
		}
	};

	final private OnItemLongClickListener onItemLongClick = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			final Mark mark = (Mark) parent.getItemAtPosition(position);
			if (mark.aya > 0) {
				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							doCopy(mark);
						} else if (which == 1) {
							doShare(mark);
						}
					}
				};
				new AlertDialog.Builder(getActivity())
						.setTitle(formatMark(mark))
						.setItems(R.array.aya_operations, listener)
						.show();
			}
			return false;
		}
	};

	private String formatMark(Mark mark) {
		return App.getSuraName(mark.sura) + " : " + mark.aya;
	}

	private String formatContent(Mark mark) {
		String arabic = app.quranText.get(mark.sura, mark.aya);
		String translation = app.translation.get(mark.sura, mark.aya);
		return arabic + "\n\n" + translation;
	}

	private void doCopy(Mark mark) {
		String body = formatMark(mark) + "\n\n" + formatContent(mark);
		ClipboardManager clipboard = (ClipboardManager)
				getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
		clipboard.setText(body);
	}

	private void doShare(Mark mark) {
		String subject = formatMark(mark);
		String body = formatContent(mark);
		String share = getString(R.string.share_via);
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(android.content.Intent.EXTRA_TEXT, body);
		startActivity(Intent.createChooser(intent, share));
	}

	private void doConfirmSingleBookmark(final Mark mark, final Folder folder) {
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				addBookmark(mark, folder);
			}
		};
		new AlertDialog.Builder(getActivity())
				.setCancelable(true)
				.setMessage(R.string.replace_confirm)
				.setPositiveButton(R.string.replace, listener)
				.setNegativeButton(R.string.cancel, null)
				.show();
	}

	private void doCreateFolder(final Mark mark) {
		View view = getActivity().getLayoutInflater().inflate(R.layout.folder_editor, null);
		final TextView folderName = (TextView) view.findViewById(R.id.folder_name);
		final RadioGroup folderType = (RadioGroup) view.findViewById(R.id.folder_type);
		folderType.check(R.id.folder_single);

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (folderName.getText().length() > 0) {
					Folder folder = new Folder(folderName.getText().toString(),
							folderType.getCheckedRadioButtonId() == R.id.folder_single ?
									Bookmark.TYPE_SINGLE : Bookmark.TYPE_MULTIPLE);
					app.bookmark.addFolder(folder);
					addBookmark(mark, folder);
				}
			}
		};

		new AlertDialog.Builder(getActivity())
				.setCancelable(true)
				.setTitle(R.string.create_folder_title)
				.setView(view)
				.setPositiveButton(R.string.create_add, listener)
				.setNegativeButton(R.string.cancel, null)
				.show();
	}

	private void addBookmark(Mark mark, Folder folder) {
		folder.add(new Item(mark.sura, mark.aya, mPaging));
		app.bookmark.save(getActivity());

		String message = formatMark(mark) + " added to " + folder.getName();
		Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
	}

	private int findPosition(int sura, int aya) {
		int pos = 0;
		int start = mStart.aya == 1 ? 0 : mStart.aya;
		for (int i = mStart.sura; i < sura; i++) {
			pos += app.metaData.getSura(i).ayas + 1 - start;
			start = 0;
		}
		pos += (aya - start) - (aya == 1 ? 1 : 0);
		return pos;
	}

	private void updateTitle(int position) {
		Mark mark = (Mark) mAdapter.getItem(position);
		Sura sura = app.metaData.getSura(mark.sura);
		String title = "" + sura.index + ". " + App.getSuraName(sura.index);

		int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_subtitle", "id", "android");
		TextView tv = (TextView) getActivity().getWindow().findViewById(actionBarTitle);
		if (tv == null) {
			tv = (TextView) getActivity().getWindow().findViewById(R.id.action_bar_subtitle);
		}
		if (tv != null) {
			tv.setMinEms(title.length());
		}

		ActionBar ab = ((ViewerActivity) getActivity()).getSupportActionBar();
		if (!title.equals(ab.getSubtitle())) {
			ab.setSubtitle(title);
		}
	}

	private String intToArabic(int n) {
		StringBuffer sb = new StringBuffer(Integer.toString(n));
		for (int i = 0; i < sb.length(); i++) {
			char ch = sb.charAt(i);
			switch (app.config.fontArabic) {
			case Config.FONT_NOOREHUDA:
				// No transformation
				break;
			case Config.FONT_QALAM_MAJEED:
				ch += '\u06F0' - '0';
				break;
			default:
				ch += '\u0660' - '0';
			}
			sb.setCharAt(i, ch);
		}
		return sb.reverse().toString();
	}

	private String fixArabic(String s) {
		switch (app.config.fontArabic) {
		case Config.FONT_QALAM_MAJEED:
		case Config.FONT_NASKH:
			// (Small Waw | Small Yeh) + Maddah
			s = s.replaceAll("[\u06E5\u06E6]\u0653?", "");
		}

		// Add sukun on mem | nun
		s = s.replaceAll("([\u0645\u0646])([ \u0627-\u064A]|$)", "$1\u0652$2");

		// Tatweel + Hamza Above (joining chairless hamza) => Yeh With Hamza Above
		s = s.replaceAll("\u0640\u0654", "\u0626");

		return s;
	}

	private static class SuraRowHolder {
		public TextView suraName;
		public TextView suraTranslation;
		public TextView bismillah;
		public View pageSeparator;
	}

	private static class AyaRowHolder {
		public TextView ayaNumber;
		public TextView arabic;
		public TextView translation;
		public TextView juzNumber;
		public TextView pageNumber;
		public FlowLayout wordByWord;
		View pageSeparator;
		View leftSide;
	}

	private class QuranAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			int count = 0;
			int start = mStart.aya == 1 ? 0 : mStart.aya;
			for (int i = mStart.sura; i < mEnd.sura; i++) {
				count += app.metaData.getSura(i).ayas + 1 - start;
				start = 0;
			}
			count += mEnd.aya - start + 1;
			return count;
		}

		@Override
		public Object getItem(int position) {
			int sura = mStart.sura;
			int aya = mStart.aya == 1 ? 0 : mStart.aya;
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
				SuraRowHolder holder;
				if (convertView == null) {
					convertView = getActivity().getLayoutInflater().inflate(R.layout.viewer_sura_row, null);

					holder = new SuraRowHolder();
					holder.suraName = (TextView) convertView.findViewById(R.id.sura_name);
					holder.suraTranslation = (TextView) convertView.findViewById(R.id.sura_traslation);
					holder.bismillah = (TextView) convertView.findViewById(R.id.bismillah);
					holder.pageSeparator = convertView.findViewById(R.id.page_separator);
					convertView.setTag(holder);
				} else {
					holder = (SuraRowHolder) convertView.getTag();
				}

				holder.suraName.setText("" + mark.sura + ". " + App.getSuraName(mark.sura));
				holder.suraTranslation.setText("(" + App.getSuraTranslation(mark.sura) + ")");
				if (mark.sura == 1 || mark.sura == 9) {
					holder.bismillah.setVisibility(View.GONE);
				} else {
					holder.bismillah.setVisibility(View.VISIBLE);
					holder.bismillah.setText(app.quranText.get(1, 1));
					holder.bismillah.setTextSize(TypedValue.COMPLEX_UNIT_SP, app.config.fontSizeArabic);
				}

				mark.aya = 1;
				int index = app.metaData.find(Paging.PAGE, mark.sura, mark.aya);
				Mark page = app.metaData.getMarkStart(Paging.PAGE, index);
				if (mark.equals(page)) {
					holder.pageSeparator.setVisibility(View.VISIBLE);
				} else {
					holder.pageSeparator.setVisibility(View.INVISIBLE);
				}
			} else {
				AyaRowHolder holder;
				if (convertView == null) {
					convertView = getActivity().getLayoutInflater().inflate(R.layout.viewer_aya_row, null);

					holder = new AyaRowHolder();
					holder.ayaNumber = (TextView) convertView.findViewById(R.id.aya_number);
					holder.arabic = (TextView) convertView.findViewById(R.id.arabic);
					holder.translation = (TextView) convertView.findViewById(R.id.translation);
					holder.juzNumber = (TextView) convertView.findViewById(R.id.juz_number);
					holder.pageNumber = (TextView) convertView.findViewById(R.id.page_number);
					holder.pageSeparator = convertView.findViewById(R.id.page_separator);
					holder.leftSide = convertView.findViewById(R.id.left_side);
					holder.wordByWord = (FlowLayout) convertView.findViewById(R.id.word_by_word);

					convertView.setTag(holder);
				} else {
					holder = (AyaRowHolder) convertView.getTag();
				}

				String ayaNumber = "(" + mark.aya + ")";

				if (App.app.config.wordByWord) {
					holder.arabic.setVisibility(View.GONE);
					holder.wordByWord.setVisibility(View.VISIBLE);
					String[][] words = App.app.quranWord.get(mark.sura,
							mark.aya);

					// Make sure we have sufficient childs
					LayoutInflater inflater = getActivity().getLayoutInflater();
					for (int i = holder.wordByWord.getChildCount(); i < words.length + 1; i++) {
						View view = inflater.inflate(R.layout.word_by_word, null);
						TextView arabic = (TextView) view.findViewById(R.id.arabic);
						TextView translation = (TextView) view.findViewById(R.id.translation);
						arabic.setTextSize(TypedValue.COMPLEX_UNIT_SP, app.config.fontSizeArabic);
						translation.setTextSize(TypedValue.COMPLEX_UNIT_SP, app.config.fontSizeTranslation - 4);
						view.setVisibility(View.GONE);
						holder.wordByWord.addView(view);
					}

					for (int i = 0; i < words.length; i++) {
						View view = holder.wordByWord.getChildAt(i);
						TextView arabic = (TextView) view.findViewById(R.id.arabic);
						TextView translation = (TextView) view.findViewById(R.id.translation);
						arabic.setText(fixArabic(words[i][0]));
						translation.setText(words[i][1]);
						view.setVisibility(View.VISIBLE);
					}
					if (app.config.fullWidth) {
						View view = holder.wordByWord.getChildAt(words.length);
						TextView arabic = (TextView) view.findViewById(R.id.arabic);
						TextView translation = (TextView) view.findViewById(R.id.translation);
						arabic.setText("\uFD3F" + intToArabic(mark.aya) + "\uFD3E");
						translation.setText("(" + Integer.toString(mark.aya) + ")");
						view.setVisibility(View.VISIBLE);
					}
					int len = app.config.fullWidth ? words.length + 1 : words.length;
					for (int i = len; i < holder.wordByWord.getChildCount(); i++) {
						holder.wordByWord.getChildAt(i).setVisibility(View.GONE);
					}
				} else {
					String arabic = fixArabic(app.quranText.get(mark.sura, mark.aya));
					if (app.config.fullWidth) {
						arabic = arabic + " \uFD3F" + intToArabic(mark.aya) + "\uFD3E";
					}
					holder.wordByWord.setVisibility(View.GONE);
					holder.arabic.setVisibility(View.VISIBLE);
					holder.arabic.setText(arabic);
					holder.arabic.setTextSize(TypedValue.COMPLEX_UNIT_SP, app.config.fontSizeArabic);
					holder.arabic.setGravity(Gravity.RIGHT);
				}

				String translation = app.translation.get(mark.sura, mark.aya);
				if (app.config.fullWidth) {
					translation = ayaNumber + " " + translation;
				}
				holder.translation.setText(translation);
				holder.translation.setTextSize(TypedValue.COMPLEX_UNIT_SP, app.config.fontSizeTranslation);
				holder.translation.setVisibility(app.config.showTranslation ? View.VISIBLE : View.GONE);

				int pageNumber = app.metaData.find(Paging.PAGE, mark.sura, mark.aya);
				Mark page = app.metaData.getMarkStart(Paging.PAGE, pageNumber);
				if (mark.equals(page) && mark.aya != 1) {
					holder.pageSeparator.setVisibility(View.VISIBLE);
				} else {
					holder.pageSeparator.setVisibility(View.INVISIBLE);
				}

				if (app.config.fullWidth) {
					holder.leftSide.setVisibility(View.GONE);
				} else {
					holder.leftSide.setVisibility(View.VISIBLE);
					holder.ayaNumber.setText("(" + mark.aya + ")");

					int hizbNumber = app.metaData.find(Paging.HIZB, mark.sura, mark.aya);
					Mark hizb = app.metaData.getMarkStart(Paging.HIZB, hizbNumber);
					if (mark.equals(hizb)) {
						String parts[] = { "", "⅛", "¼", "⅜", "½", "⅝", "¾", "⅞" };
						hizbNumber--;
						int juz = (hizbNumber / 8) + 1;
						int part = hizbNumber % 8;
						holder.juzNumber.setText("Juz\n" + juz + parts[part]);
						holder.juzNumber.setVisibility(View.VISIBLE);
					} else {
						holder.juzNumber.setVisibility(View.INVISIBLE);
					}

					if (mark.equals(page)) {
						holder.pageNumber.setText("Page\n" + pageNumber);
						holder.pageNumber.setVisibility(View.VISIBLE);
					} else {
						holder.pageNumber.setVisibility(View.INVISIBLE);
					}
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
