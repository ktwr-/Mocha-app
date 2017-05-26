package com.example.mocha;


import android.util.Log;


public class MochaMain {
	
	MochaMain(){
		
	}
	
	/*public void MochaMain(String apkName, String apkDirectory){
		Log.d("mocha", "in mocha main");
		try {
			Log.d("mocha", "try");
			CspAndroidHelper.CspHelper(apkName, apkDirectory);
			// sdcard 
			addSignature(apkDirectory+"/"+apkName, "csp.keystore","/sdcard");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}*/
	
	public void addSignature(String apkName, String sigName, String sigDirectory) throws Exception{
		PutSignature putSignature = new PutSignature();
		String[] test = {"-p","csp_key1", sigDirectory+"/"+sigName , apkName+".apk", "/data/data/com.example.mocha/files/cspch.apk"};
		putSignature.putSignature(test);
	}

}