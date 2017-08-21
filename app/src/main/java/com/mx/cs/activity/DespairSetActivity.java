package com.mx.cs.activity;

import java.util.ArrayList;
import java.util.List;

import com.mx.cs.R;
import com.mx.cs.adapter.DespairListAdapter;
import com.mx.cs.adapter.DespairListAdapter.DespairTouchListener;
import com.mx.cs.listener.ListenerListViewScrollHandler;
import com.mx.cs.vo.DespairInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class DespairSetActivity extends BaseActivity {
	
	private Button mBtnAdd;
	private ListView mLvDespairMain;
	private DespairListAdapter mAdapter;
	private List<DespairInfo> mList;
	private RelativeLayout pageVboxLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_despair);
		
		mBtnAdd = (Button) findViewById(R.id.btnDespairAdd);
		mBtnAdd.setOnClickListener(onAddBtnClickListerner);
		
		mLvDespairMain = (ListView) findViewById(R.id.lvDespairMain);
		pageVboxLayout = (RelativeLayout) findViewById(R.id.pageVBox);
		pageVboxLayout.setVisibility(View.GONE);
		
		mLvDespairMain.setOnScrollListener(new ListenerListViewScrollHandler(mLvDespairMain, pageVboxLayout));
		mList = new ArrayList<DespairInfo>();
		mAdapter = new DespairListAdapter(this, mList);
		mAdapter.setDespairTouchListener(despairTouchListener);
		
		searchMain();
		
	}
	
	DespairTouchListener despairTouchListener = new DespairTouchListener(){

		@Override
		public void onSaveBtnClickListener(DespairInfo info) {
			long result = mDBHelper.updateDespairName(info);
			if( result > -1) {
				Toast.makeText(DespairSetActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
				searchMain();
			}
				
		}
		
	};
	
	View.OnClickListener onAddBtnClickListerner = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mList.add(0, new DespairInfo());
			updateList(false);
		}
	};
	
	Handler mainHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				mList.clear();
				mList.addAll((List<DespairInfo>) msg.obj);
				updateList(true);
			}
		}
		
	};
	
	private void searchMain(){
		mainHandler.post( new Runnable() {
			@Override
			public void run() {
				List<DespairInfo> list = mDBHelper.queryDespair();
				Message msg = mainHandler.obtainMessage();
				msg.what = 1;
				msg.obj = list;
				mainHandler.sendMessage(msg);
			}
		});
	}
	
	private void updateList(boolean flag) {
		if (flag)
			mLvDespairMain.setAdapter(mAdapter);
		else
			mAdapter.notifyDataSetChanged();
	}
	
}

