package com.mx.cs.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringTokenizer;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.mx.cs.R;
import com.mx.cs.common.MConfig;
import com.mx.cs.dialog.DialogSetMatrix;
import com.mx.cs.util.CommonUtil;
import com.mx.cs.vo.CardInfo;
import com.mx.cs.vo.MatrixInfo;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class AddCardActivity extends BaseActivity {

	private Button btnSave;
	private Button btnSet;
	private Button btnDel;
	private Spinner spinnerAttr;
	private Spinner spinnerLevel;
	private Spinner spinnerType;
	private EditText etNid;
	private EditText etName;
	private EditText etCost;
	private EditText etHP;
	private EditText etAttack;
	private EditText etDefense;
	private ImageView ivNumber;
	private ImageView ivAll;
	private ImageView ivPoint;
	private ImageView ivNid;
	private ProgressDialog m_pDialog;
	private File m_fileNumber;
	private File m_fileAll;
	private Bitmap m_BitMapNumber;
	private Bitmap m_BitMapAll;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(btnSaveClickListener);
		btnSet = (Button) findViewById(R.id.btnSet);
		btnSet.setOnClickListener(btnSetClickListener);
		btnDel = (Button) findViewById(R.id.btnDel);
		btnDel.setOnClickListener(btnDelClickListener);
		
		spinnerAttr = (Spinner) findViewById(R.id.spinnerAttr);
		spinnerLevel = (Spinner) findViewById(R.id.spinnerLevel);
		spinnerLevel.setOnItemSelectedListener(onLevelSelectlistener);
		etNid = (EditText) findViewById(R.id.etDetailNid);
		etName = (EditText) findViewById(R.id.etDetailName);
		etCost = (EditText) findViewById(R.id.etDetailCost);
		etHP = (EditText) findViewById(R.id.etDetailHP);
		etAttack = (EditText) findViewById(R.id.etDetailAttack);
		etDefense = (EditText) findViewById(R.id.etDetailDefense);
		spinnerType  = (Spinner) findViewById(R.id.spinnerType);
		spinnerType.setSelection(0);
		spinnerType.setOnItemSelectedListener(onTypeSelectlistener);
		
		ivNumber = (ImageView) findViewById(R.id.imgWithNumber);
		ivAll = (ImageView) findViewById(R.id.imgAll);
		ivPoint = (ImageView) findViewById(R.id.imgPoint);
		ivNid = (ImageView) findViewById(R.id.imgNid);
		
		m_pDialog = new ProgressDialog(this);
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		m_pDialog.setMessage("请稍等。。。");
		// 设置ProgressDialog 的进度条是否不明确
		m_pDialog.setIndeterminate(false);
		// 设置ProgressDialog 是否可以按退回按键取消
		m_pDialog.setCancelable(false);

		try {
			showPicture( 2 );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void showPicture( int max ) throws FileNotFoundException, IOException {
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			File fileDir = new File(Environment.getExternalStorageDirectory(),
					MConfig.SRC_PATH);
			if (!fileDir.exists())
				return;

			File file = new File(fileDir.getPath());
			File[] fs = file.listFiles();
			Arrays.sort(fs, new Comparator<File>() {
				public int compare(File f1, File f2) {
					long diff = f1.lastModified() - f2.lastModified();
					if (diff > 0)
						return -1;
					else if (diff == 0)
						return 0;
					else
						return 1;
				}

				public boolean equals(Object obj) {
					return true;
				}

			});

			for (int i = 0; i < fs.length; i++) {
				if ( i == max )
					break;

				Bitmap bmp = MediaStore.Images.Media.getBitmap(
						this.getContentResolver(), Uri.fromFile(fs[i]));
				if (i == 0) {
					m_fileAll = fs[i];
					m_BitMapAll = bmp;
					ivAll.setImageBitmap(bmp);
				}
				else {
					m_fileNumber = fs[i];
					m_BitMapNumber = bmp;
					ivNumber.setImageBitmap(bmp);
					showImageNum();
					addHandler.post(new Runnable() {
						@Override
						public void run() {
							ocrNid();
						}
					});
				}

			}

		}

	}

	private void showImageNum() {
		MatrixInfo matrixInfo = new MatrixInfo();
		matrixInfo.setX(0);
		matrixInfo.setY((m_BitMapNumber.getHeight() * 577) / 1280);
		matrixInfo.setWidth(m_BitMapNumber.getWidth());
		matrixInfo.setHeight(30); 
																// 掉整到数值显示的位置
		final Bitmap bits = CommonUtil.cutBitmap(m_BitMapNumber, matrixInfo,
				false);
		final Bitmap bit = CommonUtil.gray2Binary(bits);

		ivPoint.setImageBitmap(bit);
		if (etHP.getText().toString().trim().equals("")
				|| etAttack.getText().toString().trim().equals("")
				|| etDefense.getText().toString().trim().equals("")) {
			addHandler.post(new Runnable() {
				@Override
				public void run() {
					ocr(bit);
				}
			});
		}

	}
	
	protected void ocrNid() {
		MatrixInfo matrixInfo = CommonUtil.getMatrixInfo(this, 3);
		final Bitmap bits = CommonUtil.cutBitmap(m_BitMapNumber, matrixInfo,
				false);
		final Bitmap bit = CommonUtil.gray2Binary(bits);

		ivNid.setImageBitmap(bit);
		
		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		baseApi.init(Environment.getExternalStorageDirectory()
				+ MConfig.TESSBASE_PATH, MConfig.DEFAULT_LANGUAGE);
		baseApi.setImage(bit);
		String recognizedText = baseApi.getUTF8Text().trim();
		baseApi.end();

		if (recognizedText != null) {
			etNid.setText(recognizedText);
		}
	}
	

	protected void ocr(Bitmap bitmap) {

		bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		baseApi.init(Environment.getExternalStorageDirectory()
				+ MConfig.TESSBASE_PATH, MConfig.DEFAULT_LANGUAGE);
		baseApi.setImage(bitmap);
		String recognizedText = baseApi.getUTF8Text().trim();
		baseApi.end();

		if (recognizedText != null) {
			StringTokenizer st = new StringTokenizer(recognizedText);
			if (st.hasMoreTokens())
				st.nextToken();
			if (st.hasMoreTokens())
				etHP.setText(st.nextToken());
			if (st.hasMoreTokens())
				st.nextToken();
			if (st.hasMoreTokens())
				etCost.setText(st.nextToken());
			if (st.hasMoreTokens())
				etAttack.setText(st.nextToken());
			if (st.hasMoreTokens())
				etDefense.setText(st.nextToken());
		}
	}

	AdapterView.OnItemSelectedListener onLevelSelectlistener = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int index,
				long arg3) {

			String[] array = getResources().getStringArray(R.array.cardLevel);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}

	};
	
	AdapterView.OnItemSelectedListener onTypeSelectlistener = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int index,
				long arg3) {

			String[] array = getResources().getStringArray(R.array.addType);
			if( array[index].equals("更新附加图"))
			{
				ivNumber.setImageDrawable(null);
				clearText(etCost);
				clearText(etHP);
				clearText(etAttack);
				clearText(etDefense);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}

	};
	
	
	View.OnClickListener btnSaveClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (etNid.getText().toString().trim().equals("")) {
				Toast.makeText(AddCardActivity.this, "Nid是必输项！",
						Toast.LENGTH_SHORT).show();
				return;
			}
			
			m_pDialog.show();
			new Thread() {
				public void run() {
					
					CardInfo card = new CardInfo();
					card.setNid(Integer.parseInt(etNid.getText().toString()));
					String attr = spinnerAttr.getSelectedItem().toString();
					card.setAttr(attr.equals("爱") ? "A"
							: (attr.equals("憎") ? "Z" : "L"));
					card.setLevel(Integer.parseInt(spinnerLevel
							.getSelectedItem().toString()));
					card.setName(etName.getText().toString().trim());
					if (!etCost.getText().toString().trim().equals(""))
						card.setCost(Integer.parseInt(etCost.getText()
								.toString()));
					if (!etHP.getText().toString().trim().equals(""))
						card.setMaxHP(Integer.parseInt(etHP.getText()
								.toString()));
					if (!etAttack.getText().toString().trim().equals(""))
						card.setMaxAttack(Integer.parseInt(etAttack.getText()
								.toString()));
					if (!etDefense.getText().toString().trim().equals(""))
						card.setMaxDefense(Integer.parseInt(etDefense.getText()
								.toString()));
					
					int type = spinnerType.getSelectedItemPosition();

					if(type == 1)
						card.setImgUpdated(1);
					else if( type == 0)
						card.setImgUpdated(0);
					
					if (m_BitMapNumber != null
							&& m_BitMapAll != null) {
						File fileDir = new File(
								Environment.getExternalStorageDirectory(),
								MConfig.SD_PATH);
						File imageFile = null;
						try {
							//更新正式图     更新附加图
							if(type == 2 || type == 3)
							{
								imageFile = new File(fileDir.getPath(),
										card.getNid() + "_1.png");
								
								int checknum = 3;
								while(true){
									File check = new File(fileDir.getPath(),
											card.getNid() + "_" + checknum + ".png");
									if(!check.exists()){
										if(type == 2)
											imageFile.renameTo(check);
										else if(type == 3)
											CommonUtil.exportImgFromBitmap(m_BitMapAll, check);
										break;
									}
									else
										checknum++;
								}
							}	
							
							//更新正式图   新增  
							if(type == 0 || type == 1 || type == 2)
							{
								imageFile = new File(fileDir.getPath(),
										card.getNid() + "_1.png");
								CommonUtil.exportImgFromBitmap(m_BitMapNumber, imageFile);
							}
							
							//更新正式图   新增 
							if(type == 0 || type == 1 || type == 2)
							{
								imageFile = new File(fileDir.getPath(),
										card.getNid() + "_2.png");
								CommonUtil.exportImgFromBitmap(m_BitMapAll, imageFile);
							}
							
							//自动生成头像
							CommonUtil.generateHeaderImg(AddCardActivity.this, new int[]{card.getNid()}, false);
							if( type != 3)
								CommonUtil.deleteImages(AddCardActivity.this, m_fileNumber);
							CommonUtil.deleteImages(AddCardActivity.this,
									m_fileAll);

						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					// type为3  只保存图片  不更新数据
					if( type == 3)
						addHandler.sendEmptyMessage(2);
					else{
						int dbnid = mDBHelper.queryCard(etNid.getText().toString().trim()).getNid();
						if( dbnid <= 0){
							mDBHelper.addCardInfo(card);
							addHandler.sendEmptyMessage(1);
						}
						else{
							if(type == 2)
								mDBHelper.updateCardInfo(card);
							else{
								CardInfo addcard = new CardInfo();
								addcard.setNid(card.getNid());
								addcard.setImgUpdated(card.getImgUpdated());
								mDBHelper.updateCardInfo(addcard);
							}
							addHandler.sendEmptyMessage(2);
						}
					}
				}
			}.start();
		}
	};
	
	View.OnClickListener btnSetClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			DialogSetMatrix.show(AddCardActivity.this, addHandler);
		}
	};
	
	View.OnClickListener btnDelClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			ivAll.setImageDrawable(null);
			ivNumber.setImageDrawable(null);
			ivPoint.setImageDrawable(null);
			ivNid.setImageDrawable(null);
			m_fileNumber = null;
			m_fileAll = null;
			m_BitMapNumber.recycle();
			m_BitMapAll.recycle();
			m_BitMapNumber = null;
			m_BitMapAll = null;
			
			clearText(etNid);
			clearText(etCost);
			clearText(etHP);
			clearText(etAttack);
			clearText(etDefense);
		}
	};
	
	private void clearText(EditText et){
		String input = et.getText().toString();
		if(!CommonUtil.isNumeric(input))
			et.setText("");
	}
	
	Handler addHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				Toast.makeText(AddCardActivity.this, "新增成功", Toast.LENGTH_SHORT)
						.show();
				forwardBack();
			} else if (msg.what == 2) {
				Toast.makeText(AddCardActivity.this, "更新成功", Toast.LENGTH_SHORT)
				.show();
				forwardBack();
			}
			else if (msg.what == 100) {
				addHandler.post(new Runnable() {
					@Override
					public void run() {
						ocrNid();
					}
				});
			}
			
		}

	};
	
	private void forwardBack()
	{
		Intent intent = new Intent(AddCardActivity.this,
				MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		AddCardActivity.this.finish();
		m_pDialog.dismiss();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mDBHelper != null)
			mDBHelper.Close();
	}
	
}
