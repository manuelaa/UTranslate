package com.example.home1;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class Homepage extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.tabs);
				
		ImageButton iButton= (ImageButton)findViewById(R.id.ibNewMes);
		iButton.setOnClickListener(this);
		
		TabHost th=(TabHost)findViewById(R.id.tabhost);
		th.setup();
		
		//tab1
		TabSpec specs = th.newTabSpec("tab1");
		specs.setContent(R.id.tab3);
		specs.setIndicator("My");
		th.addTab(specs);
		
		//tab2
		specs = th.newTabSpec("tab2");
		specs.setContent(R.id.tab3);
		specs.setIndicator("Others");
		th.addTab(specs);
		
		//tab2
		specs = th.newTabSpec("tab3");
		specs.setContent(R.id.tab3);
		specs.setIndicator("Translated");
		th.addTab(specs);
		
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
		//Intent i = new Intent(this, Tr.class);
		//startActivity(i);
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
