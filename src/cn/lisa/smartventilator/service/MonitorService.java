package cn.lisa.smartventilator.service;

import java.util.Timer;
import org.json.JSONObject;

import cn.lisa.smartventilator.hardware.UartAgent;
import cn.lisa.smartventilator.manager.VentilatorManager;
import cn.lisa.smartventilator.network.DevDefine;
import cn.lisa.smartventilator.network.DevMonitor;
import cn.lisa.smartventilator.network.HostDefine;
import cn.lisa.smartventilator.network.JSONDefine;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * 
 * */
public class MonitorService extends Service {

	public static final String BROADCASTACTION = "getinfo";
	public static final String SENDACTION = "send";
	Timer timer;
	public UartAgent uartagent;
	SendReciever sendReciever;
	/***
	 * handle to send switch info to hardware
	 */
	Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case VentilatorManager.SEND_DATA:
				byte[] buf = new byte[2];
				buf[0] = (byte) 0x01;
				buf[1] = (byte)msg.arg1;
//				Log.i("switch", "handle:" + buf[1]);
				uartagent.frame.send(buf, buf.length);
				break;
			default:
				break;
			}			
		};
	};
	
	@Override
	public void onCreate() {
		initSendReceiver();

		super.onCreate();
	}

	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// baudrate:115200, data:8,stop:1, parity:N
		// uartagent=new UartAgent("/dev/ttyO1", 115200, 8, 1, (byte)'N', true);
		// baudrate:ttyS6, 38400, data:8,stop:1, parity:N
		uartagent = new UartAgent("/dev/ttyS6", 38400, 8, 1, (byte) 'N', true);
		uartagent.init();
		//thread to get info from hardware and report info to network
		new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {
					String jsonString = uartagent.getStatusBlock();
					Log.i("sv", "load_data:" + jsonString);
					if (jsonString == null || "".equals(jsonString))
						continue;

					Intent intent = new Intent();
					intent.setAction(BROADCASTACTION);
					intent.putExtra("jsonstr", jsonString);
					sendBroadcast(intent);
					
					VentilatorManager manager=new VentilatorManager(getBaseContext());
					manager.reportData(jsonString);
					manager=null;
				}
			}
		}).start();
		
		//Thread to send switch to network
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				DevMonitor monitor = new DevMonitor(HostDefine.HOSTID_LTCP, DevDefine.FAKE_ID);
				boolean ok = monitor.open(HostDefine.HOST_LTCP, HostDefine.PORT_LTCP_listen);
				if(!ok) {
					Log.e("monitor","monitor:open failed");
					return;
				}
				while(true) {
					String jstring = monitor.watch();
					Log.i("monitor","devMonitor:jString="+jstring);
					try{
						JSONObject json = new JSONObject(jstring);
						byte mSwitch=(byte) json.getInt(JSONDefine.KEY_switch);
						Log.i("monitor","sw="+mSwitch);
						VentilatorManager manager=new VentilatorManager(getBaseContext());
						manager.sendSwitch(mSwitch);
						manager=null;
					}catch(Exception e){
						Log.e("monitor","monitor:error");
					}
				}
			}
		}).start();
		
		return super.onStartCommand(intent, flags, startId);
	}

	private void initSendReceiver() {
		sendReciever = new SendReciever();
		IntentFilter filter = new IntentFilter(SENDACTION);
		registerReceiver(sendReciever, filter);
	}
	
	@Override
	public void onDestroy() {
		try{
			unregisterReceiver(sendReciever);
		}catch(Exception e){
			Log.i("service", e.getLocalizedMessage());
		}
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
		}
	}

	public class SendReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			Message msg= handler.obtainMessage();
			msg.what=VentilatorManager.SEND_DATA;
			msg.arg1=intent.getExtras().getByte("send");
//			Log.i("switch", "receive:"+msg.arg1);
			handler.sendMessage(msg);
		}

	}

	// public String getVentilator() {
	// String srsString = "";
	// try
	// {
	// srsString = getJsonStringGet( "http://lingshuo.net.cn" );
	// }
	// catch (Exception e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// Log.i( "sv",e.getMessage() );
	// }
	//
	// return srsString;
	// }
	//
	// public String getJsonStringGet(String uri) throws Exception
	// {
	// String result = null;
	// URL url = new URL( uri );
	// HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	// conn.setConnectTimeout( 6 * 1000 );// 设置连接超时
	// Log.i( "sv", conn.getResponseCode() + conn.getResponseMessage() );
	// if (conn.getResponseCode() == 200)
	// {
	// Log.i( "sv", "成功" );
	// InputStream is = conn.getInputStream();// 得到网络返回的输入流
	// result = readData( is, "UTF-8" );
	// }
	// else
	// {
	// Log.i( "sv", "失败" );
	// result = "";
	// }
	// conn.disconnect();
	// return result;
	//
	// }
	//
	//
	// private String readData(InputStream inSream, String charsetName) throws
	// Exception
	// {
	// ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	// byte[] buffer = new byte[1024];
	// int len = -1;
	// while ((len = inSream.read( buffer )) != -1)
	// {
	// outStream.write( buffer, 0, len );
	// }
	// byte[] data = outStream.toByteArray();
	// outStream.close();
	// inSream.close();
	// return new String( data, charsetName );
	// }
}
