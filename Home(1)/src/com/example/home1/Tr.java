package com.example.home1;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class Tr extends Activity {
	private ArrayList<Text> answers = new ArrayList<Text>();
	
	//koji request prikazujem
	public static Request request;	
	
	private ImageButton btnPicture;
	private ImageButton btnAudio;
	
	private ArrayAdapter<Text> adapter;
	
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
	}
	
	//ucitava request
	private void LoadRequest() {
		TextView reqText = (TextView)findViewById(R.id.tv_show);
		reqText.setText(request.text);
		
		btnPicture = (ImageButton) findViewById(R.id.ibCam);
		if (request.pictureURLPath == null)
			btnPicture.setImageDrawable(getResources().getDrawable(R.drawable.cam_2));
		else
			btnPicture.setImageDrawable(getResources().getDrawable(R.drawable.cam_1));	
		
		btnPicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
			}
		});

		btnAudio = (ImageButton) findViewById(R.id.ibSound);
		if (request.audioURLPath == null)
			btnAudio.setImageDrawable(getResources().getDrawable(R.drawable.sound_2));
		else
			btnAudio.setImageDrawable(getResources().getDrawable(R.drawable.sound_1));	
		
		btnAudio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
			}
		});

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
							newAnswer.timePosted = obj.getString("timePosted");
							newAnswer.rating = (float)obj.getDouble("rate");							
							if (obj.get("audioExtension") != JSONObject.NULL)
								newAnswer.audioURLPath = obj.getString("audio");

							newAnswer.user = new User(Tr.this, Integer.parseInt(obj.getString("user")));							
							answers.add(newAnswer);							
						} catch (JSONException e) {
						}						
					}
					
					if (!answers.isEmpty()) getUsersData();
				}
			}				
		};		
		webTask.execute((Void)null);
	}
	
	private void populateListView() {
		adapter = new ListAdapter();
		ListView list=(ListView) findViewById(R.id.listView1);
		list.setAdapter(adapter);		
 	}
	
	//dohvaca slike svih usera u listi odgovora 1 po 1 sa cachiranjem 
	private void getUsersData() {					
		AsyncTask<Void, Void, Void> webTask = new AsyncTask<Void, Void, Void>() {
			private byte[] bytes;			
			private User user;
			
			@Override
			protected Void doInBackground(Void... params) {
				for(int i = 0; i < answers.size(); i++) {
					user = answers.get(i).user;
					
					//dohvati podatke prvo
					Uri.Builder builder = Uri.parse(Connection.WEB_SERVICE_URL).buildUpon();
					builder.appendPath("User");
					builder.appendPath("GetUserDetails");		
					
					Connection.ProvjeriInicijalizaciju(Tr.this);		
					String response = Connection.callWebService(builder.build().toString(), "{userId:" + String.valueOf(user.userId) + "}");
					
					if (response != null) {
						try {
							JSONObject obj = new JSONObject(response);					
							user.name = obj.getString("name");
							user.email = obj.getString("email");
							user.pictureURLPath = obj.getString("picture");
						} catch(JSONException e) {						
						}			
					}	
					
					if (user.pictureURLPath == null) continue;
					
					//dohvati sliku
					if (user.picture != null) user.picture.recycle();
					bytes = DownloadFile.getFileFromCache(Tr.this, user.pictureURLPath, "user_" + String.valueOf(user.userId) + ".dat");					
					
					if (bytes != null) 
						user.picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);					
				}
								
				return null;	
			}
			
			@Override
			protected void onPostExecute(Void result) {
				populateListView();
			}
		};
		
		webTask.execute((Void)null);		
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
			userN.setText(current.user.name);	
			
			if (current.user.picture != null) {
				ImageView userP = (ImageView)itemView.findViewById(R.id.userPhoto);
				userP.setImageBitmap(current.user.picture);
			}
	
			TextView aText = (TextView)itemView.findViewById(R.id.askedText);
			aText.setText(current.text);
			
			/*
			ImageView LangA = (ImageView)itemView.findViewById(R.id.langA);			
			LangA.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
			
			ImageView LangB = (ImageView)itemView.findViewById(R.id.LangB);
			LangB.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));								
			*/
			
			/*
			RatingBar rb = (RatingBar) itemView.findViewById(R.id.ratingBar1);
			rb.setRating(0);
			*/
			
			return itemView;
		}
	}
}

