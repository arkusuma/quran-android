package com.grafian.quran;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.TypedValue;
import android.view.Gravity;
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

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.grafian.quran.parser.MetaData.Mark;
import com.grafian.quran.parser.MetaData.Sura;
import com.grafian.quran.prefs.Bookmark;
import com.grafian.quran.prefs.Bookmark.Folder;
import com.grafian.quran.prefs.Bookmark.Item;

@SuppressWarnings("deprecation")
public class QuranFragment extends SherlockListFragment {
	final public static String PAGING_MODE = "PAGING_MODE";
	final public static String SURA = "SURA";
	final public static String AYA = "AYA";

	final private QuranAdapter mAdapter = new QuranAdapter();
	private App app;

	private int mPagingMode;
	private Mark mFrom;
	private Mark mTo;
	private int mSura;
	private int mAya;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		app = (App) getActivity().getApplication();

		Bundle args = getArguments();
		mPagingMode = args.getInt(PAGING_MODE);
		mSura = args.getInt(SURA);
		mAya = args.getInt(AYA);

		int pos;
		switch (mPagingMode) {
		case PagingMode.SURA:
			mFrom = new Mark(mSura, 1);
			mTo = new Mark(mSura, app.metaData.getSura(mSura).ayas);
			break;
		case PagingMode.PAGE:
			pos = app.metaData.findPage(mSura, mAya);
			mFrom = new Mark(app.metaData.getPage(pos));
			if (pos < app.metaData.getPageCount()) {
				mTo = new Mark(app.metaData.getPage(pos + 1));
				app.metaData.makeBefore(mTo);
			} else {
				mTo = app.metaData.lastAya();
			}
			break;
		case PagingMode.JUZ:
			pos = app.metaData.findJuz(mSura, mAya);
			mFrom = new Mark(app.metaData.getJuz(pos));
			if (pos < app.metaData.getJuzCount()) {
				mTo = new Mark(app.metaData.getJuz(pos + 1));
				app.metaData.makeBefore(mTo);
			} else {
				mTo = app.metaData.lastAya();
			}
			break;
		case PagingMode.HIZB:
			pos = app.metaData.findHizb(mSura, mAya);
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
		getListView().setFastScrollEnabled(true);
		getListView().setSelection(findPosition(mSura, mAya));
		getListView().setOnItemLongClickListener(onItemLongClick);

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
		int pagingMode = 0;
		switch (item.getItemId()) {
		case R.id.view_as_sura:
			pagingMode = PagingMode.SURA;
			break;
		case R.id.view_as_page:
			pagingMode = PagingMode.PAGE;
			break;
		case R.id.view_as_juz:
			pagingMode = PagingMode.JUZ;
			break;
		case R.id.view_as_hizb:
			pagingMode = PagingMode.HIZB;
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		if (pagingMode != mPagingMode) {
			Mark m = getCurrentPosition();
			((ViewerActivity) getActivity()).showPage(pagingMode, m.sura, m.aya);
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

	public void setup() {
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
			if (app.loaded && getUserVisibleHint()) {
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
		String arabic = app.quran.get(mark.sura, mark.aya);
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
		folder.add(new Item(mark.sura, mark.aya, mPagingMode));
		app.bookmark.save(getActivity());

		String message = formatMark(mark) + " added to " + folder.getName();
		Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
	}

	private int findPosition(int sura, int aya) {
		int pos = 0;
		int start = mFrom.aya == 1 ? 0 : mFrom.aya;
		for (int i = mFrom.sura; i < sura; i++) {
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
			tv = (TextView) getActivity().getWindow().findViewById(R.id.abs__action_bar_subtitle);
		}
		if (tv != null) {
			tv.setMinEms(title.length());
		}

		ActionBar ab = ((ViewerActivity) getActivity()).getSupportActionBar();
		ab.setSubtitle(title);
	}

	private String fix(String s) {
		return s.replace("\u064E\u0670", "\u0670");
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
		View pageSeparator;
	}

	private class QuranAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (!app.loaded) {
				return 0;
			}

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
					holder.bismillah.setText(fix(app.quran.get(1, 1)));
					holder.bismillah.setTextSize(TypedValue.COMPLEX_UNIT_SP, app.config.fontSizeArabic);
				}

				mark.aya = 1;
				int index = app.metaData.findPage(mark.sura, mark.aya);
				Mark page = app.metaData.getPage(index);
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

					convertView.setTag(holder);
				} else {
					holder = (AyaRowHolder) convertView.getTag();
				}

				holder.ayaNumber.setText("(" + mark.aya + ")");

				holder.arabic.setText(fix(app.quran.get(mark.sura, mark.aya)));
				holder.arabic.setTextSize(TypedValue.COMPLEX_UNIT_SP, app.config.fontSizeArabic);
				holder.arabic.setGravity(Gravity.RIGHT);

				holder.translation.setText(app.translation.get(mark.sura, mark.aya));
				holder.translation.setTextSize(TypedValue.COMPLEX_UNIT_SP, app.config.fontSizeTranslation);
				holder.translation.setVisibility(app.config.showTranslation ? View.VISIBLE : View.GONE);

				int index = app.metaData.findHizb(mark.sura, mark.aya);
				Mark hizb = app.metaData.getHizb(index);
				if (mark.equals(hizb)) {
					String parts[] = { "", "⅛", "¼", "⅜", "½", "⅝", "¾", "⅞" };
					index--;
					int juz = (index / 8) + 1;
					index %= 8;
					holder.juzNumber.setText("Juz\n" + juz + parts[index]);
					holder.juzNumber.setVisibility(View.VISIBLE);
				} else {
					holder.juzNumber.setVisibility(View.INVISIBLE);
				}

				index = app.metaData.findPage(mark.sura, mark.aya);
				Mark page = app.metaData.getPage(index);
				if (mark.equals(page)) {
					holder.pageNumber.setText("Page\n" + index);
					holder.pageNumber.setVisibility(View.VISIBLE);
				} else {
					holder.pageNumber.setVisibility(View.INVISIBLE);
				}
				if (mark.equals(page) && mark.aya != 1) {
					holder.pageSeparator.setVisibility(View.VISIBLE);
				} else {
					holder.pageSeparator.setVisibility(View.INVISIBLE);
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
