package com.example.home1;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Tab1 extends Activity {
	private List<TabText> lista=new ArrayList<TabText>(); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab1);
	
		populateList(); 
		populateListView();
	}

	private void populateList() {
		// TODO Auto-generated method stub
		lista.add(new TabText("boook", 0, 0));
		lista.add(new TabText("Ciao", 0,0));
		lista.add(new TabText("Bok bok", 0,0));
		lista.add(new TabText("BOK", 0,0));
		lista.add(new TabText("Boooook", 0,0));
		
	}

	private void populateListView() {
		// TODO Auto-generated method stub
		ArrayAdapter<TabText> adapter = new MyListAdapter();
		ListView list=(ListView) findViewById(R.id.lvMy);
		list.setAdapter(adapter);
 	}
	
	
	private class MyListAdapter extends ArrayAdapter<TabText> {

		public MyListAdapter() {
			super(Tab1.this, R.layout.item_view_tab1, lista); //base class called
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//Make sure we have a view to work with (may have been given null)
			View itemView=convertView;
			if(itemView==null)
				itemView=getLayoutInflater().inflate(R.layout.item_view_tab1, parent, false);
			
			// find the car to work with
			TabText currentlista=lista.get(position);
			
			
			ImageView trokut = (ImageView)itemView.findViewById(R.id.item_ivTrokut);
			trokut.setImageResource(R.drawable.trokut_small);
			
			ImageView zastava1 = (ImageView)itemView.findViewById(R.id.item_ivLangA);
			zastava1.setImageResource(R.drawable.lang_10);
			
			ImageView zastava2 = (ImageView)itemView.findViewById(R.id.item_ivLangB);
			zastava2.setImageResource(R.drawable.lang_3);

		
			//make
			TextView makeText = (TextView) itemView.findViewById(R.id.item_tvText);
			makeText.setText(currentlista.getText());

		
			return itemView;
		}
		
		}
	}
	