package com.example.home1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

public class LanguagesList extends Activity implements OnClickListener{
//	ImageButton imagebutton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_languages);
		
		ImageButton imagebutton = (ImageButton) findViewById(R.id.imBack);
		imagebutton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent i = new Intent(this,RTranslation.class);
		startActivity(i);
	}

}
