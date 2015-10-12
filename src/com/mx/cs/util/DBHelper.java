package com.mx.cs.util;


import java.util.ArrayList;
import java.util.List;

import com.mx.cs.provider.Providerdata.Card;
import com.mx.cs.vo.CardInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {   
    
    public DBHelper(Context context, String name,    
            CursorFactory factory, int version) {   
        super(context, name, factory, version);
        this.getWritableDatabase(); 
    }
    
    /**
     * should be invoke when you never use DBhelper
     * To release the database and etc.
     */
    public void Close() {
    	this.getWritableDatabase().close();
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {   
        db.execSQL("CREATE TABLE IF NOT EXISTS "    
                + Card.TABLE_NAME + " ("    
                + Card.ID + " INTEGER PRIMARY KEY,"    
                + Card.COLUMN_NID + " INTEGER,"  
                + Card.COLUMN_NAME + " VARCHAR," 
                + Card.COLUMN_LEVEL + " INTEGER," 
                + Card.COLUMN_ATTR + " VARCHAR," 
                + Card.COLUMN_COST + " INTEGER," 
                + Card.COLUMN_MAXHP + " INTEGER," 
                + Card.COLUMN_MAXATTACK + " INTEGER," 
                + Card.COLUMN_MAXDEFENSE + " INTEGER)"); 
     
    }   
    
    @Override
    public void onUpgrade(SQLiteDatabase db,    
            int oldVersion, int newVersion) {   
        //db.execSQL("DROP TABLE IF EXISTS " + SMS.TABLE_NAME);   
        //onCreate(db);   
    }
    
    
    public List<CardInfo> queryCards(CardInfo cardinfo, String orderBy) {
    	List<CardInfo> infos = new ArrayList<CardInfo>();
    	String selection = null;
    	String[] selectionArg = null;
    	if(cardinfo != null && cardinfo.getName() != null)
    	{
    		selection = Card.COLUMN_NAME + " like ?";
        	selectionArg = new String[] {"%" + cardinfo.getName() + "%"};
    	}
    	else if(cardinfo != null && cardinfo.getNid() != 0)
    	{
    		selection = Card.COLUMN_NID + "=?";
        	selectionArg = new String[] {String.valueOf(cardinfo.getNid())};
    	}
    	
    	Cursor cusor = this.getWritableDatabase().query(Card.TABLE_NAME, null, selection, selectionArg, null, null, orderBy);
		if (cusor != null) {
			while (cusor.moveToNext()) {
				CardInfo cardInfo = new CardInfo();
				cardInfo.setNid(cusor.getInt(cusor.getColumnIndex(Card.COLUMN_NID)));
				cardInfo.setName(cusor.getString(cusor.getColumnIndex(Card.COLUMN_NAME)));
				cardInfo.setAttr(cusor.getString(cusor.getColumnIndex(Card.COLUMN_ATTR)));
				cardInfo.setLevel(cusor.getInt(cusor.getColumnIndex(Card.COLUMN_LEVEL)));
				cardInfo.setCost(cusor.getInt(cusor.getColumnIndex(Card.COLUMN_COST)));
				cardInfo.setMaxHP(cusor.getInt(cusor.getColumnIndex(Card.COLUMN_MAXHP)));
				cardInfo.setMaxAttack(cusor.getInt(cusor.getColumnIndex(Card.COLUMN_MAXATTACK)));
				cardInfo.setMaxDefense(cusor.getInt(cusor.getColumnIndex(Card.COLUMN_MAXDEFENSE)));
				infos.add(cardInfo);
			}
			cusor.close();
		}
		return infos;
    }
    
    public void addAllCardInfo(List<CardInfo> list){
    	ContentValues values;   
    	for(int i = 0; i < list.size(); i++)
    	{
    		values = new ContentValues();   
    		values.put(Card.COLUMN_NID, list.get(i).getNid());   
    		values.put(Card.COLUMN_NAME, list.get(i).getName()); 
    		values.put(Card.COLUMN_LEVEL, list.get(i).getLevel());   
    		values.put(Card.COLUMN_ATTR, list.get(i).getAttr());   
    		values.put(Card.COLUMN_COST, list.get(i).getCost());   
    		values.put(Card.COLUMN_MAXHP, list.get(i).getMaxHP());   
    		values.put(Card.COLUMN_MAXATTACK, list.get(i).getMaxAttack());   
    		values.put(Card.COLUMN_MAXDEFENSE, list.get(i).getMaxDefense());   
    	    this.getWritableDatabase().insert(
    	    		Card.TABLE_NAME, null, values);  
    	}
    }
    
    public int updateCardInfo(CardInfo cardinfo)
    {
    	String selection = Card.COLUMN_NID + "=?";
    	String[] selectionArg = new String[] {String.valueOf(cardinfo.getNid())};
		ContentValues values = new ContentValues(); 
		if(cardinfo.getName() != null)
			values.put(Card.COLUMN_NAME, cardinfo.getName()); 
		values.put(Card.COLUMN_MAXHP, cardinfo.getMaxHP());
		values.put(Card.COLUMN_MAXATTACK, cardinfo.getMaxAttack());
		values.put(Card.COLUMN_MAXDEFENSE, cardinfo.getMaxDefense());
		return this.getWritableDatabase().update(Card.TABLE_NAME, values, selection, selectionArg);
    }
    
    public long addCardInfo(CardInfo cardinfo)
    {
    	CardInfo card = new CardInfo();
    	card.setNid(cardinfo.getNid());
    	List<CardInfo> list = queryCards(card, null);
    	if(list.size() > 0)
    		return -1;
    	ContentValues values = new ContentValues();   
		values.put(Card.COLUMN_NID, cardinfo.getNid());   
		values.put(Card.COLUMN_NAME, cardinfo.getName()); 
		values.put(Card.COLUMN_LEVEL, cardinfo.getLevel());   
		values.put(Card.COLUMN_ATTR, cardinfo.getAttr());   
		values.put(Card.COLUMN_COST, cardinfo.getCost());   
		values.put(Card.COLUMN_MAXHP, cardinfo.getMaxHP());   
		values.put(Card.COLUMN_MAXATTACK, cardinfo.getMaxAttack());   
		values.put(Card.COLUMN_MAXDEFENSE, cardinfo.getMaxDefense());   
	    return this.getWritableDatabase().insert(Card.TABLE_NAME, null, values);  
    }
    
    public long delCardInfo(CardInfo cardinfo)
    {
	    String selection = Card.COLUMN_NID + "=?";
    	String[] selectionArg = new String[] {String.valueOf(cardinfo.getNid())};
		return this.getWritableDatabase().delete(Card.TABLE_NAME, selection, selectionArg);
    }
    
} 