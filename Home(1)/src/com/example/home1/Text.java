package com.example.home1;

public class Text {
	public String username;
	public int idSlike;
	public String text;
	public int idLang1;
	public int idLang2; //= new int[5];	//ne bi trebalo više od 5 valjda
	
	
	
	public Text(String username, int idSlike, String text, int idLang1, int idLang2) {		
		super();
		this.username = username;
		this.idSlike=idSlike;
		this.text=text;
		this.idLang1 = idLang1;
		this.idLang2 = idLang2;
	}
	
	public String getUsername(){
		return username;
	}
	
	public int getId(){
		return idSlike;
	}
	
	public String getText(){
		return text;
	}
	
	public int getIdLang1(){
		return idLang1;
	} 
	
	public int getIdLang2(){
		return idLang2;
	}
	
}
