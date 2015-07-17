package cn.lisa.smartventilator.utility.network;

public class JSONDefine {
	//for get switch&sensor value
	public static final String KEY_reault	= "result";
	public static final String KEY_switch	= "sw";
	public static final String KEY_smog	= "smog";
	public static final String KEY_hcho	= "hcho";
	public static final String KEY_pm25	= "pm25";
	public static final String KEY_hwError	= "hwError";
	
	//for set switch value
	public static final String KEY_focus	= "focus";
	public static final String SW_lamp	= "lamp";
	public static final String SW_ultra	= "ultra";
	public static final String SW_plasma	= "plasma";
	public static final String SW_fan	= "fan";
	
	public static final String KEY_swValue	= "swVal";
	public static final int VAL_swOFF	= 0;
	public static final int VAL_swON	= 1;
	public static final int VAL_swLevel1	= 1;
	public static final int VAL_swLevel2	= 2;
	public static final int VAL_swLevel3	= 3;
}
