package com.example.home1;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class RequestListAdapter extends ArrayAdapter<Request> {
	private Activity activity;
	private int layoutId;
	private ArrayList<Request> lista;
		
	public RequestListAdapter(Activity activity, int layoutId, ArrayList<Request> lista) {
		super(activity, layoutId, lista);
		
		this.activity = activity;
		this.layoutId = layoutId;
		this.lista = lista;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Make sure we have a view to work with (may have been given null)
		View itemView = convertView;
		if(itemView == null)
			itemView = activity.getLayoutInflater().inflate(layoutId, parent, false);
		
		//uzmi element
		final Request current = lista.get(position);
				
		ImageView trokut = (ImageView)itemView.findViewById(R.id.item_ivTrokut);		
		trokut.setImageDrawable(activity.getResources().getDrawable(R.drawable.trokut_small));
		
		ImageView zastava1 = (ImageView)itemView.findViewById(R.id.item_ivLangA);
		zastava1.setImageDrawable(activity.getResources().getDrawable(current.resourceIdLang1));
		
		ImageView zastava2 = (ImageView)itemView.findViewById(R.id.item_ivLangB);
		zastava2.setImageDrawable(activity.getResources().getDrawable(current.resourceIdLang2));

		//tekst
		TextView makeText = (TextView) itemView.findViewById(R.id.item_tvText);
		makeText.setText(current.text);		
		if (current.notification) 
			makeText.setTypeface(null, Typeface.BOLD);
		else
			makeText.setTypeface(null, Typeface.NORMAL);
		
		//zvuk
		ImageView audioBtn = (ImageView)itemView.findViewById(R.id.item_ivMic);
		if (current.audio.URLPath == null)		
			audioBtn.setImageDrawable(activity.getResources().getDrawable(R.drawable.microphone_2));			
		else
			audioBtn.setImageDrawable(activity.getResources().getDrawable(R.drawable.microphone_1));
		
		audioBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//pusti zvuk
				current.audio.show(activity, "audio/*");				
			}
		});
		
		//photo
		ImageView photoBtn = (ImageView)itemView.findViewById(R.id.item_ivCam);
		if (current.picture.URLPath == null)		
			photoBtn.setImageDrawable(activity.getResources().getDrawable(R.drawable.cam_2));			
		else
			photoBtn.setImageDrawable(activity.getResources().getDrawable(R.drawable.cam_1));
	
		photoBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//pusti sliku
				current.picture.show(activity, "image/*");				
			}
		});

		
		//ako se klikne otvori request
		itemView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				Tr.request = current;
				Intent i = new Intent(activity, Tr.class);
				activity.startActivity(i);
				Homepage.ocistiListeRequestova();
			}			
		});
		
		return itemView;
	}
}
