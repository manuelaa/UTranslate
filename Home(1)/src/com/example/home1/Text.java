package com.example.home1;

import java.security.Timestamp;

import android.graphics.Bitmap;

public class Text {
	public String text;
	public float rating;
	public String timePosted;
	
	//url path do zvuka (ili null ako nema na ovom odgovoru)
	public String audioURLPath = null;
	//path do skinutog zvuka na disku ili null ako nisam skinuo
	public String audioFilePath = null;
	
	//user
	public User user;
	
	public Text() {}
}
