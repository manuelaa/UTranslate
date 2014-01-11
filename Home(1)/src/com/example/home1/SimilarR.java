package com.example.home1;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SimilarR extends Activity {
	private List<Similar> texts=new ArrayList<Similar>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.similar_requests);
		populateList();	
		populateListView();
	}

	private void populateList() { 
		// TODO Auto-generated method stub
		texts.add(new Similar("bok"));
		texts.add(new Similar("bok bok"));
		texts.add(new Similar("hi"));
		texts.add(new Similar("ciao"));
		texts.add(new Similar("ola"));
		texts.add(new Similar("whatzap"));
	}
	
	private void populateListView() {
		// TODO Auto-generated method stub
		ArrayAdapter<Similar> adapter = new SimilarListAdapter();
		ListView list=(ListView) findViewById(R.id.lvSimilar);
		list.setAdapter(adapter);
 	}
	
	private class SimilarListAdapter extends ArrayAdapter<Similar> {

		public SimilarListAdapter() {
			super(SimilarR.this, R.layout.item_view_similar, texts); //base class called
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//Make sure we have a view to work with (may have been given null)
			View itemView=convertView;
			if(itemView==null)
				itemView=getLayoutInflater().inflate(R.layout.item_view_similar, parent, false);
		
		
		Similar currentText=texts.get(position);
		
		TextView written = (TextView) itemView.findViewById(R.id.item_txtSimilar);
		written.setText(currentText.getWritten());

		return itemView;
	}
	}
}

