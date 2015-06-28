package cn.lisa.smartventilator.util;
import org.json.*;

import cn.lisa.smartventilator.bean.Ventilator;
public class VentilatorUtil {
	Ventilator ventilator;
	
	public void setVentilator(String jsonString){
		try{
			JSONObject jsonObject=new JSONObject(jsonString);
			String RPT_all=jsonObject.getString("RPT_all");
			String pm2_5=jsonObject.getString("pm2.5");
			String aldehyde=jsonObject.getString("aldehyde");
			String smog=jsonObject.getString("smog");
			JSONObject object=jsonObject.getJSONObject("switch");
			int ventilator1=object.getInt("ventilator");
			int ultraviolet=object.getInt("ultraviolet");
			int plasma=object.getInt("plasma");
			int lamp=object.getInt("lamp");
			int[] Switch={ventilator1,ultraviolet,plasma,lamp};
			this.ventilator=new Ventilator(RPT_all,Switch,pm2_5,aldehyde,smog);
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	
	public Ventilator getVentilator(){
		return ventilator;
	}
}
