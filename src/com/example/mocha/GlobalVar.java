package com.example.mocha;

import java.util.HashMap;

import android.app.Application;
import android.content.pm.PackageInfo;

public class GlobalVar extends Application {
	
	private String spinnertext;
	private HashMap<PackageInfo,String> m_appSourceValue = new HashMap<PackageInfo,String>();
	private HashMap<String,PackageInfo> m_appKeyValue= new HashMap<String,PackageInfo>();
	private String certssource="/data/data/com.example.mocha/files/csp.keystore";

	@Override
	public void onCreate() {
	}
	
	public String getSpinnerString(){
		return spinnertext;
	}
	
	public void setSpinnerString(String str){
		spinnertext = str;
	}
	
	public void setKeyValue(HashMap<String,PackageInfo> map){
		m_appKeyValue = new HashMap<String,PackageInfo>(map);
	}
	
	public void setSourceValue(HashMap<PackageInfo,String> map){
		m_appSourceValue= new HashMap<PackageInfo,String>(map);
	}
	
	public PackageInfo getPackageInfo(String key){
		return m_appKeyValue.get(key);
	}
	
	public String getAPKSource(PackageInfo key){
		return m_appSourceValue.get(key);
	}
	
	public void setCertsSource(String str){
		certssource = str;
	}
	public String getCertsString(){
		return certssource;
	}
	
	

}
