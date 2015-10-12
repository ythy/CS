package com.mx.cs.activity;

import java.io.File;
import java.util.StringTokenizer;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.mx.cs.R;
import com.mx.cs.dialog.DialogSetMatrix;
import com.mx.cs.provider.Providerdata;
import com.mx.cs.util.CommonUtil;
import com.mx.cs.util.DBHelper;
import com.mx.cs.vo.CardInfo;
import com.mx.cs.vo.MatrixInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends Activity {

	private static String SD_PATH = "Android/data/com.mx.cs/images";
	private static final String DEFAULT_LANGUAGE = "eng";
	private static final String TESSBASE_PATH = "/mnt/sdcard/tesseract/";
	 
	private ImageView ivNumber;
	private ImageView ivAll;
	private ImageView ivHP;
	private ImageView ivAttack;
	private ImageView ivDefense;
	private Button btnSave;
	private Button btnDel;
	private Button btnSet;
	private DBHelper mDBHelper;
	private EditText etHP;
	private EditText etAttack;
	private EditText etDefense;
	private EditText etName;
	private CardInfo mCardInfo;
	private Bitmap bitMap1; // 图片1 bitmap
	private ImageView ivPoint;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		Intent intent = getIntent();
		CardInfo info = intent.getParcelableExtra("card");
		mCardInfo = info;
		mDBHelper = new DBHelper(DetailActivity.this,
				Providerdata.DATABASE_NAME, null, Providerdata.DATABASE_VERSION);

		etHP = (EditText) findViewById(R.id.etDetailHP);
		etAttack = (EditText) findViewById(R.id.etDetailAttack);
		etDefense = (EditText) findViewById(R.id.etDetailDefense);
		etName = (EditText) findViewById(R.id.etDetailName);
		TextView tvAttr = (TextView) findViewById(R.id.tvDetailAttr);
		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(btnSaveClickListener);
		btnDel = (Button) findViewById(R.id.btnDel);
		btnDel.setOnClickListener(btnDelClickListener);
		btnSet = (Button) findViewById(R.id.btnSet);
		btnSet.setOnClickListener(btnSetClickListener);
		
		etHP.setText(info.getMaxHP() == 0 ? "" : String.valueOf(info.getMaxHP()));
		etAttack.setText(info.getMaxAttack() == 0 ? "" : String.valueOf(info.getMaxAttack()));
		etDefense.setText(info.getMaxDefense() == 0 ? "" : String.valueOf(info.getMaxDefense()));
		etName.setText(info.getName());
		String attr = info.getAttr();
		tvAttr.setText((attr.equals("A") ? "爱" : (attr.equals("Z") ? "憎" : "力")) + " / " + String.valueOf(info.getCost()));

		ivNumber = (ImageView) findViewById(R.id.imgWithNumber);
		ivAll = (ImageView) findViewById(R.id.imgAll);
		ivHP = (ImageView) findViewById(R.id.imgHp);
		ivAttack = (ImageView) findViewById(R.id.imgAttack);
		ivDefense = (ImageView) findViewById(R.id.imgDefense);
		ivPoint = (ImageView) findViewById(R.id.imgPoint);
		
		showImage(1, info.getNid() + "_1.png");
		showImage(2, info.getNid() + "_2.png");
	}

	View.OnClickListener btnSaveClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			CardInfo card = new CardInfo();
			card.setNid(mCardInfo.getNid());
			card.setName(etName.getText().toString().trim());
			card.setMaxHP(etHP.getText().toString().trim().equals("") ? 0 : Integer.parseInt(etHP.getText().toString()));
			card.setMaxAttack(etAttack.getText().toString().trim().equals("") ? 0 : Integer.parseInt(etAttack.getText().toString()));
			card.setMaxDefense(etDefense.getText().toString().trim().equals("") ? 0 : Integer.parseInt(etDefense.getText().toString()));
			int result = mDBHelper.updateCardInfo(card);
			if (result != -1) {
				Intent intent = new Intent(DetailActivity.this,
						MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				DetailActivity.this.finish();
			} else
				Toast.makeText(DetailActivity.this, "保存失败", Toast.LENGTH_SHORT)
						.show();

		}
	};

	View.OnClickListener btnDelClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			new AlertDialog.Builder(DetailActivity.this)
					.setMessage("确定要删除吗")
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									CardInfo card = new CardInfo();
									card.setNid(mCardInfo.getNid());
									long result = mDBHelper.delCardInfo(card);
									if (result != -1) {
										Intent intent = new Intent(
												DetailActivity.this,
												MainActivity.class);
										intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(intent);
										DetailActivity.this.finish();
									} else
										Toast.makeText(DetailActivity.this,
												"删除失败", Toast.LENGTH_SHORT)
												.show();
								}
							}).setNegativeButton("Cancel", null).show();
		}
	};
	
	
	View.OnClickListener btnSetClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			DialogSetMatrix.show(DetailActivity.this, detailHandler);
		}
	};
	

	private void showImage(int index, String path) {
		Bitmap bit = null;
		MatrixInfo matrixInfo = CommonUtil.getMatrixInfo(this, index);
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			File fileDir = new File(Environment.getExternalStorageDirectory(),
					SD_PATH);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			File imageFile = new File(fileDir.getPath(), path);
			if (imageFile.exists())
			{
				Bitmap compress = CommonUtil.compressImageFromFile(imageFile.getPath());
				if(index == 1)
					bitMap1 = compress;
				bit = CommonUtil.cutBitmap(compress, matrixInfo, true);
			}
		}
		if (bit != null)
		{
			if(index == 1)
			{
				ivNumber.setImageBitmap(bit);
				showImageNum();
			}
			else if(index == 2)
				ivAll.setImageBitmap(bit);
		}
			
	}
	
	private void showImageNum()
	{
		MatrixInfo matrixInfo = CommonUtil.getMatrixInfo(this, 3);
		final Bitmap bits = CommonUtil.cutBitmap(bitMap1, matrixInfo, false);
		//final Bitmap bit = CommonUtil.lineGrey(bits);
		final Bitmap bit = CommonUtil.gray2Binary(bits);
		 
		ivPoint.setImageBitmap(bit);
		if(etHP.getText().toString().trim().equals("") || etAttack.getText().toString().trim().equals("") ||
				etDefense.getText().toString().trim().equals("")	)
		{
			detailHandler.post(new Runnable() {
	            @Override
	            public void run() {
	                ocr(bit);
	            }
	        });
		}
		
	}
	
	 protected void ocr(Bitmap bitmap) { 
	 
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);   
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(TESSBASE_PATH, DEFAULT_LANGUAGE);
        baseApi.setImage(bitmap);
        String recognizedText = baseApi.getUTF8Text().trim();
        baseApi.end(); 
        
        if(recognizedText != null)
        {
        	 StringTokenizer st = new StringTokenizer(recognizedText);
        	 if(st.hasMoreTokens())
        		 etHP.setText(st.nextToken());
        	 if(st.hasMoreTokens())
        		 st.nextToken();
        	 if(st.hasMoreTokens())
        		 etAttack.setText(st.nextToken());
        	 if(st.hasMoreTokens())
        		 etDefense.setText(st.nextToken());
        }
	 }
	 
	Handler detailHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == 1)
			{
				showImage(1, mCardInfo.getNid() + "_1.png");
				showImage(2, mCardInfo.getNid() + "_2.png");
			}
		}
		
	};
}
