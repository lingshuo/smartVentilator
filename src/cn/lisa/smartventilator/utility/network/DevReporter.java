package cn.lisa.smartventilator.utility.network;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

	public DevReporter(String marketID, String host, int port) {
		this.marketID = marketID;
		this.speaker = new LDATSpeaker(host, port);

		setOpened(false);
	}

	public boolean open() {
		setOpened(false);
		boolean ok = speaker.open();
		if (ok) {
			setOpened(true);
		}

		return ok;
	}

	public void close() {
		if (speaker != null) {
			speaker.close();
			setOpened(false);
		}
	}

	public boolean isOpen() {
		return getOpened();
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
		DevReporter reporter = new DevReporter(HostDefine.HOSTID_LDAT, HostDefine.HOST_LDAT,
				HostDefine.PORT_LDAT_speak);

		int cnt = 0;

		while (true) {
			Thread.sleep(5 * 1000);

			boolean ok = reporter.open();
			if (!ok) {
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

			if (!ok) {
				System.out.println("setter:set failed\n");
			}

			System.out.println("DevReporter:" + json.toString());

			cnt = (byte) ((cnt + 1) % 256);
		}

	}
}
