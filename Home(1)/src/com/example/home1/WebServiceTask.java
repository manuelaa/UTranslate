package com.example.home1;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

//ovo je AsyncTask koji se koristi za pozivanje metoda web servisa (nakon logiranja)
//treba ga se koristiti kod pozivanja s tim da se overridea onPostExecute() u kojem se radi obrada
//jedino je bitno da se pozove i onPostExecute bazne klase!
public class WebServiceTask extends AsyncTask<Void, Void, String> {			
	private ProgressDialog dialog = null;
	private Activity activity = null;
	protected String postData = null;
	protected String url = null;	
	protected JSONObject json = null;
	
	public WebServiceTask(Activity activity, String url, String postData, String dialogTitle, String dialogMessage) {
		if (dialogTitle != null) {
			dialog = new ProgressDialog(activity);
			dialog.setTitle(dialogTitle);
			dialog.setMessage(dialogMessage);
			dialog.setCancelable(false);
			dialog.setIndeterminate(true);
		}
		this.postData = postData;
		this.url = url;
		this.activity = activity;
	}

	public WebServiceTask(Activity activity, String url, String dialogTitle, String dialogMessage) {		
		this(activity, url, null, dialogTitle, dialogMessage);
	}

	public WebServiceTask(Activity activity, String url) {		
		this(activity, url, null, null, null);
	}
	
	public WebServiceTask(Activity activity, String url, String postData) {		
		this(activity, url, postData, null, null);
	}

	@Override
	protected void onPreExecute() {
		if (dialog != null) dialog.show();
	}
	
	@Override
	protected String doInBackground(Void... params) {
		Connection.ProvjeriInicijalizaciju(activity);		
		return Connection.callWebService(url, postData);	
	}
				
	@Override
	protected void onPostExecute(String result) {
		if (dialog != null) dialog.dismiss();
		
		if (result != null)
			try {
				json = new JSONObject(result);
			} catch (JSONException e) {				
				Connection.errorLogout(activity);
			}
		else
			Connection.errorLogout(activity);
	}
};	