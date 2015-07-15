package cn.lisa.smartventilator.network;

import org.json.JSONException;
import org.json.JSONObject;

import cn.LCloud.Client.Role.LDATSpeaker;
import cn.LCloud.Common.TMsg.THead;
import cn.LCloud.Common.TMsg.TMType;
import cn.LCloud.Common.TMsg.TMsg;
import cn.LCloud.Common.TMsg.TMsgDefine;
import cn.LCloud.Common.TMsg.TPoint;



public class DevReporter {
	
	LDATSpeaker speaker = null;
	String marketID = "";
	
	public DevReporter(String marketID) {
		this.marketID = marketID;
	}
	
	public boolean open(String host, int port) {
		this.speaker = new LDATSpeaker(host, port);
		return speaker.open();
	}
	
	public void close() {
		if(speaker!=null) {
			speaker.close();
			speaker = null;
		}
	}
	
	public boolean report(String idDev, String status) {		
		TMsg tmsg = new TMsg();
		tmsg.setHead(new THead(TMsgDefine.LMSG_MAGIC, TMsgDefine.LMSG_VERSION, TMType.DATA));
		tmsg.setDst(new TPoint(marketID, "", idDev));
		tmsg.setSrc(new TPoint("", "", ""));
		tmsg.setStamp(System.currentTimeMillis());
		tmsg.setContent(status.getBytes());
		
		return speaker.say(tmsg);
	}
	
	
	public static void main(String[] args) throws InterruptedException, JSONException {
		DevReporter reporter = new DevReporter(HostDefine.HOSTID_LDAT);
		
		int cnt = 0;
		
		while(true) {
			Thread.sleep(5*1000);
			
			boolean ok = reporter.open(HostDefine.HOST_LDAT, HostDefine.PORT_LDAT_speak);
			if(!ok) {
				System.out.println("setter:connnect server failed\n");
				continue;
			}
			
			JSONObject json = new JSONObject();
			
			json.put(JSONDefine.KEY_switch, cnt);
			json.put(JSONDefine.KEY_pm25, cnt);
			json.put(JSONDefine.KEY_smog, cnt);
			json.put(JSONDefine.KEY_hcho, cnt);
			json.put(JSONDefine.KEY_hwError, cnt);
			
			ok = reporter.report(DevDefine.FAKE_ID, json.toString());
			reporter.close();
			
			if(!ok) {
				System.out.println("setter:set failed\n");
			}
			
			cnt = (byte)( (cnt+1)%256 );
		}

	}
}
