package cn.lisa.smartventilator.utility.network;

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
		this.speaker = new LTCPSpeaker(new LHostDesc(host, port), new TPoint("", "", ""), "okLicense");
		return speaker.open();
	}

	public void close() {
		if (speaker != null) {
			speaker.close();
			speaker = null;
		}
	}

	public boolean setStatus(String idDev, String sw) {
		TMsg tmsg = new TMsg();
		tmsg.setHead(new THead(TMsgDefine.LMSG_MAGIC, TMsgDefine.LMSG_VERSION, TMType.DATA));
		tmsg.setDst(new TPoint(marketID, "", idDev));
		tmsg.setSrc(new TPoint("", "", ""));
		tmsg.setStamp(System.currentTimeMillis());
		tmsg.setContent(sw.getBytes());

		return speaker.say(tmsg);
	}

	public static void main(String[] args) throws InterruptedException, JSONException {
		RemoteSetter setter = new RemoteSetter(HostDefine.HOSTID_LTCP);

		int cnt = 0;

		while (true) {
			Thread.sleep(5 * 1000);

			boolean ok = setter.open(HostDefine.HOST_LTCP, HostDefine.PORT_LTCP_speak);
			if (!ok) {
				System.out.println("setter:connnect server failed\n");
				continue;
			}

			JSONObject json = new JSONObject();
			json.put(JSONDefine.KEY_focus, JSONDefine.SW_fan);
			json.put(JSONDefine.KEY_swValue, cnt);

			ok = setter.setStatus(DevDefine.FAKE_ID, json.toString());
			setter.close();
			System.out.printf("setter:sw=%d\n", cnt);
			if (!ok) {
				System.out.println("setter:set failed\n");
			}

			cnt = (cnt + 1) % 4;
		}
	}
}
