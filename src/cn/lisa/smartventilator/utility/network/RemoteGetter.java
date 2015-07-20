package cn.lisa.smartventilator.utility.network;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

	private Lock lock = new ReentrantLock();
	boolean opened = false;

	private void setOpened(boolean open) {
		lock.lock();
		this.opened = open;
		lock.unlock();
	}

	private boolean getOpened() {
		lock.lock();
		boolean open = this.opened;
		lock.unlock();

		return open;
	}

	public RemoteGetter(String marketID, String host, int port) {
		this.marketID = marketID;
		querier = new LDATQuerier(host, port);
	}

	public boolean open() {
		setOpened(false);
		boolean ok = querier.open();
		if (ok) {
			setOpened(true);
		}

		return ok;
	}

	public void close() {
		if (querier != null) {
			querier.close();
			setOpened(false);
		}
	}

	public boolean isOpen() {
		return getOpened();
	}

	public String getStatus(String idDev) throws JSONException {
		TMsg tmsg = new TMsg();
		tmsg.setHead(new THead(TMsgDefine.LMSG_MAGIC, TMsgDefine.LMSG_VERSION, TMType.DATA));
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
	public static void main(String[] args) throws InterruptedException, JSONException {
		String DevID = args[0];

		RemoteGetter getter = new RemoteGetter(HostDefine.HOSTID_LDAT, HostDefine.HOST_LDAT,
				HostDefine.PORT_LDAT_query);

		while (true) {
			Thread.sleep(5 * 1000);

			boolean ok = getter.open();
			if (!ok)
				continue;

			// String status = getter.getStatus(DevDefine.FAKE_ID);
			String status = getter.getStatus(DevID);
			getter.close();

			JSONObject json = new JSONObject(status);
			if (!json.getBoolean(JSONDefine.KEY_reault)) {
				System.out.printf("remoteGetter:get status failed\n");
				continue;
			}

			System.out.printf("status:[switch=%d,smog=%d;HCHO=%d,PM25=%d,hwError=%d]\n",
					json.getInt(JSONDefine.KEY_switch), json.getInt(JSONDefine.KEY_smog),
					json.getInt(JSONDefine.KEY_hcho), json.getInt(JSONDefine.KEY_pm25),
					json.getInt(JSONDefine.KEY_hwError));
		}
	}
}
