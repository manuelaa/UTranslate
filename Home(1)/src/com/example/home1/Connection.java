package com.example.home1;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Button;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class Connection {
	public static final String WEB_SERVICE_URL = "http://192.168.0.20:48917/api/";
	private static final String GOOGLE_SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email";
	private static final int REQUEST_CODE_GOOGLE_AUTH_ERROR = 1001;
	private static final int REQUEST_CODE_PLAY_SERVICE_ERROR = 1002;
	private static final String CONNECTION_FILE = "ConnPrefs.dat";
	
	private static Activity loginActivity;
	private static Button loginButton;
	private static ProgressDialog loginProgress;
	
	//pamti nas token
	private static String token;
	
	//pamti odabrani email za Google login
	private static String googleMail;
	//pamti Googleov token
	private static String googleToken;
	
	//da li se nakon logina treba pokazati prozor sa errorom
	//posto dvije greske oko Google logina vec imaju vlastiti handling onda koristim ovu varijablu
	//da ne pokazujem gresku 2 puta
	private static boolean showLoginError = true;
		
	//provjerava da li korisnik ima potreban library i sprema login activity za kasnije koristenje
	public static void initialize(Activity activity)
	{
		loginActivity = activity;
		loginButton = (Button)loginActivity.findViewById(R.id.bLogin);
		
		int res = GooglePlayServicesUtil.isGooglePlayServicesAvailable(loginActivity);
		
		if (res != ConnectionResult.SUCCESS)
			GooglePlayServicesUtil.getErrorDialog(res, loginActivity, REQUEST_CODE_PLAY_SERVICE_ERROR);
		
		//provjeri da li sam vec logiran od prije
		//sejvaj postavke da se moze obnoviti connection kad se app ponovno pokrene
		try {
			FileInputStream fis = loginActivity.openFileInput(CONNECTION_FILE);
			ObjectInputStream in = new ObjectInputStream(fis);
			
			boolean connected = in.readBoolean();			
			if (connected) {
				googleMail = in.readUTF();
				googleToken = in.readUTF();
				token = in.readUTF();
			}
			
			in.close();
			fis.close();
			
			if (connected) redirectToHomepage();
		} catch (IOException ex) {}
	}
	
	private static void redirectToHomepage()
	{
		Intent i = new Intent(loginActivity, Homepage.class);
		loginActivity.startActivity(i);
		loginActivity.finish();
	}
	
	//funkcija za login
	//prima odabrani email za account
	//poziva ju Login Activity
	public static void login(String mail)	
	{			
		loginButton.setEnabled(false);		
		googleMail = mail;
		showLoginError = true;
		
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPreExecute() {
				loginProgress = new ProgressDialog(loginActivity);
				loginProgress.setTitle("Login");
				loginProgress.setMessage("Please wait...");
				loginProgress.setCancelable(false);
				loginProgress.setIndeterminate(true);
				loginProgress.show();
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				googleToken = getGoogleToken();
				token = getToken();
				return (Void)null;	
			}
			
			@Override
			protected void onPostExecute(Void result) {					
				loginButton.setEnabled(true);
				loginProgress.dismiss();
				
				//TODO MAKNI DA BI LOGIN FUNKCIONIRAO
				//OVO JE TU DA SE NE MORA LOGIRATI DOK WEB SERVIS JOS NIJE GOTOV
				token = "nekaj";
				
				if (token != null) {
					//login je uspio
					System.out.println("TESTIRANJE - LOGIN USPJESAN, TOKEN: " + token);
					
					//sejvaj postavke da se moze obnoviti connection kad se app ponovno pokrene
					try {
						FileOutputStream fos = loginActivity.openFileOutput(CONNECTION_FILE, Context.MODE_PRIVATE);
						ObjectOutputStream out = new ObjectOutputStream(fos);
						out.writeBoolean(true);
						out.writeUTF(googleMail);
						out.writeUTF(googleToken);
						out.writeUTF(token);						
						out.flush();
						out.close();
						fos.close();			
					} catch (IOException ex) {}
														
					//prebaci na homepage
					redirectToHomepage();
				} else {
					//login nije uspio - pokazi error (ako nije vec handlan)
					if (showLoginError) {
						AlertDialog.Builder builder = new AlertDialog.Builder(loginActivity);
						builder.setMessage("Error: Cannot connect to server!")
						       .setCancelable(false)
						       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
						           public void onClick(DialogInterface dialog, int id) {
						           }
						       });		
						AlertDialog alert = builder.create();
						alert.show();		
					}
				}
			}
		};
		task.execute((Void)null);
	}
	
	//vraca Google token ako je moguce
	//ne pozivati izravno nego uvijek preko background threada
	//ako ne uspije vraca null
	private static String getGoogleToken() {
		try {
			return GoogleAuthUtil.getToken(loginActivity, googleMail, GOOGLE_SCOPE);
		} catch (GooglePlayServicesAvailabilityException ex) {
	        //GooglePlayServices.apk je disejblan, star ili ne postoji
			//teoretski se ne bi trebalo dogoditi jer se to provjerava na samom pocetku
			showLoginError = false;
			GooglePlayServicesUtil.getErrorDialog(ex.getConnectionStatusCode(), loginActivity, REQUEST_CODE_PLAY_SERVICE_ERROR);			
		} catch (UserRecoverableAuthException ex) {
			//problem sa autorizacijom
			//pokazi korisniku prozor kako bi to mogao ispraviti
			showLoginError = false;
	        loginActivity.startActivityForResult(ex.getIntent(), REQUEST_CODE_GOOGLE_AUTH_ERROR);
	    } catch (GoogleAuthException ex) {
	        //fatal error
	    } catch (IOException ex) {
	    	//nije dobio odgovor
	    }
		
		return null;
	}
	
	//vraca nas token sa web servisa
	//ne pozivati izravno nego uvijek preko background threada
	//ako ne uspije vraca null	
	private static String getToken()
	{			
		if (googleToken == null) return null;
				
		Uri.Builder builder = Uri.parse(WEB_SERVICE_URL).buildUpon();
		builder.appendPath("login");
		builder.appendQueryParameter("googleToken", googleToken);		
						
		try {
			String response = getResponse(builder.build().toString());			
			if (response == null) return null;

		    JSONObject json = new JSONObject(response);
		    return json.getString("token");	
		} catch (JSONException ex) {
			return null;
		}       
	}
	
	private static String readOutput(InputStream is) throws IOException
	{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();            
        byte[] data = new byte[2048];
        
        int len = 0;
        while ((len = is.read(data, 0, data.length)) >= 0) {
            bos.write(data, 0, len);
        }
        
        String response = new String(bos.toByteArray(), "UTF-8");        

        bos.close();
        is.close();
                   
        return response;		
	}
	
	//poziva URL i vraca response kao string
	//ako dodje do bilo kakve greske vraca null
	private static String getResponse(String urlPath)
	{	
		try {
			URL url = new URL(urlPath);					
	        HttpURLConnection con = (HttpURLConnection)url.openConnection();
	        
	        int code = con.getResponseCode();
	        
	        if (code == 200) {
	            InputStream is = con.getInputStream();            
	            String response = readOutput(is);
	            is.close();	                       
	            return response;
	        } else
	        	return null;
		} catch(IOException ex) {
			return null;		
		}
	}
	
	//poziva URL i vraca response kao string
	//razlika u odnosu na getResponse je da dodaje nas token u http headere
	//ako dodje do bilo kakve greske vraca null
	private static String getResponseWithToken(String urlPath)
	{	
		if (token == null) return null;
		
		try {
			URL url = new URL(urlPath);					
	        HttpURLConnection con = (HttpURLConnection)url.openConnection();
	        con.addRequestProperty("Authorization", token);
	        
	        int code = con.getResponseCode();
	        
	        if (code == 200) {
	            InputStream is = con.getInputStream();            
	            String response = readOutput(is);
	            is.close();	                       
	            return response;
	        } else
	        	return null;
		} catch(IOException ex) {
			return null;		
		}
	}
	
	//ovo je glavna funkcija koja se koristi za pozivanje naseg web servisa unutar aplikacije
	//ako nam je token istekao pokusati ce ga obnoviti i opet pozvati web servis
	//ako ne uspije obnoviti vraca null
	//ne pozivati izravno nego uvijek preko background threada
	public static String callWebService(String urlPath)
	{
		String response = getResponseWithToken(urlPath);
		
		if (response == null) {
			if (googleToken != null) GoogleAuthUtil.invalidateToken(loginActivity, googleToken);
			googleToken = getGoogleToken();
			token = getToken();
			response = getResponseWithToken(urlPath);			
		}
		
		return response;
	}
	

	//ako se negdje kasnije dogodila greska - callWebService je vratio null nakon oba 2 pokusaja
	//sa ovom funkcijom se pokazuje korisniku da je doslo do greske i baca ga se nazad na login
	public static void errorLogout(final Activity activity) 
	{
		//spremi da vise nije connectan
		try {
			FileOutputStream fos = activity.openFileOutput(CONNECTION_FILE, Context.MODE_PRIVATE);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeBoolean(false);
			out.writeUTF("");
			out.writeUTF("");
			out.writeUTF("");						
			out.flush();
			out.close();
			fos.close();			
		} catch (IOException ex) {}
		
		//pokazi alert
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage("Error: Cannot connect to server!")
		       .setCancelable(false)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   Intent i = new Intent(activity, Login.class);
		        	   activity.startActivity(i);
		        	   activity.finish();
		           }
		       });		
		AlertDialog alert = builder.create();
		alert.show();		
	}
}
