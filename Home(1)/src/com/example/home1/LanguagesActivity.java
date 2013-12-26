package com.example.home1;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LanguagesActivity extends Activity /*implements OnCheckedChangeListener*/ {
	private List<Lang> Languages = new ArrayList<Lang>();
	public String choosen;

	public static int mLastCorrectPosition = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.languages_list);
		
		Button backB= (Button) findViewById(R.id.bOK);
		backB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		populateListView();
		populateLangList();
		
		
	}

	private void populateLangList() {
		// TODO Auto-generated method stub
		Field[] fields = R.drawable.class.getFields();
		final String PREFIX = "lang_";

		int i=0;
		for (Field f : fields) {
				
				if(f.getName()==null) break;
				if (f.getName().contains(PREFIX)) {
					Languages.add(new Lang(
							f.getName().replace(PREFIX, ""), 
							getApplicationContext().getResources().getIdentifier(f.getName(),"drawable",getApplicationContext().getPackageName()),
							i));
				i++;
			}
		}
	}

	private void populateListView() {
		// TODO Auto-generated method stub
		ArrayAdapter<Lang> adapter = new MyListAdapter();
		ListView list = (ListView) findViewById(R.id.LangListView);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		list.setAdapter(adapter);

		//list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	private class MyListAdapter extends ArrayAdapter<Lang> {

		public MyListAdapter() {
			super(LanguagesActivity.this, R.layout.item_view, Languages); // base
																		// class
																			// called
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// Make sure we have a view to work with (may have been given null)
			
			View itemView = convertView;
			if (itemView == null)
				itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);

			// find lang to work with
			final Lang currentLang = Languages.get(position);
			
			// fill the view
			ImageView imageView = (ImageView) itemView.findViewById(R.id.item_icon);
			imageView.setImageResource(currentLang.getIconID());

			final ImageView imageView1 = (ImageView) itemView.findViewById(R.id.ibTest);

			// Lang
			TextView makeText = (TextView) itemView.findViewById(R.id.item_txtLang);
			makeText.setText(currentLang.getName());

			// checkbox
			final CheckBox checkB = (CheckBox) itemView.findViewById(R.id.CheckBox);
			final CheckBox cb = (CheckBox) itemView.findViewById(R.id.CheckBox);
			//checkB.setSelected(false);
			
			
			checkB.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {	
					SparseBooleanArray checked;
					long[] checked_id;
					//Lang currentLang = Languages.get(position);
					Button newB = (Button)findViewById(R.id.bOK);
			        newB.setText(currentLang.getName());
			        ImageButton newImB=(ImageButton)findViewById(R.id.ibTest);
			        newImB.setImageResource(currentLang.getIconID());			      
			        ListView list = (ListView) findViewById(R.id.LangListView);
					 if (mLastCorrectPosition != -1) {
	                    	//Lang lastLang = (Languages.get(mLastCorrectPosition));
	                    	//CheckBox cb=(CheckBox)findViewById(lastLang.getcheckboxID());
	                    	newB.setText(Languages.get(mLastCorrectPosition).getName()+"last");
	                    	
	                    	if (checkB.isChecked()) {
	                    		 newB.setText(Languages.get(mLastCorrectPosition).getName()+"-1");
	                    		// ((CheckBox)v).setChecked(true);
	                    		// ((CheckBox)cb).setChecked(false);
	                    		 
	                    		 	int len = list.getCount();
	  		 			       		checked = list.getCheckedItemPositions();
	  		 			       		checked_id=list.getCheckedItemIds();
	  		 			       		for (int i = 0; i < len; i++){
	  		 			       			if (checked.get(i) && i!=position) {
	  		 			       				CheckBox cb=(CheckBox)findViewById((int)checked_id[i]);
	  		 			       				((CheckBox)cb).setChecked(false);
	  		 			       				//CheckBox cb1=(CheckBox)findViewById(position);
	  		 			       			   //((CheckBox)cb1).setChecked(true);
	  		 			        // do whatever you want with the checked item
	  		 			       			}
	                        }
	                    }

					 	}
	                   	else {
		 			        newB.setText(Languages.get(position).getName());
		 			       }
	                        
		              checked = list.getCheckedItemPositions();
		              mLastCorrectPosition = position;
	             }   
	           });


			return itemView;
		}
	}
}


	

