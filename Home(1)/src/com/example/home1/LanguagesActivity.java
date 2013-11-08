package com.example.home1;


import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.app.ListActivity;

import java.io.File;
import java.io.FileDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.res.Resources;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;

public class LanguagesActivity extends ListActivity {

	static final String PREFIX = "lang_";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		Field[] fields = R.drawable.class.getFields();
		ArrayList<String> names = new ArrayList<String>(8);
		for (Field f:fields){
			if (f.getName().contains(PREFIX)){
				names.add(f.getName().replace(PREFIX, ""));
			}
		}
		
		setListAdapter(new LanguagesArrayAdapter(this, names, PREFIX));
		
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {


	}

}
