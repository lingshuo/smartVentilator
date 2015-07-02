package cn.lisa.smartventilator.manager;
import java.util.Map;

import org.json.*;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import cn.lisa.smartventilator.bean.Ventilator;
import cn.lisa.smartventilator.hardware.UartAgent;
import cn.lisa.smartventilator.service.MonitorService;
public class VentilatorManager {
	Ventilator ventilator;
	private UartAgent uartagent;
	public final static int SHOW_DATA=3;
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
	
	public void forceUpdate(Context context){
		uartagent=new UartAgent("/dev/ttyO1", 115200, 8, 1, (byte)'N', true);
//		MonitorService s=new MonitorService();
		Object[] ob = new Object[2];
//		ob[0]=s;
		ob[0]=uartagent;
		ob[1]=context;
		new UpdateTask().execute(ob);
		
	}
	
	class UpdateTask extends AsyncTask<Object, Integer, String>{
		String result;
		Context c;
		@Override
		protected String doInBackground(Object... ob) {
//			MonitorService s=(MonitorService)ob[0];
			UartAgent u=(UartAgent)ob[0];
			this.c=(Context)ob[1];
//			result=s.getVentilator();
			this.result=u.getStatusBlock();
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			Intent intent = new Intent();  
            intent.setAction( MonitorService.BROADCASTACTION );  
            intent.putExtra( "jsonstr", result );  
            Log.i("sv", "force update data:"+result);
            c.sendBroadcast(intent);
			super.onPostExecute(result);
		}
		
	}

}
