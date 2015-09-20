package cn.lisa.smartventilator.controller.service;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import cn.LCloud.Web.DevMonitor;
import cn.LCloud.Web.DevReporter;
import cn.LCloud.Web.HostDefine;
import cn.LCloud.Web.JSONDefine;
import cn.lisa.smartventilator.controller.manager.VentilatorManager;
import cn.lisa.smartventilator.debug.Debug;
import cn.lisa.smartventilator.utility.hardware.UartAgent;
import cn.lisa.smartventilator.view.activity.MainActivity;
import cn.lisa.smartventilator.view.fragment.ClockFragment;
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
	public static final String STARTCOUNTACTION="count";
	public static final String STOPCOUNTACTION="stopcount";
	public static final String PAUSECOUNTACTION="pausecount";
	public static String state="";
	private Timer timer;
	public UartAgent uartagent;
	private SendReciever sendReciever;
	private DevReporter myreporter = null;
	private DevMonitor mymonitor = null;
	private String mid;
	private byte[] heartBeat = new byte[2];
	private Thread reportDataThread;
	private Thread tryConnectThread;
	private Thread networkMonitorThread;
	Timer timer1 = new Timer(true);
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			handler.sendEmptyMessage(222);
		}
	};
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

				Debug.info(Debug.DEBUG_SWITCH, "switch", "send:sw", sw + "/val:" + val + "/"
						+ success);

				break;
			case 222:
				if(!state.equals("")){
					if(state.equals(STARTCOUNTACTION)){
						if(MainActivity.mCount>0){
							MainActivity.mCount--;
							Log.i("Service:mCount", "Service:mCount:" + MainActivity.mCount);
							Intent intent=new Intent(ClockFragment.SHOWCOUNTACTION);
							sendBroadcast(intent);
						}
					}else if(state.equals(STOPCOUNTACTION)){
						MainActivity.mCount=0;
						Log.i("Service:mCount", "Stop:mCount:" + MainActivity.mCount);
					}else{
						Log.i("Service:mCount", "HOLD:mCount:" + MainActivity.mCount);
					}
				}
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
		// baudrate:115200, data:8,stop:1, parity:N
		// uartagent=new UartAgent("/dev/ttyO1", 115200, 8, 1, (byte)'N', true);
		// baudrate:ttyS6, 38400, data:8,stop:1, parity:N
		uartagent = new UartAgent("/dev/ttyS6", 38400, 8, 1, (byte) 'N', true);
		uartagent.init();
		myreporter = new DevReporter(HostDefine.HOSTID_LDAT, HostDefine.HOST_LDAT,
				HostDefine.PORT_LDAT_speak);
		mymonitor = new DevMonitor(HostDefine.HOSTID_LTCP, mid, HostDefine.HOST_LTCP,
				HostDefine.PORT_LTCP_listen);
		heartBeat[0] = 0x03;
		heartBeat[1] = 0x01;
		initThread();
		super.onCreate();
	}

	private void initThread() {
		// thread to get info from hardware and report info to network
		this.reportDataThread=new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {
					String jsonString = uartagent.getStatusBlock();

					Debug.info(Debug.DEBUG_SERVICE_SV, "sv", "load_data", jsonString);

					if (jsonString == null || "".equals(jsonString))
						continue;

					Intent intent = new Intent();
					intent.setAction(BROADCASTACTION);
					intent.putExtra("jsonstr", jsonString);
					sendBroadcast(intent);
					Debug.info(Debug.DEBUG_SERVICE_SV, "sv", "", "intent sent");
					
					// if reporter is open, then report json to network
					if (myreporter.isOpen()) {
//						for(int i=0;i<100;i++){
							boolean ok = myreporter.report(mid, jsonString.toString());
							if (!ok) {
								myreporter.close();
							} else {
								Debug.info(Debug.DEBUG_SERVICE_SV, "sv", "", "network report");
							}
//						}
					}
				}
			}
		});

		// thread to try open reporter
		this.tryConnectThread=new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					if (!myreporter.isOpen()) {
						boolean ok = myreporter.open();
						if (!ok) {
							Log.e("report", "try connect failed");
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
		});

		// Thread to get monitor from network and send switch
		this.networkMonitorThread=new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {

					// try open
					boolean ok=false;
					if(!mymonitor.isOpen()) {
						ok = mymonitor.open();
						Debug.info(Debug.DEBUG_SERVICE_MONITOR, "monitor", "","open:"+
								ok);
						if (!ok) {
							Log.e("monitor", "monitor:open failed");
							try {
								Debug.info(Debug.DEBUG_SERVICE_MONITOR, "monitor", "","sleep");
								Thread.sleep(5 * 1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
								continue;
							}
							continue;
						}
					}
					
					// open successfully, monitoring...
					while (true) {
						Debug.info(Debug.DEBUG_SERVICE_MONITOR, "monitor", "","before watch");
						String jstring = mymonitor.watch();
						Debug.info(Debug.DEBUG_SERVICE_MONITOR, "monitor", "","after watch");
						if (jstring == null || "".equals(jstring)) {
							Log.w("monitor", "devNonitor:network broken");
							mymonitor.close();
							try {
								Thread.sleep(5*1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							break;
							// network down, try reconnect
						}

						Debug.info(Debug.DEBUG_SERVICE_MONITOR, "monitor", "devMonitor:jString",
								jstring);
						try {

							JSONObject json = new JSONObject(jstring);

							String device = json.getString(JSONDefine.KEY_focus);
							byte command = (byte) json.getInt(JSONDefine.KEY_swValue);

							Debug.info(Debug.DEBUG_SERVICE_MONITOR, "monitor", "sw=", command);
							VentilatorManager manager = new VentilatorManager(getBaseContext());
							manager.sendVentilatorCommand(device, command);
							manager = null;

						} catch (Exception e) {
							Log.e("monitor", "monitor:error");
						}
					}
				}
			}
		});

		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// byte[] heartBeat = new byte[2];
		// heartBeat[0] = 0x03;
		// heartBeat[1] = 0x01;
		// while (true) {
		// int value=uartagent.frame.send(heartBeat, heartBeat.length);
		//
		// Debug.info(Debug.DEBUG, "heartbeat", "send heartbeat", value);
		// try {
		// Thread.sleep(1*1000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// continue;
		// }
		// }
		// }
		// }).start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (timer != null) {
			timer.cancel();
		}
		
		if(reportDataThread==null||tryConnectThread==null||networkMonitorThread==null)
			initThread();
		
		if(!reportDataThread.isAlive())
			reportDataThread.start();
		
		if(!tryConnectThread.isAlive())
			tryConnectThread.start();
		
		if(!networkMonitorThread.isAlive())
			networkMonitorThread.start();

		
		// Heartbeat
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				int value = uartagent.frame.send(heartBeat, heartBeat.length);
				handler.sendEmptyMessage(222);
				Debug.info(Debug.DEBUG_SERVICE_HEARTBEAT, "heartbeat", "send heartbeat", value);
			}

		}, 1 * 1000, 1 * 1000);

		return super.onStartCommand(intent, flags, startId);
	}

	private void initSendReceiver() {
		sendReciever = new SendReciever();
		IntentFilter filter = new IntentFilter();
		filter.addAction(SENDACTION);
		filter.addAction(STARTCOUNTACTION);
		filter.addAction(PAUSECOUNTACTION);
		filter.addAction(STOPCOUNTACTION);
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

			if(intent.getAction().equals(SENDACTION)){
				Message msg = handler.obtainMessage();
				msg.what = VentilatorManager.SEND_DATA;
				msg.arg1 = intent.getExtras().getByte("sw");
				msg.arg2 = intent.getExtras().getByte("val");
				handler.sendMessage(msg);
			}
			if(intent.getAction().equals(STARTCOUNTACTION)){
				state=STARTCOUNTACTION;
			}
			if(intent.getAction().equals(STOPCOUNTACTION)){
				state=STOPCOUNTACTION;
			}
			if(intent.getAction().equals(PAUSECOUNTACTION)){
				state=PAUSECOUNTACTION;
			}
			
		}

	}
}
