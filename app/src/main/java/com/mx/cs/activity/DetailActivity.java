package com.mx.cs.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.mx.cs.R;
import com.mx.cs.adapter.DespairSpinnerAdapter;
import com.mx.cs.common.MConfig;
import com.mx.cs.dialog.DialogSetMatrix;
import com.mx.cs.util.CommonUtil;
import com.mx.cs.vo.CardInfo;
import com.mx.cs.vo.DespairInfo;
import com.mx.cs.vo.MatrixInfo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

public class DetailActivity extends BaseActivity {
	
	private Button btnSave;
	private Button btnSave2;
	private Button btnDel;
	private Button btnSet;
	private Button btnDelOver;
	private EditText etHP;
	private EditText etAttack;
	private EditText etDefense;
	private EditText etName;
	private EditText etDetail;
	private CardInfo mCardInfo;
	private ImageView ivPoint;
	private Spinner spinnerAttr;
	private Spinner spinnerLevel;
	private Spinner spinnerOwned;
	private EditText etCost;
	private CheckBox chkModify;
	private int mNid;
	private SparseArray<File> mImagesFiles;
	private SparseArray<View> mImagesView;
	
	private Button mBtnSaveDespair;
	private Button mBtnAddDespair;
	private LinearLayout mLLAddDespair;
	private LinearLayout mLLImages;
	private ScrollView mScrollView;
	private List<DespairInfo> mDespairArray;
	private SparseArray<View> mDespairView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		Intent intent = getIntent();
		CardInfo info = intent.getParcelableExtra("card");
		mCardInfo = info;
		mNid = info.getNid();
		
		chkModify = (CheckBox) findViewById(R.id.chkModify);
		etHP = (EditText) findViewById(R.id.etDetailHP);
		etAttack = (EditText) findViewById(R.id.etDetailAttack);
		etDefense = (EditText) findViewById(R.id.etDetailDefense);
		etName = (EditText) findViewById(R.id.etDetailName);
		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave2 = (Button) findViewById(R.id.btnSave2);
		btnSave.setOnClickListener(btnSaveClickListener);
		btnSave2.setOnClickListener(btnSaveClickListener);
		btnDel = (Button) findViewById(R.id.btnDel);
		btnDel.setOnClickListener(btnDelClickListener);
		btnSet = (Button) findViewById(R.id.btnSet);
		btnSet.setOnClickListener(btnSetClickListener);
		btnDelOver = (Button) findViewById(R.id.btnDelOver);
		btnDelOver.setOnClickListener(btnDelOverClickListener);
		
		etDetail = (EditText) findViewById(R.id.etDetail);
		etDetail.setText(info.getRemark());
		
		etHP.setText(info.getMaxHP() == 0 ? "" : String.valueOf(info.getMaxHP()));
		etAttack.setText(info.getMaxAttack() == 0 ? "" : String.valueOf(info.getMaxAttack()));
		etDefense.setText(info.getMaxDefense() == 0 ? "" : String.valueOf(info.getMaxDefense()));
		etName.setText(info.getName());
		String attr = info.getAttr();
		
		spinnerAttr = (Spinner) findViewById(R.id.spinnerAttr);
		CommonUtil.setSpinnerItemSelectedByValue(spinnerAttr, (attr.equals("A") ? "爱" : (attr.equals("Z") ? "憎" : "力")));
		spinnerLevel = (Spinner) findViewById(R.id.spinnerLevel);
		CommonUtil.setSpinnerItemSelectedByValue(spinnerLevel, String.valueOf(info.getLevel()));
		spinnerOwned = (Spinner) findViewById(R.id.spinnerExist);
		CommonUtil.setSpinnerItemSelectedByValue(spinnerOwned, info.getCardExist() == null ? "N" : info.getCardExist() );
		
		etCost = (EditText) findViewById(R.id.etDetailCost);
		etCost.setText(String.valueOf(info.getCost()));
		
		mDespairArray = mDBHelper.queryDespair();
		mDespairArray.add(0, new DespairInfo());
		mDespairView = new SparseArray<View>();
		mScrollView = (ScrollView) findViewById(R.id.scrollview);
		mLLAddDespair = (LinearLayout) findViewById(R.id.llAddDespair);
		mLLImages = (LinearLayout) findViewById(R.id.llImages);
		mBtnSaveDespair = (Button) findViewById(R.id.btnSave3);
		mBtnSaveDespair.setOnClickListener(btnSaveDespairClickListener);
		mBtnAddDespair = (Button) findViewById(R.id.btnAdd);
		mBtnAddDespair.setOnClickListener(btnAddDespairClickListener);
		showDespair();
		
		ivPoint = (ImageView) findViewById(R.id.imgPoint);
		mImagesFiles = new SparseArray<File>();
		mImagesView = new SparseArray<View>();
		showImages();
	}
	
	private void showDespair()
	{
		List<Integer> list = mDBHelper.queryCardDespairs(mNid);
		for( int i = 0; i < list.size(); i++ )
			addDespair().setSelection(getDespairArrayIndex(list.get(i)));
	}
	
	private int getDespairArrayIndex(int id){
		for( int i = 0; i < mDespairArray.size(); i++ )
		{
			if(mDespairArray.get(i).getId() == id)
				return i;
		}
		return 0;
	}
	
	private void showImages()
	{
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			File fileDir = new File(Environment.getExternalStorageDirectory(),
					MConfig.SD_PATH);
			int index = 0;
			while(++index < 20){
				File imageFile = new File(fileDir.getPath(), mNid + "_" + index + ".png");
				Bitmap bitmap = null;
				if (imageFile.exists())
				{
					mImagesFiles.append(index, imageFile);
					try {
						bitmap = MediaStore.Images.Media.getBitmap(
								this.getContentResolver(), Uri.fromFile(imageFile));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					View child = LayoutInflater.from(DetailActivity.this).inflate(
							R.layout.child_images, null);
					mLLImages.addView(child);
					mImagesView.append(index, child);
					
					ImageView image = (ImageView) child.findViewById(R.id.imgDetails);
					image.setImageBitmap(bitmap);
					Button btnDel = (Button) child.findViewById(R.id.btnDel);
					btnDel.setTag(index + "*" + 0); 
					btnDel.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							String[] tag = v.getTag().toString().split("\\*");
							int key = Integer.parseInt(tag[0]);
							View line = mImagesView.get(key);
							long timenow = Calendar.getInstance().getTime().getTime();
							if(Math.abs(timenow - Long.valueOf(tag[1])) > 5000)
							{
								Toast.makeText(DetailActivity.this, "请再次点击删除", Toast.LENGTH_SHORT).show();
								v.setTag(key + "*" + timenow);
							}else{
								v.setTag(key + "*" + 0);
								CommonUtil.deleteImage(DetailActivity.this, mImagesFiles.get(key)); 
								mLLImages.removeView(line);
								mImagesView.remove(key);
								mImagesFiles.remove(key);
								Toast.makeText(DetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
							}
								
						}
					});
					
					if(index == 1)
						showImageNum(bitmap);
					
				}
			}
		}
	}
	
	
	View.OnClickListener btnSaveClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			CardInfo card;
			long result = 0;
			//名称优先批量更新
			if(!chkModify.isChecked() &&  !mCardInfo.getName().equals("") &&  !mCardInfo.getName().equals(etName.getText().toString().trim()))
			{
				CardInfo cardOld = mCardInfo;
				card = new CardInfo();
				card.setName(etName.getText().toString().trim());
				result = mDBHelper.updateCardName(card, cardOld);
			}
			
			card = new CardInfo();
			card.setNid(mCardInfo.getNid());
			String attr = spinnerAttr.getSelectedItem().toString();
			card.setAttr(attr.equals("爱") ? "A"
					: (attr.equals("憎") ? "Z" : "L"));
			card.setLevel(Integer.parseInt(spinnerLevel
					.getSelectedItem().toString()));
			card.setCost(Integer.parseInt(etCost.getText()
					.toString()));
			card.setCardExist(spinnerOwned.getSelectedItem().toString());
			card.setName(etName.getText().toString().trim());
			card.setMaxHP(etHP.getText().toString().trim().equals("") ? 0 : Integer.parseInt(etHP.getText().toString()));
			card.setMaxAttack(etAttack.getText().toString().trim().equals("") ? 0 : Integer.parseInt(etAttack.getText().toString()));
			card.setMaxDefense(etDefense.getText().toString().trim().equals("") ? 0 : Integer.parseInt(etDefense.getText().toString()));
			card.setRemark(etDetail.getText().toString().trim());
			result += mDBHelper.updateCardInfo(card);
			
			if (result > 0) {
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
	
	
	View.OnClickListener btnDelOverClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			for( int i = 0; i < mImagesView.size(); i++ )
			{
				Button del = (Button) mImagesView.get(mImagesView.keyAt(i)).findViewById(R.id.btnDel);
				del.setVisibility(View.VISIBLE);
			}
			
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
										
										for(int i = 0; i < mImagesFiles.size(); i++)
										{
											CommonUtil.deleteImage(DetailActivity.this,
													mImagesFiles.get(mImagesFiles.keyAt(i)));
										}
										
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
	
	
	View.OnClickListener btnAddDespairClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			addDespair();
			mScrollView.post(new Runnable() {
			    @Override
			    public void run() {
			    	mScrollView.smoothScrollTo(0, 5000);
			    }
			});
			
		}
	};
	
	View.OnClickListener btnSaveDespairClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int[] despair = new int[ mDespairView.size() ];
			for( int i = 0; i < mDespairView.size(); i++ )
			{
				Spinner spinner = (Spinner) mDespairView.get(mDespairView.keyAt(i)).findViewById(R.id.spinnerDespair);
				DespairInfo info = (DespairInfo) spinner.getSelectedItem();
				despair[i] = info.getId();
			}
			mDBHelper.setCardDespair(mNid, despair);
			Toast.makeText(DetailActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
		}
	};
	
	private Spinner addDespair(){
		View child = LayoutInflater.from(DetailActivity.this).inflate(
				R.layout.child_despair, null);
		mLLAddDespair.addView(child);
		Spinner spinner = (Spinner) child.findViewById(R.id.spinnerDespair);
		DespairSpinnerAdapter adapter = 
				new DespairSpinnerAdapter( DetailActivity.this, mDespairArray);
		spinner.setAdapter(adapter);
		Button delBtn = (Button) child.findViewById(R.id.btnDel);
		
		int roundtag = (int) Math.round(Math.random() * 100000000);
		delBtn.setTag( roundtag + "*" + 0); 
		
		delBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String[] tag = v.getTag().toString().split("\\*");
				View line = mDespairView.get(Integer.parseInt(tag[0]));
				Spinner select = (Spinner) line.findViewById(R.id.spinnerDespair);
				DespairInfo info = (DespairInfo) select.getSelectedItem();			
				int id = info.getId();
				if( id == -1 ) {
					mLLAddDespair.removeView(line);
					mDespairView.remove(Integer.parseInt(tag[0]));
				}else{
					long timenow = Calendar.getInstance().getTime().getTime();
					if(Math.abs(timenow - Long.valueOf(tag[1])) > 5000)
					{
						Toast.makeText(DetailActivity.this, "请再次点击删除", Toast.LENGTH_SHORT).show();
						v.setTag(tag[0] + "*" + timenow);
					}else{
						v.setTag(tag[0] + "*" + 0);
						long r = mDBHelper.delCardDespair(mNid, id);
						if( r > -1)
						{
							mLLAddDespair.removeView(line);
							mDespairView.remove(Integer.parseInt(tag[0]));
							Toast.makeText(DetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
						}
					}
				}
					
			}
		});
		
		mDespairView.append(roundtag, child);
		
		return spinner;
	}
	
	private void showImageNum(Bitmap bitOrig)
	{
		//MatrixInfo matrixInfo = CommonUtil.getMatrixInfo(this, 3);
		MatrixInfo matrixInfo = new MatrixInfo();
		matrixInfo.setX(0);
		matrixInfo.setY((bitOrig.getHeight() * 577) / 1280);
		matrixInfo.setWidth(bitOrig.getWidth());
		matrixInfo.setHeight(30); 
		final Bitmap bits = CommonUtil.cutBitmap(bitOrig, matrixInfo, false);
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
        baseApi.init(Environment.getExternalStorageDirectory() + MConfig.TESSBASE_PATH, MConfig.DEFAULT_LANGUAGE);
        baseApi.setImage(bitmap);
        String recognizedText = baseApi.getUTF8Text().trim();
        baseApi.end(); 
        
        if(recognizedText != null)
        {
        	 StringTokenizer st = new StringTokenizer(recognizedText);
        	 if (st.hasMoreTokens())
 				st.nextToken();
 			if (st.hasMoreTokens())
 				etHP.setText(st.nextToken());
 			if (st.hasMoreTokens())
 				st.nextToken();
 			if (st.hasMoreTokens())
 				st.nextToken();
 			if (st.hasMoreTokens())
 				etAttack.setText(st.nextToken());
 			if (st.hasMoreTokens())
 				etDefense.setText(st.nextToken());
        }
	 }
	 
	Handler detailHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == 100)
			{
				showImages();
			}
		}
		
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mDBHelper != null)
			mDBHelper.Close();
	}
}
