package cn.lisa.smartventilator.receiver;


import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LockReceiver extends DeviceAdminReceiver{ 
	   
	   
    @Override 
    public void onReceive(Context context, Intent intent) { 
        super.onReceive(context, intent); 
        Log.i("sv","onreceiver"); 
    } 
   
    @Override 
    public void onEnabled(Context context, Intent intent) { 
        Log.i("sv","����ʹ��"); 
        super.onEnabled(context, intent); 
    } 
   
    @Override 
    public void onDisabled(Context context, Intent intent) {
    	Log.i("sv","ȡ������");
        super.onDisabled(context, intent); 
    } 
   
   
} 
