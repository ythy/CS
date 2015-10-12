package com.mx.cs.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.mx.cs.R;
import com.mx.cs.adapter.DataListAdapter;
import com.mx.cs.dialog.DialogExportImg;
import com.mx.cs.provider.Providerdata;
import com.mx.cs.provider.Providerdata.Card;
import com.mx.cs.util.CommonUtil;
import com.mx.cs.util.DBHelper;
import com.mx.cs.util.JsonFileReader;
import com.mx.cs.vo.CardInfo;
import com.mx.cs.vo.MatrixInfo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private static final String EXPECTED_FILE_PATH = "tesseract/tessdata/";
	private static final String EXPECTED_FILE_NAME = "eng.traineddata";
	private static String HEADER_PATH = "Android/data/com.mx.cs/headerImg";
	private static String SD_PATH = "Android/data/com.mx.cs/images";
	
	private ListView listViewMain;
	private DBHelper mDBHelper;
	private Button btnAdd;
	private Button btnSearch;
	private EditText etSearch;
	private List<CardInfo> mList;
	private DataListAdapter mAdapter;

	private TextView tvHP;
	private TextView tvAttack;
	private TextView tvDefense;
	private TextView tvNid;
	private TextView tvName;
	private TextView tvAttr;
	
	private String mCurrentOrderBy;
	private String mCurrentOrderType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		listViewMain = (ListView) findViewById(R.id.lvMain);
		btnAdd = (Button) findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(btnAddClickListener);
		btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(btnSearchClickListener);
		etSearch = (EditText) findViewById(R.id.etSearch);

		View headerView = LayoutInflater.from(getBaseContext()).inflate(
				R.layout.adapter_mainlist_header, null);
		tvNid = (TextView) headerView.findViewById(R.id.tvHeaderNid);
		tvName = (TextView) headerView.findViewById(R.id.tvHeaderName);
		tvAttr = (TextView) headerView.findViewById(R.id.tvHeaderAttr);
		tvHP = (TextView) headerView.findViewById(R.id.tvHeaderHP);
		tvAttack = (TextView) headerView.findViewById(R.id.tvHeaderAttack);
		tvDefense = (TextView) headerView.findViewById(R.id.tvHeaderDefense);
		tvHP.setOnClickListener(tvHeaderHPClickHandler);
		tvAttack.setOnClickListener(tvHeaderAttackClickHandler);
		tvDefense.setOnClickListener(tvHeaderDefenseClickHandler);
		tvNid.setOnClickListener(tvHeaderNidClickHandler);
		tvName.setOnClickListener(tvHeaderNameClickHandler);
		tvAttr.setOnClickListener(tvHeaderAttrClickHandler);
		
		listViewMain.addHeaderView(headerView);
		listViewMain.setOnItemClickListener(itemClickListener);
		mList = new ArrayList<CardInfo>();
		mAdapter = new DataListAdapter(this, mList);
		mDBHelper = new DBHelper(MainActivity.this, Providerdata.DATABASE_NAME,
				null, Providerdata.DATABASE_VERSION);
		
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			File fileDir = new File(Environment.getExternalStorageDirectory(),
					EXPECTED_FILE_PATH);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			File ocrFile = new File(fileDir.getPath(), EXPECTED_FILE_NAME);
			if (!ocrFile.exists())
			{
				 try {
					CommonUtil.copyBigDataToSD(this, EXPECTED_FILE_NAME, ocrFile.getPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		showList();
	}

	private void showList() {
		mCurrentOrderBy = Card.COLUMN_NID;
		mCurrentOrderType = Card.SORT_DESC;
		mList.clear();
		mList.addAll(mDBHelper.queryCards(null, mCurrentOrderBy + mCurrentOrderType));
		if (mList.size() == 0) {
			new Thread() {
				public void run() {
					String out = JsonFileReader.getJson(MainActivity.this,
							"cardinfo.json");
					mDBHelper.addAllCardInfo(JsonFileReader.setListData(out));
					mainHandler.sendEmptyMessage(1);
				}
			}.start();
		} else {
			setHeaderColor(mCurrentOrderBy);
			updateList(true);
		}

	}

	Handler mainHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				showList();
			}
			else if(msg.what == 2 || msg.what == 4)
			{
				final int index = msg.what;
				new Thread() {
					public void run() {
						File imageDir = new File(Environment.getExternalStorageDirectory(),
								SD_PATH);
						File headerDir = new File(Environment.getExternalStorageDirectory(),
								HEADER_PATH);
						if(!headerDir.exists()){
							headerDir.mkdirs();
			            }
						File headerFile;
						File imageFile;
						FileOutputStream bos;
						Bitmap compress = null;
						MatrixInfo sets = CommonUtil.getMatrixInfo(MainActivity.this, 6);
						for(int i = 0; i < mList.size(); i++)
						{
							headerFile = new File(headerDir.getPath(),
									mList.get(i).getNid() + "_h.png");
							imageFile = new File(imageDir.getPath(), mList.get(i).getNid() + "_1.png");
							if ((index == 2 && headerFile.exists()) || !imageFile.exists())  
								continue;
								
							try {
								compress = CommonUtil.compressImageFromFile(imageFile.getPath());
								bos = new FileOutputStream(headerFile);
								CommonUtil.toRoundBitmap(CommonUtil.cutBitmap(compress,
										sets, false)).compress(Bitmap.CompressFormat.PNG, 100, bos);
								bos.flush();
								bos.close();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						mainHandler.sendEmptyMessage(3);
					}
				}.start();
			}
			else if (msg.what == 3) {
				Toast.makeText(MainActivity.this, "生成头像完成", Toast.LENGTH_SHORT).show();
			}
		}
		

	};

	AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			if (position != 0) {
				Intent intent = new Intent(MainActivity.this,
						DetailActivity.class);
				CardInfo info = (CardInfo) arg0.getItemAtPosition(position);
				intent.putExtra("card", info);
				startActivity(intent);
			}

		}
	};

	View.OnClickListener btnAddClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
			startActivity(intent);
		}
	};

	View.OnClickListener btnSearchClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			searchData(Card.COLUMN_NID, Card.SORT_DESC);
		}
	};

	View.OnClickListener tvHeaderHPClickHandler = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(mCurrentOrderBy == Card.COLUMN_MAXHP)
				searchData(Card.COLUMN_MAXHP, mCurrentOrderType.equals(Card.SORT_ASC) ? Card.SORT_DESC : Card.SORT_ASC);
			else
				searchData(Card.COLUMN_MAXHP, Card.SORT_DESC);
		}
	};

	View.OnClickListener tvHeaderAttackClickHandler = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(mCurrentOrderBy == Card.COLUMN_MAXATTACK)
				searchData(Card.COLUMN_MAXATTACK, mCurrentOrderType.equals(Card.SORT_ASC) ? Card.SORT_DESC : Card.SORT_ASC);
			else
				searchData(Card.COLUMN_MAXATTACK, Card.SORT_DESC);
		}
	};

	View.OnClickListener tvHeaderDefenseClickHandler = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(mCurrentOrderBy == Card.COLUMN_MAXDEFENSE)
				searchData(Card.COLUMN_MAXDEFENSE, mCurrentOrderType.equals(Card.SORT_ASC) ? Card.SORT_DESC : Card.SORT_ASC);
			else
				searchData(Card.COLUMN_MAXDEFENSE, Card.SORT_DESC);
		}
	};
	
	View.OnClickListener tvHeaderNidClickHandler = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(mCurrentOrderBy == Card.COLUMN_NID)
				searchData(Card.COLUMN_NID, mCurrentOrderType.equals(Card.SORT_ASC) ? Card.SORT_DESC : Card.SORT_ASC);
			else
				searchData(Card.COLUMN_NID, Card.SORT_DESC);
		}
	};
	
	View.OnClickListener tvHeaderNameClickHandler = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(mCurrentOrderBy == Card.COLUMN_NAME)
				searchData(Card.COLUMN_NAME, mCurrentOrderType.equals(Card.SORT_ASC) ? Card.SORT_DESC : Card.SORT_ASC);
			else
				searchData(Card.COLUMN_NAME, Card.SORT_DESC);
		}
	};
	
	View.OnClickListener tvHeaderAttrClickHandler = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(mCurrentOrderBy == Card.COLUMN_ATTR)
				searchData(Card.COLUMN_ATTR, mCurrentOrderType.equals(Card.SORT_ASC) ? Card.SORT_DESC : Card.SORT_ASC);
			else
				searchData(Card.COLUMN_ATTR, Card.SORT_DESC);
		}
	};

	private void searchData(String orderBy, String orderType) {
		setHeaderColor(orderBy);
		mCurrentOrderBy = orderBy;
		mCurrentOrderType = orderType;
		CardInfo card = new CardInfo();
		if (!etSearch.getText().toString().trim().equals(""))
			card.setName(etSearch.getText().toString().trim());
		mList.clear();
		mList.addAll(mDBHelper.queryCards(card, orderBy + orderType));
		updateList(true);
	}

	private void setHeaderColor(String orderBy) {
		if (mCurrentOrderBy.equals(Card.COLUMN_MAXHP))
			tvHP.setBackgroundColor(getResources().getColor(
					R.color.color_white2));
		else if (mCurrentOrderBy.equals(Card.COLUMN_MAXATTACK))
			tvAttack.setBackgroundColor(getResources().getColor(
					R.color.color_white2));
		else if (mCurrentOrderBy.equals(Card.COLUMN_MAXDEFENSE))
			tvDefense.setBackgroundColor(getResources().getColor(
					R.color.color_white2));
		else if (mCurrentOrderBy.equals(Card.COLUMN_NID))
			tvNid.setBackgroundColor(getResources().getColor(
					R.color.color_white2));
		else if (mCurrentOrderBy.equals(Card.COLUMN_NAME))
			tvName.setBackgroundColor(getResources().getColor(
					R.color.color_white2));
		else if (mCurrentOrderBy.equals(Card.COLUMN_ATTR))
			tvAttr.setBackgroundColor(getResources().getColor(
					R.color.color_white2));
		

		if (orderBy.equals(Card.COLUMN_MAXHP))
			tvHP.setBackgroundColor(getResources()
					.getColor(R.color.color_white));
		else if (orderBy.equals(Card.COLUMN_MAXATTACK))
			tvAttack.setBackgroundColor(getResources().getColor(
					R.color.color_white));
		else if (orderBy.equals(Card.COLUMN_MAXDEFENSE))
			tvDefense.setBackgroundColor(getResources().getColor(
					R.color.color_white));
		else if (orderBy.equals(Card.COLUMN_NID))
			tvNid.setBackgroundColor(getResources().getColor(
					R.color.color_white));
		else if (orderBy.equals(Card.COLUMN_NAME))
			tvName.setBackgroundColor(getResources().getColor(
					R.color.color_white));
		else if (orderBy.equals(Card.COLUMN_ATTR))
			tvAttr.setBackgroundColor(getResources().getColor(
					R.color.color_white));

	}

	private void updateList(boolean flag) {
		if (flag)
			listViewMain.setAdapter(mAdapter);
		else
			mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
	
	@Override 
    public boolean onOptionsItemSelected(MenuItem item) { 
        super.onOptionsItemSelected(item); 
        switch(item.getItemId())  
        { 
        	case  R.id.menu_out :
	        	try {
	        		CommonUtil.printFile(generateJsonString(), CommonUtil.generateDataFile("cardinfo.json"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
	            break; 
        	case  R.id.menu_header :
        		DialogExportImg.show(this, mList.get(0).getNid(), mainHandler);
		        break; 
	    } 
        return true; 
    } 
	
	
	private String generateJsonString() throws JSONException
	{
		List<CardInfo> data = mDBHelper.queryCards(null, null);
		JSONArray rows = new JSONArray();
		for(int i = 0; i < data.size(); i++)
		{
			JSONObject line = new JSONObject();
			line.put("nid", data.get(i).getNid());
			line.put("name", data.get(i).getName());
			line.put("attr", data.get(i).getAttr());
			line.put("cost", data.get(i).getCost());
			line.put("level", data.get(i).getLevel());
			line.put("maxHP", data.get(i).getMaxHP());
			line.put("maxAttack", data.get(i).getMaxAttack());
			line.put("maxDefense", data.get(i).getMaxDefense());
			rows.put(line);
		}
		
		JSONObject result = new JSONObject();
		result.put("rows", rows);
		result.put("head", "CS");
		return result.toString();
	}
	 
}
