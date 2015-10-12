package com.mx.cs.adapter;

import java.io.File;
import java.util.List;

import com.mx.cs.R;
import com.mx.cs.vo.CardInfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DataListAdapter extends BaseAdapter {

	private Context mcontext;
	private LayoutInflater layoutInflator;
	private List<CardInfo> list;
	private static String HEADER_PATH = "Android/data/com.mx.cs/headerImg";
	
	public DataListAdapter() {
	}

	public DataListAdapter(Context context, List<CardInfo> items) {
		mcontext = context;
		layoutInflator = LayoutInflater.from(mcontext);
		list = items;
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
					R.layout.adapter_mainlist, null);
			component= new Component();
			component.ivHeader = (ImageView) convertView
					.findViewById(R.id.ivHeader);
			component.tvNid = (TextView) convertView
					.findViewById(R.id.tvNid);
			component.tvName = (TextView) convertView
					.findViewById(R.id.tvName);
			component.tvAttr = (TextView) convertView
					.findViewById(R.id.tvAttr);
			component.tvHP = (TextView) convertView
					.findViewById(R.id.tvHP);
			component.tvAttack = (TextView) convertView
					.findViewById(R.id.tvAttack);
			component.tvDefense = (TextView) convertView
					.findViewById(R.id.tvDefense);
			convertView.setTag(component);
		}
		else
		{
			component = (Component) convertView.getTag();  
		}
		
		try {
			File imageDir = new File(Environment.getExternalStorageDirectory(), HEADER_PATH);
			File file = new File(imageDir.getPath(), list.get(arg0).getNid() + "_h.png");
			if(file.exists())
			{
				Bitmap bmp = MediaStore.Images.Media.getBitmap(mcontext.getContentResolver(), Uri.fromFile(file));
				component.ivHeader.setImageBitmap(bmp);
			}
			else
				component.ivHeader.setImageBitmap(null);
			component.tvNid.setText(String.valueOf(list.get(arg0).getNid()));
			component.tvName.setText(list.get(arg0).getName());
			String attr = list.get(arg0).getAttr();
			component.tvAttr.setText(attr.equals("A") ? "爱" : (attr.equals("Z") ? "憎" : "力"));
			component.tvHP.setText(String.valueOf(list.get(arg0).getMaxHP()));
			component.tvAttack.setText(String.valueOf(list.get(arg0).getMaxAttack()));
			component.tvDefense.setText(String.valueOf(list.get(arg0).getMaxDefense()));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return convertView;
	}
	
	private static class Component{
		 public ImageView ivHeader;  
		 public TextView tvNid;  
		 public TextView tvName;  
		 public TextView tvAttr; 
		 public TextView tvCost;  
		 public TextView tvHP;  
		 public TextView tvAttack; 
		 public TextView tvDefense;  
	}
}
