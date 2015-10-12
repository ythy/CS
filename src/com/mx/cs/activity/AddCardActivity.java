package com.mx.cs.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import com.mx.cs.R;
import com.mx.cs.provider.Providerdata;
import com.mx.cs.util.CommonUtil;
import com.mx.cs.util.DBHelper;
import com.mx.cs.vo.CardInfo;

import android.app.Activity;
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

public class AddCardActivity extends Activity {

	private Button btnSave;
	private Spinner spinnerAttr;
	private Spinner spinnerLevel;
	private EditText etNid;
	private EditText etName;
	private EditText etCost;
	private EditText etHP;
	private EditText etAttack;
	private EditText etDefense;
	private DBHelper mDBHelper;
	private ImageView ivNumber;
	private ImageView ivAll;
	private ProgressDialog m_pDialog;
	private File m_fileNumber;
	private File m_fileAll;
	private Bitmap m_BitMapNumber;
	private Bitmap m_BitMapAll;
	
	private static String SD_PATH = "Android/data/com.mx.cs/images";
	private static String SRC_PATH = "Pictures/Screenshots";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

		mDBHelper = new DBHelper(AddCardActivity.this,
				Providerdata.DATABASE_NAME, null, Providerdata.DATABASE_VERSION);

		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(btnSaveClickListener);

		spinnerAttr = (Spinner) findViewById(R.id.spinnerAttr);
		spinnerLevel = (Spinner) findViewById(R.id.spinnerLevel);
		spinnerLevel.setOnItemSelectedListener(onLevelSelectlistener);
		etNid = (EditText) findViewById(R.id.etDetailNid);
		etName = (EditText) findViewById(R.id.etDetailName);
		etCost = (EditText) findViewById(R.id.etDetailCost);
		etHP = (EditText) findViewById(R.id.etDetailHP);
		etAttack = (EditText) findViewById(R.id.etDetailAttack);
		etDefense = (EditText) findViewById(R.id.etDetailDefense);

		ivNumber = (ImageView) findViewById(R.id.imgWithNumber);
		ivAll = (ImageView) findViewById(R.id.imgAll);

		m_pDialog = new ProgressDialog(this);
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		m_pDialog.setMessage("请稍等。。。");
		// 设置ProgressDialog 的进度条是否不明确
		m_pDialog.setIndeterminate(false);
		// 设置ProgressDialog 是否可以按退回按键取消
		m_pDialog.setCancelable(false);

		try {
			showPicture();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void showPicture() throws FileNotFoundException, IOException {
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			File fileDir = new File(Environment.getExternalStorageDirectory(),
					SRC_PATH);
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
				if (i == 2)
					break;

				Bitmap bmp = MediaStore.Images.Media.getBitmap(
						this.getContentResolver(), Uri.fromFile(fs[i]));
				if (i == 0)
				{
					m_fileAll = fs[i];
					m_BitMapAll = bmp;
					ivAll.setImageBitmap(bmp);
				}
				else
				{
					m_fileNumber = fs[i];
					m_BitMapNumber = bmp;
					ivNumber.setImageBitmap(bmp);
				}
					
			}

		}

	}

	AdapterView.OnItemSelectedListener onLevelSelectlistener = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int index,
				long arg3) {

			String[] array = getResources().getStringArray(R.array.cardLevel);
			if (array[index].equals("8"))
				etCost.setText("52");
			else if (array[index].equals("7"))
				etCost.setText("40");
			else
				etCost.setText("");
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

					if (ivAll.getDrawable() != null
							&& ivNumber.getDrawable() != null) {
						File fileDir = new File(
								Environment.getExternalStorageDirectory(),
								SD_PATH);
						File imageFile;
						FileOutputStream bos;

						try {
							imageFile = new File(fileDir.getPath(),
									card.getNid() + "_1.png");
							bos = new FileOutputStream(imageFile);
							m_BitMapNumber.compress(
									Bitmap.CompressFormat.PNG, 100, bos);
							bos.flush();
							bos.close();

							imageFile = new File(fileDir.getPath(),
									card.getNid() + "_2.png");
							bos = new FileOutputStream(imageFile);
							m_BitMapAll.compress(
									Bitmap.CompressFormat.PNG, 100, bos);
							bos.flush();
							bos.close();
							
							CommonUtil.deleteImages(AddCardActivity.this, m_fileAll);
							CommonUtil.deleteImages(AddCardActivity.this, m_fileNumber);

						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					long result = mDBHelper.addCardInfo(card);
					if (result != -1) {
						addHandler.sendEmptyMessage(2);
					} else
						addHandler.sendEmptyMessage(1);

				}
			}.start();
		}
	};

	Handler addHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				Toast.makeText(AddCardActivity.this, "保存失败", Toast.LENGTH_SHORT)
						.show();
			} else if (msg.what == 2) {
				Intent intent = new Intent(AddCardActivity.this,
						MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				AddCardActivity.this.finish();
			}
			m_pDialog.dismiss();
		}

	};
}
