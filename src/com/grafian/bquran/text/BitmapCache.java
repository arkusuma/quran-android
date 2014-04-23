package com.grafian.bquran.text;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class BitmapCache {
	final private static int CACHE_SIZE = 256;

	private LruCache<String, Bitmap> mCache;

	private static BitmapCache mInstance;

	private BitmapCache() {
		mCache = new LruCache<String, Bitmap>(CACHE_SIZE);
	}

	public static BitmapCache getInstance() {
		if (mInstance == null) {
			mInstance = new BitmapCache();
		}
		return mInstance;
	}

	public void addBitmap(String key, Bitmap bitmap) {
		if (getBitmap(key) == null) {
			mCache.put(key, bitmap);
		}
	}

	public Bitmap getBitmap(String key) {
		return mCache.get(key);
	}

	public void clearCache() {
		mCache.evictAll();
	}
}
