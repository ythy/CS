package com.mx.cs.util;


import java.util.ArrayList;
import java.util.List;

import com.mx.cs.provider.Providerdata.Card;
import com.mx.cs.provider.Providerdata.DESPAIR;
import com.mx.cs.provider.Providerdata.DESPAIR_CHAIN;
import com.mx.cs.vo.CardInfo;
import com.mx.cs.vo.DespairInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
                + Card.COLUMN_MAXDEFENSE + " INTEGER," 
                + Card.COLUMN_OWN + " VARCHAR DEFAULT 'N'," 
                + Card.COLUMN_IMGUPDATE + " INTEGER DEFAULT 0)"); 
        
        db.execSQL("CREATE TABLE IF NOT EXISTS "    
                + DESPAIR.TABLE_NAME + " ("    
                + DESPAIR.ID + " INTEGER PRIMARY KEY,"   
                + DESPAIR.COLUMN_NAME + " VARCHAR )" ); 
    	
    	db.execSQL("CREATE TABLE IF NOT EXISTS "    
                + DESPAIR_CHAIN.TABLE_NAME + " ("    
                + DESPAIR_CHAIN.COLUMN_CARD_NID + " INTEGER ,"   
                + DESPAIR_CHAIN.COLUMN_DESPAIR_ID + " INTEGER, " 
                + "PRIMARY KEY (" + DESPAIR_CHAIN.COLUMN_CARD_NID + " , "  
                + DESPAIR_CHAIN.COLUMN_DESPAIR_ID + " )) "); 
    	
     
    }   
    
    @Override
    public void onUpgrade(SQLiteDatabase db,    
            int oldVersion, int newVersion) {   
    	
        if(newVersion == 2)
        {
        	db.execSQL("ALTER TABLE " + Card.TABLE_NAME + 
        			" ADD " + Card.COLUMN_IMGUPDATE + " INTEGER;");  
        	db.execSQL(  
               "UPDATE " + Card.TABLE_NAME +  
                " SET " + Card.COLUMN_IMGUPDATE + "=1" +  
                " WHERE " + Card.COLUMN_NID + " IN (814, 803, 784, 782, 781, 779, 778, 777, 768, 765, 737, 733, 730, 723, 681, 677, 671, 634, 603, 531, 516, 447, 432, 429, 397)");  
        }
        else if(newVersion == 3)
        {
        	db.execSQL("ALTER TABLE " + Card.TABLE_NAME + 
        			" ADD " + Card.COLUMN_OWN + " VARCHAR DEFAULT 'N';");  
        }
        else if(newVersion == 4)
        {
        	db.execSQL(  
                    "DELETE FROM " + Card.TABLE_NAME +  
                     " WHERE " + Card.COLUMN_NID + " < 860 AND " + 
                     Card.COLUMN_COST + " < 30 AND " + 
                     Card.COLUMN_LEVEL + " < 8 ;" );  
        }
        else if(newVersion == 5)
        {
        	db.execSQL("CREATE TABLE IF NOT EXISTS "    
                    + DESPAIR.TABLE_NAME + " ("    
                    + DESPAIR.ID + " INTEGER PRIMARY KEY,"   
                    + DESPAIR.COLUMN_NAME + " VARCHAR )" ); 
        	
        	db.execSQL("CREATE TABLE IF NOT EXISTS "    
                    + DESPAIR_CHAIN.TABLE_NAME + " ("    
                    + DESPAIR_CHAIN.COLUMN_CARD_NID + " INTEGER ,"   
                    + DESPAIR_CHAIN.COLUMN_DESPAIR_ID + " INTEGER, " 
                    + "PRIMARY KEY (" + DESPAIR_CHAIN.COLUMN_CARD_NID + " , "  
                    + DESPAIR_CHAIN.COLUMN_DESPAIR_ID + " )) "); 
        }
        else if(newVersion == 6)
        {
        	db.execSQL("ALTER TABLE " + Card.TABLE_NAME + 
        			" ADD " + Card.COLUMN_REMARK + " VARCHAR;");  
        }
    }
    
    
    public List<CardInfo> queryCards(CardInfo cardinfo, String orderBy) {
    	List<CardInfo> infos = new ArrayList<CardInfo>();
    	String[] selectionArg = null;
    	String sql = "SELECT *, ( SELECT count(*) FROM " + DESPAIR_CHAIN.TABLE_NAME
    					+ " WHERE " + Card.TABLE_NAME + "." + Card.COLUMN_NID + "=" 
    					+ DESPAIR_CHAIN.TABLE_NAME + "." + DESPAIR_CHAIN.COLUMN_CARD_NID 
    					+ "  ) " + Card.COLUMN_DESPAIR + " FROM " + Card.TABLE_NAME;
    	String sqlWhere = "";
    	if(cardinfo != null)
    	{
    		String selectionValue = "";
    		if(cardinfo.getName() != null)
        	{
    			if(!sqlWhere.equals(""))
    				sqlWhere += " and ";
    			sqlWhere += Card.COLUMN_NAME + " like ? ";
        		selectionValue += "%" + cardinfo.getName() + "%,";
        	}
        	if(cardinfo.getNid() != 0)
        	{
        		if(!sqlWhere.equals(""))
    				sqlWhere += " and ";
        		sqlWhere += Card.COLUMN_NID + "=? ";
        		selectionValue += String.valueOf(cardinfo.getNid()) + ",";
        	}
        	if(cardinfo.getCardExist() != null)
        	{
        		if(!sqlWhere.equals(""))
    				sqlWhere += " and ";
        		sqlWhere += Card.COLUMN_OWN + "=? ";
        		selectionValue += String.valueOf(cardinfo.getCardExist()) + ",";
        	}
        	if(cardinfo.getAttr() != null)
        	{
        		if(!sqlWhere.equals(""))
    				sqlWhere += " and ";
        		sqlWhere += Card.COLUMN_ATTR + "=? ";
        		selectionValue += String.valueOf(cardinfo.getAttr()) + ",";
        	}
        	if(cardinfo.getLevel() != 0)
        	{
        		if(!sqlWhere.equals(""))
    				sqlWhere += " and ";
        		sqlWhere += Card.COLUMN_LEVEL + "=? ";
        		selectionValue += String.valueOf(cardinfo.getLevel()) + ",";
        	}
        	if(cardinfo.getCost() != 0)
        	{
        		if(!sqlWhere.equals(""))
    				sqlWhere += " and ";
        		sqlWhere += Card.COLUMN_COST + "=? ";
        		selectionValue += String.valueOf(cardinfo.getCost()) + ",";
        	}
        	if(cardinfo.getDespairId() >= 0)
        	{
        		if(!sqlWhere.equals(""))
    				sqlWhere += " and ";
        		sqlWhere += "EXISTS ( SELECT * FROM  " 
    				+ DESPAIR_CHAIN.TABLE_NAME  
    				+ " WHERE " + Card.TABLE_NAME + "." + Card.COLUMN_NID + " = " 
    				+ DESPAIR_CHAIN.TABLE_NAME + "." + DESPAIR_CHAIN.COLUMN_CARD_NID + " and " 
    				+ DESPAIR_CHAIN.TABLE_NAME + "." + DESPAIR_CHAIN.COLUMN_DESPAIR_ID + " = ? ) ";
        		
        		selectionValue += String.valueOf(cardinfo.getDespairId()) + ",";
        	}
        	
        	if(!selectionValue.equals(""))
        		selectionArg = selectionValue.split(",");
    	}
    	
    	Cursor cusor = this.getWritableDatabase().rawQuery(sql + (selectionArg == null ? 
    			"" : " WHERE " + sqlWhere) + " order by " + orderBy, selectionArg);
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
				cardInfo.setImgUpdated(cusor.getInt(cusor.getColumnIndex(Card.COLUMN_IMGUPDATE)));
				cardInfo.setCardExist(cusor.getString(cusor.getColumnIndex(Card.COLUMN_OWN)));
				cardInfo.setDespairId(cusor.getInt(cusor.getColumnIndex(Card.COLUMN_DESPAIR)));
				cardInfo.setRemark(cusor.getString(cusor.getColumnIndex(Card.COLUMN_REMARK)));
				infos.add(cardInfo);
			}
			cusor.close();
		}
		return infos;
    }
    
    
    public CardInfo[] queryCardDropList(String Type) {
    	String sql = "SELECT distinct("+ Type + "), count(*) FROM " + Card.TABLE_NAME + " GROUP BY " + Type;
    	Cursor cusor = this.getWritableDatabase().rawQuery(sql, null);
    	int total = cusor.getCount();
    	CardInfo[] infos = new CardInfo[total];
    	int i = 0;
		if (cusor != null) {
			while (cusor.moveToNext()) {
				CardInfo cardInfo = new CardInfo();
				cardInfo.setName(cusor.getString(0));
				cardInfo.setNid(cusor.getInt(1));
				infos[i++] = cardInfo;
			}
			cusor.close();
		}
		return infos;
    }
    
    public CardInfo queryCard(String nid) {
    	String selection = Card.COLUMN_NID + "=?";
    	String[] selectionArg = new String[] {nid};
    	CardInfo cardInfo = new CardInfo();
    	Cursor cusor = this.getWritableDatabase().query(Card.TABLE_NAME, null, selection, selectionArg, null, null, null);
		if (cusor != null) {
			while (cusor.moveToNext()) {
				cardInfo.setNid(cusor.getInt(cusor.getColumnIndex(Card.COLUMN_NID)));
				cardInfo.setName(cusor.getString(cusor.getColumnIndex(Card.COLUMN_NAME)));
				cardInfo.setAttr(cusor.getString(cusor.getColumnIndex(Card.COLUMN_ATTR)));
				cardInfo.setLevel(cusor.getInt(cusor.getColumnIndex(Card.COLUMN_LEVEL)));
				cardInfo.setCost(cusor.getInt(cusor.getColumnIndex(Card.COLUMN_COST)));
				cardInfo.setMaxHP(cusor.getInt(cusor.getColumnIndex(Card.COLUMN_MAXHP)));
				cardInfo.setMaxAttack(cusor.getInt(cusor.getColumnIndex(Card.COLUMN_MAXATTACK)));
				cardInfo.setMaxDefense(cusor.getInt(cusor.getColumnIndex(Card.COLUMN_MAXDEFENSE)));
				cardInfo.setImgUpdated(cusor.getInt(cusor.getColumnIndex(Card.COLUMN_IMGUPDATE)));
				cardInfo.setCardExist(cusor.getString(cusor.getColumnIndex(Card.COLUMN_OWN)));
				cardInfo.setRemark(cusor.getString(cusor.getColumnIndex(Card.COLUMN_REMARK)));
			}
			cusor.close();
		}
		return cardInfo;
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
    		values.put(Card.COLUMN_IMGUPDATE, list.get(i).getImgUpdated());   
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
		if(cardinfo.getCardExist() != null)
			values.put(Card.COLUMN_OWN, cardinfo.getCardExist()); 
		if(cardinfo.getMaxHP() > 0)
			values.put(Card.COLUMN_MAXHP, cardinfo.getMaxHP());
		if(cardinfo.getMaxAttack() > 0)
			values.put(Card.COLUMN_MAXATTACK, cardinfo.getMaxAttack());
		if(cardinfo.getMaxDefense() > 0)
			values.put(Card.COLUMN_MAXDEFENSE, cardinfo.getMaxDefense());
		if(cardinfo.getAttr() != null)
			values.put(Card.COLUMN_ATTR, cardinfo.getAttr());
		if(cardinfo.getLevel() > 0)
			values.put(Card.COLUMN_LEVEL, cardinfo.getLevel());
		if(cardinfo.getCost() > 0)
			values.put(Card.COLUMN_COST, cardinfo.getCost());
		if(cardinfo.getImgUpdated() > -1)
			values.put(Card.COLUMN_IMGUPDATE, cardinfo.getImgUpdated());
		if(cardinfo.getRemark() != null)
			values.put(Card.COLUMN_REMARK, cardinfo.getRemark());
		
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
		values.put(Card.COLUMN_IMGUPDATE, cardinfo.getImgUpdated());  
	    return this.getWritableDatabase().insert(Card.TABLE_NAME, null, values);  
    }
    
    public long delCardInfo(CardInfo cardinfo)
    {
	    String selection = Card.COLUMN_NID + "=?";
    	String[] selectionArg = new String[] {String.valueOf(cardinfo.getNid())};
		return this.getWritableDatabase().delete(Card.TABLE_NAME, selection, selectionArg);
    }
    
    public long updateCardName(CardInfo cardinfoNew, CardInfo cardinfoOld)
    {
	    String selection = Card.COLUMN_NAME + "=?";
    	String[] selectionArg = new String[] {String.valueOf(cardinfoOld.getName())};
    	ContentValues values = new ContentValues(); 
		values.put(Card.COLUMN_NAME, cardinfoNew.getName()); 
		return this.getWritableDatabase().update(Card.TABLE_NAME, values, selection, selectionArg);
    }
    
    public long addDespairName(DespairInfo info)
    {
    	ContentValues values = new ContentValues();   
		values.put(DESPAIR.COLUMN_NAME, info.getName()); 
	    return this.getWritableDatabase().insert(DESPAIR.TABLE_NAME, null, values);  
    }
    
    public long updateDespairName(DespairInfo despairInfo)
    {
    	int id = despairInfo.getId();
    	if( id == -1 )
    		return addDespairName(despairInfo);
	    String selection = DESPAIR.ID + "=?";
    	String[] selectionArg = new String[] {String.valueOf(id)};
    	ContentValues values = new ContentValues(); 
		values.put(DESPAIR.COLUMN_NAME, despairInfo.getName()); 
		return this.getWritableDatabase().update(DESPAIR.TABLE_NAME, values, selection, selectionArg);
    }
    
    public List<DespairInfo> queryDespair() {
    	List<DespairInfo> infos = new ArrayList<DespairInfo>();
    	String[] selectionArg = null;
    	String sql = "SELECT * FROM " + DESPAIR.TABLE_NAME;

    	Cursor cusor = this.getWritableDatabase().rawQuery(sql, selectionArg);
		if (cusor != null) {
			while (cusor.moveToNext()) {
				DespairInfo despairInfo = new DespairInfo();
				despairInfo.setId(cusor.getInt(cusor.getColumnIndex(DESPAIR.ID)));
				despairInfo.setName(cusor.getString(cusor.getColumnIndex(DESPAIR.COLUMN_NAME)));
				infos.add(despairInfo);
			}
			cusor.close();
		}
		 
		return infos;
    }
    
    /**
     * get card`s despair name
     * @param nid
     * @return
     */
    public List<Integer> queryCardDespairs( int nid ) {
    	String selection = DESPAIR_CHAIN.COLUMN_CARD_NID + "=?";
    	String[] selectionArg = new String[] {String.valueOf(nid)};
    	Cursor cusor = this.getWritableDatabase().query(DESPAIR_CHAIN.TABLE_NAME, null, selection, selectionArg, null, null, null);
		List<Integer> despairlist = new  ArrayList<Integer>();
    	if (cusor != null) {
			while (cusor.moveToNext()) {
				despairlist.add(cusor.getInt(cusor.getColumnIndex(DESPAIR_CHAIN.COLUMN_DESPAIR_ID)));
			}
			cusor.close();
		}
		return despairlist;
    }
    
    public List<Integer[]> queryAllCardDespairs() {
    	Cursor cusor = this.getWritableDatabase().query(DESPAIR_CHAIN.TABLE_NAME, null, null, null, null, null, null);
		List<Integer[]> despairlist = new  ArrayList<Integer[]>();
    	if (cusor != null) {
			while (cusor.moveToNext()) {
				Integer[] array = new Integer[2];
				array[0] = cusor.getInt(cusor.getColumnIndex(DESPAIR_CHAIN.COLUMN_CARD_NID));
				array[1] = cusor.getInt(cusor.getColumnIndex(DESPAIR_CHAIN.COLUMN_DESPAIR_ID));
				despairlist.add(array);
			}
			cusor.close();
		}
		return despairlist;
    }
    
    /**
     * get despair name by id
     * @param name
     * @return
     */
    private int getDespairId( String name ) {
    	String selection = DESPAIR.COLUMN_NAME + "=?";
    	String[] selectionArg = new String[] {name};
    	int result = -1;
    	Cursor cusor = this.getWritableDatabase().query(DESPAIR.TABLE_NAME, null, selection, selectionArg, null, null, null);
		if (cusor != null) {
			while (cusor.moveToNext()) {
				result = cusor.getInt(cusor.getColumnIndex(DESPAIR.ID));
			}
			cusor.close();
		}
		return result;
    }
    
    
    public long setCardDespair( int nid, int[] despair ) {
    	for( int i = 0; i < despair.length; i++ ){
    		addCardDespair( nid, despair[i] );
    	}
    	return 0;
    }
    
    private boolean checkCardDespairIsExisting( int nid, int id   ){
    	String selection = DESPAIR_CHAIN.COLUMN_CARD_NID + "=?  AND " +  DESPAIR_CHAIN.COLUMN_DESPAIR_ID + "=? ";
    	String[] selectionArg = new String[] {String.valueOf(nid), String.valueOf(id)};
    	Cursor cusor = this.getWritableDatabase().query(DESPAIR_CHAIN.TABLE_NAME, null, selection, selectionArg, null, null, null);
    	if(cusor.getCount() > 0)
    		return true;
    	else
    		return false;
    }
    
    private long addCardDespair( int nid, int id ){
    	if(checkCardDespairIsExisting(nid, id) || id == -1)
    		return -1;
    	ContentValues values = new ContentValues();   
		values.put(DESPAIR_CHAIN.COLUMN_CARD_NID, nid); 
		values.put(DESPAIR_CHAIN.COLUMN_DESPAIR_ID,id); 
	    return this.getWritableDatabase().insert(DESPAIR_CHAIN.TABLE_NAME, null, values);  
    }
    
    public long delCardDespair( int nid, int id ){
    	String selection = DESPAIR_CHAIN.COLUMN_CARD_NID + "=? and " 
    					+ DESPAIR_CHAIN.COLUMN_DESPAIR_ID + "=?";
     	String[] selectionArg = new String[] {String.valueOf(nid), String.valueOf(id)};
 		return this.getWritableDatabase().delete(DESPAIR_CHAIN.TABLE_NAME, selection, selectionArg);
    }
    
    
} 