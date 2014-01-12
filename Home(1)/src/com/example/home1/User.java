package com.example.home1;

import java.util.ArrayList;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

//ova klasa je za opcenitog usera u sustavu
//na settingsima se ovo moze koristiti za dohvacanje osnovnih informacija
//ali se mora u activity dodati i lista jezika (ArrayList<Lang>)
//listu jezika ne dodavati ovdje!
public class User {
	public long userId;
	public String name;
	public String email;
	public String pictureURLPath = null;
	public Bitmap picture = null;
	
	public Activity activity;
	
	public User(Activity activity, long userId) {
		this.userId = userId;
		this.activity = activity;
	}
	
	//dohvaca podatke name, email i pictureURLPath
	//ne dohvaca sliku!
	//paziti uvijek da se dohvaca samo 1 user u svakom trenutku!
	public void getData() {
		Uri.Builder builder = Uri.parse(Connection.WEB_SERVICE_URL).buildUpon();
		builder.appendPath("User");
		builder.appendPath("GetUserDetails");						
		WebServiceTask webTask = new WebServiceTask(activity, builder.build().toString(), "{userId:" + String.valueOf(userId) + "}") {
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				
				if (result != null) {
					try {
						JSONObject obj = (JSONObject)json;					
						name = obj.getString("name");
						email = obj.getString("email");
						pictureURLPath = obj.getString("picture");
					} catch(JSONException e) {						
					}
				}
			}				
		};		
		webTask.execute((Void)null);
	}
	
	//dohvaca podatke name, email i pictureURLPath
	//ne dohvaca sliku!
	//koristi trenutnu dretvu
	public void getDataCurrentThread() {
		Uri.Builder builder = Uri.parse(Connection.WEB_SERVICE_URL).buildUpon();
		builder.appendPath("User");
		builder.appendPath("GetUserDetails");
		
		Connection.ProvjeriInicijalizaciju(activity);		
		String response = Connection.callWebService(builder.build().toString(), "{userId:" + String.valueOf(userId) + "}");
		
		if (response != null) {
			try {
				JSONObject obj = new JSONObject(response);					
				name = obj.getString("name");
				email = obj.getString("email");
				pictureURLPath = obj.getString("picture");
			} catch(JSONException e) {						
			}			
		}		
	}

	//nakon sto je pozvan getData dohvaca sliku
	//paziti uvijek da se dohvaca SAMO 1 slika u svakom trenutku!
	//1. parametar imageview gdje ide slika
	//ovo se koristi za settings kad se zeli slika samo 1 korisnika
	public void getPicture(final ImageView pictureImageView) {
		if (picture != null) picture.recycle();
		
		AsyncTask<Void, Void, Void> webTask = new AsyncTask<Void, Void, Void>() {
			private byte[] bytes;
			
			@Override
			protected Void doInBackground(Void... params) {
				bytes = DownloadFile.getFileFromCache(activity, pictureURLPath, "user_" + String.valueOf(userId) + ".dat");
				return null;	
			}
						
			@Override
			protected void onPostExecute(Void result) {
				if (bytes != null) {
					picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
					pictureImageView.setImageBitmap(picture);
				}
			}			
		};
		
		webTask.execute((Void)null);
	}
}
