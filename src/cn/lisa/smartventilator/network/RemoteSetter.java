package cn.lisa.smartventilator.network;

import org.json.JSONException;
import org.json.JSONObject;

import cn.LCloud.Client.Role.LTCPSpeaker;
import cn.LCloud.Common.Struct.LHostDesc;
import cn.LCloud.Common.TMsg.THead;
import cn.LCloud.Common.TMsg.TMType;
import cn.LCloud.Common.TMsg.TMsg;
import cn.LCloud.Common.TMsg.TMsgDefine;
import cn.LCloud.Common.TMsg.TPoint;



public class RemoteSetter {
	
	LTCPSpeaker speaker = null;
	String marketID = "";
	
	public RemoteSetter(String marketID) {
		this.marketID = marketID;
	}
	
	public boolean open(String host, int port) {
		this.speaker = new LTCPSpeaker( new LHostDesc(host, port),
				new TPoint("","", ""), "okLicense");
		return speaker.open();
	}
	
	public void close() {
		if(speaker!=null) {
			speaker.close();
			speaker = null;
		}
	}
	
	public boolean setStatus(String idDev, byte sw) throws JSONException {
		JSONObject json = new JSONObject();
		
		json.put("sw", sw);
		
		TMsg tmsg = new TMsg();
		tmsg.setHead(new THead(TMsgDefine.LMSG_MAGIC, TMsgDefine.LMSG_VERSION, TMType.DATA));
		tmsg.setDst(new TPoint(marketID, "", idDev));
		tmsg.setSrc(new TPoint("", "", ""));
		tmsg.setStamp(System.currentTimeMillis());
		tmsg.setContent(json.toString().getBytes());
		
		return speaker.say(tmsg);
	}
	
	
	public static void main(String[] args) throws InterruptedException, JSONException {
		RemoteSetter setter = new RemoteSetter(HostDefine.HOSTID_LTCP);
		
		byte sw = 0;
		
		while(true) {
			Thread.sleep(5*1000);
			
			boolean ok = setter.open(HostDefine.HOST_LTCP, HostDefine.PORT_LTCP_speak);
			if(!ok) {
				System.out.println("setter:connnect server failed\n");
				continue;
			}
			
			ok = setter.setStatus(DevDefine.FAKE_ID, sw);
			setter.close();
			System.out.printf("setter:sw=%d\n", sw);
			if(!ok) {
				System.out.println("setter:set failed\n");
			}
			
			sw = (byte)( (sw+1)%128 );
		}

	}
}
