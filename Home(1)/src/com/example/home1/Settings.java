package com.example.home1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class Settings extends Activity implements OnClickListener {
//	ImageButton imagebutton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings);
		ImageButton imagebutton= (ImageButton) findViewById(R.id.ibBack);
		imagebutton.setOnClickListener(this); 
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, Homepage.class);
		startActivity(i);
	}

}