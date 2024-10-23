package com.grafian.quran;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.grafian.quran.model.Paging;

public class MainActivity extends BaseActivity {

	final private static String PAGE = "page";

	private ViewPager mPager;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pager);

		mPager = findViewById(R.id.pager);
		mPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

		int page = Paging.SURA;
		if (savedInstanceState != null) {
			page = savedInstanceState.getInt(PAGE);
		}
		mPager.setCurrentItem(page);
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(PAGE, mPager.getCurrentItem());
	}

	@Override
	public void onBackPressed() {
        new AlertDialog.Builder(this)
				.setMessage(R.string.confirm_quit)
				.setPositiveButton(R.string.quit, (dialog, which) -> finish())
				.setNegativeButton(R.string.cancel, null)
				.show();
	}

	private static class PagerAdapter extends FragmentPagerAdapter {

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
			return 5;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			String[] titles = { "Bookmark", "Sura", "Page", "Juz", "Hizb" };
			return titles[position];
		}

	}

}
