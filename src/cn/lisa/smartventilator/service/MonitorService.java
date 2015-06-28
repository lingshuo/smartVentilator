package cn.lisa.smartventilator.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
/**
 * 
 * */
public class MonitorService extends Service {

	public static final String BROADCASTACTION = "getinfo";
	Timer timer;
	@Override
	
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		timer=new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				String jsonString=getVentilator();
				Intent intent = new Intent();  
                intent.setAction( BROADCASTACTION );  
                intent.putExtra( "jsonstr", jsonString );  
                sendBroadcast(intent);
			}
		},0, 10*1000);
		return super.onStartCommand(intent, flags, startId);
	}
	
	private String getVentilator() {
		String srsString = "";  
        try  
        {  
            srsString = getJsonStringGet( "http://lingshuo.net.cn" );  
        }  
        catch (Exception e)  
        {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
            Log.i( "sv",e.getMessage() );  
        }  
  
        return srsString; 
	}
	
	 public String getJsonStringGet(String uri) throws Exception  
	    {  
	        String result = null;  
	        URL url = new URL( uri );  
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
	        conn.setConnectTimeout( 6 * 1000 );// 设置连接超时  
	        Log.i( "sv", conn.getResponseCode() + conn.getResponseMessage() );  
	        if (conn.getResponseCode() == 200)  
	        {  
	            Log.i( "sv", "成功" );  
	            InputStream is = conn.getInputStream();// 得到网络返回的输入流  
	            result = readData( is, "UTF-8" );  
	        }  
	        else  
	        {  
	            Log.i( "sv", "失败" );  
	            result = "";  
	        }  
	        conn.disconnect();  
	        return result;  
	  
	    }  
	  
	  
	    private String readData(InputStream inSream, String charsetName) throws Exception  
	    {  
	        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
	        byte[] buffer = new byte[1024];  
	        int len = -1;  
	        while ((len = inSream.read( buffer )) != -1)  
	        {  
	            outStream.write( buffer, 0, len );  
	        }  
	        byte[] data = outStream.toByteArray();  
	        outStream.close();  
	        inSream.close();  
	        return new String( data, charsetName );  
	    }  
	      
	      
	    @Override  
	    public void onDestroy()  
	    {  
	        // TODO Auto-generated method stub  
	        super.onDestroy();  
	        if(timer != null){  
	            timer.cancel();  
	        }  
	    }  


}
