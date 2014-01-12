package com.example.home1;

public class TabText {
	private String text;
	private int idLangA;
	private int idLangB;
	
	public TabText(String text, int idLangA, int idLangB){
		super();
		this.text=text;
		this.idLangA=idLangA;
		this.idLangB=idLangB;
	}
	
	public String getText(){
		return text;
	}
	
	public int getIdLangA(){
		return idLangA;
	}
	
	public int getidLangB(){
		return idLangB;
	}
	
}
