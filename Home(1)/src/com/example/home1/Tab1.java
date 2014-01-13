package com.example.home1;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Tab1 extends Activity {
	public static ArrayList<Request> lista = null; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab1);
	
		populateList();
	}
	
	@Override
	public void onResume() {
	    super.onResume(); 	
	    populateList();
	}

	private void populateList() {
		if (lista != null) return;
		
		lista = new ArrayList<Request>();
		
		Uri.Builder builder = Uri.parse(Connection.WEB_SERVICE_URL).buildUpon();
		builder.appendPath("Requests");
		builder.appendPath("GetRequests");		
		
		WebServiceTask webTask = new WebServiceTask(Tab1.this, builder.build().toString(), "", "Loading", "Please wait...") {
			@Override
			protected String doInBackground(Void... params) {
				Connection.ProvjeriInicijalizaciju(activity);		
				String result = Connection.callWebService(url, postData);	
				
				if (result != null) {
					try {
						JSONArray jsonArray = new JSONArray(result);
						
						for(int i = 0; i < jsonArray.length(); i++) {
							JSONObject obj = jsonArray.getJSONObject(i);
							Request request = new Request(
								Tab1.this,
								Long.parseLong(obj.getString("userId")),
								Long.parseLong(obj.getString("requestId")),
								obj.getString("text"),
								((obj.get("audioExtension") == JSONObject.NULL) ? null : obj.getString("audio")),
								((obj.get("pictureExtension") == JSONObject.NULL) ? null : obj.getString("picture")),							
								obj.getInt("languageAsk"),
								obj.getInt("languageTold"),
								obj.getString("timePosted"),
								obj.getBoolean("notification")							
							);
							
							lista.add(request);
						}
					} catch(JSONException e) {
					}
				}
				
				return result;
			}
						
			@Override
			protected void onPostExecute(String result) {
				if (dialog != null) dialog.dismiss();
						
				if (result != null)
					populateListView();
				else					
					Connection.errorLogout(activity);
			}
		};		
		webTask.execute((Void)null);
	}

	private void populateListView() {
		ArrayAdapter<Request> adapter = new RequestListAdapter(this, R.layout.item_view_tab1, lista);
		ListView list = (ListView) findViewById(R.id.lvMy);
		list.setAdapter(adapter);
 	}	
}
	