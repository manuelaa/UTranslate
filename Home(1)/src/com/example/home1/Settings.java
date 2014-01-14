package com.example.home1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.home1.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Settings extends Activity implements OnClickListener {

	private List<Sett> Languages=new ArrayList<Sett>(); 			

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings);
		
		ImageButton imagebutton= (ImageButton) findViewById(R.id.ibBack);
		imagebutton.setOnClickListener(this); 
		
		GetId();
	
		populateLang();
		populateList();
	}

	
private void GetId(){
		
		Uri.Builder builder = Uri.parse(Connection.WEB_SERVICE_URL).buildUpon();
		builder.appendPath("User");
		builder.appendPath("GetCurrentId");
		
		WebServiceTask webTask = new WebServiceTask(Settings.this, builder.build().toString(), "", "Loading", "Please wait..."){
			@Override
			protected String doInBackground(Void... params) {
				Connection.ProvjeriInicijalizaciju(activity);		
				String result = Connection.callWebService(url, postData);	

				if (result != null) {	
					try {
						JSONObject obj = new JSONObject(result);
						long userId;
						userId= (long)(Integer.parseInt(obj.getString("userId")));
						
						//////////////////////////////////////////////////////////////////
						/*
						User korisnik = new User(Settings.this, userId);
						korisnik.getDataCurrentThread();
						String mail = korisnik.email;
						
						TextView tv = (TextView) findViewById (R.id.tv);
						tv.setText(mail);
						
						ImageView iv = (ImageView) findViewById(R.id.ibPhoto);
						korisnik.getPicture(iv);
							*/
						////////////////////////////////////
			 
						}
					catch (JSONException e) {
					}	
					
				}
				return result;
			}
			
			/*@Override
			protected void onPostExecute(String result) {
				if (dialog != null) dialog.dismiss();
						
				if (result != null)
					populateList();
				else					
					Connection.errorLogout(activity);
			}*/
		};	
		webTask.execute((Void)null);		
	}
	
	
private void populateLang() {
		
		//final List<Short> listaJezika = null;

		Uri.Builder builder = Uri.parse(Connection.WEB_SERVICE_URL).buildUpon();
		builder.appendPath("UserLanguages");
		builder.appendPath("GetUserLanguages");
		
		WebServiceTask webTask = new WebServiceTask(Settings.this, builder.build().toString(), "", "Loading", "Please wait...") {
			@Override
			protected String doInBackground(Void... params) {
				Connection.ProvjeriInicijalizaciju(activity);		
				String result = Connection.callWebService(url, postData);	
						
				
				if (result != null) {	
					try {
						JSONArray jsonArray = new JSONArray(result);
						Short idjezika;
						
						for(int i = 0; i < jsonArray.length(); i++) {
							JSONObject obj = jsonArray.getJSONObject(i);
							idjezika = Short.parseShort(obj.toString());
							//int pom = (int) idjezika;
						
							Languages.add(new Sett(Settings.this,(int)idjezika));
							 
							
						}
					}catch (JSONException e) {
					}	
					
				}
				return result;
			}
			
			@Override
			protected void onPostExecute(String result) {
				if (dialog != null) dialog.dismiss();
						
				if (result != null)
					populateList();
				else					
					Connection.errorLogout(activity);
			}
		};	
		webTask.execute((Void)null);		
	}
	
	
	
	private void populateList(){
		ArrayAdapter<Sett> adapter = new MySettingsAdapter ();
		ListView list2=(ListView) findViewById(R.id.SettingsListView);
		list2.setAdapter(adapter);
		
	}
	
	private class MySettingsAdapter extends ArrayAdapter<Sett>{
		
		public MySettingsAdapter(){
			super(Settings.this, R.layout.item_view_2, Languages);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			//Make sure we have a view to work with (may have been given null)
			View itemView=convertView;
			if(itemView==null)
				itemView=getLayoutInflater().inflate(R.layout.item_view_2, parent, false);
			
			// find lang to work with
			Sett currentLang=Languages.get(position);
			/*
			// fill the view
			ImageView imageView = (ImageView)itemView.findViewById(R.id.item_icon);
			imageView.setImageDrawable(getResources().getDrawable(currentLang.resourceId));
		
			//Lang
			TextView makeText = (TextView) itemView.findViewById(R.id.item_txtLang);
			makeText.setText(currentLang.name);
			
			ImageButton imageButton = (ImageButton) itemView.findViewById(R.id.ibMinus);
			*/
			return itemView;
		}
		
	}
	

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, Homepage.class);
		startActivity(i);
	}

}

