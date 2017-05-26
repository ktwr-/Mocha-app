package com.example.mocha;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
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
		int i = 0;
		for(PackageInfo info : package_list){
			if(info.applicationInfo != null){
				if((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0){
					// system image application
					//Log.d("systemApp", info.packageName);
				}else{
					//Log.d("installApp", info.packageName);
					listPackageInfo.add(info);
					i++;
					ApplicationInfo ai;
					try {
						ai = pm.getApplicationInfo(info.packageName, 0);
						m_appSourceValue.put(info, ai.publicSourceDir);
						m_appKeyValue.put(info.applicationInfo.loadLabel(pm).toString(),info);
						Log.d("Mocha", info.applicationInfo.loadLabel(pm).toString());
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
		}
		
	}
	public void onFileSelect(File file){
		Toast.makeText(this, "File Selected : "+file.getPath() , Toast.LENGTH_SHORT).show();
		m_strInitialDir = file.getParent();
	}
		
			

}
