package com.example.home1;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
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
	private List<Text> answers = new ArrayList<Text>();
	
	//koji request prikazujem
	public static Request request;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		/* TODO TESTING */
		//(Context context, long userId, long requestId, String text, String audioURLPath, String pictureURLPath, int idLang1, int idLang2, String timePosted, boolean notification) {
		request = new Request(this, 1, 1, "tekst", null, null, 2, 4, "1.1.2014.", false);
		
		setContentView(R.layout.translation_3);
		LoadRequest();
		LoadAnswers();
		populateListView();
	}
	
	//ucitava request
	private void LoadRequest() {
		TextView reqText = (TextView)findViewById(R.id.tv_show);
		
	}
	
	//ucitava odgovore
	private void LoadAnswers() {
		Uri.Builder builder = Uri.parse(Connection.WEB_SERVICE_URL).buildUpon();
		builder.appendPath("Answers");
		builder.appendPath("GetRequestAnswers");						
		
		JSONObject obj = new JSONObject();
		try {
			obj.put("Id", request.requestId);
			obj.put("languageTold", request.idLang2);			
		} catch (JSONException e) {			
			return;
		}
				
		WebServiceTask webTask = new WebServiceTask(Tr.this, builder.build().toString(), obj.toString(), "Loading", "Please wait...") {
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				
				if (result != null) {
					JSONArray jsonArray = (JSONArray)json;
					for(int i = 0; i < jsonArray.length(); i++) {
						try {
							JSONObject obj = jsonArray.getJSONObject(i);
							Text newAnswer = new Text();						
							newAnswer.text = obj.getString("Text");
							//newAnswer.timePosted = Timestamp. obj.getString("timePosted");
							newAnswer.rating = (float)obj.getDouble("rate");							
							if (obj.get("audioExtension") != JSONObject.NULL)
								newAnswer.audioURLPath = obj.getString("audio");

							/* TODO user klasa */
							newAnswer.username = "test";
							
							answers.add(newAnswer);							
						} catch (JSONException e) {
							System.out.println("TESTIRANJE GRESKA " + e.getMessage());
						}						
					}					
				}
			}				
		};		
		webTask.execute((Void)null);
	}
	
	private void populateListView() {
		ArrayAdapter<Text> adapter = new ListAdapter();
		ListView list=(ListView) findViewById(R.id.listViewT);
		list.setAdapter(adapter);
 	}
	
	private class ListAdapter extends ArrayAdapter<Text> {

		public ListAdapter() {
			super(Tr.this, R.layout.item_view_translation, answers);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//Make sure we have a view to work with (may have been given null)
			View itemView = convertView;
			if(itemView == null)
				itemView = getLayoutInflater().inflate(R.layout.item_view_translation, parent, false);
		
		
			Text current = answers.get(position);
			
			TextView userN = (TextView)itemView.findViewById(R.id.UserName);
			userN.setText(current.username);	
			
			//ImageView userP = (ImageView)itemView.findViewById(R.id.userPhoto);
			//userP.setImageResource(currentId());
	
			TextView aText = (TextView)itemView.findViewById(R.id.askedText);
			aText.setText(current.text);
				
			ImageView LangA = (ImageView)itemView.findViewById(R.id.langA);			
			LangA.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
			
			ImageView LangB = (ImageView)itemView.findViewById(R.id.LangB);
			LangB.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
			
						
			/*
			RatingBar rb = (RatingBar) itemView.findViewById(R.id.ratingBar1);
			rb.setRating(0);
			*/
			return itemView;
		}
	}
}

