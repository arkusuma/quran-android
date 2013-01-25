package com.grafian.quran;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.grafian.quran.parser.MetaData.Mark;
import com.grafian.quran.parser.MetaData.Sajda;
import com.grafian.quran.parser.MetaData.Sura;

public class PageFragment extends SherlockListFragment {

	private App app;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		app = (App) getActivity().getApplication();

		setListAdapter(new PageAdapter());
		getListView().setFastScrollEnabled(true);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(getActivity(), ViewerActivity.class);
		Mark mark = app.metaData.getPage(position + 1);
		intent.putExtra(QuranFragment.PAGING_MODE, PagingMode.PAGE);
		intent.putExtra(QuranFragment.SURA, mark.sura);
		intent.putExtra(QuranFragment.AYA, mark.aya);
		startActivity(intent);
	}

	private static class PageRowHolder {
		public TextView pageNumber;
		public TextView suraName;
		public TextView ayaNumber;
	}

	class PageAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return app.metaData.getPageCount();
		}

		@Override
		public Object getItem(int position) {
			return app.metaData.getPage(position + 1);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			PageRowHolder holder;
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.main_page_row, null);

				holder = new PageRowHolder();
				holder.pageNumber = (TextView) convertView.findViewById(R.id.page_number);
				holder.suraName = (TextView) convertView.findViewById(R.id.sura_name);
				holder.ayaNumber = (TextView) convertView.findViewById(R.id.aya_number);
				convertView.setTag(holder);
			} else {
				holder = (PageRowHolder) convertView.getTag();
			}

			Sajda mark = (Sajda) getItem(position);
			Sura sura = app.metaData.getSura(mark.sura);

			holder.pageNumber.setText("" + (position + 1));
			holder.suraName.setText("" + sura.index + ". " + App.getSuraName(sura.index) + " :");
			holder.ayaNumber.setText("" + mark.aya);

			return convertView;
		}
	}

}
