package cn.lisa.smartventilator.controller.entity;

/***
 * 显示监控数据专用类
 */
public class Ventilator {

	private final byte RPT_all = 0x02;

	private byte Switch; // 开关状态

	// 数据
	private int pm2_5;
	private int aldehyde;
	private int smog;

	private int hwError; // 故障信息

	private int gear_ventilator; // 风机档位

	// 状态
	private boolean state_ventilator;
	private boolean state_ultraviolet;
	private boolean state_plasma;
	private boolean state_lamp;

	// 错误码
	private boolean error_pm25;
	private boolean error_aldehyde;
	private boolean error_smog;

	public Ventilator(int m_Switch, int pm2_5, int aldehyde, int smog, int hwError) {
		this.hwError = hwError;
		this.pm2_5 = pm2_5;
		this.aldehyde = aldehyde;
		this.smog = smog;
		this.Switch = (byte) m_Switch;

		setventilatorGearandState(this.Switch);
		setHwError((short) hwError);
	}

	// private byte[] int2byte(int res) {
	// byte[] targets = new byte[4];
	//
	// targets[0] = (byte) (res & 0xff);// 最低位
	// targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
	// targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
	// targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
	// return targets;
	// }
	/***
	 * 风机状态和档位读取
	 * 
	 * @param sw
	 */
	private void setventilatorGearandState(byte sw) {
		this.state_lamp = (sw & 0x80) == 0 ? false : true;
		this.state_plasma = (sw & 0x40) == 0 ? false : true;
		this.state_ultraviolet = (sw & 0x20) == 0 ? false : true;
		int v = sw & 0x03;
		switch (v) {
		case 1:
		case 2:
		case 3:
			this.state_ventilator = true;
			this.gear_ventilator = v;
			break;
		case 0:
		default:
			this.state_ventilator = false;
			this.gear_ventilator = 0;
			break;
		}
	}

	/***
	 * 错误码
	 */
	private void setHwError(short hwError) {
		this.error_pm25 = (hwError & 0x8000) == 0 ? false : true;
		this.error_aldehyde = (hwError & 0x4000) == 0 ? false : true;
		this.error_smog = (hwError & 0x2000) == 0 ? false : true;
	}

	public byte getRPT_all() {
		return this.RPT_all;
	}

	public int getHwError() {
		return hwError;
	}

	public byte getSwitch() {
		return Switch;
	}

	public int getPm2_5() {
		return pm2_5;
	}

	public int getAldehyde() {
		return aldehyde;
	}

	public int getSmog() {
		return smog;
	}

	public int getGear_ventilator() {
		return gear_ventilator;
	}

	public boolean getState_ventilator() {
		return state_ventilator;
	}

	public boolean getState_ultraviolet() {
		return state_ultraviolet;
	}

	public boolean getState_plasma() {
		return state_plasma;
	}

	public boolean getState_lamp() {
		return state_lamp;
	}

	public boolean isError_pm25() {
		return error_pm25;
	}

	public boolean isError_aldehyde() {
		return error_aldehyde;
	}

	public boolean isError_smog() {
		return error_smog;
	}

	@Override
	public String toString() {
		return "ventilator:aldehyde:" + this.aldehyde + "/pm2.5:" + this.pm2_5 + "/smog:"
				+ this.smog + "/state_lamp:" + state_lamp + "/state_plasma:" + state_plasma
				+ "/state_ultraviolet:" + state_ultraviolet + "/state_ventilator:"
				+ state_ventilator + "/gear_ventilator:" + gear_ventilator;
	}
}
