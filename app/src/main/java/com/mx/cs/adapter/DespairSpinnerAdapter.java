package com.mx.cs.adapter;

import java.util.List;

import com.mx.cs.R;
import com.mx.cs.vo.DespairInfo;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class DespairSpinnerAdapter extends BaseAdapter implements
		SpinnerAdapter {

	private Context mContext;
	private LayoutInflater layoutInflator;
	private List<DespairInfo> list;

	public DespairSpinnerAdapter(Context context, List<DespairInfo> items) {
		mContext = context;
		layoutInflator = LayoutInflater.from(mContext);
		list = items;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		TextView text = new TextView(mContext);
		text.setTextColor(Color.BLACK);
		text.setSingleLine(true);
		text.setText(list.get(position).getName());
		return text;

	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {

		convertView = layoutInflator.inflate(R.layout.adapter_spinner_normal,
				null);
		TextView tvdropdowview = (TextView) convertView
				.findViewById(R.id.tvSpinner);
		tvdropdowview.setText(list.get(position).getName());
		return convertView;

	}

}
