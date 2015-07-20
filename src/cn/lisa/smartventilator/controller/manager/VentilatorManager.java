package cn.lisa.smartventilator.controller.manager;

import org.json.*;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import cn.lisa.smartventilator.controller.entity.Ventilator;
import cn.lisa.smartventilator.controller.service.MonitorService;
import cn.lisa.smartventilator.utility.network.JSONDefine;

public class VentilatorManager {
	private Ventilator ventilator;
	public final static int SHOW_DATA = 3;
	public final static int SEND_DATA = 4;
	public final static byte DEVICE_ON = 1;
	public final static byte DEVICE_OFF = 0;
	public final static byte LAMP = 0x01;
	public final static byte PLASMA = 0x02;
	public final static byte ULTRAVIOLET = 0x03;
	public final static byte VENTILATOR = 0x04;
	public final static byte VENTILATOR_1 = 0x01;
	public final static byte VENTILATOR_2 = 0x02;
	public final static byte VENTILATOR_3 = 0x03;
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
			JSONObject json = new JSONObject(jsonString);
			int hwError = json.getInt(JSONDefine.KEY_hwError);
			int pm2_5 = json.getInt(JSONDefine.KEY_pm25);
			int aldehyde = json.getInt(JSONDefine.KEY_hcho);
			int smog = json.getInt(JSONDefine.KEY_smog);
			int m_Switch = json.getInt(JSONDefine.KEY_switch);

			this.ventilator = new Ventilator(m_Switch, pm2_5, aldehyde, smog, hwError);
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
	public void sendVentilatorCommand(int device, int command, Ventilator ventilator) {

		byte Switch = ventilator.getSwitch();
		Log.i("switch", "old:" + Switch);
		byte mSwitch = Switch;
		if (command == DEVICE_OFF) {
			// 关闭设备
			switch (device) {
			case LAMP:
				mSwitch = (byte) (0x7F & Switch);
				break;
			case PLASMA:
				mSwitch = (byte) (0xBF & Switch);
				break;
			case ULTRAVIOLET:
				mSwitch = (byte) (0xDF & Switch);
				break;
			case VENTILATOR:
				mSwitch = (byte) (0xFC & Switch);
				break;
			default:
				break;
			}
		} else {
			// 打开设备
			switch (device) {
			case LAMP:
				mSwitch = (byte) (0x80 | Switch);
				break;
			case PLASMA:
				mSwitch = (byte) (0x40 | Switch);
				break;
			case ULTRAVIOLET:
				mSwitch = (byte) (0x20 | Switch);
				break;
			case VENTILATOR:
				mSwitch = (byte) (command | (0xFC & Switch));
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
	 * 
	 * @param device
	 *            which device to be changed
	 * @param command
	 *            which command it will receive
	 */
	public void sendVentilatorCommand(String device, int command) {
		if (command == JSONDefine.VAL_swOFF) {
			// close device
			if (JSONDefine.SW_lamp.equals(device)) {
				sendSwitch(LAMP, DEVICE_OFF);
			} else if (JSONDefine.SW_plasma.equals(device)) {
				sendSwitch(PLASMA, DEVICE_OFF);
			} else if (JSONDefine.SW_ultra.equals(device)) {
				sendSwitch(ULTRAVIOLET, DEVICE_OFF);
			} else if (JSONDefine.SW_fan.equals(device)) {
				sendSwitch(VENTILATOR, DEVICE_OFF);
			}
		} else if (command != JSONDefine.VAL_swOFF && !JSONDefine.SW_fan.equals(device)) {
			// open device
			if (JSONDefine.SW_lamp.equals(device)) {
				sendSwitch(LAMP, DEVICE_ON);
			} else if (JSONDefine.SW_plasma.equals(device)) {
				sendSwitch(PLASMA, DEVICE_ON);
			} else if (JSONDefine.SW_ultra.equals(device)) {
				sendSwitch(ULTRAVIOLET, DEVICE_ON);
			}
		} else if (command != JSONDefine.VAL_swOFF && JSONDefine.SW_fan.equals(device)) {
			switch (command) {
			case JSONDefine.VAL_swLevel1:
				sendSwitch(VENTILATOR, VENTILATOR_1);
				break;
			case JSONDefine.VAL_swLevel2:
				sendSwitch(VENTILATOR, VENTILATOR_2);
				break;
			case JSONDefine.VAL_swLevel3:
				sendSwitch(VENTILATOR, VENTILATOR_3);
				break;
			default:
				break;
			}
		} else {
			return;
		}
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

	/**
	 * new function to send switch
	 * 
	 * @param sw
	 * @param val
	 */
	public void sendSwitch(byte sw, byte val) {
		Intent intent = new Intent();
		intent.setAction(MonitorService.SENDACTION);
		intent.putExtra("sw", sw);
		intent.putExtra("val", val);
		context.sendBroadcast(intent);
	}
}
