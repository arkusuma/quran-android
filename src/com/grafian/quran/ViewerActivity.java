package com.grafian.quran;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.grafian.quran.MetaData.Mark;

public class ViewerActivity extends BaseActivity {

	private ViewPager mPager;
	private ViewerAdapter mAdapter;
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
			mPager.setCurrentItem(findPage(mSura, mAya));
		} else {
			mApp.loadAllData(this, new ProgressListener() {
				@Override
				public void onProgress() {
				}

				@Override
				public void onFinish() {
					mAdapter.notifyDataSetChanged();
					mPager.setCurrentItem(findPage(mSura, mAya));
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

	private int findPage(int sura, int aya) {
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
		return item;
	}

	private Mark getMark(int position) {
		Mark mark = null;
		switch (mMode) {
		case Config.MODE_SURA:
			mark = new Mark(position + 1, 1);
			break;
		case Config.MODE_PAGE:
			mark = new Mark(mApp.metaData.getPage(position + 1));
			break;
		case Config.MODE_JUZ:
			mark = new Mark(mApp.metaData.getJuz(position + 1));
			break;
		case Config.MODE_HIZB:
			mark = new Mark(mApp.metaData.getHizb(position + 1));
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
			if (findPage(m.sura, m.aya) == findPage(mSura, mAya)) {
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
