package com.example.mocha;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class SettingActivity extends Activity implements FileSelectionDialog.OnFileSelectListener{

	int setting = 0;
	private String m_strInitialDir = Environment.getExternalStorageDirectory().toString();
	private HashMap<String,PackageInfo> m_appKeyValue = new HashMap<String,PackageInfo>();
	private HashMap<PackageInfo,String> m_appSourceValue = new HashMap<PackageInfo,String>();
	private List<PackageInfo> package_list;
	private String spinnerItems[];
	public TextView spinnertext;
	public GlobalVar globalvar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		
		// back button
		Button btn = (Button)findViewById(R.id.backbutton);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				Intent intent = new Intent(getApplicationContext(),MainActivity.class);
				startActivity(intent);
			}	
		});
		
		// set Certification location
		//if(setting == 0){
			TextView textView = (TextView)findViewById(R.id.textView3);
			textView.setText("/data/data/com.example.mocha/files/csp.keystore");
			setting = 1;
		//}
		Button selectcerts = (Button)findViewById(R.id.selectcerts);
		selectcerts.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				callFileSelection();
			}	
		});
		
		globalvar = (GlobalVar)this.getApplication();
		// set Spinner
		getPackage();
		spinnertext = (TextView)findViewById(R.id.spinnertext1);
		Spinner spinner = (Spinner)findViewById(R.id.spinner1);
		Log.d("Mocha", String.valueOf(spinnerItems.length));
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,spinnerItems);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent,View view, int position, long id){
				Spinner spinner = (Spinner) parent;
				String item = (String)spinner.getSelectedItem();
				if(!item.isEmpty()){
					spinnertext.setText(item);
					globalvar.setSpinnerString(item);
				}
				
			}
			
			public void onNothingSelected(AdapterView<?> parent){
				
			}
		});
	}		
	
	
	public void callFileSelection(){
		FileSelectionDialog apkdlg = new FileSelectionDialog(this,this);
		apkdlg.show(new File(m_strInitialDir));
		
	}
	public void getPackage(){
		List<PackageInfo> listPackageInfo = new ArrayList<PackageInfo>();
		
		PackageManager pm = this.getPackageManager();
		package_list = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		ArrayList<String> defaultApp;
		int i = 0;
		for(PackageInfo info : package_list){
			if(info.applicationInfo != null){
				if((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0){
					// system image application
					//Log.d("systemApp", info.packageName);
				}else{
					//Log.d("installApp", info.packageName);
					listPackageInfo.add(info);
					ApplicationInfo ai;
					defaultApp = new ArrayList<String>(RestoreDefaultApp());
					try {
						ai = pm.getApplicationInfo(info.packageName, 0);
						if(compareDefaultApp(defaultApp,ai.publicSourceDir) && !info.applicationInfo.loadLabel(pm).toString().equals("Mocha")){
							i++;
							m_appSourceValue.put(info, ai.publicSourceDir);
							m_appKeyValue.put(info.applicationInfo.loadLabel(pm).toString(),info);
							//writeDefaultApp(ai.publicSourceDir);
							Log.d("Mocha", info.applicationInfo.loadLabel(pm).toString());
						}
							
							
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
		globalvar.setKeyValue(m_appKeyValue);
		globalvar.setSourceValue(m_appSourceValue);
		spinnerItems = new String[i];
		i = 0;
		for(String key : m_appKeyValue.keySet()){
			spinnerItems[i++] = key;
			Log.d("mocha", String.valueOf(m_appKeyValue.size()));
		}
		Log.d("mocha", String.valueOf(spinnerItems.length));
		
	}
	public void onFileSelect(File file){
		Toast.makeText(this, "File Selected : "+file.getPath() , Toast.LENGTH_SHORT).show();
		m_strInitialDir = file.getParent();
	}
	
	// for evaluate
	public void writeDefaultApp(String str){
		try {
			FileOutputStream fileOutputstream = openFileOutput("app.txt",Context.MODE_APPEND);
			fileOutputstream.write(str.getBytes());
			fileOutputstream.write("\n".getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean compareDefaultApp(ArrayList<String> list,String str){
		if(list != null && list.contains(str)){
			return false;
		}
		return true;
		
	}
	
	public ArrayList<String> RestoreDefaultApp(){
		ArrayList<String> defaultAppList = new ArrayList<String>();
		try {
			FileInputStream fis = openFileInput("app.txt");
			String lineBuffer = null;
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis,"UTF-8"));
			while( (lineBuffer = reader.readLine()) != null){
				defaultAppList.add(lineBuffer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return defaultAppList;
	}
		
			

}
