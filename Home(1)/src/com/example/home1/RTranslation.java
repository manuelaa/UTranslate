package com.example.home1;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class RTranslation extends Activity {	
	//podaci za dodavanje slike na request
	private final CharSequence[] pictureDialogOptions = {"Take a photo with camera", "Existing photo", "Remove photo"};
	private AlertDialog pictureDialog;
	private ImageButton addPictureButton;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int ACTIVITY_CHOOSE_IMAGE = 200;	
	//path do slike koja se uploada
	private String photoPath = null;
	//temporary path do nove slike koja se slika
	private String photoPathNew = null;
	
	//podaci za dodavanje zvuka na request
	private final CharSequence[] audioDialogOptions = {"Record new audio", "Existing audio", "Remove audio"};
	private AlertDialog audioDialog;
	private AlertDialog audioRecordDialog;
	private ImageButton addAudioButton;
	private MediaRecorder audioRecorder = null;
	//path do zvuka koja se uploada
	private String audioPath = null;
	private static final int ACTIVITY_CHOOSE_AUDIO = 300;
	
	//podaci sa kojeg jezika
	public ArrayList<Lang> languagesFrom;
	public ArrayList<Lang> checkedLanguagesFrom;
	private ImageButton fromLanguageButton;
	private static final int ACTIVITY_FROM_LANGUAGE = 400;
	
	//podaci na koji jezik
	public ArrayList<Lang> languagesTo;
	public ArrayList<Lang> checkedLanguagesTo;
	private ImageButton toLanguageButton;
	private static final int ACTIVITY_TO_LANGUAGE = 500;
	
	private static final int ACTIVITY_SIMILAR_REQUEST = 600;
	
	private Button postButton;
	private EditText editText;
	
	//stvara file za sliku
	private File createImageFile() throws IOException {
	    String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
	    String imageFileName = "UTranslate_" + timeStamp;
	    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	    
	    //File image = File.createTempFile(imageFileName, ".jpg", storageDir);
	    File image = new File(storageDir, imageFileName + ".jpg");

	    //photoPathNew = "file:" + image.getAbsolutePath();
	    photoPathNew = image.getAbsolutePath();	    
	    return image;
	}
	
	//dodaje sliku u galeriju
	private void galleryAddPic() {
	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    File f = new File(photoPath);
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
	}
	
	//poziva kameru za slikanje
	private void takeNewPicture() {	    
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    //da li postoji activity za kameru
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        //file za sliku
	        File photoFile = null;
	        try {
	            photoFile = createImageFile();
	        } catch (IOException ex) {
	        	photoPathNew = null;
	        }
	        
	        //ako je stvoren file
	        if (photoFile != null) {
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	        }
	    }
	}
	
	//poziva explorer za odabir slike sa diska
	private void selectPictureFromDisk() {
		Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);		
		chooseFile.setType("image/*");		
		Intent intent = Intent.createChooser(chooseFile, "Choose a picture");		
		startActivityForResult(intent, ACTIVITY_CHOOSE_IMAGE);
	}
	
	//poziva explorer za odabir zvuka sa diska
	private void selectAudioFromDisk() {
		Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);		
		chooseFile.setType("audio/*");		
		Intent intent = Intent.createChooser(chooseFile, "Choose audio");		
		startActivityForResult(intent, ACTIVITY_CHOOSE_AUDIO);
	}
	
	//zapocinje snimanje zvuka
	private void startAudioRecording() {
        audioRecorder = new MediaRecorder();
        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        
	    String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
	    String audioFileName = "UTranslate_" + timeStamp;
        audioPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + audioFileName + ".3gp";        
        audioRecorder.setOutputFile(audioPath);
        
        try {        	
            audioRecorder.prepare();
            audioRecorder.start();
            audioRecordDialog.show();
        } catch (IOException e) {
        }
    }

    private void stopAudioRecording() {
        audioRecorder.stop();
        audioRecorder.release();
        audioRecorder = null;
        
        ContentValues values = new ContentValues(3);
        long current = System.currentTimeMillis();
        File file = new File(audioPath);
        values.put(MediaStore.Audio.Media.TITLE, "audio" + file.getName());
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp");
        values.put(MediaStore.Audio.Media.DATA, file.getAbsolutePath());
        ContentResolver contentResolver = getContentResolver();         
        Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri newUri = contentResolver.insert(base, values);         
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
        
        addAudioButton.setImageResource(R.drawable.sound_1);
    }
	
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getBaseContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	        //zavrsilo je slikanje
			
			if (resultCode == RESULT_OK) {
	        	//spremi novi file
	        	
				photoPath = photoPathNew;        	
	        	galleryAddPic();
	        	addPictureButton.setImageDrawable(getResources().getDrawable(R.drawable.cam_1));
	        } else if (resultCode == RESULT_CANCELED) {
	        	//izbrisi novi file
	        	
	        	File file = new File(photoPathNew);
	        	file.delete();	        
	        }
	    } else if (requestCode == ACTIVITY_CHOOSE_IMAGE) {
	    	//zavrsio je odabir slike sa diska
	    	
	    	if (resultCode == RESULT_OK) {
	    		//dohvati lokaciju slike
	    		
	    		Uri uri = data.getData();
	    		photoPath = getRealPathFromURI(uri);    		
	    		addPictureButton.setImageDrawable(getResources().getDrawable(R.drawable.cam_1));	
	    	} else if (resultCode == RESULT_CANCELED) {
	    		
	    	}	    	
	    } else if (requestCode == ACTIVITY_CHOOSE_AUDIO) {
	    	//zavrsio je odabir zvuka sa diska
	    	
	    	if (resultCode == RESULT_OK) {
	    		//dohvati lokaciju zvuka
	    		
	    		Uri uri = data.getData();	    		
	    		audioPath = getRealPathFromURI(uri);	    		
	    		addAudioButton.setImageDrawable(getResources().getDrawable(R.drawable.sound_1));
	    	} else if (resultCode == RESULT_CANCELED) {
	    		
	    	}	    	
	    } else if (requestCode == ACTIVITY_FROM_LANGUAGE) {
	    	//zavrsio je odabir jezika sa kojeg se prevodi
	    	
	    	if (resultCode == RESULT_OK) {
	    		if (checkedLanguagesFrom.size() == 1)
	    			fromLanguageButton.setImageDrawable(getResources().getDrawable(checkedLanguagesFrom.get(0).resourceId));
	    		else
	    			fromLanguageButton.setImageDrawable(getResources().getDrawable(R.drawable.upitnik1));
	    	}	    	
	    } else if (requestCode == ACTIVITY_TO_LANGUAGE) {
	    	//zavrsio je odabir jezika sa kojeg se prevodi
	    	
	    	if (resultCode == RESULT_OK) {
	    		if (checkedLanguagesTo.size() == 1)
	    			toLanguageButton.setImageDrawable(getResources().getDrawable(checkedLanguagesTo.get(0).resourceId));
	    		else if (checkedLanguagesTo.size() > 1)
	    			toLanguageButton.setImageDrawable(getResources().getDrawable(R.drawable.vise_jezika));
	    		else
	    			toLanguageButton.setImageDrawable(getResources().getDrawable(R.drawable.upitnik1));
	    	}	    	
	    } else if (requestCode == ACTIVITY_SIMILAR_REQUEST) {
	    	//zavrsio je odabir slicnog requesta
	    	dodajRequest(SimilarR.similarRequestId);
	    }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
				
		//slozi dijalog za odabir izmedju kamere/slike sa diska
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Add photo");
	    builder.setItems(pictureDialogOptions, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					//zeli novu sliku sa kamerom
					takeNewPicture();
				} else if (which == 1) {
					//zeli postojecu sliku sa diska
					selectPictureFromDisk();
				} else {
					//zeli maknut sliku
					photoPath = null;
					addPictureButton.setImageDrawable(getResources().getDrawable(R.drawable.cam_2));
				}
			}	    	
	    });
	    pictureDialog = builder.create();
		
		//slozi dijalog za odabir izmedju snimanja/zvuka sa diska
	    builder = new AlertDialog.Builder(this);
	    builder.setTitle("Add audio");
	    builder.setItems(audioDialogOptions, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					//zeli snimiti novi zvuk
					startAudioRecording();
				} else if (which == 1) {
					//zeli postojeci zvuk sa diska
					selectAudioFromDisk();
				} else {
					//zeli maknuti zvuk
					audioPath = null;
					addAudioButton.setImageDrawable(getResources().getDrawable(R.drawable.sound_2));
				}
			}	    	
	    });
	    audioDialog = builder.create();
	    
	    //slozi dijalog za konkretno snimanje zvuka
	    builder = new AlertDialog.Builder(this);
	    builder.setMessage("Recording audio...")
	       .setCancelable(false)	       
	       .setPositiveButton("Stop", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                stopAudioRecording();
	           }
	       });	       
	    audioRecordDialog = builder.create();
	    
		//gumb za dodavanje slike		
		addPictureButton = (ImageButton) findViewById(R.id.imageButton1);
		addPictureButton.setImageDrawable(getResources().getDrawable(R.drawable.cam_2));
		addPictureButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pictureDialog.show();				
			}					
		});
		 
		//gumb za dodavanje zvuka
		addAudioButton = (ImageButton) findViewById(R.id.imageButton2);
		addAudioButton.setImageDrawable(getResources().getDrawable(R.drawable.sound_2));
		addAudioButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				audioDialog.show();
			}					
		});
		
		populateLangList();
		
		//gumb za jezik sa kojeg se prevodi
		fromLanguageButton = (ImageButton) findViewById(R.id.ibUpitnik2);
		fromLanguageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//namjesti podatke
				LanguagesActivity.languages = languagesFrom;
				LanguagesActivity.checkedLanguages = checkedLanguagesFrom;
				LanguagesActivity.multiSelectEnabled = false;
				
				Intent i = new Intent(RTranslation.this, LanguagesActivity.class);				
				startActivityForResult(i, ACTIVITY_FROM_LANGUAGE);
			}
		});
		
		//gumb za jezike na koje se prevodi
		toLanguageButton = (ImageButton) findViewById(R.id.ibUpitnik);
		toLanguageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//namjesti podatke
				LanguagesActivity.languages = languagesTo;
				LanguagesActivity.checkedLanguages = checkedLanguagesTo;
				LanguagesActivity.multiSelectEnabled = true;
				
				Intent i = new Intent(RTranslation.this, LanguagesActivity.class);				
				startActivityForResult(i, ACTIVITY_TO_LANGUAGE);
			}
		});
		
				
		//gumb za postanje		
		postButton = (Button) findViewById(R.id.bPost);
		editText = (EditText) findViewById(R.id.editText1);
		postButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//provjera da li je sve uneseno
				if (checkedLanguagesFrom.size() == 0 || checkedLanguagesTo.size() == 0 || editText.getText().toString() == "")
					return;
				
				/* za testiranje */
				/*				
				checkedLanguagesFrom.add(languagesFrom.get(0));
				checkedLanguagesTo.add(languagesFrom.get(1));
				checkedLanguagesTo.add(languagesFrom.get(2));
				editText.setText("bok");
				*/
				
				//prvo treba naci da li ima slicnih requestova
				Uri.Builder builder = Uri.parse(Connection.WEB_SERVICE_URL).buildUpon();
				builder.appendPath("Requests");
				builder.appendPath("FindSimilar");
				JSONObject obj;				
				
				try {
					obj = new JSONObject();
					obj.put("text", editText.getText());
					obj.put("languageAsk", Integer.parseInt(checkedLanguagesFrom.get(0).id));					
					
					ArrayList<Integer> languageTold = new ArrayList<Integer>();
					for(int i = 0; i < checkedLanguagesTo.size(); i++)
						languageTold.add(Integer.parseInt(checkedLanguagesTo.get(i).id));									
					obj.put("languageTold", new JSONArray(languageTold));
				} catch (JSONException e) {
					return;
				}
								
				WebServiceTask webTask = new WebServiceTask(RTranslation.this, builder.build().toString(), obj.toString(), "Searching", "Searching for similar requests, please wait...") {
					@Override
					protected void onPostExecute(String result) {
						super.onPostExecute(result);
						
						if (result != null) {
							if (((JSONArray)json).length() == 0)
								dodajRequest(null);
							else
								prikaziSlicne((JSONArray)json);	
						}
					}				
				};		
				webTask.execute((Void)null);	
			}
		});
	}
	
	//sagradi listu jezika
	private void populateLangList() {
		languagesTo = new ArrayList<Lang>();
		checkedLanguagesTo = new ArrayList<Lang>();
		languagesFrom = new ArrayList<Lang>();
		checkedLanguagesFrom = new ArrayList<Lang>();
				
		TypedArray resLangs = getResources().obtainTypedArray(R.array.languages);		
		for(int i = 0; i < resLangs.length(); i++) {
			int arrayId = resLangs.getResourceId(i, 0);
			if (arrayId > 0) {
				String[] lang = getResources().getStringArray(arrayId);
				languagesTo.add(new Lang(lang[0], lang[1], this));
				languagesFrom.add(new Lang(lang[0], lang[1], this));				
			}
		}
		resLangs.recycle();	
	}
	
	//prikazi listu slicnih requestova da korisnik odabere
	private void prikaziSlicne(JSONArray json) {
		SimilarR.similarRequests.clear();
		
		try {
			JSONObject jsonObject;
			for(int i = 0; i < json.length(); i++)
			{
				jsonObject = json.getJSONObject(i);
				SimilarR.similarRequests.add(new Similar(this, Integer.parseInt(jsonObject.getString("requestId")), jsonObject.getString("text"), jsonObject.getString("languageTold")));
			}
			
			Intent i = new Intent(RTranslation.this, SimilarR.class);				
			startActivityForResult(i, ACTIVITY_SIMILAR_REQUEST);
		} catch (JSONException e) {
		}
	}

	//dodaje novi request i salje se similarId (null ako nema slicnog)
	private void dodajRequest(Long similarId) {
		Uri.Builder builder = Uri.parse(Connection.WEB_SERVICE_URL).buildUpon();
		builder.appendPath("Requests");
		builder.appendPath("UpdateRequest");
		JSONObject obj;				
		
		try {
			obj = new JSONObject();
			obj.put("languageAsk", Integer.parseInt(checkedLanguagesFrom.get(0).id));
			obj.put("text", editText.getText());
						
			ArrayList<Integer> languageTold = new ArrayList<Integer>();
			for(int i = 0; i < checkedLanguagesTo.size(); i++)
				languageTold.add(Integer.parseInt(checkedLanguagesTo.get(i).id));												
			obj.put("languageTold", new JSONArray(languageTold));
						
			if (similarId == null) 
				obj.put("similarId", JSONObject.NULL);		
			else
				obj.put("similarId", similarId);
		} catch (JSONException e) {			 
			return;
		}
		
		WebServiceTask webTask = new WebServiceTask(RTranslation.this, builder.build().toString(), obj.toString(), "Uploading", "Please wait...") {
			private File tmpJson;
			
			@Override
			protected String doInBackground(Void... params) {
				Connection.ProvjeriInicijalizaciju(activity);		
							
				BufferedOutputStream stream = null;
				String ret = null;
				
				try {
				    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);				    
				    tmpJson = File.createTempFile("request", ".dat", storageDir);					    
				    stream = new BufferedOutputStream(new FileOutputStream(tmpJson));
					
				    //posto uploadam velike fajlove moram to raditi sa bufferima
				    //pa cu onda prvo poslat JSON pa fajlove nakon toga
				    //pa moram djelomicno rucno graditi JSON i upisivati u stream
				    //izbaci } a dodaj , na kraju jsona
					byte[] data = (postData.substring(0, postData.length() - 1) + ",").getBytes(Charset.forName("UTF-8"));					
				    stream.write(data);
					stream.flush();
					Connection.AddFileToJSON(RTranslation.this, stream, photoPath, "picture", "pictureExtension");
					stream.write(',');					
					Connection.AddFileToJSON(RTranslation.this, stream, audioPath, "audio", "audioExtension");
					stream.write('}');
					stream.close();
					
					ret = Connection.callWebServicePostFile(url, tmpJson);									    
				    return ret;
				} catch (IOException e) {							
					return null;
				}
			}
			
			@Override
			protected void onPostExecute(String result) {
				tmpJson.delete();
				
				super.onPostExecute(result);				
				
				if (result != null) {
					Intent i = new Intent(RTranslation.this, Homepage.class);				
	        		startActivity(i);
	        		finish();
				}
			}				
		};		
		webTask.execute((Void)null);		
	}
}
