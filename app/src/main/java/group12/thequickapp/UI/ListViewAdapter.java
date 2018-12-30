package group12.thequickapp.UI;

import java.util.ArrayList;

import group12.thequickapp.model.AppList;
import group12.thequickapp.model.PInfo;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ListViewAdapter extends BaseAdapter {
	Context context;
	ArrayList<PInfo> installedApps;
	
	public ListViewAdapter(Context context){
		this.context = context;
		this.installedApps = new ArrayList<PInfo>();
	}
	
	public void setList(ArrayList<PInfo> installedApps){
		this.installedApps = installedApps;
	}
	
	public void clearList(){
		this.installedApps = new ArrayList<PInfo>();
	}
	
	@Override
	public int getCount() {
		return installedApps.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView iv = new ImageView(context);
		int id = AppList.getInstance().getAppsToShow().get(position).getId();
		iv.setImageDrawable(installedApps.get(id).icon);
		//iv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		return iv;
	}

}
