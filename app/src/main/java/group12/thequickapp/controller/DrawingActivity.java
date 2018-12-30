package group12.thequickapp.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import group12.thequickapp.UI.DrawingView;
import group12.thequickapp.UI.ListViewAdapter;
import group12.thequickapp.UI.ShakeDetector;
import group12.thequickapp.UI.ShakeDetector.OnShakeListener;
import group12.thequickapp.model.Analyzer;
import group12.thequickapp.model.AppList;
import group12.thequickapp.model.DrawPattern;
import group12.thequickapp.model.DrawingModel;
import group12.thequickapp.model.FeedBack;
import group12.thequickapp.model.InstalledApps;
import group12.thequickapp.model.LearningModel;
import group12.thequickapp.model.OnLineLearning;
import group12.thequickapp.model.PInfo;
import group12.thequickapp.model.Pattern;
import group12.thequickapp.model.Recognizer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class DrawingActivity extends Activity {
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ShakeDetector mShakeDetector;
	
	private DrawingModel drawingModel;
	private DrawingView dv;
	private ArrayList<PInfo> installedApps;
	
	private ListView lv;
	private Button btn;
	private ListViewAdapter listAdapter;
	private Handler handler;
	// private int n;
	private int totalUnit;
	private Analyzer analyzer;
	private boolean isRecording;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				//Toast.makeText(DrawingActivity.this, "3 seconds passed!", Toast.LENGTH_SHORT).show();
				drawAgain();
			}
		};
		
		drawingModel = new DrawingModel();
		totalUnit = drawingModel.N;
		dv = new DrawingView(this, drawingModel);
		dv.setOnTouchListener(new View.OnTouchListener() {
			int unitSize = dv.getAreaSize()/totalUnit;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int s = ((int)event.getX()-dv.left)/unitSize;
				int t = ((int)event.getY()-dv.top)/unitSize;
				int action = event.getAction();
				if(s>=0&&s<totalUnit&&t>=0&&t<totalUnit&&(action==MotionEvent.ACTION_DOWN||action==MotionEvent.ACTION_MOVE)){
					drawingModel.set(s, t);
					if(!dv.press){
						dv.press = true;
						handler.postDelayed(new Runnable(){
							@Override
							public void run() {
								handler.sendEmptyMessage(0);
							}
						}, 3000);						
					}
					if(action==MotionEvent.ACTION_DOWN){
						dv.path.moveTo(event.getX(), event.getY());
					}else if(action==MotionEvent.ACTION_MOVE){
						if(dv.path.isEmpty()) dv.path.moveTo(event.getX(), event.getY());
						else dv.path.lineTo(event.getX(), event.getY());
					}
					dv.postInvalidate();
				}
				return true;
			}
		});
		
		LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		LayoutParams layoutParam = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		btn = new Button(this);
		btn.setText("Voice!");
		btn.setLayoutParams(params);
		btn.setBackgroundColor(Color.GREEN);
		btn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				if(action==MotionEvent.ACTION_DOWN){
					Log.d("chengyuan", "voice press down");
					btn.setBackgroundColor(Color.RED);
	                Thread thread = new Thread(new Runnable() {
	                    public void run() {
	                      record();
	                    }    
	                });
	                thread.start();
				} else if (action==MotionEvent.ACTION_UP) {
					Log.d("chengyuan", "voice press up");
					btn.setBackgroundColor(Color.GREEN);
					isRecording = false;
					// play();
					Intent i = Recognizer.analyze();
					if(i==null) Toast.makeText(DrawingActivity.this, "Not found!", Toast.LENGTH_SHORT).show();
				}
				return true;
			}
			
		});

		LinearLayout layout1 = new LinearLayout(this);
		layout1.setOrientation(LinearLayout.VERTICAL);
		layout1.setGravity(Gravity.CENTER);
		layout1.addView(dv);
		layout1.addView(btn);
		
		installedApps = new InstalledApps(this).getInstalledApps(false);
		Log.d("chengyuan", "installed apps" + installedApps.toString());
		AppList.getInstance(installedApps.size());
		Log.d("chengyuan", "apps " + AppList.apps.toString());
		listAdapter = new ListViewAdapter(this);
		lv = new ListView(this);
		lv.setAdapter(listAdapter);
		lv.setLayoutParams(params);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					int actualPosition = AppList.getInstance().getAppsToShow().get(position).getId();
					Log.d("chengyuan", "position " + position + " id " + id);
					finish();
					startActivity(installedApps.get(actualPosition).toOpen);
					FeedBack fb = new FeedBack(position);
					analyzer.userFeedback(fb);
					// Toast.makeText(DrawingActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
					listAdapter.clearList();
					lv.setAdapter(listAdapter);
					lv.postInvalidate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});

		LinearLayout layout2 = new LinearLayout(this);
		layout2.setOrientation(LinearLayout.HORIZONTAL);
		layout2.addView((View)layout1);
		layout2.addView(lv);
		this.addContentView(layout2, layoutParam);
		
		//set ShakeDetector
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mShakeDetector = new ShakeDetector();
		mShakeDetector.setOnShakeListener(new OnShakeListener(){
			@Override
			public void onShake(int count) {
				finish();
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
	}
	
	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(mShakeDetector);
		super.onPause();
	}
	
	private void drawAgain(){
		/*for(int i=0;i<n;i++){
			for(int j=0;j<n;j++)
				System.out.println(drawingModel.get(i, j));
		}*/
		Pattern pattern = new DrawPattern(drawingModel.getArray());
		LearningModel lm =
				OnLineLearning.getInstance(AppList.getInstance().getSize(), totalUnit*totalUnit);
		analyzer = new Analyzer(pattern, lm);
		analyzer.analyze();

		Log.d("chengyuan", "analyzed result " + AppList.getInstance().getAppsToShow().toString());
		listAdapter.setList(installedApps);
		lv.setAdapter(listAdapter);
		lv.postInvalidate();
		
		dv.press = false;
		dv.path.reset();
		dv.postInvalidate();
	}
	
	private void record(){
		int frequency = 11025;
	    int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/recordIn.pcm");
	    if(file.exists()) file.delete();
	    try{
	    	file.createNewFile();
	    }catch(IOException e) {
	    	throw new IllegalStateException("Failed to create " + file.toString());
	    }
	    try{
	    	OutputStream os = new FileOutputStream(file);
	    	BufferedOutputStream bos = new BufferedOutputStream(os);
	    	DataOutputStream dos = new DataOutputStream(bos);
	    	int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration,  audioEncoding);
	    	AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
	                                                  frequency, channelConfiguration,
	                                                  audioEncoding, bufferSize);
	      
	    	short[] buffer = new short[bufferSize];  
	    	audioRecord.startRecording();
	    	Log.d("chengyuan", "start record");
	 
	    	isRecording = true ;
	    	while(isRecording) {
	    		int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
	    		for(int i = 0; i<bufferReadResult; i++)
	    			dos.writeShort(buffer[i]);
	    	}
	    	Log.d("chengyuan", "stop record");
	    	audioRecord.stop();
	    	dos.close();	      
	    }catch(Throwable t) {
	    	Log.e("AudioRecord","Recording Failed");
	    }
	}
	
	private void play(){
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/recordIn.pcm");
	    int musicLength = (int)(file.length()/2);
	    short[] music = new short[musicLength];
	    try{
	    	InputStream is = new FileInputStream(file);
	    	BufferedInputStream bis = new BufferedInputStream(is);
	    	DataInputStream dis = new DataInputStream(bis);

	        int i = 0;
	        while (dis.available() > 0) {
	        	music[i] = dis.readShort();
	        	i++;
	        }
	        dis.close();    
	        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
	                                               11025,
	                                               AudioFormat.CHANNEL_CONFIGURATION_MONO,
	                                               AudioFormat.ENCODING_PCM_16BIT,
	                                               musicLength*2,
	                                               AudioTrack.MODE_STREAM);
	        audioTrack.play();
	        audioTrack.write(music, 0, musicLength);
	        audioTrack.stop() ;
	    }catch(Throwable t){
	        Log.e("AudioTrack","Playback Failed");
	    }
	}
}
