package com.mx.cs.activity;

import com.mx.cs.provider.Providerdata;
import com.mx.cs.util.DBHelper;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity {
	
	public DBHelper mDBHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mDBHelper = new DBHelper(this, Providerdata.DATABASE_NAME,
				null, Providerdata.DATABASE_VERSION);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mDBHelper.Close();
	}
	
}
