package cn.lisa.smartventilator.controller.manager;

import org.json.*;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import cn.lisa.smartventilator.controller.entity.Ventilator;
import cn.lisa.smartventilator.controller.service.MonitorService;
import cn.lisa.smartventilator.utility.network.DevDefine;
import cn.lisa.smartventilator.utility.network.DevReporter;
import cn.lisa.smartventilator.utility.network.HostDefine;
import cn.lisa.smartventilator.utility.network.JSONDefine;

public class VentilatorManager {
	private Ventilator ventilator;
	public final static int SHOW_DATA = 3;
	public final static int SEND_DATA = 4;
	public final static int DEVICE_ON = 1;
	public final static int DEVICE_OFF = 0;
	public final static int LAMP = 1;
	public final static int ULTRAVIOLET = 2;
	public final static int PLASMA = 3;
	public final static int VENTILATOR = 10;
	public final static int VENTILATOR_1 = 0x01;
	public final static int VENTILATOR_2 = 0x02;
	public final static int VENTILATOR_3 = 0x03;
	private Context context;

	/***
	 * init manager by context
	 * 
	 * @param context
	 */
	public VentilatorManager(Context context) {
		this.context = context;
	}

	/****
	 * set up a ventilator entity
	 * 
	 * @param jsonString
	 */

	public void setVentilator(String jsonString) {
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			int hwError = jsonObject.getInt("hwError");
			int pm2_5 = jsonObject.getInt("PM25");
			int aldehyde = jsonObject.getInt("HCHO");
			int smog = jsonObject.getInt("smog");
			int m_Switch = jsonObject.getInt("sw");

			this.ventilator = new Ventilator(m_Switch, pm2_5, aldehyde, smog,
					hwError);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/****
	 * get info of ventilator
	 * 
	 * @return
	 */

	public Ventilator getVentilator() {
		return ventilator;
	}

	/***
	 * send command from android ui to hardware
	 * 
	 * @param device
	 *            which device to open or close
	 * @param command
	 *            open close or gears
	 * @param ventilator
	 *            the original info of ventilator switch
	 */
	public void sendVentilatorCommand(int device, int command,
			Ventilator ventilator) {

		byte Switch = ventilator.getSwitch();
		Log.i("switch", "old:" + Switch);
		byte mSwitch = Switch;
		if (command == DEVICE_OFF) {
			// 关闭设备
			switch (device) {
			case LAMP:
				//if (ventilator.getState_lamp())
					mSwitch = (byte) (0x7F & Switch);
				break;
			case PLASMA:
				//if (ventilator.getState_plasma())
					mSwitch = (byte) (0xBF & Switch);
				break;
			case ULTRAVIOLET:
				//if (ventilator.getState_ultraviolet())
					mSwitch = (byte) (0xDF & Switch);
				break;
			case VENTILATOR:
				// 关闭设备
				//if (ventilator.getState_ventilator())
				//	mSwitch = (byte) (ventilator.getGear_ventilator() ^ Switch);
				mSwitch = (byte) (0xFC & Switch);
				break;
			default:
				break;
			}
		} else {
			// 打开设备
			switch (device) {
			case LAMP:
//				if (!ventilator.getState_lamp())
					mSwitch = (byte) (0x80 | Switch);
				break;
			case PLASMA:
//				if (!ventilator.getState_plasma())
					mSwitch = (byte) (0x40 | Switch);
				break;
			case ULTRAVIOLET:
//				if (!ventilator.getState_ultraviolet())
					mSwitch = (byte) (0x20 | Switch);
				break;
			case VENTILATOR:
				mSwitch = (byte) ( command | (0xFC&Switch) );
				// 打开&调节档位
				//switch (command) {
				
				//case VENTILATOR_1:
					//if (ventilator.getGear_ventilator() == 1)
					//	;
					//if (ventilator.getGear_ventilator() == 2)
					//	mSwitch = (byte) (0x03 ^ Switch);
					//if (ventilator.getGear_ventilator() == 3)
					//	mSwitch = (byte) (0x02 ^ Switch);
					//break;
				//case VENTILATOR_2:
					//if (ventilator.getGear_ventilator() == 1)
					//	mSwitch = (byte) (0x03 ^ Switch);
					//if (ventilator.getGear_ventilator() == 2)
					//	;
					//if (ventilator.getGear_ventilator() == 3)
					//	mSwitch = (byte) (0x01 ^ Switch);
					//break;
				//case VENTILATOR_3:
					//if (!ventilator.getState_ventilator())
					//	mSwitch = (byte) (0x03 ^ Switch);
					//else {
					//	if (ventilator.getGear_ventilator() == 1)
					//		mSwitch = (byte) (0x02 ^ Switch);
					//	if (ventilator.getGear_ventilator() == 2)
					//		mSwitch = (byte) (0x01 ^ Switch);
					//	if (ventilator.getGear_ventilator() == 3)
					//		;
					
					//}
					//break;
				//default:
				//	break;
				//}
				break;
			default:
				break;
			}
		}
		Log.i("switch", "new:" + mSwitch);

		sendSwitch(mSwitch);
	}
	/***
	 * a new function to send data
	 * @param device
	 * @param command
	 */
	public void sendVentilatorCommand(String device, int command){
		String mDevice=device;
		int mCommand;
		if(device==JSONDefine.SW_lamp){
			
			if(command==1);
		}
	}
	/***
	 * report data to network
	 * 
	 * @param ventilator
	 */
	public void reportData(String jsonString) {
		//setVentilator(jsonString);

		DevReporter reporter = new DevReporter(HostDefine.HOSTID_LDAT);
		boolean ok = reporter.open(HostDefine.HOST_LDAT,
				HostDefine.PORT_LDAT_speak);
		if (!ok) {
			Log.e("report", "setter:connnect server failed");
			return;
		}
	/*
		JSONObject json = new JSONObject();
		try {
			json.put(JSONDefine.KEY_switch, ventilator.getSwitch());
			json.put(JSONDefine.KEY_pm25, ventilator.getPm2_5());
			json.put(JSONDefine.KEY_smog, ventilator.getSmog());
			json.put(JSONDefine.KEY_hcho, ventilator.getAldehyde());
			json.put(JSONDefine.KEY_hwError, ventilator.getHwError());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		*/
		
		JSONObject json;
		try {
			json = new JSONObject(jsonString);
			//json.put(name, value)
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		ok = reporter.report(DevDefine.FAKE_ID, json.toString());
		Log.i("report", "report:" + json);
		reporter.close();
		if (!ok) {
			Log.e("report", "setter:set failed\n");
			return;
		}
		
		reporter = null;
	}

	/***
	 * send switch info to service
	 * 
	 * @param mSwitch
	 */
	public void sendSwitch(byte mSwitch) {
		Intent intent = new Intent();
		intent.setAction(MonitorService.SENDACTION);
		intent.putExtra("send", mSwitch);
		context.sendBroadcast(intent);
	}
}
