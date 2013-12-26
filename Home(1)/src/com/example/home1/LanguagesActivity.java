package com.example.home1;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
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
	private List<Lang> languages = new ArrayList<Lang>();
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
		//sagradi listu jezika	
		TypedArray resLangs = getResources().obtainTypedArray(R.array.languages);		
		for(int i = 0; i < resLangs.length(); i++) {
			int arrayId = resLangs.getResourceId(i, 0);
			if (arrayId > 0) {
				String[] lang = getResources().getStringArray(arrayId);
				languages.add(new Lang(lang[0], lang[1], this));
			}
		}
		resLangs.recycle();
	}

	private void populateListView() {
		// TODO Auto-generated method stub
		ArrayAdapter<Lang> adapter = new LanguageListAdapter();
		ListView list = (ListView) findViewById(R.id.LangListView);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		list.setAdapter(adapter);

		//list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	private class LanguageListAdapter extends ArrayAdapter<Lang> {
		public LanguageListAdapter() {
			super(LanguagesActivity.this, R.layout.item_view, languages);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// Make sure we have a view to work with (may have been given null)			
			View itemView = convertView;
			if (itemView == null)
				itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);

			// find lang to work with
			final Lang currentLang = languages.get(position);
			
			// fill the view	
			ImageView imageView = (ImageView) itemView.findViewById(R.id.item_icon);
			imageView.setImageResource(currentLang.resourceId);

			final ImageView imageView1 = (ImageView) itemView.findViewById(R.id.ibTest);

			// Lang
			TextView makeText = (TextView) itemView.findViewById(R.id.item_txtLang);
			makeText.setText(currentLang.name);

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
			        newB.setText(currentLang.name);
			        ImageButton newImB=(ImageButton)findViewById(R.id.ibTest);
			        newImB.setImageResource(currentLang.resourceId);			      
			        ListView list = (ListView) findViewById(R.id.LangListView);
					 if (mLastCorrectPosition != -1) {
	                    	//Lang lastLang = (Languages.get(mLastCorrectPosition));
	                    	//CheckBox cb=(CheckBox)findViewById(lastLang.getcheckboxID());
	                    	newB.setText(languages.get(mLastCorrectPosition).name+"last");
	                    	
	                    	if (checkB.isChecked()) {
	                    		 newB.setText(languages.get(mLastCorrectPosition).name+"-1");
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
		 			        newB.setText(languages.get(position).name);
		 			       }
	                        
		              checked = list.getCheckedItemPositions();
		              mLastCorrectPosition = position;
	             }  
	           });


			return itemView;
		}
	}
}


	

