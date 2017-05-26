package com.example.mocha;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import source.main.mocha.CspAndroidHelper;

public class MainActivity extends Activity implements PackageSelectionDialog.OnPackageSelectListener,FileSelectionDialog.OnFileSelectListener {
	// option menu id
	private static final int MENUID_PACKAGE= 0;

	private static final int MENUID_APK = 1;
	
	private static final int SETTING = 2;
	
	// initial folda
	private String m_strInitialDir = Environment.getExternalStorageDirectory().toString();
	
	private GlobalVar globalvar;
	
	
	AlertDialog mAlertDlg;
	public boolean start = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		globalvar = (GlobalVar)this.getApplication();

		// make instanse of AlertDialog.Builder 
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dlg_title);
		builder.setMessage(Environment.getDataDirectory().toString());
		builder.setPositiveButton("OK", null);
		
		// make /data/data/com.example.mocha/files directory
		this.getFilesDir();
		//File mkdir_files = new File("/data/data/com.example.mocha/files");
		//mkdir_files.setReadable(true,false);
		if(start){
			//mkdir_files.mkdir();
			start = false;
			// copy res/assets/csp.keystore to /data/data/com.example.mocha/files/directory
			try {
				(new FileHandle()).certsCopy(getAssets().open("csp.keystore"), openFileOutput("csp.keystore",MODE_PRIVATE));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// button 
		Button install_button = (Button)findViewById(R.id.backbutton);
		install_button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				apkUninstall(globalvar.getPackageInfo(globalvar.getSpinnerString()).packageName);
				File file = new File("/data/data/com.example.mocha/files/cspch.apk");
				file.setReadable(true,false);
				apkInstall("/data/data/com.example.mocha/files/cspch.apk");
			}
		});
		
		Button cspedit_button = (Button)findViewById(R.id.button2);
		cspedit_button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO make csp edit function
				Log.d("Mocha", globalvar.getSpinnerString());
				apkCopy(globalvar.getAPKSource(globalvar.getPackageInfo(globalvar.getSpinnerString())));
				MochaMain mocha = new MochaMain();
				//mocha.MochaMain("Back", "/data/data/com.example.mocha/files");
				Boolean unpackZip = MainActivity.unpackZip("/data/data/com.example.mocha/files/Back.apk","/data/data/com.example.mocha/files/");
				ArrayList<String> tmp = new ArrayList<String>(MainActivity.getFiles("/data/data/com.example.mocha/files/Back/"));
				try {
					CspAndroidHelper.CspHelper("/data/data/com.example.mocha/files/Back");
				} catch (Exception e) {
					e.printStackTrace();
				}
				MainActivity.compressZip(tmp, "/data/data/com.example.mocha/files/Back/");
				try {
					Log.d("mocha", "addSignature");
					mocha.addSignature("/data/data/com.example.mocha/files/Back/test", "csp.keystore", "/data/data/com.example.mocha/files");
				} catch (Exception e) {
					e.printStackTrace();
				}
						
				
			}
		});
		
		mAlertDlg = builder.create();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		menu.add(0,MENUID_PACKAGE,0,"Select Package...");
		menu.add(0,MENUID_APK,0,"Select APK...");
		menu.add(0,SETTING,0,"Settings");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()){
		case MENUID_PACKAGE:
			// dialog object
			PackageSelectionDialog dlg = new PackageSelectionDialog(this, this);
			dlg.show(this);
			return true;
		case MENUID_APK:
			FileSelectionDialog apkdlg = new FileSelectionDialog(this,this);
			apkdlg.show(new File(m_strInitialDir));
			return true;
		case SETTING:
			Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
			startActivity(intent);
		}
		return false;
	}
	
	public void onPackageSelect(String packageName,String apkSource) {
		Toast.makeText(this, "Package Uninstall", Toast.LENGTH_SHORT).show();
		apkCopy(apkSource);
		//apkUninstall(packageName);
		
		
	}
	
	public void onFileSelect(File file){
		Toast.makeText(this, "Apk Install"+ file.getPath(), Toast.LENGTH_SHORT).show();
		m_strInitialDir = file.getParent();
		apkCopy(file.getPath());
		MochaMain mocha = new MochaMain();
		Log.d("mocha", this.getFilesDir().getPath());
		try {
			mocha.addSignature("mine.apk", "csp.keystore", this.getFilesDir().getPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		//mocha.MochaMain("Back", this.getFilesDir().getPath());
		//apkInstall(file.getPath());
		
	}
	
	public void apkCopy(String apkPath){
		PackageManager pm = getPackageManager();
		
		if(pm == null){
			Log.d("error", "none package manager");
		}else{
			String workDirPath = this.getFilesDir().getPath();
			InputStream input;
			if(apkPath != ""){
				try{
					File exist = new File(apkPath);
					File copy = new File(workDirPath+"/Back.apk");
					
					input = new FileInputStream(exist);
					OutputStream output = new FileOutputStream(copy);
					
					byte[] buffer = new byte[1024];
					int length;
					while((length = input.read(buffer)) > 0){
						output.write(buffer,0,length);
					}
					output.flush();
					output.close();
					input.close();
				}catch(FileNotFoundException e){
					e.printStackTrace();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 
	 * @param path path of zip
	 * @param zipName name of zip
	 * @return if success unzip return true, else false
	 */
	public static boolean unpackZip(String zipFileFullPath,String unzipPath){
		 File baseFile = new File(zipFileFullPath);
	        File baseDir = new File(baseFile.getParent(), baseFile.getName().substring(0, baseFile.getName().lastIndexOf(".")));
	        if ( !baseDir.mkdir() )
	            System.out.println("Couldn't create directory because directory with the same name exists.: " + baseDir);
	 
	        ZipFile zipFile = null;
	        try {
	            // ZIPファイルオブジェクト作成
	            zipFile = new ZipFile(zipFileFullPath);
	 
	            // ZIPファイル内のファイルを列挙
	            Enumeration<? extends ZipEntry>  enumZip = zipFile.entries();
	 
	            // ZIPファイル内の全てのファイルを展開
	            while ( enumZip.hasMoreElements() ) {
	 
	                // ZIP内のエントリを取得
	                ZipEntry zipEntry = (java.util.zip.ZipEntry)enumZip.nextElement();
	 
	                //出力ファイル取得
	                File unzipFile = new File(unzipPath);
	                File outFile = new File(unzipFile.getAbsolutePath() + "/" + baseDir.getName(), zipEntry.getName());
	                if ( zipEntry.isDirectory() )
	                    outFile.mkdir();
	                else {
	                    // 圧縮ファイル入力ストリーム作成
	                    BufferedInputStream in = new BufferedInputStream(zipFile.getInputStream(zipEntry));
	                    // 親ディレクトリがない場合、ディレクトリ作成
	                    if ( !outFile.getParentFile().exists() )
	                        outFile.getParentFile().mkdirs();
	                    // 出力オブジェクト取得
	                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
	                    // 読み込みバッファ作成
	                    byte[] buffer = new byte[1024];
	                    // 解凍ファイル出力
	                    int readSize = 0;
	                    while ( (readSize = in.read(buffer)) != -1 ) {
	                        out.write(buffer, 0, readSize);
	                    }
	                    // クローズ
	                    try { out.close(); } catch (Exception e) {}
	                    try { in.close(); } catch (Exception e) {}
	                }
	            }

	            // 解凍処理成功

	            return true;

	        } catch(Exception e) {

	            // エラーログ出力

	            System.out.println(e.toString());

	            // 解凍処理失敗

	            return false;

     } finally {

	            if ( zipFile != null )

	                try { zipFile.close();    } catch (Exception e) {}

	        }	
	}
	
	public static boolean compressZip(ArrayList<String> inputFile,String path){
		InputStream is = null;
		
		ZipOutputStream zos = null;
		byte[] buffer = new byte[1024];
		try{
			zos = new ZipOutputStream(new FileOutputStream(path+"test.apk"));
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}
		try{
			for(int i = 0; i < inputFile.size(); i++){
				is = new FileInputStream((String)inputFile.get(i));
				String filename = inputFile.get(i);
				ZipEntry ze = new ZipEntry(filename.replace(path, ""));
				zos.putNextEntry(ze);
				
				int len = 0;
				while((len = is.read(buffer)) != -1){
					zos.write(buffer,0,len);
				}
				is.close();
				zos.closeEntry();
			}
			zos.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		return true;
		
	}
	/**
	 * this function return list of files
	 * but this list don't contain .apk file and "META_INF/" direcotry.
	 * @param path
	 * @return
	 */
	public static ArrayList<String> getFiles(String path){
		ArrayList<String> fileList = new ArrayList<String>();
		File dir = new File(path);
		File[] files = dir.listFiles();
		for(int i = 0; i < files.length; i++){
			String filename = files[i].toString();
			if(!filename.contains(".apk") && !files[i].isDirectory()){
				fileList.add(filename);
			}else if(filename.contains("META-INF")){
				Log.d("mocha file", filename);
				continue;
			}
			if(files[i].isDirectory() && !files[i].toString().contains("META-INF")){
				ArrayList<String> tmp = getFiles(files[i].toString());
				fileList.addAll(tmp);
			}
			
		}
		return fileList;
	}
	
	/**
	 *  Call method at push button
	 */
	
	public void fileDisp(View view){
		// Application List
		PackageManager pm = getPackageManager();
		
		if(pm == null){
			Log.d("test", "none package manager");
		}else{
			Log.d("tagtest", "is it visible?");
			//List<PackageInfo> package_list = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
			String appName = "";
			for(ApplicationInfo app : pm.getInstalledApplications(0)){
				Log.d("AppList", app.packageName+"source Dir ="+app.sourceDir);
				if(app.sourceDir.contains("mine")){
					appName = app.sourceDir;
				}
			}
			String workDirPath = this.getFilesDir().getPath();
			InputStream input;
			if(!appName.equals("")){
				try{
					File exist = new File(appName);
					File copy = new File(workDirPath+"/Back.apk");
					
					input = new FileInputStream(exist);
					OutputStream output = new FileOutputStream(copy);
					
					byte[] buffer = new byte[1024];
					int length;
					while((length = input.read(buffer)) > 0){
						output.write(buffer,0,length);
					}
					output.flush();
					output.close();
					input.close();
					Log.d("test", "finish copy");
				}catch(FileNotFoundException e){
					e.printStackTrace();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 
	 * @param apkName which contains source path of apk that you want to install
	 */
	public void apkInstall(String apkName){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(apkName)),"application/vnd.android.package-archive");
		startActivity(intent);
	}
	
	/**
	 * 
	 * @param packageName which you want to uninstall.
	 */
	public void apkUninstall(String packageName){
		Intent uninstall = new Intent(Intent.ACTION_DELETE);
		// Example
		//uninstall.setData(Uri.parse("package:it.reyboz.minesweeper"));
		uninstall.setData(Uri.parse("package:"+packageName));
		startActivity(uninstall);
	}

}

