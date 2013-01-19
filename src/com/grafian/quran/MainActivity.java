package com.grafian.quran;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class MainActivity extends BaseActivity {

	final private static String PAGE = "page";

	private ViewPager mPager;
	private PagerAdapter mAdapter;
	private int mPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mAdapter = new PagerAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		if (savedInstanceState != null) {
			mPage = savedInstanceState.getInt(PAGE);
		} else {
			mPage = mApp.config.pagingMode + 1;
		}

		if (mApp.loaded) {
			mPager.setCurrentItem(mPage);
		} else {
			mApp.loadAllData(this, new ProgressListener() {
				@Override
				public void onProgress() {
				}

				@Override
				public void onFinish() {
					mAdapter.notifyDataSetChanged();
					mPager.setCurrentItem(mPage);
				}
			});
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(PAGE, mPager.getCurrentItem());
	}

	private class PagerAdapter extends FragmentPagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return new BookmarkFragment();
			case 1:
				return new SuraFragment();
			case 2:
				return new PageFragment();
			case 3:
				return new JuzFragment();
			case 4:
				return new HizbFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			return mApp.loaded ? 5 : 0;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			String titles[] = { "Bookmark", "Sura", "Page", "Juz", "Hizb" };
			return titles[position];
		}

	}

}
