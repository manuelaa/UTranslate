package com.example.home1;

import java.security.Timestamp;

public class Text {
	public String username;
	public String text;
	public float rating;
	public String timePosted;
	
	//url path do zvuka (ili null ako nema na ovom odgovoru)
	public String audioURLPath = null;
	//path do skinutog zvuka na disku ili null ako nisam skinuo
	public String audioFilePath = null;
	
	//TODO display picture
	
	public Text() {}
}
