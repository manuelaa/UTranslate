package com.example.home1;

import android.content.Context;
import android.widget.CheckBox;

public class Lang {
	public String name;
	public String id;
	public int resourceId;
	public boolean checked;	
	
	public Lang(String id, String name, Context context) {		
		this.name = name;
		this.id = id;		
		this.checked = false;	
		this.resourceId = context.getResources().getIdentifier("lang_" + id, "drawable", context.getPackageName());
	}	
}
