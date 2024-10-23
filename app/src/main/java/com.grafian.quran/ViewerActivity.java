package com.grafian.quran;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.grafian.quran.model.MetaData.Mark;
import com.grafian.quran.model.Paging;

public class ViewerActivity extends BaseActivity {

	final private static int SCREEN_TIMEOUT = 600;

	final private Handler mHandler = new Handler();

	private ViewPager mPager;
	private ViewerAdapter mAdapter;
	private int mPaging;
	private int mSura;
	private int mAya;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pager);

		mAdapter = new ViewerAdapter(getSupportFragmentManager());
		mPager = findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		if (savedInstanceState != null) {
			mPaging = savedInstanceState.getInt(QuranFragment.PAGING);
			mSura = savedInstanceState.getInt(QuranFragment.SURA);
			mAya = savedInstanceState.getInt(QuranFragment.AYA);
		} else {
			Intent intent = getIntent();
			mPaging = intent.getIntExtra(QuranFragment.PAGING, Paging.SURA);
			mSura = intent.getIntExtra(QuranFragment.SURA, 1);
			mAya = intent.getIntExtra(QuranFragment.AYA, 1);
		}

		showPage(mPaging, mSura, mAya);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (App.app.config.keepScreenOn) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}

		// Check if reading direction is still valid
		QuranFragment fragment = getCurrentFragment();
		if (fragment != null && fragment.getUserVisibleHint()) {
			Mark m = fragment.getCurrentPosition();
			showPage(mPaging, m.sura, m.aya);
		}
	}

	private final Runnable clearScreenOn = () -> getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	@Override
	public void onUserInteraction() {
		super.onUserInteraction();
		if (App.app.config.keepScreenOn) {
			mHandler.removeCallbacks(clearScreenOn);
			mHandler.postDelayed(clearScreenOn, SCREEN_TIMEOUT * 1000);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		QuranFragment fragment = getCurrentFragment();
		if (fragment != null) {
			Mark m = fragment.getCurrentPosition();
			outState.putInt(QuranFragment.PAGING, mPaging);
			outState.putInt(QuranFragment.SURA, m.sura);
			outState.putInt(QuranFragment.AYA, m.aya);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	public void showPage(int pagingMode, int sura, int aya) {
		mPaging = pagingMode;
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
		switch (mPaging) {
		case Paging.SURA:
			item = sura - 1;
			break;
		case Paging.PAGE:
			item = App.app.metaData.find(Paging.PAGE, sura, aya) - 1;
			break;
		case Paging.JUZ:
			item = App.app.metaData.find(Paging.JUZ, sura, aya) - 1;
			break;
		case Paging.HIZB:
			item = App.app.metaData.find(Paging.HIZB, sura, aya) - 1;
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
			switch (mPaging) {
			case Paging.SURA:
				mark = new Mark(page + 1, 1);
				break;
			case Paging.PAGE:
				mark = App.app.metaData.getMarkStart(Paging.PAGE, page + 1);
				break;
			case Paging.JUZ:
				mark = App.app.metaData.getMarkStart(Paging.JUZ, page + 1);
				break;
			case Paging.HIZB:
				mark = App.app.metaData.getMarkStart(Paging.HIZB, page + 1);
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
			args.putInt(QuranFragment.PAGING, mPaging);
			args.putInt(QuranFragment.SURA, m.sura);
			args.putInt(QuranFragment.AYA, m.aya);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			switch (mPaging) {
			case Paging.SURA:
				return App.app.metaData.getSuraCount();
			case Paging.PAGE:
				return App.app.metaData.getMarkCount(Paging.PAGE);
			case Paging.JUZ:
				return App.app.metaData.getMarkCount(Paging.JUZ);
			default: //case Paging.HIZB:
				return App.app.metaData.getMarkCount(Paging.HIZB);
			}
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if (position < getCount()) {
				position = transformPosition(position);
				switch (mPaging) {
				case Paging.SURA:
					return (position + 1) + ". " + App.getSuraName(position + 1);
				case Paging.PAGE:
					return "Page " + (position + 1);
				case Paging.JUZ:
					return "Juz " + (position + 1);
				case Paging.HIZB:
					String[] parts = { "", "¼", "½", "¾" };
					int hizb = (position / 4) + 1;
					int part = position % 4;
					return "Hizb " + hizb + parts[part];
				}
			}
			return "";
		}
	}

}
