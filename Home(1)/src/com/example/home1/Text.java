package com.example.home1;

public class Text {
	public String username;
	public String text;
	public int idLang1;
	public int idLang2;
	public float rating;
	
	//url path do zvuka (ili null ako nema na ovom odgovoru)
	public String audioURLPath = null;
	//path do skinutog zvuka na disku ili null ako nisam skinuo
	public String audioFilePath = null;
	
	//slike
	public int resourceIdLang1;
	public int resourceIdLang2;
	//TODO display picture
	
	public Text() {}
}
