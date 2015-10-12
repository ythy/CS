package com.mx.cs.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class Providerdata {

    public static final String AUTHORITY="com.mx.cs.provider.csprovider"; 

    //数据库名称 
    public static final String DATABASE_NAME = "CS.db"; 
    //数据库的版本  
    public static final int DATABASE_VERSION = 1; 
    
    
    public static final class Card implements BaseColumns{

       //表名
       public static final String TABLE_NAME = "card_info";

       //访问该ContentProvider的URI
       public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/" + TABLE_NAME);
       
       //新增mimeType  vnd.android.cursor.dir/开头返回多条数据    vnd.android.cursor.item/开头返回单条数据
       public static final String CONTENT_TYPE="vnd.android.cursor.dir/vnd.mx.Card"; 
       public static final String CONTENT_TYPE_ITEM="vnd.android.cursor.item/vnd.mx.Card";

       //列名
       public static final String ID = "_id"; 
       public static final String COLUMN_NID = "nid";   
       public static final String COLUMN_NAME = "name";
       public static final String COLUMN_LEVEL = "level"; 
       public static final String COLUMN_ATTR= "attr"; 
       public static final String COLUMN_COST = "cost"; 
       public static final String COLUMN_MAXHP = "max_hp"; 
       public static final String COLUMN_MAXATTACK = "max_attack";
       public static final String COLUMN_MAXDEFENSE = "max_defense"; 
       
       public static final String SORT_DESC = " DESC";
       public static final String SORT_ASC = " ASC";
    }


}
