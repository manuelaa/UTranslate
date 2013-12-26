package com.example.home1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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
		
	//stvara file za sliku
	private File createImageFile() throws IOException {
	    String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
	    String imageFileName = "UTranslate_" + timeStamp;
	    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	    
	    //File image = File.createTempFile(imageFileName, ".jpg", storageDir);
	    File image = new File(storageDir, imageFileName + ".jpg");

	    photoPathNew = "file:" + image.getAbsolutePath();
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
        
        System.out.println(audioPath);
        
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
	    		photoPath = uri.getPath();	    			    		
	    		addPictureButton.setImageDrawable(getResources().getDrawable(R.drawable.cam_1));	
	    	} else if (resultCode == RESULT_CANCELED) {
	    		
	    	}	    	
	    } else if (requestCode == ACTIVITY_CHOOSE_AUDIO) {
	    	//zavrsio je odabir zvuka sa diska
	    	
	    	if (resultCode == RESULT_OK) {
	    		//dohvati lokaciju zvuka
	    		
	    		Uri uri = data.getData();	    		
	    		audioPath = uri.getPath();	    		
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
	    }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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
}
