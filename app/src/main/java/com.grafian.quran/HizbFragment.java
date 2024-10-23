package com.grafian.quran;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;

import com.grafian.quran.model.MetaData.Mark;
import com.grafian.quran.model.MetaData.Sura;
import com.grafian.quran.model.Paging;

public class HizbFragment extends ListFragment {

	private App app;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		app = (App) getActivity().getApplication();

		setListAdapter(new HizbAdapter());
		getListView().setFastScrollEnabled(true);
	}

	@Override
	public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
		Intent intent = new Intent(getActivity(), ViewerActivity.class);
		Mark mark = app.metaData.getMarkStart(Paging.HIZB, position + 1);
		intent.putExtra(QuranFragment.PAGING, Paging.HIZB);
		intent.putExtra(QuranFragment.SURA, mark.sura);
		intent.putExtra(QuranFragment.AYA, mark.aya);
		startActivity(intent);
	}

	private static class HizbRowHolder {
		public TextView hizbNumber;
		public TextView partNumber;
		public TextView suraName;
		public TextView ayaNumber;
	}

	class HizbAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return app.metaData.getMarkCount(Paging.HIZB);
		}

		@Override
		public Object getItem(int position) {
			return app.metaData.getMarkStart(Paging.HIZB, position + 1);
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
				holder.hizbNumber = convertView.findViewById(R.id.hizb_number);
				holder.partNumber = convertView.findViewById(R.id.part_number);
				holder.suraName = convertView.findViewById(R.id.sura_name);
				holder.ayaNumber = convertView.findViewById(R.id.aya_number);
				convertView.setTag(holder);
			} else {
				holder = (HizbRowHolder) convertView.getTag();
			}

			Mark mark = (Mark) getItem(position);
			Sura sura = app.metaData.getSura(mark.sura);
			String[] parts = { "", "¼", "½", "¾" };
			int hizb = (position / 4) + 1;
			int part = position % 4;

			holder.hizbNumber.setText(Integer.toString(hizb));
			holder.partNumber.setText(parts[part]);
			holder.suraName.setText(sura.index + ". " + App.getSuraName(sura.index) + " :");
			holder.ayaNumber.setText(Integer.toString(mark.aya));

			return convertView;
		}
	}

}
