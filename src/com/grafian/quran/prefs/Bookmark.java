package com.grafian.quran.prefs;

import java.util.ArrayList;

import android.app.backup.BackupManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.grafian.quran.App;

public class Bookmark {

	final public static int TYPE_SINGLE = 0;
	final public static int TYPE_MULTIPLE = 1;

	final public static String BOOKMARK_NAME = "bookmark";

	public static class Item {
		private int sura;
		private int aya;
		private int mode;
		private long timestamp;

		public Item(int sura, int aya, int mode, long timestamp) {
			this.sura = sura;
			this.aya = aya;
			this.mode = mode;
			this.timestamp = timestamp;
		}

		public Item(int sura, int aya, int mode) {
			this(sura, aya, mode, System.currentTimeMillis());
		}

		public int getSura() {
			return sura;
		}

		public int getAya() {
			return aya;
		}

		public int getMode() {
			return mode;
		}

		public long getTimestamp() {
			return timestamp;
		}

	}

	public static class Folder {
		private String name;
		private int type;
		private ArrayList<Item> items;

		public Folder(String name, int type) {
			this.name = name;
			this.type = type;
			this.items = new ArrayList<Bookmark.Item>();
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
			BackupManager.dataChanged(App.PACKAGE_NAME);
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
			if (type == TYPE_SINGLE) {
				while (items.size() > 1) {
					items.remove(1);
				}
			}
			BackupManager.dataChanged(App.PACKAGE_NAME);
		}

		public void add(Item item) {
			if (type == TYPE_SINGLE) {
				items.clear();
			} else {
				// Remove duplicate
				for (int i = 0; i < items.size(); i++) {
					Item it = items.get(i);
					if (it.sura == item.sura && it.aya == item.aya) {
						items.remove(it);
						i--;
					}
				}
			}
			items.add(item);
			BackupManager.dataChanged(App.PACKAGE_NAME);
		}

		public Item get(int index) {
			return items.get(index);
		}

		public void remove(int index) {
			items.remove(index);
			BackupManager.dataChanged(App.PACKAGE_NAME);
		}

		public void remove(Item item) {
			items.remove(item);
			BackupManager.dataChanged(App.PACKAGE_NAME);
		}

		public void clear() {
			items.clear();
			BackupManager.dataChanged(App.PACKAGE_NAME);
		}

		public int size() {
			return items.size();
		}

	}

	private ArrayList<Folder> folders = new ArrayList<Folder>();

	public void save(Context context) {
		SharedPreferences sp = context.getSharedPreferences(BOOKMARK_NAME, Context.MODE_PRIVATE);
		Editor ed = sp.edit();

		Gson gson = new Gson();
		String json = gson.toJson(this, Bookmark.class);
		ed.putString(BOOKMARK_NAME, json);

		ed.commit();
	}

	public void load(Context context) {
		SharedPreferences sp = context.getSharedPreferences(BOOKMARK_NAME, Context.MODE_PRIVATE);

		try {
			folders = null;
			Gson gson = new Gson();
			String json = sp.getString(BOOKMARK_NAME, "{}");
			Bookmark bookmark = gson.fromJson(json, Bookmark.class);
			folders = bookmark.folders;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (folders == null) {
			folders = new ArrayList<Bookmark.Folder>();
		}
		if (folders.isEmpty()) {
			folders.add(new Folder("Default", TYPE_MULTIPLE));
		}
	}

	public void addFolder(Folder folder) {
		folders.add(folder);
		BackupManager.dataChanged(App.PACKAGE_NAME);
	}

	public Folder getFolder(int i) {
		return folders.get(i);
	}

	public void removeFolder(int i) {
		folders.remove(i);
		BackupManager.dataChanged(App.PACKAGE_NAME);
	}

	public void removeFolder(Folder folder) {
		folders.remove(folder);
		BackupManager.dataChanged(App.PACKAGE_NAME);
	}

	public int getFolderCount() {
		return folders.size();
	}

	public Folder findContainingFolder(Item item) {
		for (Folder folder : folders) {
			if (folder.items.contains(item)) {
				return folder;
			}
		}
		return null;
	}

}
