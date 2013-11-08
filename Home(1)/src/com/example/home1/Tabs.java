package com.example.home1;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class Tabs extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tabs);
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
		specs.setIndicator("My");
		th.addTab(specs);
		
		//tab2
		specs = th.newTabSpec("tab3");
		specs.setContent(R.id.tab3);
		specs.setIndicator("My");
		th.addTab(specs);
		
	}

	
}
