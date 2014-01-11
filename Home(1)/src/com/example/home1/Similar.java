package com.example.home1;

import android.content.Context;

public class Similar {
	public long requestId;
	public String text;
	
	//za prikaz zastavice jezika na koji ide
	public int resourceId;
	
	public Similar(Context context, long requestId, String text, String langId){		
		this.requestId = requestId;
		this.text = text;
		this.resourceId = context.getResources().getIdentifier("lang_" + langId, "drawable", context.getPackageName());
	}
}
