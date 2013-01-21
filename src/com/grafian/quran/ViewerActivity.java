package com.grafian.quran;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.view.MenuItem;
import com.grafian.quran.MetaData.Mark;

public class ViewerActivity extends BaseActivity {

	private ViewPager mPager;
	private ViewerAdapter mAdapter;
	private int mPagingMode;
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
			mPagingMode = savedInstanceState.getInt(QuranFragment.PAGING_MODE);
			mSura = savedInstanceState.getInt(QuranFragment.SURA);
			mAya = savedInstanceState.getInt(QuranFragment.AYA);
		} else {
			Intent intent = getIntent();
			mPagingMode = intent.getIntExtra(QuranFragment.PAGING_MODE, Config.PAGING_MODE_SURA);
			mSura = intent.getIntExtra(QuranFragment.SURA, 1);
			mAya = intent.getIntExtra(QuranFragment.AYA, 1);
		}

		if (mApp.loaded) {
			showPage(mPagingMode, mSura, mAya);
		} else {
			mApp.loadAllData(this, new ProgressListener() {
				@Override
				public void onProgress() {
				}

				@Override
				public void onFinish() {
					showPage(mPagingMode, mSura, mAya);
				}
			});
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Check if reading direction is still valid
		QuranFragment fragment = getCurrentFragment();
		if (fragment.getUserVisibleHint()) {
			Mark m = fragment.getCurrentPosition();
			showPage(mPagingMode, m.sura, m.aya);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		QuranFragment fragment = getCurrentFragment();
		if (fragment != null) {
			Mark m = fragment.getCurrentPosition();
			outState.putInt(QuranFragment.PAGING_MODE, mPagingMode);
			outState.putInt(QuranFragment.SURA, m.sura);
			outState.putInt(QuranFragment.AYA, m.aya);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void showPage(int pagingMode, int sura, int aya) {
		mPagingMode = pagingMode;
		mSura = sura;
		mAya = aya;
		mAdapter.notifyDataSetChanged();
		mPager.setCurrentItem(findTransformedPosition(sura, aya));
	}

	private QuranFragment getCurrentFragment() {
		if (mAdapter.getCount() == 0) {
			return null;
		} else {
			return (QuranFragment) mAdapter.instantiateItem(mPager, mPager.getCurrentItem());
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
		switch (mPagingMode) {
		case Config.PAGING_MODE_SURA:
			item = sura - 1;
			break;
		case Config.PAGING_MODE_PAGE:
			item = mApp.metaData.findPage(sura, aya) - 1;
			break;
		case Config.PAGING_MODE_JUZ:
			item = mApp.metaData.findJuz(sura, aya) - 1;
			break;
		case Config.PAGING_MODE_HIZB:
			item = mApp.metaData.findHizb(sura, aya) - 1;
			break;
		}
		return transformPosition(item);
	}

	private class ViewerAdapter extends FragmentStatePagerAdapter {

		public ViewerAdapter(FragmentManager fm) {
			super(fm);
		}

		private Mark getStartMark(int page) {
			page = transformPosition(page);
			Mark mark = null;
			switch (mPagingMode) {
			case Config.PAGING_MODE_SURA:
				mark = new Mark(page + 1, 1);
				break;
			case Config.PAGING_MODE_PAGE:
				mark = new Mark(mApp.metaData.getPage(page + 1));
				break;
			case Config.PAGING_MODE_JUZ:
				mark = new Mark(mApp.metaData.getJuz(page + 1));
				break;
			case Config.PAGING_MODE_HIZB:
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
			args.putInt(QuranFragment.PAGING_MODE, mPagingMode);
			args.putInt(QuranFragment.SURA, m.sura);
			args.putInt(QuranFragment.AYA, m.aya);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			if (mApp.loaded) {
				switch (mPagingMode) {
				case Config.PAGING_MODE_SURA:
					return mApp.metaData.getSuraCount();
				case Config.PAGING_MODE_PAGE:
					return mApp.metaData.getPageCount();
				case Config.PAGING_MODE_JUZ:
					return mApp.metaData.getJuzCount();
				case Config.PAGING_MODE_HIZB:
					return mApp.metaData.getHizbCount();
				}
			}
			return 0;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if (position < getCount()) {
				position = transformPosition(position);
				switch (mPagingMode) {
				case Config.PAGING_MODE_SURA:
					return "" + (position + 1) + ". " + App.getSuraName(position + 1);
				case Config.PAGING_MODE_PAGE:
					return "Page " + (position + 1);
				case Config.PAGING_MODE_JUZ:
					return "Juz " + (position + 1);
				case Config.PAGING_MODE_HIZB:
					return "Hizb " + (position + 1);
				}
			}
			return "";
		}
	}

}
