package group12.thequickapp.controller;

import group12.thequickapp.R;
import group12.thequickapp.UI.GridSettingModel;
import group12.thequickapp.model.DatabaseOperations;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NamingActivity extends Activity {

	EditText name;
	EditText introduction;
	String theName;
	String theIntro;
	String appName;
	Integer threshold;
	GridSettingModel gridModel;
	Button saveButton;
	Context ct = this;
	boolean namingDuplicate = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_naming);
		name = (EditText) findViewById(R.id.editText1);
		introduction = (EditText) findViewById(R.id.editText2);
		Intent i = getIntent();
		Log.d("chengyuan", "From Which " + i.getStringExtra("FromWhich"));
		if(i.getStringExtra("FromWhich").equals("GridSetActivity")) {
			gridModel = (GridSettingModel) i.getSerializableExtra("TheModel2");
			saveButton = (Button) findViewById(R.id.saveRelation);
			saveButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					DatabaseOperations dop = new DatabaseOperations(ct);
					theName = name.getText().toString();
					theIntro = introduction.getText().toString();
					try {
						Cursor cr = dop.getInformation(dop);
						cr.moveToFirst();
						do {
							String temp = cr.getString(0);
							if (temp.equals(theName)) {
								namingDuplicate = true;
							}
						} while (cr.moveToNext());
					} catch (Exception e) {
					}
					saveRelationForGrid();
				}
			});
		} else {
			appName = i.getStringExtra("appName");
			threshold = i.getIntExtra("threshold", 0);
			saveButton = (Button) findViewById(R.id.saveRelation);
			saveButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					DatabaseOperations dop = new DatabaseOperations(ct);
					theName = name.getText().toString();
					theIntro = introduction.getText().toString();
					try{
					Cursor cr = dop.getInformation(dop);
					cr.moveToFirst();
					do{
						String temp= cr.getString(0);
						if(temp.equals(theName)){
							namingDuplicate = true;
						}
					}while(cr.moveToNext());
					}catch(Exception e){
					}
					saveRelationForSound();
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.naming, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void saveRelationForGrid() {
		if(namingDuplicate == false){
		DatabaseOperations dbo = new DatabaseOperations(ct);
		dbo.putInformation(dbo, theName, "Grid", gridModel.getSavedNames());
		dbo.putInformationToGridDB(dbo, theName, gridModel.getSavedPattern());
		startActivity(new Intent(NamingActivity.this, MainActivity.class));
		Toast.makeText(NamingActivity.this, "Relation saved!", Toast.LENGTH_SHORT).show();
		}else{
			namingDuplicate = false;
			Toast.makeText(NamingActivity.this, "The name has already exist", Toast.LENGTH_SHORT).show();	
		}
	}
	
	public void saveRelationForSound(){
		if(namingDuplicate == false){
			DatabaseOperations dbo = new DatabaseOperations(ct);
			dbo.putInformation(dbo, theName, "Sound", appName);
			dbo.putInformationToSoundDB(dbo, theName, "12345", threshold);
			startActivity(new Intent(NamingActivity.this, MainActivity.class));
			Toast.makeText(NamingActivity.this, "Relation saved!", Toast.LENGTH_SHORT).show();
//			File from = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/re.pcm");
//			File to = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + theName);
//			from.renameTo(to);
			}else{
				namingDuplicate = false;
				Toast.makeText(NamingActivity.this, "The name has already exist", Toast.LENGTH_SHORT).show();	
			}
	}
}
