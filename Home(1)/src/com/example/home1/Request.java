package com.example.home1;

import java.security.Timestamp;

import android.content.Context;

public class Request {
	public long userId;
	public long requestId;
	public String text;
	
	//url path do zvuka (ili null ako nema na ovom requestu)
	public String audioURLPath = null;
	//path do skinutog zvuka na disku ili null ako nisam skinuo jos
	public String audioFilePath = null;
	
	//url path do slike (ili null ako nema na ovom requestu)
	public String pictureURLPath = null;
	//path do skinute slike na disku ili null ako nisam skinuo
	public String pictureFilePath = null;
	
	public int idLang1, idLang2;
	public String timePosted;	
	public boolean notification;
	
	//id-evi slika zastavica
	public int resourceIdLang1;
	public int resourceIdLang2;

	
	public Request(Context context, long userId, long requestId, String text, String audioURLPath, String pictureURLPath, int idLang1, int idLang2, String timePosted, boolean notification) {
		this.userId = userId;
		this.requestId = requestId;
		this.text = text;
		this.audioURLPath = audioURLPath;
		this.pictureURLPath = pictureURLPath;
		this.idLang1 = idLang1;
		this.idLang2 = idLang2;
		this.timePosted = timePosted;
		this.notification = notification;
		this.resourceIdLang1 = context.getResources().getIdentifier("lang_" + idLang1, "drawable", context.getPackageName());
		this.resourceIdLang2 = context.getResources().getIdentifier("lang_" + idLang2, "drawable", context.getPackageName());
	}
}
