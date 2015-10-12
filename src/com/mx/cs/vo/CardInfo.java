package com.mx.cs.vo;

import android.os.Parcel;
import android.os.Parcelable;

public class CardInfo implements Parcelable{

	private int nid;
	private String name = null;
	private String attr = null;
	private int level;
	private int cost;
	private int maxHP;
	private int maxAttack;
	private int maxDefense;

	public int getNid() {
		return nid;
	}

	public void setNid(int nid) {
		this.nid = nid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public int getMaxHP() {
		return maxHP;
	}

	public void setMaxHP(int maxHP) {
		this.maxHP = maxHP;
	}

	public int getMaxAttack() {
		return maxAttack;
	}

	public void setMaxAttack(int maxAttack) {
		this.maxAttack = maxAttack;
	}

	public int getMaxDefense() {
		return maxDefense;
	}

	public void setMaxDefense(int maxDefense) {
		this.maxDefense = maxDefense;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(attr);
		dest.writeInt(nid);
		dest.writeInt(level);
		dest.writeInt(cost);
		dest.writeInt(maxHP);
		dest.writeInt(maxAttack);
		dest.writeInt(maxDefense);
		
	}
	
	 public static final Parcelable.Creator<CardInfo> CREATOR = new Creator<CardInfo>()
	    {
	        @Override
	        public CardInfo[] newArray(int size)
	        {
	            return new CardInfo[size];
	        }
	        
	        @Override
	        public CardInfo createFromParcel(Parcel in)
	        {
	            return new CardInfo(in);
	        }
	    };
	    
	    public CardInfo(Parcel in)
	    {
			name = in.readString();
			attr = in.readString();
			nid = in.readInt();
			level = in.readInt();
			cost = in.readInt();
			maxHP = in.readInt();
			maxAttack = in.readInt();
			maxDefense = in.readInt();
	    }
	    public CardInfo()
	    {
	    	
	    }
			    

}
