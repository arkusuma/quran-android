package com.grafian.quran;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.view.MenuItem;
import com.grafian.quran.parser.MetaData.Mark;

public class ViewerActivity extends BaseActivity {

	private ViewPager mPager;
	private ViewerAdapter mAdapter;
	private int mPagingMode;
	private int mSura;
	private int mAya;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pager);

		mAdapter = new ViewerAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		if (savedInstanceState != null) {
			mPagingMode = savedInstanceState.getInt(QuranFragment.PAGING_MODE);
			mSura = savedInstanceState.getInt(QuranFragment.SURA);
			mAya = savedInstanceState.getInt(QuranFragment.AYA);
		} else {
			Intent intent = getIntent();
			mPagingMode = intent.getIntExtra(QuranFragment.PAGING_MODE, PagingMode.SURA);
			mSura = intent.getIntExtra(QuranFragment.SURA, 1);
			mAya = intent.getIntExtra(QuranFragment.AYA, 1);
		}

		if (App.app.loaded) {
			showPage(mPagingMode, mSura, mAya);
		} else {
			App.app.loadAllData(this, new ProgressListener() {
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
		if (fragment != null && fragment.getUserVisibleHint()) {
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
		if (App.app.config.rtl) {
			return mAdapter.getCount() - position - 1;
		}
		return position;
	}

	private int findTransformedPosition(int sura, int aya) {
		int item = 0;
		switch (mPagingMode) {
		case PagingMode.SURA:
			item = sura - 1;
			break;
		case PagingMode.PAGE:
			item = App.app.metaData.findPage(sura, aya) - 1;
			break;
		case PagingMode.JUZ:
			item = App.app.metaData.findJuz(sura, aya) - 1;
			break;
		case PagingMode.HIZB:
			item = App.app.metaData.findHizb(sura, aya) - 1;
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
			case PagingMode.SURA:
				mark = new Mark(page + 1, 1);
				break;
			case PagingMode.PAGE:
				mark = new Mark(App.app.metaData.getPage(page + 1));
				break;
			case PagingMode.JUZ:
				mark = new Mark(App.app.metaData.getJuz(page + 1));
				break;
			case PagingMode.HIZB:
				mark = new Mark(App.app.metaData.getHizb(page + 1));
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
			if (App.app.loaded) {
				switch (mPagingMode) {
				case PagingMode.SURA:
					return App.app.metaData.getSuraCount();
				case PagingMode.PAGE:
					return App.app.metaData.getPageCount();
				case PagingMode.JUZ:
					return App.app.metaData.getJuzCount();
				case PagingMode.HIZB:
					return App.app.metaData.getHizbCount();
				}
			}
			return 0;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if (position < getCount()) {
				position = transformPosition(position);
				switch (mPagingMode) {
				case PagingMode.SURA:
					return "" + (position + 1) + ". " + App.getSuraName(position + 1);
				case PagingMode.PAGE:
					return "Page " + (position + 1);
				case PagingMode.JUZ:
					return "Juz " + (position + 1);
				case PagingMode.HIZB:
					return "Hizb " + (position + 1);
				}
			}
			return "";
		}
	}

}
