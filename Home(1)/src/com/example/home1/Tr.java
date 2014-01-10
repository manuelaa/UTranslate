package com.example.home1;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class Tr extends Activity {
	private List<Text> texts=new ArrayList<Text>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.translation_3);
		populateList();	
		populateListView();
	}

	private void populateList() { 
		// TODO Auto-generated method stub
		// public Text(String username, int idSlike, String text, int idLang1, int idLang2) 
		texts.add(new Text("Korisnik1", R.drawable.ic_launcher, "asydfhgfh", R.drawable.lang_10, R.drawable.lang_11));
		texts.add(new Text("Korisnik2", R.drawable.ic_launcher, "etrzui", R.drawable.lang_1, R.drawable.lang_12));
		texts.add(new Text("Korisnik3", R.drawable.ic_launcher, "gfghjkl", R.drawable.lang_7, R.drawable.lang_3));
	}
	
	private void populateListView() {
		// TODO Auto-generated method stub
		ArrayAdapter<Text> adapter = new ListAdapter();
		ListView list=(ListView) findViewById(R.id.listViewT);
		list.setAdapter(adapter);
 	}
	
	private class ListAdapter extends ArrayAdapter<Text> {

		public ListAdapter() {
			super(Tr.this, R.layout.item_view_translation, texts); //base class called
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//Make sure we have a view to work with (may have been given null)
			View itemView=convertView;
			if(itemView==null)
				itemView=getLayoutInflater().inflate(R.layout.item_view_translation, parent, false);
		
		
		Text currentText=texts.get(position);
		
		TextView userN = (TextView) itemView.findViewById(R.id.UserName);
		userN.setText(currentText.getUsername());

		
		ImageView userP = (ImageView)itemView.findViewById(R.id.userPhoto);
		userP.setImageResource(currentText.getId());

		TextView aText = (TextView) itemView.findViewById(R.id.askedText);
		aText.setText(currentText.getText());

		ImageView LangA = (ImageView)itemView.findViewById(R.id.langA);
		LangA.setImageResource(currentText.getIdLang1());
		
		ImageView LangB = (ImageView)itemView.findViewById(R.id.LangB);
		LangB.setImageResource(currentText.getIdLang2());
		
		/*
		RatingBar rb = (RatingBar) itemView.findViewById(R.id.ratingBar1);
		rb.setRating(0);
		*/
		return itemView;
	}
	}
}

