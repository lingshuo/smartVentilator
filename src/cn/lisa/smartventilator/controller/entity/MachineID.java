package cn.lisa.smartventilator.controller.entity;

import android.util.Log;

import com.example.writeid.RomID;

public class MachineID {
	public static final int ID_BYTES = 4;
	public static final int LC_BYTES = 16;
	public static final int TOTAL_BYTES = 20;
	private String mid;
	private byte[] mlic = new byte[LC_BYTES];

	public MachineID() {

	}

	public void readId() {
		RomID romID = new RomID();
		byte[] machID = new byte[MachineID.TOTAL_BYTES];
		byte[] machLice = new byte[MachineID.LC_BYTES];
		int err = romID.getID(machID);
		if (err != 0) {
			Log.i("id", "get err=" + err);
			this.mid = null;
			this.mlic = null;
		}
		System.arraycopy(machID, MachineID.ID_BYTES, machLice, 0, MachineID.LC_BYTES);
		long longID = ((long) (machID[0] & 0xFFL) << 24) | ((long) (machID[1] & 0xFFL) << 16)
				| ((long) (machID[2] & 0xFFL) << 8) | ((long) (machID[3] & 0xFFL) << 0);
		this.mid = Long.toString(longID);
		this.mlic = machLice;
	}

	public String getMid() {
		return mid;
	}

	public byte[] getMlic() {
		return mlic;
	}

}
