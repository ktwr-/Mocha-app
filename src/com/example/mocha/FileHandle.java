package com.example.mocha;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileHandle {
	
	// certification copy at onCreate method
	public void certsCopy(InputStream is, OutputStream os){
		try{
			byte[] buffer = new byte[1024];
			int length = 0;
			while((length = is.read(buffer)) >= 0){
				os.write(buffer,0,length);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}
