package com.example.home1;

import java.security.Timestamp;

import android.graphics.Bitmap;

public class Text {
	public long answerId;
	
	public String text;
	public float rating; //ukupni rating
	public String timePosted;
	
	//uploadani zvuk
	public DownloadMultimedia audio;
	
	//user
	public User user;
	
	//koji je rating dao trenutno logirani korisnik za ovaj answer
	//-1 ako nije rateao
	public int userRating;
	
	public Text() {}
}
