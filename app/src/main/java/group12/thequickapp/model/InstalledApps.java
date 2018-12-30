package group12.thequickapp.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

public class InstalledApps {

	private Context context;
	
	public InstalledApps(Context context){
		this.context = context;
	}
	
	public ArrayList<PInfo> getInstalledApps(boolean getSysPackages){
		ArrayList<PInfo> res = new ArrayList<PInfo>();
		List<PackageInfo> packs = this.context.getPackageManager().getInstalledPackages(0);
		// List<ApplicationInfo> packs2 = this.context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
		// Log.d("chengyuan", "packs " + packs2.toString());
		for(int i=0;i<packs.size();i++) {
			PackageInfo p = packs.get(i);
			if(!getSysPackages&&isSystemPackage(p)){
				continue;
			}
			PInfo newInfo = new PInfo();
			newInfo.appname = p.applicationInfo.loadLabel(this.context.getPackageManager()).toString();
			newInfo.pname = p.packageName;
			newInfo.icon = p.applicationInfo.loadIcon(this.context.getPackageManager());
			newInfo.toOpen = this.context.getPackageManager().getLaunchIntentForPackage(p.packageName);
			res.add(newInfo);
		}
		return res;
	}
	
	private boolean isSystemPackage(PackageInfo pkgInfo){
		if((pkgInfo.applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)!=0) return true;
		else return false;
	}
	
	
}
