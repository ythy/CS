package com.mx.cs.activity;

import java.io.File;
import com.mx.cs.R;
import com.mx.cs.common.MConfig;
import com.mx.cs.service.FxService;
import com.mx.cs.util.CommonUtil;
import com.mx.cs.vo.CardInfo;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SetActivity extends BaseActivity {
	
	private TextView mTimer;
	private Button mBtnHome;
	private LinearLayout mllDesktop;
	private LinearLayout mllParent;
	
	private int mPx;
	private int mPy;
	private static final int DURATION = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_set);
		
		mPx = getIntent().getIntExtra("position_x", 0);
		mPy = getIntent().getIntExtra("position_y", 0);
		
		mBtnHome = (Button) this.findViewById(R.id.btnHome);
		mTimer = (TextView) this.findViewById(R.id.tvTime);
		//mTimer.setText(String.valueOf(DURATION));
		mllDesktop = (LinearLayout) this.findViewById(R.id.ll_main);
		mllParent = (LinearLayout) this.findViewById(R.id.ll_parent);

		//initTimer(DURATION);

		mllParent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBack();
			}
		});
		
		mllDesktop.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		
		mBtnHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
				mHomeIntent.addCategory(Intent.CATEGORY_HOME);
				mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				startActivity(mHomeIntent);
				onBack();
			}
		});

	}

	private void initTimer(final int duration){
		new Thread(new Runnable() {
			@Override
			public void run() {
				int i = duration;
				try {
					while(i++ < 300){
						Thread.sleep(1000);
						setHandler.sendEmptyMessage(i);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void onBack() {
		Intent intent = new Intent(SetActivity.this, FxService.class);
		intent.putExtra("position_x", mPx);
		intent.putExtra("position_y", mPy);
		startService(intent);
		this.finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mDBHelper != null)
			mDBHelper.Close();
	}
	
	private Handler setHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String num = (msg.what < 10 ? "0" : "" ) + msg.what;
			mTimer.setText(num);
		}
	};
}
