package com.mx.cs.adapter;

import java.util.List;
import com.mx.cs.R;
import com.mx.cs.vo.DespairInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class DespairListAdapter extends BaseAdapter {

	private Context mcontext;
	private LayoutInflater layoutInflator;
	private List<DespairInfo> list;
	private DespairTouchListener mListener;
	
	public DespairListAdapter() {
	}

	public DespairListAdapter(Context context, List<DespairInfo> items) {
		mcontext = context;
		layoutInflator = LayoutInflater.from(mcontext);
		list = items;
	}
	
	public void setDespairTouchListener( DespairTouchListener listener) {
		mListener = listener;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		Component component = null;
		
		if (convertView == null) {
			convertView = layoutInflator.inflate(
					R.layout.adapter_despair, null);
			component= new Component();
			component.tvName = (TextView) convertView
					.findViewById(R.id.etDespairName);
			component.btnSave = (Button) convertView
					.findViewById(R.id.btnDespairModify);
			convertView.setTag(component);
		}
		else
			component = (Component) convertView.getTag();  
		
		final Component currentComponent = component;
		final int position = arg0;
		try {
			
			component.tvName.setText(list.get(arg0).getName());
			component.btnSave.setOnClickListener( new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					final String name = currentComponent.tvName.getText().toString();
					final int id = list.get(position).getId();
					DespairInfo despairInfo = new DespairInfo();
					despairInfo.setId(id);
					despairInfo.setName(name);
					mListener.onSaveBtnClickListener(despairInfo);
				}
			});
			
			component.tvName.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
		                 //   index = position;
	                }
	                return false;
				}
	        });
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return convertView;
	}
	
	public interface DespairTouchListener {
		public void onSaveBtnClickListener( DespairInfo info );
	}
	
	private static class Component{
		 public TextView tvName;  
		 public Button btnSave;  
	}
}
