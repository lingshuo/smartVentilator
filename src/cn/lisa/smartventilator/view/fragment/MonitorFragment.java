package cn.lisa.smartventilator.view.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.controller.entity.Ventilator;
import cn.lisa.smartventilator.controller.manager.VentilatorManager;
import cn.lisa.smartventilator.controller.service.MonitorService;

public class MonitorFragment extends Fragment implements OnClickListener, OnTouchListener {
	private ToggleButton tb_ventilator;
	private ToggleButton tb_lamp;
	private ToggleButton tb_ultraviolet;
	private ToggleButton tb_plasma;
	private TextView tv_smog;
	private TextView tv_aldehyde;
	private TextView tv_pm2_5;
	private RatingBar rb_smog;
	@SuppressWarnings("unused")
	private RatingBar rb_aldehyde;
	@SuppressWarnings("unused")
	private RatingBar rb_pm2_5;
	private ImageView iv_smog;
	@SuppressWarnings("unused")
	private ImageView iv_aldehyde;
	@SuppressWarnings("unused")
	private ImageView iv_pm2_5;
	private RelativeLayout layout_gears_control;
	private Button mBtn1;
	private Button mBtn2;
	private Button mBtn3;
	private BroadcastMain broadcastMain;
	private Context context;
	public static VentilatorManager ventilatorManager;
	private Ventilator ventilator;
	/***
	 * 处理界面数据显示的handler
	 */
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.obj != null)
				switch (msg.what) {
				case VentilatorManager.SHOW_DATA:
					ventilator = (Ventilator) msg.obj;
					if (ventilator != null) {
						Log.i("ventilator", ventilator.toString());
						initdata(ventilator);
					}
					break;

				default:
					break;
				}
			else {
				Log.e("ventilator", "null");
			}

		};

	};

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_monitor, null);
		view = initView(view);
		this.context = getActivity();
		ventilatorManager = new VentilatorManager(context);
		initReceiver();
		initListener();
		return view;
	}

	/**
	 * 初始化视图
	 */
	private View initView(View view) {
		// 显示结果
		tv_smog = (TextView) view.findViewById(R.id.monitor_smog_result);
		rb_smog = (RatingBar) view.findViewById(R.id.monitor_smog_ratingBar);
		iv_smog = (ImageView) view.findViewById(R.id.monitor_smog_bar_image);
		tv_aldehyde = (TextView) view.findViewById(R.id.monitor_aldehyde_result);
		rb_aldehyde = (RatingBar) view.findViewById(R.id.monitor_aldehyde_ratingBar);
		iv_aldehyde = (ImageView) view.findViewById(R.id.monitor_aldehyde_bar_image);
		tv_pm2_5 = (TextView) view.findViewById(R.id.monitor_pm2_5_result);
		rb_pm2_5 = (RatingBar) view.findViewById(R.id.monitor_pm2_5_ratingBar);
		iv_pm2_5 = (ImageView) view.findViewById(R.id.monitor_pm2_5_bar_image);
		// 控制
		tb_ventilator = (ToggleButton) view.findViewById(R.id.control_ventilator);
		tb_lamp = (ToggleButton) view.findViewById(R.id.control_lamp);
		tb_plasma = (ToggleButton) view.findViewById(R.id.control_plasma);
		tb_ultraviolet = (ToggleButton) view.findViewById(R.id.control_ultraviolet);

		// 档位
		layout_gears_control = (RelativeLayout) view.findViewById(R.id.control_ventilator_gears_btns);

		// 三个档位的按钮
		mBtn1 = (Button) view.findViewById(R.id.control_ventilator_gears_btn_1);
		mBtn2 = (Button) view.findViewById(R.id.control_ventilator_gears_btn_2);
		mBtn3 = (Button) view.findViewById(R.id.control_ventilator_gears_btn_3);

		return view;
	}

	/**
	 * 初始化监听器
	 */
	private void initListener() {
		// 点击，发送
		tb_lamp.setOnTouchListener(this);
		tb_plasma.setOnTouchListener(this);
		tb_ultraviolet.setOnTouchListener(this);
		tb_ventilator.setOnTouchListener(this);
		// 档位切换监听事件
		mBtn1.setOnClickListener(this);
		mBtn2.setOnClickListener(this);
		mBtn3.setOnClickListener(this);
	}

	/**
	 * 初始化广播接收器
	 */
	private void initReceiver() {
		broadcastMain = new BroadcastMain();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MonitorService.BROADCASTACTION);
		context.registerReceiver(broadcastMain, filter);
	}

	/***
	 * 初始化显示数据
	 * 
	 * @param ventilator
	 */
	private void initdata(Ventilator ventilator) {
		this.ventilator = ventilator;
		initSmog(ventilator.getSmog());
		initPm2_5(ventilator.getPm2_5());
		initAldehyde(ventilator.getAldehyde());
		initControl(ventilator);
		displayGear(ventilator.getGear_ventilator());
	}

	/**
	 * 初始化烟雾显示信息
	 * 
	 * @param smog
	 */
	private void initSmog(int smog) {
		switch (smog) {
		case 0:
			tv_smog.setText(R.string.stat_low);
			rb_smog.setRating(1f);
			iv_smog.setBackgroundResource(R.drawable.monitor_bar_1);
			break;
		case 1:
			tv_smog.setText(R.string.stat_normal);
			rb_smog.setRating(2f);
			iv_smog.setBackgroundResource(R.drawable.monitor_bar_2);
			break;
		case 2:
			tv_smog.setText(R.string.stat_high);
			rb_smog.setRating(3f);
			iv_smog.setBackgroundResource(R.drawable.monitor_bar_3);
			break;
		case 3:
			tv_smog.setText(R.string.stat_very_high);
			rb_smog.setRating(4f);
			iv_smog.setBackgroundResource(R.drawable.monitor_bar_4);
			break;
		default:
			break;
		}
	}

	/**
	 * 初始化pm2.5显示信息
	 * 
	 * @param pm2_5
	 */
	private void initPm2_5(int pm2_5) {
		tv_pm2_5.setText(String.valueOf(pm2_5));
		// switch (pm2_5) {
		// case 0:
		// tv_pm2_5.setText(R.string.stat_low);
		// rb_pm2_5.setRating(1f);
		// iv_pm2_5.setImageResource(R.drawable.monitor_bar_1);
		// break;
		// case 1:
		// tv_pm2_5.setText(R.string.stat_normal);
		// rb_pm2_5.setRating(2f);
		// iv_pm2_5.setImageResource(R.drawable.monitor_bar_2);
		// break;
		// case 2:
		// tv_pm2_5.setText(R.string.stat_high);
		// rb_pm2_5.setRating(3f);
		// iv_pm2_5.setImageResource(R.drawable.monitor_bar_3);
		// break;
		// case 3:
		// tv_pm2_5.setText(R.string.stat_very_high);
		// rb_pm2_5.setRating(4f);
		// iv_pm2_5.setImageResource(R.drawable.monitor_bar_4);
		// break;
		// default:
		// break;
		// }
	}

	/***
	 * 初始化酚醛显示信息
	 * 
	 * @param aldehyde
	 */
	private void initAldehyde(int aldehyde) {
		tv_aldehyde.setText(String.valueOf(aldehyde));
		// switch (aldehyde) {
		// case 0:
		// tv_aldehyde.setText(R.string.stat_low);
		// rb_aldehyde.setRating(1f);
		// iv_aldehyde.setImageResource(R.drawable.monitor_bar_1);
		// break;
		// case 1:
		// tv_aldehyde.setText(R.string.stat_normal);
		// rb_aldehyde.setRating(2f);
		// iv_aldehyde.setImageResource(R.drawable.monitor_bar_2);
		// break;
		// case 2:
		// tv_aldehyde.setText(R.string.stat_high);
		// rb_aldehyde.setRating(3f);
		// iv_aldehyde.setImageResource(R.drawable.monitor_bar_3);
		// break;
		// case 3:
		// tv_aldehyde.setText(R.string.stat_very_high);
		// rb_aldehyde.setRating(4f);
		// iv_aldehyde.setImageResource(R.drawable.monitor_bar_4);
		// break;
		// default:
		// break;
		// }
	}

	/**
	 * 初始化控制按钮
	 * 
	 * @param ventilator
	 */
	private void initControl(Ventilator ventilator) {
		tb_lamp.setChecked(ventilator.getState_lamp());
		tb_plasma.setChecked(ventilator.getState_plasma());
		tb_ultraviolet.setChecked(ventilator.getState_ultraviolet());
		tb_ventilator.setChecked(ventilator.getState_ventilator());
	}

	/***
	 * 显示档位
	 * 
	 * @param gear
	 */
	private void displayGear(int gear) {
		switch (gear) {
		case 1:
			layout_gears_control.setVisibility(View.VISIBLE);
			mBtn1.setBackgroundResource(R.drawable.ventilator_gears_btn_1_pressed);
			mBtn2.setBackgroundResource(R.drawable.ventilator_gears_btn_2);
			mBtn3.setBackgroundResource(R.drawable.ventilator_gears_btn_3);
			break;
		case 2:
			layout_gears_control.setVisibility(View.VISIBLE);
			mBtn1.setBackgroundResource(R.drawable.ventilator_gears_btn_1);
			mBtn2.setBackgroundResource(R.drawable.ventilator_gears_btn_2_pressed);
			mBtn3.setBackgroundResource(R.drawable.ventilator_gears_btn_3);
			break;
		case 3:
			layout_gears_control.setVisibility(View.VISIBLE);
			mBtn1.setBackgroundResource(R.drawable.ventilator_gears_btn_1);
			mBtn2.setBackgroundResource(R.drawable.ventilator_gears_btn_2);
			mBtn3.setBackgroundResource(R.drawable.ventilator_gears_btn_3_pressed);
			break;
		default:
			layout_gears_control.setVisibility(View.GONE);
			break;
		}
	}

	/**
	 * 点击档位按钮操作
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 1档
		case R.id.control_ventilator_gears_btn_1:
			ventilatorManager.sendVentilatorCommand(VentilatorManager.VENTILATOR, VentilatorManager.VENTILATOR_1, ventilator);
			break;
		// 2档
		case R.id.control_ventilator_gears_btn_2:
			ventilatorManager.sendVentilatorCommand(VentilatorManager.VENTILATOR, VentilatorManager.VENTILATOR_2, ventilator);
			break;
		// 3档
		case R.id.control_ventilator_gears_btn_3:
			ventilatorManager.sendVentilatorCommand(VentilatorManager.VENTILATOR, VentilatorManager.VENTILATOR_3, ventilator);
			break;
		default:
			break;
		}
	}

	/***
	 * 点击开关，发送指令
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			switch (v.getId()) {
			// 照明灯开关
			case R.id.control_lamp:
				Log.i("ventilator", "lamp touched");

				if (!ventilator.getState_lamp()) {
					// 开灯
					ventilatorManager.sendVentilatorCommand(VentilatorManager.LAMP, VentilatorManager.DEVICE_ON, ventilator);
				} else {
					// 关灯
					ventilatorManager.sendVentilatorCommand(VentilatorManager.LAMP, VentilatorManager.DEVICE_OFF, ventilator);
				}

				return true;
				// 紫外线设备开关
			case R.id.control_ultraviolet:
				Log.i("ventilator", "ultraviolet touched");
				if (!ventilator.getState_ultraviolet()) {
					// 开
					ventilatorManager.sendVentilatorCommand(VentilatorManager.ULTRAVIOLET, VentilatorManager.DEVICE_ON, ventilator);
				} else {
					// 关
					ventilatorManager.sendVentilatorCommand(VentilatorManager.ULTRAVIOLET, VentilatorManager.DEVICE_OFF, ventilator);
				}
				return true;
				// 等离子设备开关
			case R.id.control_plasma:
				Log.i("ventilator", "plasma touched");
				if (!ventilator.getState_plasma()) {
					// 开
					ventilatorManager.sendVentilatorCommand(VentilatorManager.PLASMA, VentilatorManager.DEVICE_ON, ventilator);
				} else {
					// 关
					ventilatorManager.sendVentilatorCommand(VentilatorManager.PLASMA, VentilatorManager.DEVICE_OFF, ventilator);
				}
				return true;
				// 风机开关
			case R.id.control_ventilator:
				Log.i("ventilator", "ventilator touched");
				if (!ventilator.getState_ventilator()) {
					// 打开
					ventilatorManager.sendVentilatorCommand(VentilatorManager.VENTILATOR, VentilatorManager.VENTILATOR_3, ventilator);
					layout_gears_control.setVisibility(View.VISIBLE);
				} else {
					// 关
					ventilatorManager.sendVentilatorCommand(VentilatorManager.VENTILATOR, VentilatorManager.DEVICE_OFF, ventilator);
					layout_gears_control.setVisibility(View.GONE);
				}
				return true;
			default:
				break;
			}
		}
		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		try {
			initReceiver();
		} catch (Exception e) {
			Log.i("ventilator", e.getLocalizedMessage());
		}
		super.onResume();
	}

	@Override
	public void onStop() {
		try {
			context.unregisterReceiver(broadcastMain);
		} catch (Exception e) {
			Log.i("ventilator", e.getLocalizedMessage());
		}
		super.onStop();
	}

	public class BroadcastMain extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String jsonString = intent.getExtras().getString("jsonstr");
			ventilatorManager.setVentilator(jsonString);
			Message msg = handler.obtainMessage();
			msg.what = VentilatorManager.SHOW_DATA;
			msg.obj = ventilatorManager.getVentilator();
			handler.sendMessage(msg);
		}

	}

}
