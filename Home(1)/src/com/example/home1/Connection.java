package com.example.home1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

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
import android.os.Environment;
import android.util.Base64;
import android.widget.Button;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.internal.ex;

public class Connection {
	public static final String WEB_SERVICE_URL = "http://nihao.fer.hr/UTranslate/api/";
	//public static final String WEB_SERVICE_URL = "http://192.168.0.20:51505/api/";	
	
	private static final String GOOGLE_SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email";
	private static final int REQUEST_CODE_GOOGLE_AUTH_ERROR = 1001;
	private static final int REQUEST_CODE_PLAY_SERVICE_ERROR = 1002;
	private static final String CONNECTION_FILE = "ConnPrefs.dat";
	
	private static Activity loginActivity = null;
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
	
	//pamti response code zadnje pozvane funkcije web servisa
	//ako je bio IOException biti ce 0
	//na taj nacin se zna da je greska bila u konekciji a da je token vjerojatno bio ispravan
	private static int lastResponseCode = 0;
	
	//pamti da li se obavila inicijalizacija
	//sluzi zato da ako veza pukne prilikom vracanja na Login activity ne ucita iz filea
	//da je ostao logiran i pokusa se logirati opet i na taj nacin pokrene beskonacnu petlju
	public static boolean initialized = false;
		
	//provjerava da li korisnik ima potreban library i sprema login activity za kasnije koristenje
	public static void initialize(Activity activity)
	{
		loginActivity = activity;
		loginButton = (Button)loginActivity.findViewById(R.id.bLogin);
		
		int res = GooglePlayServicesUtil.isGooglePlayServicesAvailable(loginActivity);
		
		if (res != ConnectionResult.SUCCESS)
			GooglePlayServicesUtil.getErrorDialog(res, loginActivity, REQUEST_CODE_PLAY_SERVICE_ERROR);
		
		//ako je ovo zaista prva inicijalizacija, provjeri da li sam vec logiran od prije
		//sejvaj postavke da se moze obnoviti connection kad se app ponovno pokrene
		if (!initialized) {
			initialized = true;
			
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
				
				if (token != null && !token.isEmpty()) {
					//login je uspio
					
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
					} catch (IOException ex) {
					}
														
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
		builder.appendPath("Login");
		builder.appendPath("Login");
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
	//ako je postData == null koristi cisti GET, inace dodaje postData i koristi POST
	private static String getResponse(String urlPath, String postData)
	{	
		HttpURLConnection con = null;
		URL url = null;
		
		try {
			url = new URL(urlPath);
		} catch(MalformedURLException ex) {
			lastResponseCode = 0;
			return null;
		}
				
		try {
	        con = (HttpURLConnection)url.openConnection();	        	        
	        con.setInstanceFollowRedirects(false);
	        con.setUseCaches(false);
	        
	        if (postData != null) {	        	
		        con.setDoInput(true);
		        con.setDoOutput(true);
	        	byte[] data = postData.getBytes(Charset.forName("UTF-8"));
	        	con.setRequestMethod("POST"); 
	        	con.setRequestProperty("Content-Type", "application/json"); 
	        	con.setRequestProperty("charset", "utf-8");
	        	con.setRequestProperty("Content-Length", Integer.toString(data.length));
		        DataOutputStream dataOutputStream = new DataOutputStream(con.getOutputStream());
		        dataOutputStream.write(data);		        
		        dataOutputStream.close();
	        }
	        
			lastResponseCode = con.getResponseCode();
				        
	        if (lastResponseCode == 200) {
	            InputStream is = con.getInputStream();            
	            String response = readOutput(is);
	            is.close();
	            con.disconnect();
	            return response;
	        } else {
	        	con.disconnect();
	        	return null;
	        }	        	
		} catch(IOException ex) {
			if (!ex.getMessage().contains("connect failed")) {
				try {
					lastResponseCode = con.getResponseCode();
				} catch(IOException ex2) {
					lastResponseCode = 0;
				}
			} else
				lastResponseCode = 0;		
			
			if (con != null) con.disconnect();
			return null;		
		}
	}
	
	//kao i getResponse ali uvijek koristi cisti GET
	private static String getResponse(String urlPath) {
		return getResponse(urlPath, null);	
	}
	
	
	//poziva URL i vraca response kao string
	//razlika u odnosu na getResponse je da dodaje nas token u http headere
	//ako dodje do bilo kakve greske vraca null
	//ako je postData == null koristi cisti GET, inace dodaje postData i koristi POST
	private static String getResponseWithToken(String urlPath, String postData)
	{	
		if (token == null) {
			lastResponseCode = 0;
			return null;
		}
		
		HttpURLConnection con = null;
		URL url = null;
		
		try {
			url = new URL(urlPath);
		} catch(MalformedURLException ex) {
			lastResponseCode = 0;
			return null;
		}
						
		try {
			con = (HttpURLConnection)url.openConnection();
	        con.addRequestProperty("Authorization", token);       
	        con.setInstanceFollowRedirects(false);
	        con.setUseCaches (false);
	        	        
	        if (postData != null) {
	        	con.setDoInput(true);
		        con.setDoOutput(true);	 
	        	byte[] data = postData.getBytes(Charset.forName("UTF-8"));
	        	con.setRequestMethod("POST"); 
	        	con.setRequestProperty("Content-Type", "application/json"); 
	        	con.setRequestProperty("charset", "utf-8");
	        	con.setRequestProperty("Content-Length", Integer.toString(data.length));
		        DataOutputStream dataOutputStream = new DataOutputStream(con.getOutputStream());		        
		        dataOutputStream.write(data);		        
		        dataOutputStream.close();
	        }
	        
	        lastResponseCode = con.getResponseCode();
	        
	        if (lastResponseCode == 200) {
	            InputStream is = con.getInputStream();            
	            String response = readOutput(is);
	            is.close();
	            con.disconnect();
	            return response;
	        } else {
	        	con.disconnect();
	        	return null;
	        }
		} catch(IOException ex) {	
			if (!ex.getMessage().contains("connect failed")) {
				try {
					lastResponseCode = con.getResponseCode();
				} catch(IOException ex2) {
					lastResponseCode = 0;
				}
			} else
				lastResponseCode = 0;
			
			if (con != null) con.disconnect();			
			return null;		
		}
	}
	
	//kao i getResponseWithToken ali uvijek koristi cisti GET
	private static String getResponseWithToken(String urlPath) {
		return getResponseWithToken(urlPath, null);
	}
	
	//poziva URL i vraca response kao string
	//razlika u odnosu na getResponse je da dodaje nas token u http headere
	//ako dodje do bilo kakve greske vraca null
	//kao i ostale dvije getResponseWithToken samo sto ova koristi postData iz datoteke
	public static String getResponseWithTokenPostFile(String urlPath, File filePostData)
	{	
		if (token == null) {
			lastResponseCode = 0;
			return null;
		}
		
		HttpURLConnection con = null;
		URL url = null;
		
		try {
			url = new URL(urlPath);
		} catch(MalformedURLException ex) {
			lastResponseCode = 0;
			return null;
		}
						
		try {			
			con = (HttpURLConnection)url.openConnection();
	        con.addRequestProperty("Authorization", token);       
	        con.setInstanceFollowRedirects(false);
	        con.setUseCaches (false);
	        con.setDoInput(true);
		    con.setDoOutput(true);	
        	con.setRequestMethod("POST"); 
        	con.setRequestProperty("Content-Type", "application/json"); 
        	con.setRequestProperty("charset", "utf-8");
        	con.setRequestProperty("Content-Length", Long.toString(filePostData.length()));        	
        	
	        BufferedOutputStream bos = new BufferedOutputStream(con.getOutputStream());		        		        
		    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePostData));
			byte[] bytes = new byte[300000];			
			int count = 0;
			while(true) {
				count = bis.read(bytes);
				if (count == -1) break;
				bos.write(bytes, 0, count);
				bos.flush();
			}
			bis.close();
			bos.close();
			
	        lastResponseCode = con.getResponseCode();
	        
	        if (lastResponseCode == 200) {
	            InputStream is = con.getInputStream();            
	            String response = readOutput(is);
	            is.close();
	            con.disconnect();
	            return response;
	        } else {
	        	con.disconnect();
	        	return null;
	        }
		} catch(IOException ex) {			
			if (!ex.getMessage().contains("connect failed")) {
				try {
					lastResponseCode = con.getResponseCode();
				} catch(IOException ex2) {
					lastResponseCode = 0;
				}
			} else
				lastResponseCode = 0;
			
			if (con != null) con.disconnect();			
			return null;		
		}
	}
	
	//ako se aplikacija srusi onda postoji mogucnost da ne ode odmah na Login activity
	//i na taj nacin se ova klasa ne inicijalizira
	//ova funkcija to provjerava i po potrebi redirecta natrag na Login kako bi se prosla inicijalizacija
	//prima trenutno aktivni activity da ga ugasi po potrebi
	public static void ProvjeriInicijalizaciju(Activity activity) 
	{
		if (!initialized) {
     	   Intent i = new Intent(activity, Login.class);
     	   activity.startActivity(i);
     	   activity.finish();			
		}	
	}
	
	//ovo je glavna funkcija koja se koristi za pozivanje naseg web servisa unutar aplikacije
	//ako nam je token istekao pokusati ce ga obnoviti i opet pozvati web servis
	//ako ne uspije obnoviti vraca null
	//ne pozivati izravno nego uvijek preko background threada
	//ako je postData == null koristi cisti GET, inace dodaje postData i koristi POST
	public static String callWebService(String urlPath, String postData)
	{		
		String response = getResponseWithToken(urlPath, postData);
		
		if (response == null) {
			if (googleToken != null) GoogleAuthUtil.invalidateToken(loginActivity, googleToken);
			googleToken = getGoogleToken();			
			token = getToken();
			response = getResponseWithToken(urlPath, postData);			
		}
		
		return response;
	}
	
	//kao i callWebService ali uvijek koristi cisti GET
	public static String callWebService(String urlPath)
	{				
		return callWebService(urlPath, null);
	}	
	
	public static String callWebServicePostFile(String urlPath, File filePostData)
	{		
		String response = getResponseWithTokenPostFile(urlPath, filePostData);
		
		if (response == null) {
			if (googleToken != null) GoogleAuthUtil.invalidateToken(loginActivity, googleToken);
			googleToken = getGoogleToken();			
			token = getToken();
			response = getResponseWithTokenPostFile(urlPath, filePostData);			
		}
		
		return response;
	}
	
	//ako se negdje kasnije dogodila greska - callWebService je vratio null nakon oba 2 pokusaja
	//sa ovom funkcijom se pokazuje korisniku da je doslo do greske i baca ga se nazad na login
	public static void errorLogout(final Activity activity) 
	{
		//ako je do greske doslo zbog tokena a ne zbog IOExceptiona, spremi da vise nije connectan		
		if (lastResponseCode != 0) {
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
		}
		
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
	
	
	//pretvara file utf8 string i dodaje u POST stream
	//koristi se za dodavanje slike i zvuka u json objekt
	//posto se bufferano ucitava zadaje se stream (file)
	public static void AddFileToJSON(Activity activity, BufferedOutputStream stream, String filePath, String jsonName, String jsonExtension) {				
		try {
			if (filePath == null) {				
				stream.write(("\"" + jsonName + "\": \"\",").getBytes(Charset.forName("UTF-8")));
				stream.write(("\"" + jsonExtension + "\": null").getBytes(Charset.forName("UTF-8")));					
			} else {
				//file limit za upload je 10mb
				File file = new File(filePath);
				long size = file.length();
				if (size > 10485760) return; 
							    
				byte[] bytes = new byte[300000];
				int offset = 0;
				int cnt = 0;
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
				stream.write(("\"" + jsonName + "\": \"").getBytes(Charset.forName("UTF-8")));				
				while(true) {
					cnt = bis.read(bytes, offset, bytes.length - offset);
										
					if (cnt == -1) {					
						stream.write(Base64.encode(bytes, Base64.NO_WRAP));
						stream.flush();
						break;
					} else {
						offset += cnt;
						if (offset == bytes.length) {
							stream.write(Base64.encode(bytes, Base64.NO_WRAP));
							stream.flush();
							offset = 0;								
						}						
					}
				}
				bis.close();

				stream.write("\",".getBytes(Charset.forName("UTF-8")));
				
				int p = filePath.lastIndexOf('.');
				
				if (p == -1)
					stream.write(("\"" + jsonExtension + "\": null").getBytes(Charset.forName("UTF-8")));
				else
					stream.write(("\"" + jsonExtension + "\":\"" + filePath.substring(p + 1) + "\"").getBytes(Charset.forName("UTF-8")));
			}
		} catch (IOException ex) {			
		}
	}
}
