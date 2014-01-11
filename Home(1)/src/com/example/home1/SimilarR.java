package com.example.home1;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SimilarR extends Activity {
	//ovu listu ce popuniti RTranslation
	public static ArrayList<Similar> similarRequests = new ArrayList<Similar>();
	//ovo oznacava sto sam odabrao za grupiranje
	public static Long similarRequestId = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.similar_requests);
		populateListView();
		
		Button newRequest = (Button)findViewById(R.id.bNewG);
		newRequest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				similarRequestId = null;
				izadji();
			}
		});
	}
		
	@Override
	public void onBackPressed() {
		return;
	}
	
	private void populateListView() {
		ArrayAdapter<Similar> adapter = new SimilarListAdapter();
		ListView list=(ListView) findViewById(R.id.lvSimilar);
		list.setAdapter(adapter);
		
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				similarRequestId = similarRequests.get((int)arg3).requestId;
				izadji();
			}			
		});
 	}
	
	public void izadji() {
		setResult(RESULT_OK);
		finish();
	}
	
	private class SimilarListAdapter extends ArrayAdapter<Similar> {

		public SimilarListAdapter() {
			super(SimilarR.this, R.layout.item_view_similar, similarRequests);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//Make sure we have a view to work with (may have been given null)
			View itemView=convertView;
			if(itemView==null)
				itemView=getLayoutInflater().inflate(R.layout.item_view_similar, parent, false);
					
			Similar current = similarRequests.get(position);
			
			//tekst
			TextView written = (TextView) itemView.findViewById(R.id.item_txtSimilar);
			written.setText(current.text);
			
			//slika
			ImageView imageView = (ImageView) itemView.findViewById(R.id.ivSimilarFlag);
			imageView.setImageDrawable(getResources().getDrawable(current.resourceId));
	
			return itemView;
		}
	}
}

