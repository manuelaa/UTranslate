package com.example.home1;

public class Lang {
	private String name;
	private int iconID;
	private int checkboxID;
	private boolean selected;
	
	public Lang(String name, int iconID, int getcheckboxID){
		super();
		this.name=name;
		this.iconID=iconID;
		this.selected=selected;
	}
	
	public String getName(){
		return name;
	}

	public int getIconID() {
		// TODO Auto-generated method stub
		return iconID;
	}
	
	public int getcheckboxID(){
		return checkboxID;
	}
	
	public boolean isSelected(){
		return selected;
	}
	
	public String toString(){
		return this.name;
		
	}
	
}
