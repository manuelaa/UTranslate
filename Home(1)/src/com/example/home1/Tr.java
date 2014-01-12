package com.example.home1;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class Tr extends Activity {
	private ArrayList<Text> answers = new ArrayList<Text>();
	
	//koji request prikazujem
	public static Request request;	
	
	private ImageButton btnPicture;
	private ImageButton btnAudio;
	
	//lista
	private ArrayAdapter<Text> adapter;
	private ListView list;
	
	//podaci za dodavanje zvuka na answer
	private final CharSequence[] audioDialogOptions = {"Record new audio", "Existing audio", "Remove audio"};
	private AlertDialog audioDialog;
	private AlertDialog audioRecordDialog;
	private ImageButton addAudioButton;
	private MediaRecorder audioRecorder = null;
	//path do zvuka koja se uploada
	private String audioPath = null;
	private static final int ACTIVITY_CHOOSE_AUDIO = 300;
	
	//odgovor i gumb za slanje odgovora
	private EditText editText;
	private Button btnPostAnswer;
	
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
        audioPath = CacheManager.EXTERNAL_STORAGE_PATH + audioFileName + ".3gp";        
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
        
        addAudioButton.setImageResource(R.drawable.microphone_1);
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
		if (requestCode == ACTIVITY_CHOOSE_AUDIO) {
	    	//zavrsio je odabir zvuka sa diska
	    	
	    	if (resultCode == RESULT_OK) {
	    		//dohvati lokaciju zvuka
	    		
	    		Uri uri = data.getData();	    		
	    		audioPath = getRealPathFromURI(uri);	    		
	    		addAudioButton.setImageDrawable(getResources().getDrawable(R.drawable.microphone_1));
	    	}    	
	    } 
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//stvori direktorij za external cache
		if (CacheManager.EXTERNAL_STORAGE_PATH == null)
			CacheManager.createExternalDataDir();		
		
		/* TODO TESTING */
		//(Context context, long userId, long requestId, String text, String audioURLPath, String pictureURLPath, int idLang1, int idLang2, String timePosted, boolean notification) {
		request = new Request(this, 1, 1, "tekst", null, null, 2, 4, "1.1.2014.", false);
		//request = new Request(this, 1, 63, "tekst", "http://nihao.fer.hr/UTranslate/data/request_audio/request_63.3gp", "http://nihao.fer.hr/UTranslate/data/request_pictures/request_63.jpg", 1, 1, "1.1.2014.", false);
		
		setContentView(R.layout.translation_3);
		
		//slozi dijalog za odabir izmedju snimanja/zvuka sa diska
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
					addAudioButton.setImageDrawable(getResources().getDrawable(R.drawable.microphone_2));
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
		 
		//gumb za dodavanje zvuka
	    addAudioButton = (ImageButton) findViewById(R.id.ibMic);	    
		addAudioButton.setImageDrawable(getResources().getDrawable(R.drawable.microphone_2));
		addAudioButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				audioDialog.show();
			}					
		});
		
		//tekstbox za odgovor
		editText = (EditText) findViewById(R.id.askedText);
		
		//gumb za slanje odgovora
	    btnPostAnswer = (Button) findViewById(R.id.bPost);	    
		btnPostAnswer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//postaj odgovor
				posaljiOdgovor();
			}					
		});
		
		loadRequest();
		loadAnswers();
	}
	
	//salje odgovor
	private void posaljiOdgovor() {
		//provjera da li je unesen ili zvuk ili tekst
		if (audioPath == null && editText.getText().toString().isEmpty())
			return;
		
		Uri.Builder builder = Uri.parse(Connection.WEB_SERVICE_URL).buildUpon();
		builder.appendPath("Answers");
		builder.appendPath("AddAnswer");
		JSONObject obj;				
		
		try {
			obj = new JSONObject();
			obj.put("requestId", request.requestId);
			obj.put("text", editText.getText());
			obj.put("languageId", request.idLang2);
		} catch (JSONException e) {			 
			return;
		}
		
		WebServiceTask webTask = new WebServiceTask(Tr.this, builder.build().toString(), obj.toString(), "Uploading", "Please wait...") {
			private File tmpJson;
			private Text newAnswer = null;
			
			@Override
			protected String doInBackground(Void... params) {
				Connection.ProvjeriInicijalizaciju(activity);		
							
				BufferedOutputStream stream = null;
				String ret = null;
				
				try {
				    File storageDir = new File(CacheManager.EXTERNAL_STORAGE_PATH);				    
				    tmpJson = File.createTempFile("answer", ".dat", storageDir);					    
				    stream = new BufferedOutputStream(new FileOutputStream(tmpJson));
					
				    //posto uploadam velike fajlove moram to raditi sa bufferima
				    //pa cu onda prvo poslat JSON pa fajlove nakon toga
				    //pa moram djelomicno rucno graditi JSON i upisivati u stream
				    //izbaci } a dodaj , na kraju jsona
					byte[] data = (postData.substring(0, postData.length() - 1) + ",").getBytes(Charset.forName("UTF-8"));					
				    stream.write(data);
					stream.flush();
					Connection.AddFileToJSON(Tr.this, stream, audioPath, "audio", "audioExtension");
					stream.write('}');
					stream.close();
					
					ret = Connection.callWebServicePostFile(url, tmpJson);									    
				     
					if (ret != null) {
						//uspio sam postati answer, dobivam natrag cijeli answer objekt kako bi azurirao listu
						JSONObject obj = new JSONObject(ret);
						
						newAnswer = new Text();
						newAnswer.answerId = Integer.parseInt(obj.getString("answerId"));
						newAnswer.text = obj.getString("Text");
						newAnswer.timePosted = obj.getString("timePosted");
						newAnswer.rating = (float)obj.getDouble("rate");
						newAnswer.userRating = obj.getInt("userRating");
						
						if (obj.get("audioExtension") != JSONObject.NULL)
							newAnswer.audio = new DownloadMultimedia(obj.getString("audio"));
						else
							newAnswer.audio = new DownloadMultimedia(null);

						//dohvati usera i njegove podatke
						newAnswer.user = new User(Tr.this, Integer.parseInt(obj.getString("user")));
						
						//dohvati podatke usera
						Uri.Builder builder = Uri.parse(Connection.WEB_SERVICE_URL).buildUpon();
						builder.appendPath("User");
						builder.appendPath("GetUserDetails");		
						
						String response = Connection.callWebService(builder.build().toString(), "{userId:" + String.valueOf(newAnswer.user.userId) + "}");
						
						if (response != null) {
							try {
								JSONObject userObj = new JSONObject(response);					
								newAnswer.user.name = userObj.getString("name");
								newAnswer.user.email = userObj.getString("email");
								newAnswer.user.pictureURLPath = userObj.getString("picture");
							} catch(JSONException e) {						
							}			
						}	
						
						if (newAnswer.user.pictureURLPath != null) {						
							//dohvati sliku
							if (newAnswer.user.picture != null) newAnswer.user.picture.recycle();
							byte[] bytes = DownloadFile.getFileFromCache(Tr.this, newAnswer.user.pictureURLPath, "user_" + String.valueOf(newAnswer.user.userId) + ".dat");					
							
							if (bytes != null) 
								newAnswer.user.picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);								
						}
						
						//provjeri da li u listi vec imam answer sa ovim id-om
						//onda se radilo o updateu pa ga izbrisi
						/* <nema updateanja>
						 * 
						for(int i = 0; i < answers.size(); i++) {
							if (answers.get(i).answerId == newAnswer.answerId) {
								answers.remove(i);
								break;
							}
						}
						*/
						
						//dodaj novi odgovor na pocetak liste
						answers.add(0, newAnswer);
					}
					
					return ret;
				} catch (IOException e) {							
					return null;
				} catch (JSONException e) {
					return null;
				}
			}
			
			@Override
			protected void onPostExecute(String result) {
				tmpJson.delete();
				
				super.onPostExecute(result);				
				
				if (newAnswer != null) {
					//answer je postan
					//ocisti textbox i eventualni audio
					editText.setText("");
					audioPath = null;
					addAudioButton.setImageDrawable(getResources().getDrawable(R.drawable.microphone_2));
					
					//dojavi adapteru
					adapter.notifyDataSetChanged();
					
					//odscrollaj na vrh liste
					list.smoothScrollToPosition(0);
				}
			}				
		};		
		webTask.execute((Void)null);		
	}
	
	//ucitava request
	private void loadRequest() {
		TextView reqText = (TextView)findViewById(R.id.tv_show);
		reqText.setText(request.text);
		
		btnPicture = (ImageButton) findViewById(R.id.ibCam);
		if (request.picture.URLPath == null)
			btnPicture.setImageDrawable(getResources().getDrawable(R.drawable.cam_2));
		else
			btnPicture.setImageDrawable(getResources().getDrawable(R.drawable.cam_1));	
		
		btnPicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				request.picture.show(Tr.this, "image/*");
			}
		});

		btnAudio = (ImageButton) findViewById(R.id.ibSound);
		if (request.audio.URLPath == null)
			btnAudio.setImageDrawable(getResources().getDrawable(R.drawable.sound_2));
		else
			btnAudio.setImageDrawable(getResources().getDrawable(R.drawable.sound_1));	
		
		btnAudio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				request.audio.show(Tr.this, "audio/*");
			}
		});
		
		//slike jezika za request
		ImageView lang1 = (ImageView) findViewById(R.id.imageView4);
		lang1.setImageDrawable(getResources().getDrawable(request.resourceIdLang1));
		
		ImageView lang2 = (ImageView) findViewById(R.id.imageView5);
		lang2.setImageDrawable(getResources().getDrawable(request.resourceIdLang2));
	}
	
	//ucitava odgovore
	private void loadAnswers() {
		Uri.Builder builder = Uri.parse(Connection.WEB_SERVICE_URL).buildUpon();
		builder.appendPath("Answers");
		builder.appendPath("GetRequestAnswers");						
		
		JSONObject obj = new JSONObject();
		try {
			obj.put("Id", request.requestId);
			obj.put("languageTold", request.idLang2);			
		} catch (JSONException e) {			
			return;
		}
		
		WebServiceTask webTask = new WebServiceTask(Tr.this, builder.build().toString(), obj.toString(), "Loading answers", "Please wait...") {
			@Override
			protected String doInBackground(Void... params) {
				Connection.ProvjeriInicijalizaciju(activity);		
				String result =  Connection.callWebService(url, postData);
				
				JSONArray jsonArray;
				try {
					jsonArray = new JSONArray(result);
				} catch (JSONException e) {
					return null;
				}
				
				for(int i = 0; i < jsonArray.length(); i++) {
					try {
						JSONObject obj = jsonArray.getJSONObject(i);
						Text newAnswer = new Text();
						newAnswer.answerId = Integer.parseInt(obj.getString("answerId"));
						newAnswer.text = obj.getString("Text");
						newAnswer.timePosted = obj.getString("timePosted");
						newAnswer.rating = (float)obj.getDouble("rate");						
						newAnswer.userRating = obj.getInt("userRating");
						
						if (obj.get("audioExtension") != JSONObject.NULL)
							newAnswer.audio = new DownloadMultimedia(obj.getString("audio"));
						else
							newAnswer.audio = new DownloadMultimedia(null);

						//dohvati usera i njegove podatke
						newAnswer.user = new User(Tr.this, Integer.parseInt(obj.getString("user")));
						
						//dohvati podatke usera
						Uri.Builder builder = Uri.parse(Connection.WEB_SERVICE_URL).buildUpon();
						builder.appendPath("User");
						builder.appendPath("GetUserDetails");		
						
						String response = Connection.callWebService(builder.build().toString(), "{userId:" + String.valueOf(newAnswer.user.userId) + "}");
						
						if (response != null) {
							try {
								JSONObject userObj = new JSONObject(response);					
								newAnswer.user.name = userObj.getString("name");
								newAnswer.user.email = userObj.getString("email");
								newAnswer.user.pictureURLPath = userObj.getString("picture");
							} catch(JSONException e) {						
							}			
						}	
						
						if (newAnswer.user.pictureURLPath != null) {						
							//dohvati sliku
							if (newAnswer.user.picture != null) newAnswer.user.picture.recycle();
							byte[] bytes = DownloadFile.getFileFromCache(Tr.this, newAnswer.user.pictureURLPath, "user_" + String.valueOf(newAnswer.user.userId) + ".dat");					
							
							if (bytes != null) 
								newAnswer.user.picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);								
						}
						
						answers.add(newAnswer);							
					} catch (JSONException e) {
					}	
				}
				
				return result;
			}
			
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				
				populateListView();
			}				
		};		
		webTask.execute((Void)null);
	}
	
	private void populateListView() {
		adapter = new ListAdapter();
		list = (ListView) findViewById(R.id.listView1);
		list.setAdapter(adapter);
 	}
	
	//ocjenjuje zadani odgovor
	private void rateAnswer(final Text answer, final int rating) {
		if (answer == null) return;
		
		Uri.Builder builder = Uri.parse(Connection.WEB_SERVICE_URL).buildUpon();
		builder.appendPath("Ratings");
		builder.appendPath("RateAnswer");
		JSONObject obj;				
		
		try {
			obj = new JSONObject();
			obj.put("answerId", answer.answerId);
			obj.put("rate", rating);			
		} catch (JSONException e) {			 
			return;
		}
		
		WebServiceTask webTask = new WebServiceTask(Tr.this, builder.build().toString(), obj.toString(), "Rating", "Please wait...") {						
			@Override
			protected void onPostExecute(String result) {				
				super.onPostExecute(result);				
				
				if (result != null) {
					answer.rating = rating;
					adapter.notifyDataSetChanged();
				}
			}
		};		
		webTask.execute((Void)null);		
	}
	
	
	private class ListAdapter extends ArrayAdapter<Text> {
		public ListAdapter() {
			super(Tr.this, R.layout.item_view_translation, answers);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//Make sure we have a view to work with (may have been given null)
			View itemView = convertView;
			if(itemView == null)
				itemView = getLayoutInflater().inflate(R.layout.item_view_translation, parent, false);
				
			final Text current = answers.get(position);
			
			//ime usera
			TextView userN = (TextView)itemView.findViewById(R.id.UserName);
			userN.setText(current.user.name);	
			
			//profile picture
			if (current.user.picture != null) {
				ImageView userP = (ImageView)itemView.findViewById(R.id.userPhoto);
				userP.setImageBitmap(current.user.picture);
			}
	
			//tekst odgovora
			TextView aText = (TextView)itemView.findViewById(R.id.askedText);
			aText.setText(current.text);
						
			//rating
			RatingBar rb = (RatingBar) itemView.findViewById(R.id.ratingBar1);
			rb.setRating(current.rating);			
			
			//vrijeme
			TextView timePosted = (TextView)itemView.findViewById(R.id.ddmmgggg);
			timePosted.setText(current.timePosted);
			
			/*
			Button rateBtn = (Button) findViewById(R.id.bR1);
			rateBtn.setOnClickListener(new View.OnClickListener() {
			     public void onClick(View v) {
			        final Dialog rankDialog = new Dialog(Tr.this, R.style.FullHeightDialog);
			        rankDialog.setContentView(R.layout.rank_dialog);
			        rankDialog.setCancelable(true);
			        RatingBar ratingBar = (RatingBar)rankDialog.findViewById(R.id.rbRate);
			        //ratingBar.setRating(userRankValue);
			        
			 
			        Button updateButton = (Button) rankDialog.findViewById(R.id.bRate);
			        updateButton.setOnClickListener(new View.OnClickListener() {
			            @Override
			            public void onClick(View v) {
			                rankDialog.dismiss();
			            }
			        });
			        //now that the dialog is set up, it's time to show it    
			        rankDialog.show();                
			    }
			});
			
			*/
			
			
			//audio button na odgovoru
			final ImageView audioButton = (ImageView)itemView.findViewById(R.id.imageView1);
			
			//slika za audio button
			if (current.audio.URLPath == null)
				audioButton.setImageDrawable(getResources().getDrawable(R.drawable.sound_2));
			else
				audioButton.setImageDrawable(getResources().getDrawable(R.drawable.sound_1));
			
			//pustanje zvuka za answer
			audioButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//pusti zvuk na odgovoru
					current.audio.show(Tr.this, "audio/*");
				}
			});
			
			//gumb za rateanje odgovora
			final Button rateButton = (Button)itemView.findViewById(R.id.bR1);
			
			//da li je korisnik vec rateo
			if (current.userRating != -1)
				rateButton.setBackgroundColor(Color.WHITE);
			else
				rateButton.setBackgroundColor(Color.rgb(194, 167, 122));
			
			rateButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					rateAnswer(current, 3);					
				}
			});
			
			return itemView;
		}
	}
}

