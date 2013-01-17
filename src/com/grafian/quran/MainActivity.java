package com.grafian.quran;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public class MainActivity extends BaseFragmentActivity {

	private ViewPager mPager;
	private PagerAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pager);

		mAdapter = new PagerAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		mPager.setOnPageChangeListener(onPageChange);

		if (app.loaded) {
			updatePage();
		} else {
			app.loadAllData(this, new ProgressListener() {
				@Override
				public void onProgress() {
				}

				@Override
				public void onFinish() {
					mAdapter.notifyDataSetChanged();
					updatePage();
				}
			});
		}
	}

	final private OnPageChangeListener onPageChange = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int page) {
			app.config.setMode(page);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

	private void updatePage() {
		mPager.setCurrentItem(app.config.getMode());
	}

	private class PagerAdapter extends FragmentPagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return new SuraFragment();
			case 1:
				return new PageFragment();
			case 2:
				return new JuzFragment();
			case 3:
				return new HizbFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			return app.loaded ? 4 : 0;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			String titles[] = { "Sura", "Page", "Juz", "Hizb" };
			return titles[position];
		}

	}

}
