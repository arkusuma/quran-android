package com.grafian.quran;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.grafian.quran.MetaData.Mark;

public class ViewerActivity extends BaseActivity {

	private ViewPager mPager;
	private ViewerAdapter mAdapter;
	private QuranFragment mCurrentPage;
	private int mMode;
	private int mSura;
	private int mAya;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewer);

		mAdapter = new ViewerAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		if (savedInstanceState != null) {
			mMode = savedInstanceState.getInt(QuranFragment.MODE);
			mSura = savedInstanceState.getInt(QuranFragment.SURA);
			mAya = savedInstanceState.getInt(QuranFragment.AYA);
		} else {
			Intent intent = getIntent();
			mMode = intent.getIntExtra(QuranFragment.MODE, Config.MODE_SURA);
			mSura = intent.getIntExtra(QuranFragment.SURA, 1);
			mAya = intent.getIntExtra(QuranFragment.AYA, 1);
		}

		if (mApp.loaded) {
			mPager.setCurrentItem(findTransformedPosition(mSura, mAya));
		} else {
			mApp.loadAllData(this, new ProgressListener() {
				@Override
				public void onProgress() {
				}

				@Override
				public void onFinish() {
					mAdapter.notifyDataSetChanged();
					mPager.setCurrentItem(findTransformedPosition(mSura, mAya));
				}
			});
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Check if reading direction is still valid
		QuranFragment fragment = (QuranFragment) mAdapter.instantiateItem(mPager, mPager.getCurrentItem());
		if (fragment.getUserVisibleHint()) {
			Mark m = fragment.getCurrentPosition();
			int pos = findTransformedPosition(m.sura, m.aya);
			if (pos != mPager.getCurrentItem()) {
				mSura = m.sura;
				mAya = m.aya;
				mPager.setCurrentItem(pos);
			}
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mCurrentPage != null) {
			Mark mark = mCurrentPage.getCurrentPosition();
			outState.putInt(QuranFragment.MODE, mMode);
			outState.putInt(QuranFragment.SURA, mark.sura);
			outState.putInt(QuranFragment.AYA, mark.aya);
		}
	}

	private int transformPosition(int position) {
		if (mApp.config.rtl) {
			return mAdapter.getCount() - position - 1;
		}
		return position;
	}

	private int findTransformedPosition(int sura, int aya) {
		int item = 0;
		switch (mMode) {
		case Config.MODE_SURA:
			item = sura - 1;
			break;
		case Config.MODE_PAGE:
			item = mApp.metaData.findPage(sura, aya) - 1;
			break;
		case Config.MODE_JUZ:
			item = mApp.metaData.findJuz(sura, aya) - 1;
			break;
		case Config.MODE_HIZB:
			item = mApp.metaData.findHizb(sura, aya) - 1;
			break;
		}
		return transformPosition(item);
	}

	private class ViewerAdapter extends FragmentStatePagerAdapter {

		public ViewerAdapter(FragmentManager fm) {
			super(fm);
		}

		@SuppressWarnings("deprecation")
		@Override
		public void setPrimaryItem(View container, int position, Object object) {
			super.setPrimaryItem(container, position, object);
			mCurrentPage = (QuranFragment) object;
		}

		private Mark getStartMark(int page) {
			page = transformPosition(page);
			Mark mark = null;
			switch (mMode) {
			case Config.MODE_SURA:
				mark = new Mark(page + 1, 1);
				break;
			case Config.MODE_PAGE:
				mark = new Mark(mApp.metaData.getPage(page + 1));
				break;
			case Config.MODE_JUZ:
				mark = new Mark(mApp.metaData.getJuz(page + 1));
				break;
			case Config.MODE_HIZB:
				mark = new Mark(mApp.metaData.getHizb(page + 1));
				break;
			}
			return mark;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new QuranFragment();
			Bundle args = new Bundle();
			Mark m = getStartMark(position);
			if (position == findTransformedPosition(mSura, mAya)) {
				m.sura = mSura;
				m.aya = mAya;
			}
			args.putInt(QuranFragment.MODE, mMode);
			args.putInt(QuranFragment.SURA, m.sura);
			args.putInt(QuranFragment.AYA, m.aya);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			if (mApp.loaded) {
				switch (mMode) {
				case Config.MODE_SURA:
					return mApp.metaData.getSuraCount();
				case Config.MODE_PAGE:
					return mApp.metaData.getPageCount();
				case Config.MODE_JUZ:
					return mApp.metaData.getJuzCount();
				case Config.MODE_HIZB:
					return mApp.metaData.getHizbCount();
				}
			}
			return 0;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			position = transformPosition(position);
			switch (mMode) {
			case Config.MODE_SURA:
				return "" + (position + 1) + ". " + App.getSuraName(position + 1);
			case Config.MODE_PAGE:
				return "Page " + (position + 1);
			case Config.MODE_JUZ:
				return "Juz " + (position + 1);
			case Config.MODE_HIZB:
				return "Hizb " + (position + 1);
			}
			return "";
		}
	}

}
