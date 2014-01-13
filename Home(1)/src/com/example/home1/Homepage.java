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
	private static final String TabName1 = "My";
    private static final String TabName2 = "Others";
    private static final String TabName3 = "Translated";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.tabs);
		
		ocistiListeRequestova();
				
		ImageButton iButton= (ImageButton)findViewById(R.id.ibNewMes);
		iButton.setOnClickListener(this);
		
		TabHost tabHost = getTabHost();
        
        // Tab1 Tab
        TabSpec tab_1 = tabHost.newTabSpec(TabName1);
        // Tab Icon
        tab_1.setIndicator(TabName1, getResources().getDrawable(R.drawable.ic_launcher));
        Intent tab1Intent = new Intent(this, Tab1.class);
        // Tab Content
        tab_1.setContent(tab1Intent);
         
        // Tab2 Tab
        TabSpec tab_2 = tabHost.newTabSpec(TabName2);
        tab_2.setIndicator(TabName2, getResources().getDrawable(R.drawable.ic_launcher));
        Intent tab2Intent = new Intent(this, Tab2.class);
        tab_2.setContent(tab2Intent);
        
        // Tab3 Tab
        TabSpec tab_3 = tabHost.newTabSpec(TabName3);
        tab_3.setIndicator(TabName3, getResources().getDrawable(R.drawable.ic_launcher));
        Intent profileIntent = new Intent(this, Tab3.class);
        tab_3.setContent(profileIntent);
                
        // Adding all TabSpec to TabHost
        tabHost.addTab(tab_1);
        tabHost.addTab(tab_2);
        tabHost.addTab(tab_3);      
		
		//stvori direktorij za external cache
		if (CacheManager.EXTERNAL_STORAGE_PATH == null)
			CacheManager.createExternalDataDir();
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
			ocistiListeRequestova();
			break;		
		}
	}
	
	//kada izlazim iz ovog activityja treba ocistiti liste requestova tako da se opet 
	//ucitaju sljedeci put sa web servisa (jer moze proci dugo vremena kad se sljedeci put vratim)
	public static void ocistiListeRequestova() {
		Tab1.lista = null;
		Tab2.lista = null;
		Tab3.lista = null;
	}
}
