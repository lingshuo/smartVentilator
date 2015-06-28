package cn.lisa.smartventilator.bean;

public class Ventilator {
	private String RPT_all;
	private int[] Switch=new int[4];
	private String pm2_5;
	private String aldehyde;
	private String smog;
	
	public Ventilator(String RPT_all,int[] Switch,String pm2_5,String aldehyde,String smog){
		this.RPT_all=RPT_all;
		this.Switch=Switch;
		this.pm2_5=pm2_5;
		this.aldehyde=aldehyde;
		this.smog=smog;
	}

	public String getRPT_all() {
		return RPT_all;
	}

	public void setRPT_all(String rPT_all) {
		RPT_all = rPT_all;
	}

	public int[] getSwitch() {
		return Switch;
	}

	public void setSwitch(int[] switch1) {
		Switch = switch1;
	}

	public String getPm2_5() {
		return pm2_5;
	}

	public void setPm2_5(String pm2_5) {
		this.pm2_5 = pm2_5;
	}

	public String getAldehyde() {
		return aldehyde;
	}

	public void setAldehyde(String aldehyde) {
		this.aldehyde = aldehyde;
	}

	public String getSmog() {
		return smog;
	}

	public void setSmog(String smog) {
		this.smog = smog;
	}
	
}
