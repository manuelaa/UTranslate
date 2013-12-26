package com.example.home1;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LanguagesActivity extends Activity {
	//sve je static da bi RTranslation mogao pristupiti tome
	public static ArrayList<Lang> languages;
	public static ArrayList<Lang> checkedLanguages;
	public static boolean multiSelectEnabled;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.languages_list);
		
		populateListView();	

		Button backB = (Button)findViewById(R.id.bOK);
		backB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pokusajIzaci();			}
		});		
	}
	
	public void pokusajIzaci() {
		//ako nije odabrao niti jedan jezik nema izlaza
		if (!checkedLanguages.isEmpty()) { 
			setResult(RESULT_OK);
			finish();
		}		
	}
	
	@Override
	public void onBackPressed() {
		pokusajIzaci();	
	}

	private void populateListView() {
		ArrayAdapter<Lang> adapter = new LanguageListAdapter();
		ListView list = (ListView) findViewById(R.id.LangListView);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		list.setAdapter(adapter);
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
				itemView = LanguagesActivity.this.getLayoutInflater().inflate(R.layout.item_view, parent, false);

			//uzmi lang
			final Lang currentLang = languages.get(position);
			
			//slika	
			ImageView imageView = (ImageView) itemView.findViewById(R.id.item_icon);
			imageView.setImageDrawable(getResources().getDrawable(currentLang.resourceId));

			//tekst
			TextView langText = (TextView) itemView.findViewById(R.id.item_txtLang);
			langText.setText(currentLang.name);
		
			//checkbox
			final CheckBox checkBox = (CheckBox)itemView.findViewById(R.id.CheckBox);
			checkBox.setChecked(currentLang.checked);
						
			checkBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (checkBox.isChecked()) {
						if (!multiSelectEnabled) {
							//treba disableati ostale checkboxove jer multiselect nije dozvoljen
							for(Lang lang : checkedLanguages)
								lang.checked = false;						
							
							checkedLanguages.clear();
						}
					
						//oznaci checkiranim
						currentLang.checked = true;
						checkedLanguages.add(currentLang);						
					} else {
						currentLang.checked = false;
						checkedLanguages.remove(currentLang);						
					}

					//refresh
					notifyDataSetChanged();
				}
			});
			
			return itemView;
		}
	}
}


	

