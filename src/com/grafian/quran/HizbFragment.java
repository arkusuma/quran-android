package com.grafian.quran;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.grafian.quran.MetaData.Mark;
import com.grafian.quran.MetaData.Sura;

public class HizbFragment extends SherlockListFragment {

	private App app;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		app = (App) getActivity().getApplication();

		setListAdapter(new HizbAdapter());
		getListView().setFastScrollEnabled(true);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(getActivity(), ViewerActivity.class);
		Mark mark = app.metaData.getHizb(position + 1);
		intent.putExtra(QuranFragment.MODE, Config.MODE_HIZB);
		intent.putExtra(QuranFragment.SURA, mark.sura);
		intent.putExtra(QuranFragment.AYA, mark.aya);
		startActivity(intent);
	}

	private static class HizbRowHolder {
		public TextView hizbNumber;
		public TextView juzNumber;
		public TextView suraName;
		public TextView ayaNumber;
	}

	class HizbAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return app.loaded ? app.metaData.getHizbCount() : 0;
		}

		@Override
		public Object getItem(int position) {
			return app.metaData.getHizb(position + 1);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HizbRowHolder holder;
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.main_hizb_row, null);

				holder = new HizbRowHolder();
				holder.hizbNumber = (TextView) convertView.findViewById(R.id.hizb_number);
				holder.juzNumber = (TextView) convertView.findViewById(R.id.juz_number);
				holder.suraName = (TextView) convertView.findViewById(R.id.sura_name);
				holder.ayaNumber = (TextView) convertView.findViewById(R.id.aya_number);
				convertView.setTag(holder);
			} else {
				holder = (HizbRowHolder) convertView.getTag();
			}

			Mark mark = (Mark) getItem(position);
			Sura sura = app.metaData.getSura(mark.sura);
			String s[] = { "", "⅛", "¼", "⅜", "½", "⅝", "¾", "⅞" };
			int juz = (position / 8) + 1;
			int part = position % 8;

			holder.hizbNumber.setText("" + (position + 1));
			holder.suraName.setText("" + sura.index + ". " + App.getSuraName(sura.index) + " :");
			holder.ayaNumber.setText("" + mark.aya);
			holder.juzNumber.setText("Juz " + juz + s[part]);

			return convertView;
		}
	}

}
