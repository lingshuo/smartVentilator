package cn.lisa.smartventilator.bean;

public class Switch {
	private int SET_switch;
	private int Switch_ventilator;
	private int Switch_ultraviolet;
	private int Switch_plasma;
	private int Switch_lamp;
	private int[] Switch=new int[4];
	public Switch(int sET_switch, int switch_ventilator,
			int switch_ultraviolet, int switch_plasma, int switch_lamp) {
		SET_switch = sET_switch;
		Switch_ventilator = switch_ventilator;
		Switch_ultraviolet = switch_ultraviolet;
		Switch_plasma = switch_plasma;
		Switch_lamp = switch_lamp;
		Switch[0]=Switch_ventilator;
		Switch[1]=Switch_ultraviolet;
		Switch[2]=Switch_plasma;
		Switch[3]=Switch_lamp;
	}
	
}
