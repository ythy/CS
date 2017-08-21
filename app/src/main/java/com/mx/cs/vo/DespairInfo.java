package com.mx.cs.vo;

public class DespairInfo {
	private int id = -1;
	private String name = "";
	
	public DespairInfo(){
	}
	
	public DespairInfo( String name ){
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
