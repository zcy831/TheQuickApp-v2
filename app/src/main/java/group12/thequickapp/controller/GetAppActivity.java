package group12.thequickapp.controller;

import java.util.ArrayList;
import java.util.List;

import group12.thequickapp.R;
import group12.thequickapp.UI.GridSettingModel;
import group12.thequickapp.UI.ListViewAdapterForGetApp;
import group12.thequickapp.model.PInfo;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;



/**
 * GetAppActivity is the second step of the set up part. It shows the list of the apps
 * installed in the cell phone and allow user to select which app to be opened.
 * @author Group 12
 *
 */
public class GetAppActivity extends Activity {
	/**
	 * The list of app information installed on the cell phone
	 */
	ArrayList<PInfo> appList;

	/**
	 *  The Layout of the app info
	 */

	GridSettingModel gridModel;

	/**
	 * The message to be displayed when confirm button is pressed
	 */
	String confirmMessage;

	/**
	 * Represents if the selected app can be opened or not
	 */
	boolean isConfirmable;

	ImageView selectedAppView;
	Button confirmButton;
	PInfo selectedApp;
	PackageInfoGenerator packageInfoGenerator = new PackageInfoGenerator(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_app);
		Intent i = getIntent();
		if(i.getStringExtra("FromWhich").equals("GridSetActivity")){
			gridModel = (GridSettingModel) i.getSerializableExtra("TheModel");
			confirmButton = (Button) findViewById(R.id.button1);
			selectedAppView = (ImageView) findViewById(R.id.imageView1);
			populateListView();
			confirmButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {				
					if(isConfirmable){
						gridModel.setSavedNames(selectedApp.appname);
						Intent openNaming = new Intent(GetAppActivity.this, NamingActivity.class);
						openNaming.putExtra("TheModel2", gridModel);
						openNaming.putExtra("FromWhich", "GridSetActivity");
						startActivity(openNaming);
					}else{
						Toast.makeText(GetAppActivity.this, "The app cannot be opened! Please choose another app!", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}else{
			confirmButton = (Button) findViewById(R.id.button1);
			selectedAppView = (ImageView) findViewById(R.id.imageView1);
			populateListView();
			final int threshold = i.getIntExtra("threshold", 0);
			confirmButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {				
					if(isConfirmable){
						Intent openNaming = new Intent(GetAppActivity.this, NamingActivity.class);
						openNaming.putExtra("Threshold", threshold);
						openNaming.putExtra("appName", selectedApp.getAppname());
						openNaming.putExtra("FromWhich", "AudioRecordActivity");
						startActivity(openNaming);
					}else{
						Toast.makeText(GetAppActivity.this, "The app cannot be opened! Please choose another app!", Toast.LENGTH_SHORT).show();
					}
				}
			});

		}
	}
	/**
	 * This method get the applist, set up the ListView and the adapter
	 */
	private void populateListView() {
		appList = packageInfoGenerator.getInstalledApps(false);
		ListView lv = (ListView) findViewById(R.id.TheListView);
		List<String> appNames = new ArrayList<String>();
		List<Drawable> appIcons = new ArrayList<Drawable>();
		for(PInfo pi : appList){
			appNames.add(pi.getAppname());
			appIcons.add(pi.getIcon());
		}
		ListViewAdapterForGetApp adapter = new ListViewAdapterForGetApp(this, R.layout.app_name_icon, appNames.toArray(new String[appNames.size()]),appIcons.toArray(new Drawable[appIcons.size()]));
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				if(appList.get(position).toOpen!=null){
					isConfirmable = true;
					selectedAppView.setImageDrawable(appList.get(position).getIcon());
					selectedApp = appList.get(position);

				}else{
					isConfirmable = false;
					selectedAppView.setImageDrawable(getResources().getDrawable(R.drawable.error));
					selectedApp = null;
				}
			}
		});
	}




}
