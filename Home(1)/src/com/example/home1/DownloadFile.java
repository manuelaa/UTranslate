package com.example.home1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;

//sluzi za skidanje datoteka sa URL-a
//koristiti u background tasku
public class DownloadFile {	
	//sprema file sa url-a u zadani cache file
	public static byte[] saveToCache(Context context, String url, String name) {		
		URL urlObj;
		try {
			urlObj = new URL(url);
		} catch (MalformedURLException e) {
			return null;
		}
		
	    HttpURLConnection con = null;	    
		try {
			con = (HttpURLConnection)urlObj.openConnection();
	        con.setInstanceFollowRedirects(false);
	        con.setUseCaches(false);
	        con.connect();
	        
	        InputStream is = con.getInputStream();
	        int size = con.getContentLength();
	        
		    byte[] data = new byte[size];
		    is.read(data);		    
		    is.close();
		    con.disconnect();
		    
		    CacheManager.cacheData(context, data, name);
		    
		    return data;
		} catch (IOException e) {
			if (con != null) con.disconnect();			
			return null;
		}
	}
	
	//sprema file sa url-a u zadani file u normalnom storageu
	public static boolean saveToFile(String url, File file) {
	    URL urlObj;
		try {
			urlObj = new URL(url);
		} catch (MalformedURLException e) {
			return false;
		}
		
	    HttpURLConnection con = null;	    
		try {
			con = (HttpURLConnection)urlObj.openConnection();
	        con.setInstanceFollowRedirects(false);
	        con.setUseCaches(false);
	        con.connect();
	        
	        InputStream is = con.getInputStream();
	        FileOutputStream os = new FileOutputStream(file);	        
		    int bufferLength = 0;
	        byte[] data = new byte[102400];		    
		    while(true) {
		    	bufferLength = is.read(data);
		    	if (bufferLength == -1) break;
		    	os.write(data, 0, bufferLength);
		    	os.flush();
		    }
		    os.close();
		    is.close();
		    con.disconnect();
		    
		    return true;
		} catch (IOException e) {
			if (con != null) con.disconnect();
			return false;
		}
	}
		
	//provjerava prvo da li file vec postoji u cacheu i ako ne postoji skida u cache
	public static byte[] getFileFromCache(Context context, String url, String name) {
		byte[] ret = null;
		
		try {
			ret = CacheManager.getData(context, name);		
		} catch(IOException e) {			
		}
				
		if (ret == null) {
			return saveToCache(context, url, name);
		} else 
			return ret;
	}
}
