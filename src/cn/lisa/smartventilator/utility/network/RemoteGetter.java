package cn.lisa.smartventilator.utility.network;

import org.json.JSONException;
import org.json.JSONObject;

import cn.LCloud.Client.Role.LDATQuerier;
import cn.LCloud.Common.TMsg.THead;
import cn.LCloud.Common.TMsg.TMType;
import cn.LCloud.Common.TMsg.TMsg;
import cn.LCloud.Common.TMsg.TMsgDefine;
import cn.LCloud.Common.TMsg.TPoint;

public class RemoteGetter {

	LDATQuerier querier = null;
	String marketID = "";

	public RemoteGetter(String marketID) {
		this.marketID = marketID;
	}

	public boolean open(String host, int port) {
		querier = new LDATQuerier(host, port);
		return querier.open();
	}

	public void close() {
		if (querier != null) {
			querier.close();
			querier = null;
		}
	}

	public String getStatus(String idDev) throws JSONException {
		TMsg tmsg = new TMsg();
		tmsg.setHead(new THead(TMsgDefine.LMSG_MAGIC, TMsgDefine.LMSG_VERSION,
				TMType.DATA));
		tmsg.setDst(new TPoint(marketID, "", idDev));
		tmsg.setSrc(new TPoint(marketID, "", idDev));
		tmsg.setStamp(System.currentTimeMillis());
		tmsg.setContent(idDev.getBytes());

		tmsg = querier.query(tmsg);
		if (tmsg == null || "".equals(new String(tmsg.getContent()))) {
			JSONObject json = new JSONObject();
			json.put(JSONDefine.KEY_reault, false);
			return json.toString();
		}

		JSONObject json = new JSONObject(new String(tmsg.getContent()));
		json.put(JSONDefine.KEY_reault, true);
		return json.toString();
	}

	// demo main
	public static void main(String[] args) throws InterruptedException,
			JSONException {
		RemoteGetter getter = new RemoteGetter(HostDefine.HOSTID_LDAT);

		while (true) {
			Thread.sleep(5 * 1000);

			boolean ok = getter.open(HostDefine.HOST_LDAT,
					HostDefine.PORT_LDAT_query);
			if (!ok)
				continue;

			String status = getter.getStatus(DevDefine.FAKE_ID);
			getter.close();

			JSONObject json = new JSONObject(status);
			if (!json.getBoolean(JSONDefine.KEY_reault)) {
				System.out.printf("remoteGetter:get status failed\n");
				continue;
			}

			System.out.printf(
					"status:[switch=%d,smog=%d;HCHO=%d,PM25=%d,hwError=%d]\n",
					json.getInt(JSONDefine.KEY_switch),
					json.getInt(JSONDefine.KEY_smog),
					json.getInt(JSONDefine.KEY_hcho),
					json.getInt(JSONDefine.KEY_pm25),
					json.getInt(JSONDefine.KEY_hwError));
		}
	}
}
