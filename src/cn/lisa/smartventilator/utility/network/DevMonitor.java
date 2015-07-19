package cn.lisa.smartventilator.utility.network;

import org.json.JSONException;
import org.json.JSONObject;

import cn.LCloud.Client.Role.LTCPListener;
import cn.LCloud.Common.Struct.LHostDesc;
import cn.LCloud.Common.TMsg.Converter;
import cn.LCloud.Common.TMsg.TMsg;
import cn.LCloud.Common.TMsg.TPoint;

public class DevMonitor {
	LTCPListener listener = null;
	String marketID = "";
	String devID = "";

	public DevMonitor(String marketID, String devID) {
		this.marketID = marketID;
		this.devID = devID;
	}

	public boolean open(String host, int port) {
		listener = new LTCPListener(new LHostDesc(host, port), new TPoint(marketID, "", devID), "okLicense");
		return listener.open();
	}

	public void close() {
		if (listener != null) {
			listener.close();
			listener = null;
		}
	}

	public String watch() {
		byte[] bmsg = listener.listen();
		if (bmsg == null)
			return "";
		TMsg tmsg = Converter.toTMsgFromPBytes(bmsg);
		if (tmsg == null)
			return "";

		return new String(tmsg.getContent());
	}

	public static void main(String[] args) throws JSONException {
		DevMonitor monitor = new DevMonitor(HostDefine.HOSTID_LTCP, DevDefine.FAKE_ID);
		boolean ok = monitor.open(HostDefine.HOST_LTCP, HostDefine.PORT_LTCP_listen);
		if (!ok) {
			System.out.printf("monitor:open failed\n");
			return;
		}

		while (true) {
			String jstring = monitor.watch();
			System.out.printf("devMonitor:jString=%s\n", jstring);
			JSONObject json = new JSONObject(jstring);
			System.out.printf("%s=%d\n", json.getString(JSONDefine.KEY_focus), json.getInt(JSONDefine.KEY_swValue));
		}
	}
}
