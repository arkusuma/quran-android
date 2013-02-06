package com.grafian.quran;

import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.grafian.quran.model.MetaData.Sura;
import com.grafian.quran.prefs.Bookmark;
import com.grafian.quran.prefs.Bookmark.Folder;
import com.grafian.quran.prefs.Bookmark.Item;

public class BookmarkFragment extends SherlockListFragment {

	private BookmarkAdapter mAdapter;
	private App app;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		app = (App) getActivity().getApplication();

		mAdapter = new BookmarkAdapter();
		setListAdapter(mAdapter);
		getListView().setFastScrollEnabled(true);
		getListView().setOnItemLongClickListener(onItemLongClick);
	}

	@Override
	public void onResume() {
		super.onResume();
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(getActivity(), ViewerActivity.class);
		Object obj = getListAdapter().getItem(position);
		if (obj instanceof Item) {
			Item item = (Item) obj;
			intent.putExtra(QuranFragment.PAGING, item.getMode());
			intent.putExtra(QuranFragment.SURA, item.getSura());
			intent.putExtra(QuranFragment.AYA, item.getAya());
			startActivity(intent);
		} else {
			doEditFolder((Folder) obj);
		}
	}

	private void doEditFolder(final Folder folder) {
		View view = getActivity().getLayoutInflater().inflate(R.layout.folder_editor, null);
		final TextView folderName = (TextView) view.findViewById(R.id.folder_name);
		final RadioGroup folderType = (RadioGroup) view.findViewById(R.id.folder_type);

		folderName.setText(folder.getName());
		folderType.check(folder.getType() == Bookmark.TYPE_SINGLE ?
				R.id.folder_single : R.id.folder_multiple);

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (folderName.getText().length() > 0) {
					folder.setName(folderName.getText().toString());
					folder.setType(folderType.getCheckedRadioButtonId() == R.id.folder_single ?
							Bookmark.TYPE_SINGLE : Bookmark.TYPE_MULTIPLE);
					app.bookmark.save(getActivity());
					mAdapter.notifyDataSetChanged();
				}
			}
		};

		new AlertDialog.Builder(getActivity())
				.setCancelable(true)
				.setTitle(R.string.edit_folder_title)
				.setView(view)
				.setPositiveButton(R.string.save, listener)
				.setNegativeButton(R.string.cancel, null)
				.show();
	}

	final private OnItemLongClickListener onItemLongClick = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			Object obj = parent.getItemAtPosition(position);
			if (obj instanceof Folder) {
				doDeleteFolder((Folder) obj);
			} else {
				doDeleteItem((Item) obj);
			}
			return true;
		}
	};

	private void doDeleteItem(final Item item) {
		final Folder folder = app.bookmark.findContainingFolder(item);

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				folder.remove(item);
				app.bookmark.save(getActivity());
				mAdapter.notifyDataSetChanged();
			}
		};

		new AlertDialog.Builder(getActivity())
				.setCancelable(true)
				.setMessage(R.string.delete_bookmark_item)
				.setPositiveButton(R.string.delete, listener)
				.setNegativeButton(R.string.cancel, null)
				.show();
	}

	private void doDeleteFolder(final Folder folder) {
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				app.bookmark.removeFolder(folder);
				app.bookmark.save(getActivity());
				mAdapter.notifyDataSetChanged();
			}
		};

		new AlertDialog.Builder(getActivity())
				.setCancelable(true)
				.setMessage(R.string.delete_bookmark_folder)
				.setPositiveButton(R.string.delete, listener)
				.setNegativeButton(R.string.cancel, null)
				.show();
	}

	private static class FolderRowHolder {
		public TextView folderName;
	}

	private static class ItemRowHolder {
		public TextView suraName;
		public TextView date;
	}

	class BookmarkAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			int count = app.bookmark.getFolderCount();
			for (int i = 0; i < app.bookmark.getFolderCount(); i++) {
				count += app.bookmark.getFolder(i).size();
			}
			return count;
		}

		@Override
		public Object getItem(int position) {
			for (int i = 0; i < app.bookmark.getFolderCount(); i++) {
				if (position > app.bookmark.getFolder(i).size()) {
					position -= app.bookmark.getFolder(i).size() + 1;
				} else {
					if (position == 0)
						return app.bookmark.getFolder(i);
					return app.bookmark.getFolder(i).get(position - 1);
				}
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressWarnings("deprecation")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Object obj = getItem(position);
			if (obj instanceof Folder) {
				FolderRowHolder holder;
				if (convertView == null) {
					convertView = getActivity().getLayoutInflater().inflate(R.layout.bookmark_folder_row, null);

					holder = new FolderRowHolder();
					holder.folderName = (TextView) convertView.findViewById(R.id.folder_name);
					convertView.setTag(holder);
				} else {
					holder = (FolderRowHolder) convertView.getTag();
				}

				Folder folder = (Folder) obj;
				holder.folderName.setText(folder.getName());
			} else {
				ItemRowHolder holder;
				if (convertView == null) {
					convertView = getActivity().getLayoutInflater().inflate(R.layout.bookmark_item_row, null);

					holder = new ItemRowHolder();
					holder.suraName = (TextView) convertView.findViewById(R.id.sura_name);
					holder.date = (TextView) convertView.findViewById(R.id.date);
					convertView.setTag(holder);
				} else {
					holder = (ItemRowHolder) convertView.getTag();
				}

				Item item = (Item) obj;
				Sura sura = app.metaData.getSura(item.getSura());
				Date d = new Date(item.getTimestamp());
				Date now = new Date();

				holder.suraName.setText("" + sura.index + ". " + App.getSuraName(sura.index) + " : " + item.getAya());
				if (d.getYear() == now.getYear() && d.getMonth() == now.getMonth() && d.getDate() == now.getDate()) {
					holder.date.setText(DateFormat.format("kk:mm", d));
				} else {
					holder.date.setText(DateFormat.format("dd MMM yyyy", d));
				}

			}
			return convertView;
		}

		@Override
		public int getItemViewType(int position) {
			if (getItem(position) instanceof Folder) {
				return 0;
			}
			return 1;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}
	}

}
