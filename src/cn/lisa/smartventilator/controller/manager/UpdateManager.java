package cn.lisa.smartventilator.controller.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONObject;

import cn.lisa.smartventilator.utility.system.NetHelper;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

@SuppressLint("SdCardPath")
public class UpdateManager {

	@SuppressWarnings("unused")
	private String curVersion;
	private String newVersion;
	private int curVersionCode;
	private int newVersionCode;
	private String updateInfo;
	private String updateurl;
	private UpdateCallback callback;
	private Context ctx;
    
	private int progress;  
	private Boolean hasNewVersion;
	private Boolean canceled;

	//存放更新APK文件相应的版本说明路径
	public static final String UPDATE_CHECKURL = "http://update.yanji.lisatech.cn:8080/newest.jsp";
	public static final String UPDATE_APKNAME = "update_test.apk";
	//public static final String UPDATE_VERJSON = "ver.txt";
	public static final String UPDATE_SAVENAME = "smartVentilator.apk";
	private static final int UPDATE_CHECKCOMPLETED = 201;
	private static final int UPDATE_DOWNLOADING = 202; 
	private static final int UPDATE_DOWNLOAD_ERROR = 203; 
	private static final int UPDATE_DOWNLOAD_COMPLETED = 204; 
	private static final int UPDATE_DOWNLOAD_CANCELED = 205;

	 //从服务器上下载apk存放文件夹
//	 private String savefolder = "/mnt/innerDisk/";
	 private String savefolder = "/sdcard/";
		//public static final String SAVE_FOLDER =Storage. // "/mnt/innerDisk";
	public UpdateManager(Context context, UpdateCallback updateCallback) {
		ctx = context;
		callback = updateCallback;
		//savefolder = context.getFilesDir();
		canceled = false;
		getCurVersion();
	}
	
	public String getNewVersionName()
	{
		return newVersion;
	}
	
	public String getUpdateInfo()
	{
		return updateInfo;
	}

	public String getUpdateUrl()
	{
		return updateurl;
	}
	private void getCurVersion() {
		try {
			PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(
					ctx.getPackageName(), 0);
			curVersion = pInfo.versionName;
			curVersionCode = pInfo.versionCode;
		} catch (NameNotFoundException e) {
			Log.e("update", e.getMessage());
		} catch(Exception e){
			;
		}

	}

	public boolean checkUpdate() {		
		hasNewVersion = false;
		new Thread(){
			// ***************************************************************
			/**
			 * @by wainiwann add
			 * 
			 */
			@Override
			public void run() {
				Log.i("@@@@@", ">>>>>>>>>>>>>>>>>>>>>>>>>>>getServerVerCode() ");
				try {

					String verpage = NetHelper.getJsonStringGet(UPDATE_CHECKURL);
					Log.i("@@@@", verpage
							+ "**************************************************");
					String verjson=verpage.substring(verpage.indexOf('{'), verpage.indexOf('}'));
					verjson=verjson+'}';
					Log.i("@@@@", verjson
							+ "**************************************************");
						JSONObject obj = new JSONObject(verjson);
						try {
							newVersionCode = Integer.parseInt(obj.getString("newNumber"));
							newVersion = obj.getString("newName");
							updateurl=obj.getString("newDownloadlink");
							updateInfo = "";
							Log.i("@@@newVerCode", newVersionCode+"");
							Log.i("@@@newVerName", newVersion);
							Log.i("@@@UPDATE_DOWNURL", updateurl);
							if (newVersionCode > curVersionCode) {
								hasNewVersion = true;
							}
						} catch (Exception e) {
							newVersionCode = -1;
							newVersion = "";
							updateInfo = "";
							
						}
					}
				catch (Exception e) {
					Log.e("update", e.getMessage());
				}
				updateHandler.sendEmptyMessage(UPDATE_CHECKCOMPLETED);
			};
			// ***************************************************************
		}.start();
		return hasNewVersion;
	}

	public void update() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		
		intent.setDataAndType(
				Uri.fromFile(new File(savefolder, UPDATE_SAVENAME)),
				"application/vnd.android.package-archive");
		ctx.startActivity(intent);
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	public void downloadPackage() 
	{
		
		
		new Thread() {			
			 @Override  
		        public void run() {  
		            try {  

		                URL url = new URL(getUpdateUrl());  
		                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		                conn.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
		        		
		                conn.connect();  
		                int length = conn.getContentLength();  
		                InputStream is = conn.getInputStream();  
		                  
		               
		                File ApkFile = new File(savefolder,UPDATE_SAVENAME);
		                
		                
		                if(ApkFile.exists())
		                {
		                	
		                	ApkFile.delete();
		                }
		                
		                
		                FileOutputStream fos = new FileOutputStream(ApkFile);  
		                 
		                int count = 0;  
		                byte buf[] = new byte[512];  
		                  
		                do{  
		                	
		                    int numread = is.read(buf);  
		                    count += numread;  
		                    progress =(int)(((float)count / length) * 100);  
		                   
		                    updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOADING)); 
		                    if(numread <= 0){      
		                        
		                    	updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_COMPLETED);
		                        break;  
		                    }  
		                    fos.write(buf,0,numread);  
		                }while(!canceled);  
		                if(canceled)
		                {
		                	updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_CANCELED);
		                }
		                fos.close();  
		                is.close();  
		            } catch (MalformedURLException e) {  
		                e.printStackTrace(); 
		                
		                updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOAD_ERROR,e.getMessage()));
		            } catch(IOException e){  
		                e.printStackTrace();  
		                
		                updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOAD_ERROR,e.getMessage()));
		            }  
		              
		        } 
		}.start();
	}

	@SuppressLint("HandlerLeak")
	public void cancelDownload()
	{
		canceled = true;
	}
	
	@SuppressLint("HandlerLeak")
	Handler updateHandler = new Handler() 
	{
		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case UPDATE_CHECKCOMPLETED:
				
				callback.checkUpdateCompleted(hasNewVersion, newVersion);
				break;
			case UPDATE_DOWNLOADING:
				
				callback.downloadProgressChanged(progress);
				break;
			case UPDATE_DOWNLOAD_ERROR:
				
				callback.downloadCompleted(false, msg.obj.toString());
				break;
			case UPDATE_DOWNLOAD_COMPLETED:
				
				callback.downloadCompleted(true, "");
				break;
			case UPDATE_DOWNLOAD_CANCELED:
				
				callback.downloadCanceled();
			default:
				break;
			}
		}
	};

	public interface UpdateCallback {
		public void checkUpdateCompleted(Boolean hasUpdate,
				CharSequence updateInfo);

		public void downloadProgressChanged(int progress);
		public void downloadCanceled();
		public void downloadCompleted(Boolean sucess, CharSequence errorMsg);
	}

}