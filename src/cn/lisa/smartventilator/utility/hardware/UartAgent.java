package cn.lisa.smartventilator.utility.hardware;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import cn.LCloud.Web.JSONDefine;
import cn.lisa.smartventilator.debug.Debug;

public class UartAgent {
	public static final byte cmd_SetSwitch = 0x01;
	public static final byte cmd_ReportAll = 0x02;

	public static final byte sw_LAMP = 0x01;
	public static final byte sw_PLASMA = 0x02;
	public static final byte sw_ULTRA = 0x03;
	public static final byte sw_FAN = 0x04;

	public static final byte val_OFF = 0x00;
	public static final byte val_ON = 0x01;
	public static final byte val_LEVEL1 = 0x01;
	public static final byte val_LEVEL2 = 0x02;
	public static final byte val_LEVEL3 = 0x03;

	private static final int BUF_SIZE = 1024;

	private boolean isOpen = false;

	// for test only, when release , make it as 'private'
	public UartFrame frame = null;
	private Lock statusLock = null;
	private StringBuilder status = null;
	private StatusGetThread sThread = null;

	private String dev = null;
	private int baudrate;
	private int databits;
	private int stopbits;
	private byte parity;
	private boolean block = true;

	public UartAgent(String dev, int baudrate, int databits, int stopbits, byte parity, boolean block) {
		this.dev = dev;
		this.baudrate = baudrate;
		this.databits = databits;
		this.stopbits = stopbits;
		this.parity = parity;
		this.block = block;

		frame = new UartFrame();
		statusLock = new ReentrantLock();
		status = new StringBuilder("");
	}

	public boolean setSw(byte sw, byte val) {

		byte[] cmdSet = new byte[3];
		cmdSet[0] = cmd_SetSwitch;
		cmdSet[1] = sw;
		cmdSet[2] = val;

		int bytes = frame.send(cmdSet, cmdSet.length);
		if (bytes > 0)
			return true;

		return false;
	}

	// boolean setSw(byte sw) {
	//
	// byte[] cmdSet = new byte[2];
	// cmdSet[0] = cmd_SetSwitch;
	// cmdSet[1] = sw;
	//
	// int bytes = frame.send(cmdSet, cmdSet.length);
	// if (bytes > 0)
	// return true;
	//
	// return false;
	// }

	public String getStatusUnblock() {

		statusLock.lock();
		String s = status.toString();
		statusLock.unlock();

		return s;
	}

	public String getStatusBlock() {
		byte[] buf = new byte[BUF_SIZE];

		int bytes = frame.recv(buf, BUF_SIZE);
		
		Debug.info(Debug.DEBUG_UART, "[uartAgent]", "read bytes", bytes);
		
		if (bytes > 0) {
			byte cmd = buf[0];
			switch (cmd) {
			case cmd_ReportAll:
				byte sw = buf[1];
				int pm25 = (int) (((buf[2] << 8) & 0x0000FF00) | ((buf[3] << 0) & 0x000000FF));
				int hcho = (int) (((buf[4] << 8) & 0x0000FF00) | ((buf[5] << 0) & 0x000000FF));
				int smog = (int) (((buf[6] << 8) & 0x0000FF00) | ((buf[7] << 0) & 0x000000FF));
				int hwError = (int) (((buf[8] << 8) & 0x0000FF00) | ((buf[9] << 0) & 0x000000FF));

				String info = "sw=" + sw + ";pm25=" + pm25 + ";hcho=" + hcho + ";smog=" + smog
						+ ";hwError" + hwError;
				
				Debug.info(Debug.DEBUG_UART, "[uartAgent]", "", info);
				
				JSONObject json = new JSONObject();
				try {
					json.put(JSONDefine.KEY_switch, sw);
					json.put(JSONDefine.KEY_pm25, pm25);
					json.put(JSONDefine.KEY_smog, smog);
					json.put(JSONDefine.KEY_hcho, hcho);
					json.put(JSONDefine.KEY_hwError, hwError);

				} catch (JSONException e) {
					Log.e("[UartAgent]", e.getLocalizedMessage());
					return json.toString();
				}

				// make JSon String
				return json.toString();

			}
		}

		return null;
	}

	public void init() {
		isOpen = frame.init(dev, baudrate, databits, stopbits, parity);
		if (!isOpen)
			Log.e("[uartAgent]", "uart open failed\n ");

		if (!block) { // enable unblock mode
			sThread = new StatusGetThread();
			sThread.start();
		}
	}

	public void close() {
		frame.destroy();
		isOpen = false;
	}

	class StatusGetThread extends Thread {
		public void run() {
			byte[] buf = new byte[BUF_SIZE];

			while (true) {
				// if not open, open the UART
				if (!isOpen) {					
					Debug.info(Debug.DEBUG_UART, "[uartAgent]",  "","not open");
					close();
					isOpen = frame.init(dev, baudrate, databits, stopbits, parity);
				}
				if (!isOpen) {
					try {
						sleep(5 * 1000);
					} catch (InterruptedException e) {
						continue;
					}

					continue;
				}
				Debug.info(Debug.DEBUG_UART, "[uartAgent]","open",dev + "OK");
				// try read frame, it everything OK, I will never goback
				while (isOpen) {
					try {
						sleep(5 * 1000);
					} catch (InterruptedException e) {
						continue;
					}

					int bytes = frame.recv(buf, BUF_SIZE);
					if (bytes > 0) {
						// Log.i("[uartAgent]", "read bytes "+bytes);
						byte cmd = buf[0];
						switch (cmd) {
						case cmd_ReportAll:
							byte sw = buf[1];
							short pm25 = (short) (((buf[2] << 8) & 0xFF00) | ((buf[3] << 0) & 0x00FF));
							short hcho = (short) (((buf[4] << 8) & 0xFF00) | ((buf[5] << 0) & 0x00FF));
							short smog = (short) (((buf[6] << 8) & 0xFF00) | ((buf[7] << 0) & 0x00FF));
							short hwError = (short) (((buf[8] << 8) & 0xFF00) | ((buf[9] << 0) & 0x00FF));

							String info = "sw=" + sw + ";pm25=" + pm25 + ";hcho=" + hcho + ";smog="
									+ smog + ";hwError" + hwError;
							Debug.info(Debug.DEBUG_UART, "[uartAgent]","",info);
							// make JSon String
							JSONObject json = new JSONObject();
							try {
								json.put(JSONDefine.KEY_switch, sw);
								json.put(JSONDefine.KEY_pm25, pm25);
								json.put(JSONDefine.KEY_smog, smog);
								json.put(JSONDefine.KEY_hcho, hcho);
								json.put(JSONDefine.KEY_hwError, hwError);

							} catch (JSONException e) {
								Log.e("[UartAgent]", e.getLocalizedMessage());
								continue;
							}

							statusLock.lock();
							status.delete(0, status.length());
							status.append(json);
							statusLock.unlock();

							break;
						}

					} else if (bytes == 0) { // timeout
						// System.out.println("Uart read timeout");
						// Log.i("[uartAgent]", "read timeout");
						continue;
					} else {
						// close the UART, try reopen it
						Log.e("[uartAgent]", "read failed");
						close();
						break;
					}
				}
			}
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		final UartAgent agent = new UartAgent("/dev/ttyUSB0", 115200, 8, 1, (byte) 'N', true);
		agent.init();

		// loopback testing
		Thread sendThread = new Thread(new Runnable() {
			@Override
			public void run() {
				byte cnt = 0;

				while (true) {
					byte[] status = new byte[10];
					status[0] = UartAgent.cmd_ReportAll;

					cnt = (byte) (cnt % 128);
					for (int i = 1; i < status.length; i++)
						status[i] = cnt;

					agent.frame.send(status, status.length);

					try {
						Thread.sleep(5 * 1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					cnt++;
				}
			}
		});

		sendThread.start();

		while (true) {
			String status = agent.getStatusBlock();
			System.out.println("status=" + status);

			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}