package cn.lisa.smartventilator.utility.network;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONException;
import org.json.JSONObject;

import cn.LCloud.Client.Role.LTCPListener;
import cn.LCloud.Common.Struct.LHostDesc;
import cn.LCloud.Common.TMsg.Converter;
import cn.LCloud.Common.TMsg.TMsg;
import cn.LCloud.Common.TMsg.TPoint;

public class DevMonitor {
	LTCPListener listener = null;

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

	public DevMonitor(String marketID, String devID, String host, int port) {
		listener = new LTCPListener(new LHostDesc(host, port), new TPoint(marketID, "", devID),
				"okLicense");
	}

	public boolean open() {
		setOpened(false);
		boolean ok = listener.open();
		if (ok) {
			setOpened(true);
		}

		return ok;
	}

	public void close() {
		if (listener != null) {
			listener.close();
			setOpened(false);
		}
	}

	public boolean isOpen() {
		return getOpened();
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
		DevMonitor monitor = new DevMonitor(HostDefine.HOSTID_LTCP, DevDefine.FAKE_ID,
				HostDefine.HOST_LTCP, HostDefine.PORT_LTCP_listen);
		boolean ok = monitor.open();
		if (!ok) {
			System.out.printf("monitor:open failed\n");
			return;
		}

		while (true) {
			String jstring = monitor.watch();
			System.out.printf("devMonitor:jString=%s\n", jstring);
			JSONObject json = new JSONObject(jstring);
			System.out.printf("%s=%d\n", json.getString(JSONDefine.KEY_focus),
					json.getInt(JSONDefine.KEY_swValue));
		}
	}
}
