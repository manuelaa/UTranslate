package com.example.home1;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;



public class LanguagesArrayAdapter extends ArrayAdapter<String> {
	
	private final Context context;
	private final ArrayList<String> values; //sva imena slika
	private final String prefix;
	
	public LanguagesArrayAdapter(Context context, ArrayList<String> values, String prefix){
		super(context, R.layout.languages, values);
		this.context = context;
		this.values = values;
		this.prefix = prefix;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.languages, parent, false);			
			TextView textView = (TextView) rowView.findViewById(R.id.label);
			ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
			textView.setText(values.get(position));
			int id = context.getResources().getIdentifier(prefix+values.get(position), "drawable", context.getPackageName());//uzimanje id resursa (slike)
			imageView.setImageResource(id);
		return rowView;
	}
}
