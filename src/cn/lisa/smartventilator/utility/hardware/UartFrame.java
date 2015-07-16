package cn.lisa.smartventilator.utility.hardware;

public class UartFrame {

	static {
		System.loadLibrary("UartFrame");
	}

	public native boolean init(String dev, int baudrate, int databits,
			int stopbits, byte parity);

	public native void destroy();

	public native int recv(byte[] buf, int len);

	public native int send(byte[] buf, int len);

}
