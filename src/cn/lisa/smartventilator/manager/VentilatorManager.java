package cn.lisa.smartventilator.manager;
import java.util.Map;

import org.json.*;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.lisa.smartventilator.bean.Ventilator;
import cn.lisa.smartventilator.hardware.UartAgent;
import cn.lisa.smartventilator.hardware.UartFrame;
import cn.lisa.smartventilator.service.MonitorService;
public class VentilatorManager {
	Ventilator ventilator;
	private UartAgent uartagent;
	public final static int SHOW_DATA=3;
	public final static int SEND_DATA=4;
	public final static int DEVICE_ON=1;
	public final static int DEVICE_OFF=0;
	public final static int LAMP=1;
	public final static int ULTRAVIOLET=2;
	public final static int PLASMA=3;
	public final static int VENTILATOR=10;
	public final static int VENTILATOR_1=0x01;
	public final static int VENTILATOR_2=0x02;
	public final static int VENTILATOR_3=0x03;
	private Context context;
	
	public VentilatorManager(Context context) {
		this.context=context;
	}
	
	public void setVentilator(String jsonString){
		try{
			JSONObject jsonObject=new JSONObject(jsonString);
			int hwError=jsonObject.getInt("hwError");
			int pm2_5=jsonObject.getInt("PM25");
			int aldehyde=jsonObject.getInt("HCHO");
			int smog=jsonObject.getInt("smog");
			int m_Switch=jsonObject.getInt("sw");

			this.ventilator=new Ventilator(m_Switch,pm2_5,aldehyde,smog,hwError);
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	
	public Ventilator getVentilator(){
		return ventilator;
	}
	
	public void sendVentilator(int device,int command,Ventilator ventilator){
		
		byte Switch=ventilator.getSwitch();
		Log.i("switch", "old:"+Switch);
		byte mSwitch = Switch;
		if(command==DEVICE_OFF){
			//关闭设备
			switch (device) {
			case LAMP:
				if(ventilator.getState_lamp())
					mSwitch=(byte) (0x80 ^ Switch);
				break;
			case PLASMA:
				if(ventilator.getState_plasma())
					mSwitch=(byte) (0x40 ^ Switch);
				break;
			case ULTRAVIOLET:
				if(ventilator.getState_ultraviolet())
					mSwitch=(byte) (0x20 ^ Switch);
				break;
			case VENTILATOR:
				//关闭设备
				if(ventilator.getState_ventilator())
					mSwitch=(byte)(ventilator.getGear_ventilator() ^ Switch);
				break;
			default:
				break;
			}
		}else{
			//打开设备
			switch (device) {
			case LAMP:
				if(!ventilator.getState_lamp())
					mSwitch=(byte) (0x80 | Switch);
				break;
			case PLASMA:
				if(!ventilator.getState_plasma())
				mSwitch=(byte) (0x40 | Switch);
				break;
			case ULTRAVIOLET:
				if(!ventilator.getState_ultraviolet())
				mSwitch=(byte) (0x20 | Switch);
				break;
			case VENTILATOR:
				//打开&调节档位
				switch (command) {
				case VENTILATOR_1:
					if(ventilator.getGear_ventilator()==1)
						;
					if(ventilator.getGear_ventilator()==2)
						mSwitch=(byte)(0x03 ^ Switch);
					if(ventilator.getGear_ventilator()==3)
						mSwitch=(byte)(0x02 ^ Switch);
					break;
				case VENTILATOR_2:
					if(ventilator.getGear_ventilator()==1)
						mSwitch=(byte)(0x03 ^ Switch);
					if(ventilator.getGear_ventilator()==2)
						;
					if(ventilator.getGear_ventilator()==3)
						mSwitch=(byte)(0x01 ^ Switch);
					break;
				case VENTILATOR_3:
					if(!ventilator.getState_ventilator())
						mSwitch=(byte)(0x03 ^ Switch);
					else{
						if(ventilator.getGear_ventilator()==1)
							mSwitch=(byte)(0x02 ^ Switch);
						if(ventilator.getGear_ventilator()==2)
							mSwitch=(byte)(0x01 ^ Switch);
						if(ventilator.getGear_ventilator()==3)
							;
					}
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
		}
		Log.i("switch", "new:"+mSwitch);
		
		Intent intent=new Intent();
		intent.setAction(MonitorService.SENDACTION);
		intent.putExtra("send", mSwitch);
		context.sendBroadcast(intent);
	}
	

//	public void forceUpdate(Context context){
//		uartagent=new UartAgent("/dev/ttyO1", 115200, 8, 1, (byte)'N', true);
////		MonitorService s=new MonitorService();
//		Object[] ob = new Object[2];
////		ob[0]=s;
//		ob[0]=uartagent;
//		ob[1]=context;
//		new UpdateTask().execute(ob);
//		
//	}
//	
//	class UpdateTask extends AsyncTask<Object, Integer, String>{
//		String result;
//		Context c;
//		@Override
//		protected String doInBackground(Object... ob) {
////			MonitorService s=(MonitorService)ob[0];
//			UartAgent u=(UartAgent)ob[0];
//			this.c=(Context)ob[1];
////			result=s.getVentilator();
//			this.result=u.getStatusBlock();
//			return result;
//		}
//		
//		@Override
//		protected void onPostExecute(String result) {
//			Intent intent = new Intent();  
//            intent.setAction( MonitorService.BROADCASTACTION );  
//            intent.putExtra( "jsonstr", result );  
//            Log.i("sv", "force update data:"+result);
//            c.sendBroadcast(intent);
//			super.onPostExecute(result);
//		}
//		
//	}

}
