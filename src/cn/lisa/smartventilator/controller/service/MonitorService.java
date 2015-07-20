package cn.lisa.smartventilator.controller.service;

import java.util.Timer;

import org.json.JSONObject;

import cn.lisa.smartventilator.controller.manager.VentilatorManager;
import cn.lisa.smartventilator.utility.hardware.UartAgent;
import cn.lisa.smartventilator.utility.network.DevMonitor;
import cn.lisa.smartventilator.utility.network.DevReporter;
import cn.lisa.smartventilator.utility.network.HostDefine;
import cn.lisa.smartventilator.utility.network.JSONDefine;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
	public static final String HEARTBERTACTION = "heartbeat";

	private Timer timer;
	public UartAgent uartagent;
	private SendReciever sendReciever;
	private DevReporter myreporter = null;
	private DevMonitor mymonitor = null;
	private String mid;
	/***
	 * handle to send switch info to hardware
	 */
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case VentilatorManager.SEND_DATA:
				byte sw = (byte) msg.arg1;
				byte val = (byte) msg.arg2;

				boolean success = uartagent.setSw(sw, val);
				Log.i("switch", "send:sw:" + sw + "/val:" + val + "/" + success);

				// byte sw1=msg.getData().getByte("sw");
				// byte[] buf = new byte[2];
				// buf[0] = (byte) 0x01;
				// buf[1] = (byte) sw1;
				// uartagent.frame.send(buf, buf.length);
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onCreate() {
		initSendReceiver();
		Context context = getApplicationContext();
		SharedPreferences sp = context.getSharedPreferences("smartventilator.preferences", 0);
		mid = sp.getString("mID", "");
		myreporter = new DevReporter(HostDefine.HOSTID_LDAT, HostDefine.HOST_LDAT,
				HostDefine.PORT_LDAT_speak);
		mymonitor = new DevMonitor(HostDefine.HOSTID_LTCP, mid, HostDefine.HOST_LTCP,
				HostDefine.PORT_LTCP_listen);
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

		// thread to get info from hardware and report info to network
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

					// if reporter is open, then report json to network
					if (myreporter.isOpen()) {
						boolean ok = myreporter.report(mid, jsonString.toString());
						if (!ok) {
							myreporter.close();
						}
					}
				}
			}
		}).start();

		// thread to try open reporter
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					if (!myreporter.isOpen()) {
						boolean ok = myreporter.open();
						if (!ok) {
							Log.i("report", "try connect failed");
							try {
								Thread.sleep(5 * 1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
								continue;
							}
						}
					} else {
						try {
							Thread.sleep(5 * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
							continue;
						}
					}
				}
			}
		}).start();

		// Thread to send switch to network
		new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {

					// try open
					boolean ok = mymonitor.open();
					if (!ok) {
						Log.e("monitor", "monitor:open failed");
						try {
							Thread.sleep(5 * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
							continue;
						}
						continue;
					}

					// open successfully, monitoring...
					while (true) {
						String jstring = mymonitor.watch();
						if (jstring == null || "".equals(jstring)) {
							Log.w("monitor", "devNonitor:network broken");
							break;
							// network down, try reconnect
						}

						Log.i("monitor", "devMonitor:jString=" + jstring);
						try {
							// JSONObject json = new JSONObject(jstring);
							// byte mSwitch = (byte)
							// json.getInt(JSONDefine.KEY_switch);
							// Log.i("monitor", "sw=" + mSwitch);
							// VentilatorManager manager = new
							// VentilatorManager(getBaseContext());
							// manager.sendSwitch(mSwitch);
							// manager = null;

							JSONObject json = new JSONObject(jstring);

							String device = json.getString(JSONDefine.KEY_focus);
							byte command = (byte) json.getInt(JSONDefine.KEY_swValue);

							Log.i("monitor", "sw=" + command);
							VentilatorManager manager = new VentilatorManager(getBaseContext());
							manager.sendVentilatorCommand(device, command);
							manager = null;

						} catch (Exception e) {
							Log.e("monitor", "monitor:error");
						}
					}
				}
			}
		}).start();

		// Heartbeat
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				byte[] heartBeat = new byte[2];
//				heartBeat[0] = 0x03;
//				heartBeat[1] = 0x01;
//				while (true) {
//					int value=uartagent.frame.send(heartBeat, heartBeat.length);
//					Log.i("heartbeat", "send heartbeat"+value);
//					
//					try {
//						Thread.sleep(1 * 1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//						continue;
//					}
//				}
//			}
//		}).start();

		return super.onStartCommand(intent, flags, startId);
	}

	private void initSendReceiver() {
		sendReciever = new SendReciever();
		IntentFilter filter = new IntentFilter(SENDACTION);
		registerReceiver(sendReciever, filter);
	}

	@Override
	public void onDestroy() {
		try {
			unregisterReceiver(sendReciever);
		} catch (Exception e) {
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

			Message msg = handler.obtainMessage();
			msg.what = VentilatorManager.SEND_DATA;
			msg.arg1 = intent.getExtras().getByte("sw");
			msg.arg2 = intent.getExtras().getByte("val");
			// Bundle b=new Bundle();
			// b.putByte("sw", intent.getExtras().getByte("send"));
			// msg.setData(b);
			// Log.i("switch", "receive:"+msg.arg1);
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
