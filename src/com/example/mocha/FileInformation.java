package com.example.mocha;

import java.io.File;

public class FileInformation implements Comparable<FileInformation>{
	
	// file name
	private String m_strName;
	// file object
	private File m_file;
	
	/**
	 * constructer
	 * @param strName(file name)
	 * @param file which is file object
	 */
	public FileInformation(String strName, File file){
		m_strName = strName;
		m_file = file;
	}
	
	public String getName(){
		return m_strName;
	}
	
	public File getFile(){
		return m_file;
	}

	public int compareTo(FileInformation another) {
		if( true == m_file.isDirectory() && false == another.getFile().isDirectory()){
			return -1;
		}
		if( false == m_file.isDirectory() && true == another.getFile().isDirectory()){
			return 1;
		}
		// sort dictionaly
		return m_file.getName().toLowerCase().compareTo(another.getFile().getName().toLowerCase());
	}
	

}
