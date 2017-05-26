package com.example.mocha;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.app.AlertDialog.Builder;

public class PackageSelectionDialog implements OnItemClickListener{
	
	private Context m_parent; // call from
	private OnPackageSelectListener m_listener; // recieve result
	private AlertDialog m_dlg; //dialog
	private PackageInfoArrayAdapter m_packageinfoarrayadapter; //failinfoadapter
	
	private HashMap<PackageInfo,String> m_appKeyValue = new HashMap<PackageInfo,String>();
	private HashMap<PackageInfo,String> m_appSourceValue = new HashMap<PackageInfo,String>();

	// constructer
	public PackageSelectionDialog(Context context, OnPackageSelectListener listener){
		m_parent = context;
		m_listener = (OnPackageSelectListener)listener;
	}
	
	public void show(Context context){
		// title
		String strTitle = "Application List";
		
		// list view
		ListView listview = new ListView(m_parent);
		listview.setScrollingCacheEnabled(false);
		listview.setOnItemClickListener(this);
		List<PackageInfo> listPackageInfo = new ArrayList<PackageInfo>();
		
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> package_list = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		for(PackageInfo info : package_list){
			if(info.applicationInfo != null){
				if((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0){
					// system image application
					//Log.d("systemApp", info.packageName);
				}else{
					//Log.d("installApp", info.packageName);
					listPackageInfo.add(info);
					ApplicationInfo ai;
					try {
						ai = pm.getApplicationInfo(info.packageName, 0);
						m_appSourceValue.put(info, ai.publicSourceDir);
						m_appKeyValue.put(info, info.applicationInfo.loadLabel(pm).toString());
						Log.d("Mocha", ai.publicSourceDir);
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
		m_packageinfoarrayadapter = new PackageInfoArrayAdapter(m_parent,listPackageInfo);
		listview.setAdapter(m_packageinfoarrayadapter);
		
		Builder builder = new AlertDialog.Builder(m_parent);
		builder.setTitle(strTitle);
		builder.setPositiveButton("Cancel", null);
		builder.setView(listview);
		m_dlg = builder.show();
		
	}
	
	public void onItemClick(AdapterView<?> l, View v, int position, long id){
		if(null != m_dlg){
			m_dlg.dismiss();
			m_dlg = null;
		}
		PackageInfo packageinfo = m_packageinfoarrayadapter.getItem(position);
		m_listener.onPackageSelect(m_appKeyValue.get(packageinfo), m_appSourceValue.get(packageinfo));
		
	}
	
	public interface OnPackageSelectListener{
		public void onPackageSelect(String packageName,String apkSource);
	}
	
	public HashMap<PackageInfo,String> getApkSource(){
		return m_appSourceValue;
	}
	public HashMap<PackageInfo,String> getPackageName(){
		return m_appKeyValue;
	}
}
