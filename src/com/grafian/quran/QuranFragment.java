package com.grafian.quran;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
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
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.grafian.quran.Bookmark.Folder;
import com.grafian.quran.Bookmark.Item;
import com.grafian.quran.MetaData.Mark;
import com.grafian.quran.MetaData.Sura;

@SuppressWarnings("deprecation")
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
	private int mSura;
	private int mAya;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		app = (App) getActivity().getApplication();

		Bundle args = getArguments();
		mMode = args.getInt(MODE);
		mSura = args.getInt(SURA);
		mAya = args.getInt(AYA);
		int pos;
		app.config.setMode(mMode);
		switch (mMode) {
		case Config.MODE_SURA:
			mFrom = new Mark(mSura, 1);
			mTo = new Mark(mSura, app.metaData.getSura(mSura).ayas);
			break;
		case Config.MODE_PAGE:
			pos = app.metaData.findPage(mSura, mAya);
			mFrom = new Mark(app.metaData.getPage(pos));
			if (pos < app.metaData.getPageCount()) {
				mTo = new Mark(app.metaData.getPage(pos + 1));
				app.metaData.makeBefore(mTo);
			} else {
				mTo = app.metaData.lastAya();
			}
			break;
		case Config.MODE_JUZ:
			pos = app.metaData.findJuz(mSura, mAya);
			mFrom = new Mark(app.metaData.getJuz(pos));
			if (pos < app.metaData.getJuzCount()) {
				mTo = new Mark(app.metaData.getJuz(pos + 1));
				app.metaData.makeBefore(mTo);
			} else {
				mTo = app.metaData.lastAya();
			}
			break;
		case Config.MODE_HIZB:
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
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		mVisible = isVisibleToUser;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.viewer, menu);
	}

	private void switchMode(int mode) {
		if (mode != mMode) {
			int pos = getListView().getFirstVisiblePosition();
			Mark mark = (Mark) mAdapter.getItem(pos);
			if (mark.aya == 0) {
				mark.aya = 1;
			}
			Intent intent = getActivity().getIntent();
			intent.putExtra(MODE, mode);
			intent.putExtra(SURA, mark.sura);
			intent.putExtra(AYA, mark.aya);
			getActivity().finish();
			startActivity(intent);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.view_as_sura:
			switchMode(Config.MODE_SURA);
			return true;
		case R.id.view_as_page:
			switchMode(Config.MODE_PAGE);
			return true;
		case R.id.view_as_juz:
			switchMode(Config.MODE_JUZ);
			return true;
		case R.id.view_as_hizb:
			switchMode(Config.MODE_HIZB);
			return true;
		}
		return super.onOptionsItemSelected(item);
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
							folderType.getCheckedRadioButtonId() == Bookmark.TYPE_SINGLE ?
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
		folder.add(new Item(mark.sura, mark.aya, mMode));
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
		getActivity().setTitle(title);
	}

	private void setArabic(TextView tv, String s) {
		Spannable span = new SpannableString("\n" + s);
		span.setSpan(new RelativeSizeSpan(0.25f), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		tv.setText(span, BufferType.SPANNABLE);
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
					setArabic(holder.bismillah, app.quran.get(1, 1));
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
				setArabic(holder.arabic, app.quran.get(mark.sura, mark.aya));
				holder.translation.setText(app.translation.get(mark.sura, mark.aya));

				int index = app.metaData.findHizb(mark.sura, mark.aya);
				Mark hizb = app.metaData.getHizb(index);
				if (mark.equals(hizb)) {
					String s[] = { "", "⅛", "¼", "⅜", "½", "⅝", "¾", "⅞" };
					index--;
					int juz = (index / 8) + 1;
					index %= 8;
					holder.juzNumber.setText("Juz\n" + juz + s[index]);
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
