package com.grafian.quran;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;

import com.grafian.quran.model.MetaData.Sura;
import com.grafian.quran.model.Paging;

public class SuraFragment extends ListFragment {

	private App mApp;
	private SuraAdapter mAdapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mApp = (App) getActivity().getApplication();

		mAdapter = new SuraAdapter();
		setListAdapter(mAdapter);
		getListView().setFastScrollEnabled(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
		Intent intent = new Intent(getActivity(), ViewerActivity.class);
		intent.putExtra(QuranFragment.PAGING, Paging.SURA);
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
			return mApp.metaData.getSuraCount();
		}

		@Override
		public Object getItem(int position) {
			return mApp.metaData.getSura(position + 1);
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
				holder.suraNumber = convertView.findViewById(R.id.sura_number);
				holder.suraName = convertView.findViewById(R.id.sura_name);
				holder.suraNameArabic = convertView.findViewById(R.id.sura_name_arabic);
				convertView.setTag(holder);
			} else {
				holder = (SuraRowHolder) convertView.getTag();
			}

			Sura sura = (Sura) getItem(position);

			String arabic = sura.name;
			holder.suraNumber.setText(sura.index + ".");
			holder.suraName.setText(App.getSuraName(sura.index));
			holder.suraNameArabic.setText(arabic);
			holder.suraNameArabic.setTextSize(TypedValue.COMPLEX_UNIT_SP, mApp.config.fontSizeArabic);

			return convertView;
		}
	}

}
