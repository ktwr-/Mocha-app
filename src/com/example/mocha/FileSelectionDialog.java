package com.example.mocha;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class FileSelectionDialog implements OnItemClickListener{
	
	private Context m_parent; // call from
	private OnFileSelectListener m_listener; // recieve result
	private AlertDialog m_dlg; //dialog
	private FileInfoArrayAdapter m_fileinfoarrayadapter; //failinfoadapter
	private boolean unpackZip;

	// constructer
	public FileSelectionDialog(Context context, OnFileSelectListener listener){
		m_parent = context;
		m_listener = (OnFileSelectListener)listener;
	}
	
	public void show(File fileDirectory){
		// title
		String strTitle = fileDirectory.getAbsolutePath();
		
		// list view
		ListView listview = new ListView(m_parent);
		listview.setScrollingCacheEnabled(false);
		listview.setOnItemClickListener(this);
		// file list
		File[] aFile = fileDirectory.listFiles();
		List<FileInformation> listFileInfo = new ArrayList<FileInformation>();
		if(null != aFile){
			for(File fileTemp : aFile){
				listFileInfo.add(new FileInformation(fileTemp.getName(),fileTemp));
			}
			Collections.sort(listFileInfo);
		}
		if(null != fileDirectory.getParent()){
			listFileInfo.add(0, new FileInformation("..",new File(fileDirectory.getParent())));
		}
		m_fileinfoarrayadapter = new FileInfoArrayAdapter(m_parent,listFileInfo);
		listview.setAdapter(m_fileinfoarrayadapter);
		
		Builder builder = new AlertDialog.Builder(m_parent);
		builder.setTitle(strTitle);
		builder.setPositiveButton("Cancel", null);
		builder.setView(listview);
		m_dlg = builder.show();
		
	}
	
	public void onItemClick(AdapterView<?> l, View v, int position, long id){
		Log.d("mocha", "test");
		if(null != m_dlg){
			m_dlg.dismiss();
			m_dlg = null;
		}
		FileInformation fileinfo = m_fileinfoarrayadapter.getItem(position);
		if(true == fileinfo.getFile().isDirectory()){
			show(fileinfo.getFile());
		}else{
			//m_listener.onFileSelect(fileinfo.getFile());
			MochaMain mocha = new MochaMain();
			//mocha.MochaMain("Back", "/data/data/com.example.mocha/files");
			unpackZip = MainActivity.unpackZip("/data/data/com.example.mocha/files/Back.apk","/data/data/com.example.mocha/files/");
			ArrayList<String> tmp = new ArrayList<String>(MainActivity.getFiles("/data/data/com.example.mocha/files/Back/"));
			MainActivity.compressZip(tmp, "/data/data/com.example.mocha/files/Back/");
			try {
				Log.d("mocha", "addSignature");
				mocha.addSignature("/data/data/com.example.mocha/files/csp/test", "csp.keystore", "/data/data/com.example.mocha/files");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	
	public interface OnFileSelectListener{
		public void onFileSelect(File file);
	}
}
