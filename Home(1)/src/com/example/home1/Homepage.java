package com.example.home1;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class Homepage extends TabActivity implements OnClickListener {
	private static final String Tab1 = "My";
    private static final String Tab2 = "Others";
    private static final String Tab3 = "Translated";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.tabs);
				
		ImageButton iButton= (ImageButton)findViewById(R.id.ibNewMes);
		iButton.setOnClickListener(this);
		
		TabHost tabHost = getTabHost();
        
        // Tab1 Tab
        TabSpec tab_1 = tabHost.newTabSpec(Tab1);
        // Tab Icon
        tab_1.setIndicator(Tab1, getResources().getDrawable(R.drawable.ic_launcher));
        Intent tab1Intent = new Intent(this, Tab1.class);
        // Tab Content
        tab_1.setContent(tab1Intent);
         
        // Tab2 Tab
        TabSpec tab_2 = tabHost.newTabSpec(Tab2);
        tab_2.setIndicator(Tab2, getResources().getDrawable(R.drawable.ic_launcher));
        Intent tab2Intent = new Intent(this, Tab2.class);
        tab_2.setContent(tab2Intent);
       
        // Tab3 Tab
        //TabSpec tab_3 = tabHost.newTabSpec(Tab2);
        //tab_2.setIndicator(Tab3, getResources().getDrawable(R.drawable.ic_launcher));
        //Intent tab3Intent = new Intent(this, Tab3.class);
        //tab_2.setContent(tab2Intent);
        
     // Profile Tab
        TabSpec tab_3 = tabHost.newTabSpec(Tab3);
        tab_3.setIndicator(Tab3, getResources().getDrawable(R.drawable.ic_launcher));
        Intent profileIntent = new Intent(this, Tab3.class);
        tab_3.setContent(profileIntent);
        
        
        // Adding all TabSpec to TabHost
        tabHost.addTab(tab_1); // Adding tab1 tab
        tabHost.addTab(tab_2); // Adding tab2 tab
        //tabHost.addTab(tab_3); // Adding tab3 tab
        tabHost.addTab(tab_3);
		
		//TODO TESTIRANJE POZIVA WEB SERVISA
		Uri.Builder builder = Uri.parse(Connection.WEB_SERVICE_URL).buildUpon();
		//builder.appendPath("Login");
		//builder.appendPath("Get");
		builder.appendPath("Languages");
		builder.appendPath("GetLanguageName");						
		WebServiceTask webTask = new WebServiceTask(Homepage.this, builder.build().toString(), "{languageId:3}", "Loading", "Please wait...") {
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				
				if (json != null) 
					System.out.println("TESTIRANJE " + result); 
				else 
					System.out.println("TESTIRANJE NULL");
			}				
		};		
		webTask.execute((Void)null);
		
		/* TODO TESTING */
		Intent i = new Intent(this, Tr.class);
		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case (R.id.ibNewMes):
			Intent i = new Intent(this,RTranslation.class);
			startActivity(i);
			break;		
		}
	}
}
