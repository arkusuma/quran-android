package com.grafian.quran;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.grafian.quran.MetaData.Sura;

public class SuraFragment extends SherlockListFragment {

	private App app;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		app = (App) getActivity().getApplication();

		setListAdapter(new SuraAdapter());
		getListView().setFastScrollEnabled(true);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(getActivity(), ViewerActivity.class);
		intent.putExtra(QuranFragment.MODE, Config.MODE_SURA);
		intent.putExtra(QuranFragment.SURA, position + 1);
		intent.putExtra(QuranFragment.AYA, 1);
		startActivity(intent);
	}

	private static class SuraRowHolder {
		public TextView suraNumber;
		public TextView suraName;
		public TextView suraNameArabic;
	}

	class SuraAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return app.loaded ? app.metaData.getSuraCount() : 0;
		}

		@Override
		public Object getItem(int position) {
			return app.metaData.getSura(position + 1);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SuraRowHolder holder;
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.main_sura_row, null);

				holder = new SuraRowHolder();
				holder.suraNumber = (TextView) convertView.findViewById(R.id.sura_number);
				holder.suraName = (TextView) convertView.findViewById(R.id.sura_name);
				holder.suraNameArabic = (TextView) convertView.findViewById(R.id.sura_name_arabic);
				convertView.setTag(holder);
			} else {
				holder = (SuraRowHolder) convertView.getTag();
			}

			Sura sura = (Sura) getItem(position);

			holder.suraNumber.setText("" + sura.index + ".");
			holder.suraName.setText(App.getSuraName(sura.index));
			holder.suraNameArabic.setText(sura.name);

			return convertView;
		}
	}

}
