package com.example.home1;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

public class DownloadMultimedia {
	//url do multimedia datoteke
	public String URLPath = null;
	//path do skinute datoteke na disku ili null ako nisam skinuo jos
	public String filePath = null;
	
	public DownloadMultimedia(String URLPath) {
		this.URLPath = URLPath;
	}
	
	//skida multimedia (slika/zvuk) datoteku sa interneta i sprema u file
	//nakon toga je otvara i prikazuje u novom vanjskom activityju
	//sve to koristeci novi background thread
	//ovo je glavna funkcija za otvaranje slika/zvuka kod requesta i answera
	//	parametar filter sluzi kod pokretanja novog vanjskog activityja za pregled 
	//	kako bi se odredio koji program se treba koristit
	//	mi koristimo "image/*" i "audio/*"
	public void show(final Activity activity, final String filter) {
		if (URLPath == null) return;
		
		if (filePath == null) {
			//moram prvo skinut
			
			AsyncTask<Void, Void, Boolean> webTask = new AsyncTask<Void, Void, Boolean>() {
				private ProgressDialog dialog;
				private File file;
				
				@Override
				protected void onPreExecute() {
					dialog = new ProgressDialog(activity);
					dialog.setTitle("Downloading");
					dialog.setMessage("Please wait...");
					dialog.setCancelable(false);
					dialog.setIndeterminate(true);
					dialog.show();
				}
				
				@Override
				protected Boolean doInBackground(Void... params) {										
					int p = URLPath.lastIndexOf('/');
					String filename = CacheManager.EXTERNAL_STORAGE_PATH + "UTranslate_" + URLPath.substring(p + 1);
				    file = new File(filename);
					
				    return DownloadFile.saveToFile(URLPath, file);
				}
							
				@Override
				protected void onPostExecute(Boolean result) {
					dialog.dismiss();
					
					if (result) {
						filePath = file.getAbsolutePath();
						
						if (filter.startsWith("image")) {
							//updejtaj gallery
						    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
						    Uri contentUri = Uri.fromFile(file);
						    mediaScanIntent.setData(contentUri);
						    activity.sendBroadcast(mediaScanIntent);
						}						
						
						startMultimediaActivity(activity, filter);
					}
				}			
			};
			
			webTask.execute((Void)null);
			
		} else
			startMultimediaActivity(activity, filter);
	}
	
	//ovo pokrece activity za pregledavanje multimedije
	//poziva se interno kad je slika/zvuk zaista skinuta
	private void startMultimediaActivity(Activity activity, String filter) {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(filePath)), filter);
		activity.startActivity(intent);		
	}
}
