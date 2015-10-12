package com.mx.cs.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mx.cs.vo.CardInfo;

import android.content.Context;
import android.content.res.AssetManager;

public class JsonFileReader {

	public static String getJson(Context context, String fileName) {

		StringBuilder stringBuilder = new StringBuilder();
		try {
			AssetManager assetManager = context.getAssets();
			BufferedReader bf = new BufferedReader(new InputStreamReader(
					assetManager.open(fileName)));
			String line;
			while ((line = bf.readLine()) != null) {
				stringBuilder.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}
	
	public static List<CardInfo> setListData(String str) {
		List<CardInfo> result = new ArrayList<CardInfo>();
		try {
			JSONObject jsonObj = new JSONObject(str);
			JSONArray array = jsonObj.getJSONArray("rows");
			int len = array.length();
			for (int i = 0; i < len; i++) {
				JSONObject object = array.getJSONObject(i);
				CardInfo cardinfo = new CardInfo();
				cardinfo.setNid(object.getInt("nid"));
				cardinfo.setName(object.getString("name"));
				cardinfo.setAttr(object.getString("attr"));
				cardinfo.setLevel(object.getInt("level"));
				cardinfo.setCost(object.getInt("cost"));
				cardinfo.setMaxHP(object.getInt("maxHP"));
				cardinfo.setMaxAttack(object.getInt("maxAttack"));
				cardinfo.setMaxDefense(object.getInt("maxDefense"));
				result.add(cardinfo);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	


}
