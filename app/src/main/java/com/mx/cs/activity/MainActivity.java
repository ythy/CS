package com.mx.cs.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mx.cs.R;
import com.mx.cs.adapter.DataListAdapter;
import com.mx.cs.adapter.DespairSpinnerAdapter;
import com.mx.cs.common.MConfig;
import com.mx.cs.dialog.DialogExportImg;
import com.mx.cs.listener.ListenerListViewScrollHandler;
import com.mx.cs.provider.Providerdata.Card;
import com.mx.cs.service.FxService;
import com.mx.cs.util.CommonUtil;
import com.mx.cs.util.JsonFileReader;
import com.mx.cs.vo.CardInfo;
import com.mx.cs.vo.DespairInfo;
import com.mx.cs.vo.MatrixInfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
	

	private static final String EXPECTED_FILE_PATH = "tesseract/tessdata/";
	private static final String EXPECTED_FILE_NAME = "eng.traineddata";
	
	private ListView listViewMain;
	private Button btnAdd;
	private Button btnRefresh;
	private List<CardInfo> mList;
	private DataListAdapter mAdapter;
	
	private TextView tvImg;
	private TextView tvHP;
	private TextView tvAttack;
	private TextView tvDefense;
	private TextView tvNid;
	private TextView tvName;
	private TextView tvAttr;
	private TextView tvCost;
	private View headerView;
	
	private Spinner spinnerName;
	private Spinner spinnerCost;
	private Spinner spinnerExist;
	private Spinner spinnerAttr;
	private Spinner spinnerDespair;
	private RelativeLayout pageVboxLayout;
	
	private String mCurrentOrderBy;
	private String mCurrentOrderType;
	
	private static String DEFAULT_NAME = "名称";
	private static String DEFAULT_COST = "コスト";
	private static String DEFAULT_OWN = "持有";
	private static String DEFAULT_ATTR = "属性";
	private static String DEFAULT_DESPAIR = "绝望";
	
	private List<String> mSortColumns = new ArrayList<String>();
	private List<TextView> mSortHeader = new ArrayList<TextView>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		listViewMain = (ListView) findViewById(R.id.lvMain);
		btnAdd = (Button) findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(btnAddClickListener);
		btnRefresh = (Button) findViewById(R.id.btnRefresh);
		btnRefresh.setOnClickListener(btnRefreshClickListener);
		
		spinnerName = initSpinner(spinnerName, R.id.spinnerName);
		spinnerCost = initSpinner(spinnerCost, R.id.spinnerCost);
		spinnerExist = initSpinner(spinnerExist, R.id.spinnerExist);
		spinnerAttr = initSpinner(spinnerAttr, R.id.spinnerAttr);
		spinnerDespair = initSpinner(spinnerDespair, R.id.spinnerDespair);
		
		headerView = LayoutInflater.from(getBaseContext()).inflate(
				R.layout.adapter_mainlist_header, null);
		tvNid = initHeader(tvNid, R.id.tvHeaderNid, Card.COLUMN_NID);
		tvName = initHeader(tvName, R.id.tvHeaderName, Card.COLUMN_NAME);
		tvAttr = initHeader(tvAttr, R.id.tvHeaderAttr, Card.COLUMN_ATTR);
		tvCost = initHeader(tvCost, R.id.tvHeaderCost, Card.COLUMN_COST);
		tvHP = initHeader(tvHP, R.id.tvHeaderHP, Card.COLUMN_MAXHP);
		tvAttack = initHeader(tvAttack, R.id.tvHeaderAttack, Card.COLUMN_MAXATTACK);
		tvDefense = initHeader(tvDefense, R.id.tvHeaderDefense, Card.COLUMN_MAXDEFENSE);
		tvImg = initHeader(tvImg, R.id.tvHeaderImg, Card.COLUMN_DESPAIR);
		
		pageVboxLayout = (RelativeLayout) findViewById(R.id.pageVBox);
		pageVboxLayout.setVisibility(View.GONE);
		
		listViewMain.addHeaderView(headerView);
		listViewMain.setOnItemClickListener(itemClickListener);
		listViewMain.setOnScrollListener(new ListenerListViewScrollHandler(listViewMain, pageVboxLayout));
		
		mList = new ArrayList<CardInfo>();
		mAdapter = new DataListAdapter(this, mList);
		
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
		
		setSpinner(spinnerName, Card.COLUMN_NAME, DEFAULT_NAME);
		setSpinner(spinnerCost, Card.COLUMN_COST, DEFAULT_COST);
		setSpinner(spinnerExist, Card.COLUMN_OWN, DEFAULT_OWN);
		setSpinner(spinnerAttr, null, DEFAULT_ATTR);
		setSpinner(spinnerDespair, null, DEFAULT_DESPAIR);
		
		showList();
		//showAlertWindows();
		
		//temp
//		File fileDirTemp = new File(Environment.getExternalStorageDirectory(),
//				EXPECTED_FILE_PATH);
//		File fileTemp = new File(fileDirTemp.getPath(), "android.apk");
//
//		 try {
//			CommonUtil.copyBigDataToSD(this, "android.apk", fileTemp.getPath());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		 
	}
	
	private TextView initHeader(TextView tv, int sid, String column ){
		tv = (TextView) headerView.findViewById(sid);
		tv.setOnClickListener(tvHeaderClickHandler);
		mSortColumns.add(column);
		mSortHeader.add(tv);
		return tv;
	}
	
	private Spinner initSpinner(Spinner spinner, int sid )
	{
		spinner = (Spinner) findViewById(sid);
		spinner.setOnItemSelectedListener(onSelectlistener);
		final Spinner cp = spinner;
		LinearLayout llparent = (LinearLayout) spinner.getParent();
		llparent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cp.performClick();
			}
		});
		return spinner;
	}
	
	private void setSpinner(Spinner spinner, String columnType, String defaultStr)
	{
		String[] spinnerDataAttr = {defaultStr, "爱", "力", "憎" };
		String[] spinnerDataOwn = {defaultStr, "Y", "N" };
		String[] spinnerData = null;
		if(defaultStr.equals(DEFAULT_ATTR))
			spinnerData = spinnerDataAttr;
		else if (defaultStr.equals(DEFAULT_OWN))
			spinnerData = spinnerDataOwn;
		else if (defaultStr.equals(DEFAULT_DESPAIR)){
			List<DespairInfo> despairArray = mDBHelper.queryDespair();
			despairArray.add(0, new DespairInfo(defaultStr) );
			DespairSpinnerAdapter adapter = 
					new DespairSpinnerAdapter(this, despairArray);
			spinner.setAdapter(adapter);
		}
		else {
			CardInfo[] cardArray = mDBHelper.queryCardDropList(columnType);
			Arrays.sort(cardArray, droplistComparator);
			spinnerData = new String[cardArray.length + 1];
			spinnerData[0] = defaultStr;
			for(int i = 1; i <= cardArray.length; i++)
				spinnerData[i] = cardArray[i - 1].getName();
		}
		
		if (!defaultStr.equals(DEFAULT_DESPAIR)){
			ArrayAdapter< String> adapterName = 
					new ArrayAdapter< String>( this, 
					android.R.layout.simple_gallery_item, spinnerData);
			adapterName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapterName);
		}
		
	}
	
	static Comparator<CardInfo> droplistComparator = new Comparator<CardInfo>() {
		
		@Override
		public int compare(CardInfo o1, CardInfo o2) {
			return o2.getNid() - o1.getNid();
		}
	};
	
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
	
	private void showAlertWindows()
	{
		Intent intent = new Intent(MainActivity.this, FxService.class);
		//启动FxService
		startService(intent);

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
						int[] list = new int[mList.size()];
						for(int i = 0; i < mList.size(); i++)
							list[i] = mList.get(i).getNid();
						CommonUtil.generateHeaderImg(MainActivity.this, list, index == 2 ? false : true);
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
	
	View.OnClickListener btnRefreshClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			spinnerName.setSelection(0);
			spinnerCost.setSelection(0);
			spinnerExist.setSelection(0);
			spinnerAttr.setSelection(0);
			spinnerDespair.setSelection(0);
			showList(); 
		}
	};
	

	View.OnClickListener btnAddClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
			startActivity(intent);
		}
	};
	
	AdapterView.OnItemSelectedListener onSelectlistener = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int index,
				long arg3) {
			
			searchData(mCurrentOrderBy, mCurrentOrderType);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}

	};
	
	
	
	View.OnClickListener tvHeaderClickHandler = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int index = mSortHeader.indexOf(v);
			String column = mSortColumns.get(index);
			if(mCurrentOrderBy.equals(column))
				searchData(column, mCurrentOrderType.equals(Card.SORT_ASC) ? Card.SORT_DESC : Card.SORT_ASC);
			else
				searchData(column, Card.SORT_DESC);
		}
	};
	
	private void searchData(String orderBy, String orderType) {
		setHeaderColor(orderBy);
		mCurrentOrderBy = orderBy;
		mCurrentOrderType = orderType;
		CardInfo card = new CardInfo();
		if(!spinnerName.getSelectedItem().toString().equals("") && !spinnerName.getSelectedItem().toString().equals(DEFAULT_NAME)) 
		{
			card.setName(spinnerName.getSelectedItem().toString());
		}
		if(!spinnerCost.getSelectedItem().toString().equals(DEFAULT_COST)) 
		{
			card.setCost(Integer.parseInt(spinnerCost.getSelectedItem().toString()));
		}
		if(!spinnerExist.getSelectedItem().toString().equals(DEFAULT_OWN)) 
		{
			card.setCardExist(spinnerExist.getSelectedItem().toString());
		}
		if(!spinnerAttr.getSelectedItem().toString().equals(DEFAULT_ATTR)) 
		{
			String attr = spinnerAttr.getSelectedItem().toString();
			card.setAttr(attr.equals("爱") ? "A" : (attr.equals("力") ? "L" : "Z" ));
		}
		
		DespairInfo despairInfo = (DespairInfo) spinnerDespair.getSelectedItem();
		if(!despairInfo.getName().equals(DEFAULT_DESPAIR)) 
		{
			card.setDespairId(despairInfo.getId());
		}
		mList.clear();
		mList.addAll(mDBHelper.queryCards(card, orderBy + orderType));
		updateList(true);
	}

	private void setHeaderColor(String orderBy) {
		int indexCurrent = mSortColumns.indexOf(mCurrentOrderBy);
		mSortHeader.get(indexCurrent).setBackgroundColor(getResources().getColor(
				R.color.color_white2));
		
		int index = mSortColumns.indexOf(orderBy);
		mSortHeader.get(index).setBackgroundColor(getResources()
				.getColor(R.color.color_white));		
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
	        		Toast.makeText(this, "导出成功", Toast.LENGTH_SHORT).show();
				} catch (JSONException e) {
					e.printStackTrace();
				}
	            break; 
        	case  R.id.menu_header :
        		DialogExportImg.show(this, mList.get(0).getNid(), mainHandler);
		        break; 
        	case  R.id.menu_despair :
        		Intent intent = new Intent(MainActivity.this,
						DespairSetActivity.class);
				startActivity(intent);
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
		
		List<DespairInfo> dataDespair = mDBHelper.queryDespair();
		JSONArray rowsDespair = new JSONArray();
		for(int i = 0; i < dataDespair.size(); i++)
		{
			JSONObject line = new JSONObject();
			line.put("id", dataDespair.get(i).getId());
			line.put("name", dataDespair.get(i).getName());
			rowsDespair.put(line);
		}
		
		List<Integer[]> dataCardDespair = mDBHelper.queryAllCardDespairs();
		JSONArray rowsCardDespair = new JSONArray();
		for(int i = 0; i < dataCardDespair.size(); i++)
		{
			JSONObject line = new JSONObject();
			line.put("nid", dataCardDespair.get(i)[0]);
			line.put("id", dataCardDespair.get(i)[1]);
			rowsCardDespair.put(line);
		}
		
		
		JSONObject result = new JSONObject();
		result.put("rows", rows);
		result.put("despair", rowsDespair);
		result.put("carddespair", dataCardDespair);
		result.put("head", "CS");
		return result.toString();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mDBHelper != null)
			mDBHelper.Close();
	}
	
}
