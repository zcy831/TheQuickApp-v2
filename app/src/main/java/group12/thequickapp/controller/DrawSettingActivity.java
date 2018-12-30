package group12.thequickapp.controller;

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
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class DrawSettingActivity extends Activity {
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ShakeDetector mShakeDetector;
	
	private DrawingModel drawingModel;
	private DrawingView dv;
	private ArrayList<PInfo> installedApps;
	
	private ListView lv;
	private ListViewAdapter listAdapter;
	private Handler handler;
	// private int n;
	private int totalUnit;
	private Analyzer analyzer;
	
	private Button returnToMain;
	
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
                // Log.d("chengyuan", "x " + s);
                // Log.d("chengyuan", "y " + t);
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
		
		/*Button btn = new Button(this);
		btn.setText("Go!");
		btn.setLayoutParams(params);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(DrawingActivity.this, GridActivity.class);
				finish();
				startActivity(i);
			}
		});*/
		returnToMain = new Button(this);
		returnToMain.setText("return");
		returnToMain.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent toMain = new Intent(DrawSettingActivity.this, MainActivity.class);
				startActivity(toMain);
			}
		});
		
		LinearLayout layout1 = new LinearLayout(this);
		layout1.setOrientation(LinearLayout.VERTICAL);
		layout1.setGravity(Gravity.CENTER);
		layout1.addView(returnToMain);
		layout1.addView(dv);


		
		installedApps = new InstalledApps(this).getInstalledApps(false);
		AppList.getInstance(installedApps.size());
		//for(int i=0;i<10;i++) 
			//System.err.println(AppList.getInstance().getAppsToShow().get(i).getId());
		listAdapter = new ListViewAdapter(this);
		lv = new ListView(this);
		lv.setAdapter(listAdapter);
		lv.setLayoutParams(params);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					//id= AppList.getInstance().getAppsToShow().get(position).getId();
					//finish();
					//startActivity(installedApps.get(position).toOpen);
					FeedBack fb = new FeedBack(position);
					analyzer.userFeedback(fb);
					// Toast.makeText(DrawSettingActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
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
		
		listAdapter.setList(installedApps);
		lv.setAdapter(listAdapter);
		lv.postInvalidate();

		dv.press = false;
		dv.path.reset();
		dv.postInvalidate();
	}
}
