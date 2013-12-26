package com.example.home1;

import android.content.Context;

public class Lang {
	public String name;
	public String id;
	public int resourceId;
	public int checkboxId;	
	
	public Lang(String id, String name, Context context) {		
		this.name = name;
		this.id = id;
		this.checkboxId = 0;		
		this.resourceId = context.getResources().getIdentifier("lang_" + id, "drawable", context.getPackageName());
	}	
}
