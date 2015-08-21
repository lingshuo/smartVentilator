package cn.lisa.smartventilator.debug;

import android.util.Log;

public class Debug {
//	public static boolean DEBUG_ALL=true;
//	public static boolean DEBUG_MID=true;
//	public static boolean DEBUG_SWITCH=true;
//	public static boolean DEBUG_MANAGER=true;
//	public static boolean DEBUG_SERVICE_MONITOR=true;
//	public static boolean DEBUG_SERVICE_SV=true;
//	public static boolean DEBUG_SERVICE_HEARTBEAT=true;
//	public static boolean DEBUG_UART=true;
//	public static boolean DEBUG_LOCK=true;
//	public static boolean DEBUG_VENTILATOR=true;
	
	
	public static boolean DEBUG_UPDATE = true;
	public static boolean DEBUG_ALL=true;
	public static boolean DEBUG_MID=false;
	public static boolean DEBUG_SWITCH=false;
	public static boolean DEBUG_MANAGER=false;
	public static boolean DEBUG_SERVICE_MONITOR=false;
	public static boolean DEBUG_SERVICE_SV=true;
	public static boolean DEBUG_SERVICE_HEARTBEAT=false;
	public static boolean DEBUG_UART=false;
	public static boolean DEBUG_LOCK=false;
	public static boolean DEBUG_VENTILATOR=false;
	public static boolean DEBUG_TIME=true;
	
	public static void info(boolean on, String tag,String mtag,String msg) {
		if(on&&DEBUG_ALL) {
			if(!mtag.equals(""))
				Log.i(tag,mtag+":"+msg);
			else
				Log.i(tag,msg);
		}
	}
	
	public static void info(boolean on, String tag,String mtag,int msg) {
		if(on&&DEBUG_ALL) {
			if(!mtag.equals(""))
				Log.i(tag,mtag+":"+msg);
			else
				Log.i(tag,""+msg);
		}
	}
	
}
