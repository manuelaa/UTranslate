package com.example.home1;

import java.io.File;
import java.security.Timestamp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

public class Request {
	public long userId;
	public long requestId;
	public String text;
	
	//slika i zvuk
	public DownloadMultimedia picture;
	public DownloadMultimedia audio;
	
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
		this.picture = new DownloadMultimedia(pictureURLPath);
		this.audio = new DownloadMultimedia(audioURLPath);
		this.idLang1 = idLang1;
		this.idLang2 = idLang2;
		this.timePosted = timePosted;
		this.notification = notification;
		this.resourceIdLang1 = context.getResources().getIdentifier("lang_" + idLang1, "drawable", context.getPackageName());
		this.resourceIdLang2 = context.getResources().getIdentifier("lang_" + idLang2, "drawable", context.getPackageName());
	}
}
