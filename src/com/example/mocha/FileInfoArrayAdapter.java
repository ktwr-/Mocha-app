package com.example.mocha;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FileInfoArrayAdapter extends ArrayAdapter<FileInformation> {
	private List<FileInformation> m_listFileInformation;
	
	// Constructer
	public FileInfoArrayAdapter(Context context, List<FileInformation> objects){
		super(context, -1,objects);
		m_listFileInformation = objects;
	}
	
	// get one item of m_listFileInformation
	@Override
	public FileInformation getItem(int position){
		return m_listFileInformation.get(position);
	}
	
	// make view
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		// make layout
		if(null == convertView){
			Context context = getContext();
			LinearLayout layout = new LinearLayout(context);
			layout.setPadding(10, 10, 10, 10);
			layout.setBackgroundColor(Color.WHITE);
			convertView = layout;
			// text
			TextView textview = new TextView(context);
			textview.setTag("text");
			textview.setTextColor(Color.BLACK);
			textview.setPadding(10, 10, 10, 10);
			layout.addView(textview);
		}
		
		// set value
		FileInformation fileinfo = m_listFileInformation.get(position);
		TextView textview = (TextView)convertView.findViewWithTag("text");
		if(fileinfo.getFile().isDirectory()){
			textview.setText(fileinfo.getName()+"/");
		}else{
			textview.setText(fileinfo.getName());
		}
		
		return convertView;
		
	}
	
	
	

}
