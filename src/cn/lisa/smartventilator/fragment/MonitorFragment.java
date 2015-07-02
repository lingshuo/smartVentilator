package cn.lisa.smartventilator.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.EventLog.Event;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.bean.Ventilator;
import cn.lisa.smartventilator.manager.VentilatorManager;
import cn.lisa.smartventilator.service.MonitorService;

public class MonitorFragment extends Fragment implements OnClickListener,
		OnCheckedChangeListener ,OnTouchListener{
	private ToggleButton tb_ventilator;
	private ToggleButton tb_lamp;
	private ToggleButton tb_ultraviolet;
	private ToggleButton tb_plasma;
	private TextView tv_smog;
	private TextView tv_aldehyde;
	private TextView tv_pm2_5;
	private RelativeLayout layout_gears_control;
	private RelativeLayout layout_gears_blank;
	private Button mBtn1;
	private Button mBtn2;
	private Button mBtn3;
	private BroadcastMain broadcastMain;
	private Context context;
	public static VentilatorManager ventilatorManager;
	private Ventilator ventilator;
	/***
	 * �������������ʾ��handler
	 */
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.obj != null)
				switch (msg.what) {
				case VentilatorManager.SHOW_DATA:
					Ventilator ventilator = (Ventilator) msg.obj;
					if(ventilator!=null){
						Log.i("sv", ventilator.toString());
						initdata(ventilator);
					}
					break;

				default:
					break;
				}
			else {
				Log.e("sv", "null");
			}

		};

	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_monitor, null);
		view = initView(view);
		this.context = getActivity();
		ventilatorManager = new VentilatorManager();
		initReceiver();
		initListener();
		return view;
	}

	/**
	 * ��ʼ����ͼ
	 */
	private View initView(View view) {
		// ��ʾ���
		tv_smog = (TextView) view.findViewById(R.id.monitor_smog_result);
		tv_aldehyde = (TextView) view
				.findViewById(R.id.monitor_aldehyde_result);
		tv_pm2_5 = (TextView) view.findViewById(R.id.monitor_pm2_5_result);

		// ����
		tb_ventilator = (ToggleButton) view
				.findViewById(R.id.control_ventilator);
		tb_lamp = (ToggleButton) view.findViewById(R.id.control_lamp);
		tb_plasma = (ToggleButton) view.findViewById(R.id.control_plasma);
		tb_ultraviolet = (ToggleButton) view
				.findViewById(R.id.control_ultraviolet);

		// ��λ
		layout_gears_control = (RelativeLayout) view
				.findViewById(R.id.control_ventilator_gears);
		layout_gears_blank = (RelativeLayout) view
				.findViewById(R.id.control_ventilator_blank);
		if (!tb_ventilator.isChecked()) {
			layout_gears_blank.setVisibility(View.VISIBLE);
			layout_gears_control.setVisibility(View.GONE);
		} else {
			layout_gears_blank.setVisibility(View.GONE);
			layout_gears_control.setVisibility(View.VISIBLE);
		}

		// ������λ�İ�ť
		mBtn1 = (Button) view.findViewById(R.id.control_ventilator_gears_btn_1);
		mBtn2 = (Button) view.findViewById(R.id.control_ventilator_gears_btn_2);
		mBtn3 = (Button) view.findViewById(R.id.control_ventilator_gears_btn_3);

		return view;
	}

	/**
	 * ��ʼ��������
	 */
	private void initListener() {
		//״̬�ı�ʱ�ļ�����
		tb_lamp.setOnCheckedChangeListener(this);
		tb_plasma.setOnCheckedChangeListener(this);
		tb_ultraviolet.setOnCheckedChangeListener(this);
		tb_ventilator.setOnCheckedChangeListener(this);
		//���������
		tb_lamp.setOnTouchListener(this);
		tb_plasma.setOnTouchListener(this);
		tb_ultraviolet.setOnTouchListener(this);
		tb_ventilator.setOnTouchListener(this);
		// ��λ�л������¼�
		mBtn1.setOnClickListener(this);
		mBtn2.setOnClickListener(this);
		mBtn3.setOnClickListener(this);
	}

	/**
	 * ��ʼ���㲥������
	 */
	private void initReceiver() {
		broadcastMain = new BroadcastMain();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MonitorService.BROADCASTACTION);
		context.registerReceiver(broadcastMain, filter);
	}
	/***
	 * ��ʼ����ʾ����
	 * @param ventilator
	 */
	private void initdata(Ventilator ventilator){
		this.ventilator=ventilator;
		tv_smog.setText(ventilator.getSmog());
		tv_aldehyde.setText(ventilator.getAldehyde());
		tv_pm2_5.setText(ventilator.getPm2_5());
		tb_lamp.setChecked(ventilator.getState_lamp());
		tb_plasma.setChecked(ventilator.getState_plasma());
		tb_ultraviolet.setChecked(ventilator.getState_ultraviolet());
		tb_ventilator.setChecked(ventilator.getState_ventilator());
		displayGear(ventilator.getGear_ventilator());
	}
	/**
	 * �л���λ
	 * 
	 * @param gear
	 *            ��λ:1,2,3
	 */
	private void changeGear(int gear) {
		Log.i("sv", "change gears to " + gear);
	}
	/***
	 * ��ʾ��λ
	 * @param gear
	 */
	private void displayGear(int gear){
		switch (gear) {
		case 1:
			layout_gears_blank.setVisibility(View.GONE);
			layout_gears_control.setVisibility(View.VISIBLE);
			mBtn1.setVisibility(View.VISIBLE);
			mBtn2.setVisibility(View.GONE);
			mBtn3.setVisibility(View.GONE);
			break;
		case 2:
			layout_gears_blank.setVisibility(View.GONE);
			layout_gears_control.setVisibility(View.VISIBLE);
			mBtn1.setVisibility(View.GONE);
			mBtn2.setVisibility(View.VISIBLE);
			mBtn3.setVisibility(View.GONE);
			break;
		case 3:
			layout_gears_blank.setVisibility(View.GONE);
			layout_gears_control.setVisibility(View.VISIBLE);
			mBtn1.setVisibility(View.GONE);
			mBtn2.setVisibility(View.GONE);
			mBtn3.setVisibility(View.VISIBLE);
			break;
		default:
			layout_gears_blank.setVisibility(View.VISIBLE);
			layout_gears_control.setVisibility(View.GONE);
			break;
		}
	}

	/***
	 * �������
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		// �����ƿ���
		case R.id.control_lamp:
			break;
		// �������豸����
		case R.id.control_ultraviolet:
			break;
		// �������豸����
		case R.id.control_plasma:
			break;
		// �������
		case R.id.control_ventilator:
			Log.i("sv", "ventilator check changed");
			if (isChecked) {
				// ѡ��
				layout_gears_blank.setVisibility(View.GONE);
				layout_gears_control.setVisibility(View.VISIBLE);
			} else {
				// δѡ��
				layout_gears_blank.setVisibility(View.VISIBLE);
				layout_gears_control.setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * �����λ��ť����
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 1��
		case R.id.control_ventilator_gears_btn_1:
			changeGear(2);
			mBtn1.setVisibility(View.GONE);
			mBtn2.setVisibility(View.VISIBLE);
			break;
		// 2��
		case R.id.control_ventilator_gears_btn_2:
			changeGear(3);
			mBtn2.setVisibility(View.GONE);
			mBtn3.setVisibility(View.VISIBLE);
			break;
		// 3��
		case R.id.control_ventilator_gears_btn_3:
			changeGear(1);
			mBtn3.setVisibility(View.GONE);
			mBtn1.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
	}
	/***
	 * ������أ�����ָ��
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction()==MotionEvent.ACTION_DOWN){
			return true;
		}
		else if(event.getAction()==MotionEvent.ACTION_UP){
			switch (v.getId()) {
			// �����ƿ���
			case R.id.control_lamp:
				Log.i("sv", "lamp touched");
//				ventilatorManager.forceUpdate(context);
				return true;
			// �������豸����
			case R.id.control_ultraviolet:
				Log.i("sv", "ultraviolet touched");
//				ventilatorManager.forceUpdate(context);
				return true;
			// �������豸����
			case R.id.control_plasma:
				Log.i("sv", "plasma touched");
//				ventilatorManager.forceUpdate(context);
				return true;
			// �������
			case R.id.control_ventilator:
				Log.i("sv", "ventilator touched");
//				ventilatorManager.forceUpdate(context);
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
		super.onResume();
	}

	@Override
	public void onStop() {
		try{
			context.unregisterReceiver(broadcastMain);
		}catch(Exception e){
			Log.i("sv", e.getLocalizedMessage());
		}
		super.onStop();
	}
	public class BroadcastMain extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String jsonString = intent.getExtras().getString("jsonstr");
			ventilatorManager.setVentilator(jsonString);
			Message msg = handler.obtainMessage();
			msg.what =VentilatorManager.SHOW_DATA;
			msg.obj = ventilatorManager.getVentilator();
			handler.sendMessage(msg);
		}

	}


	
}
