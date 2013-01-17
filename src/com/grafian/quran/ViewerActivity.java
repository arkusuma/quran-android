package com.grafian.quran;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.grafian.quran.MetaData.Mark;

public class ViewerActivity extends BaseFragmentActivity {

	private ViewPager mPager;
	private ViewerAdapter mAdapter;
	private int mMode;
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
			mMode = savedInstanceState.getInt(QuranFragment.MODE);
			mSura = savedInstanceState.getInt(QuranFragment.SURA);
			mAya = savedInstanceState.getInt(QuranFragment.AYA);
		} else {
			Intent intent = getIntent();
			mMode = intent.getIntExtra(QuranFragment.MODE, Config.MODE_SURA);
			mSura = intent.getIntExtra(QuranFragment.SURA, 1);
			mAya = intent.getIntExtra(QuranFragment.AYA, 1);
		}

		if (app.loaded) {
			loadPage();
		} else {
			app.loadAllData(this, new ProgressListener() {
				@Override
				public void onProgress() {
				}

				@Override
				public void onFinish() {
					mAdapter.notifyDataSetChanged();
					loadPage();
				}
			});
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mAdapter.getCount() > 0) {
			Mark mark = getMark(mPager.getCurrentItem());
			outState.putInt(QuranFragment.MODE, mMode);
			outState.putInt(QuranFragment.SURA, mark.sura);
			outState.putInt(QuranFragment.AYA, mark.aya);
		}
	}

	private void loadPage() {
		int item = 0;
		switch (mMode) {
		case Config.MODE_SURA:
			item = mSura - 1;
			break;
		case Config.MODE_PAGE:
			item = app.metaData.searchPage(mSura, mAya) - 1;
			break;
		case Config.MODE_JUZ:
			item = app.metaData.searchJuz(mSura, mAya) - 1;
			break;
		case Config.MODE_HIZB:
			item = app.metaData.searchHizb(mSura, mAya) - 1;
			break;
		}
		mPager.setCurrentItem(item);
	}

	private Mark getMark(int position) {
		Mark mark = null;
		switch (mMode) {
		case Config.MODE_SURA:
			mark = new Mark(position + 1, 1);
			break;
		case Config.MODE_PAGE:
			mark = app.metaData.getPage(position + 1);
			break;
		case Config.MODE_JUZ:
			mark = app.metaData.getJuz(position + 1);
			break;
		case Config.MODE_HIZB:
			mark = app.metaData.getHizb(position + 1);
			break;
		}
		return mark;
	}

	private class ViewerAdapter extends FragmentStatePagerAdapter {

		public ViewerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new QuranFragment();
			Bundle args = new Bundle();
			Mark m = getMark(position);
			args.putInt(QuranFragment.MODE, mMode);
			args.putInt(QuranFragment.SURA, m.sura);
			args.putInt(QuranFragment.AYA, m.aya);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			if (app.loaded) {
				switch (mMode) {
				case Config.MODE_SURA:
					return app.metaData.getSuraCount();
				case Config.MODE_PAGE:
					return app.metaData.getPageCount();
				case Config.MODE_JUZ:
					return app.metaData.getJuzCount();
				case Config.MODE_HIZB:
					return app.metaData.getHizbCount();
				}
			}
			return 0;
		}

		@Override
		public CharSequence getPageTitle(int position) {
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
