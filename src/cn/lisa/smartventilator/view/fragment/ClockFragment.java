package cn.lisa.smartventilator.view.fragment;

import cn.lisa.smartventilator.controller.entity.SaveRun;
import cn.lisa.smartventilator.controller.service.MonitorService;
import cn.lisa.smartventilator.utility.system.ScreenInfo;
import cn.lisa.smartventilator.controller.activity.ClockActivity;
import cn.lisa.smartventilator.controller.activity.MainActivity;
import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.view.view.Wheel;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ClockFragment extends Fragment {

	View timepickerview;
	Context context;
	Wheel wheel;
	LinearLayout timepickerlin;
	Button btnselecttime, daojishijicubutton;
	RelativeLayout listjishi;
	Animation rotateAnimation, secondrotateAnimation, hourrotateAnimation;
	LinearLayout hoursoflinear;
	int mlCount = -1;
	TextView tvTime, hours;
	private ToggleButton ringtixing;
	boolean ring = true;
	static boolean screen = true;
	private Intent intent;
	private PendingIntent pendingIntent;
	private AlarmManager alarmManager;
	private SendReciever sendReciever;
	public static final String SHOWCOUNTACTION="showcount";
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 333:
//				MainActivity.mCount--;
				if (MainActivity.mCount <= 0) {
					enddaojishi();
				}
				if(MainActivity.mCount>0){
					listjishi.setVisibility(View.VISIBLE);
					timepickerlin.setVisibility(View.GONE);
				}

				int totalSec = 0;
				totalSec = (int) (MainActivity.mCount);
				int min = (totalSec / 60);
				if (min >= 60) {
					hoursoflinear.setVisibility(View.VISIBLE);
					hours.setText(String.valueOf(min / 60));
					min = min % 60;
				} else {
					hoursoflinear.setVisibility(View.GONE);
				}
				int sec = (totalSec % 60);
				try {
					tvTime.setText(String.format("%1$02d:%2$02d", min, sec));
				} catch (Exception e) {
					tvTime.setText("" + min + ":" + sec);
					e.printStackTrace();
				}
				if(MainActivity.mCount>0){
					listjishi.setVisibility(View.VISIBLE);
					timepickerlin.setVisibility(View.GONE);
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};


	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
		
		View view = inflater.inflate(R.layout.fragment_clock, null);
		initView(view, inflater);
		return view;
	}

	@SuppressLint("InflateParams")
	private void initView(View view, LayoutInflater inflater) {
		timepickerlin = (LinearLayout) view.findViewById(R.id.timepickerlin);
		listjishi = (RelativeLayout) view.findViewById(R.id.daojishirelativ);

		btnselecttime = (Button) view.findViewById(R.id.daojishistartbutton);
		ringtixing = (ToggleButton) view.findViewById(R.id.ringtixing);

		ringtixing.setChecked(true);
		
		sendReciever = new SendReciever();
		IntentFilter filter = new IntentFilter();
		filter.addAction(SHOWCOUNTACTION);
		context.registerReceiver(sendReciever, filter);

		ringtixing.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (ring) {
					ringtixing.setChecked(false);
					ring = false;
					SharedPreferences sp=context.getSharedPreferences("smartventilator.preferences", 0);
					Editor et=sp.edit();
					et.putBoolean("ring", ring);
					et.commit();
				} else {
					ringtixing.setChecked(true);
					ring = true;
					SharedPreferences sp=context.getSharedPreferences("smartventilator.preferences", 0);
					Editor et=sp.edit();
					et.putBoolean("ring", ring);
					et.commit();
				}
			}
		});
		
		daojishijicubutton = (Button) view.findViewById(R.id.daojishijicubutton);

		tvTime = (TextView) view.findViewById(R.id.daojishitvTime);
		hours = (TextView) view.findViewById(R.id.daojishihours);
		hoursoflinear = (LinearLayout) view.findViewById(R.id.daojishihoursoflinear);
		timepickerview = inflater.inflate(R.layout.timepicker, null);
		ScreenInfo screenInfo = new ScreenInfo((Activity) MainActivity.getMyActivityContext());
		wheel = new Wheel(timepickerview);
		wheel.screenheight = screenInfo.getHeight();
		wheel.initDateTimePicker(0, 0, 0);
		timepickerlin.addView(timepickerview);
		SaveRun.setisdaojishi(false);

	}

	public void enddaojishi() {
		try {
			handler.removeMessages(333);
			listjishi.setVisibility(View.GONE);
			timepickerlin.setVisibility(View.VISIBLE);
			MainActivity.mCount = -1;
			btnselecttime.setText("开始");
			SaveRun.setisdaojishi(false);
			Intent intent=new Intent();
			intent.setAction(MonitorService.STOPCOUNTACTION);
			context.sendBroadcast(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStart() {
		intent = new Intent(context, ClockActivity.class);
		pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		Log.i("Alarm", "onStart:" + MainActivity.mCount);
		if (MainActivity.mCount > 0) {
			SaveRun.setisdaojishi(true);
			btnselecttime.setText("暂停");
			listjishi.setVisibility(View.VISIBLE);
			timepickerlin.setVisibility(View.GONE);
		}
		//stop
		daojishijicubutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listjishi.setVisibility(View.GONE);
				timepickerlin.setVisibility(View.VISIBLE);
				MainActivity.mCount = -1;
				btnselecttime.setText("开始");
				tvTime.setText("00:00");
				hoursoflinear.setVisibility(View.INVISIBLE);
				SaveRun.setisdaojishi(false);
				alarmManager.cancel(pendingIntent);
				Intent intent=new Intent();
				intent.setAction(MonitorService.STOPCOUNTACTION);
				context.sendBroadcast(intent);
			}
		});
		//start and pause
		btnselecttime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (MonitorService.state!=MonitorService.STARTCOUNTACTION) {
					int h = wheel.getwv_year();
					int m = wheel.getwv_month();
					int s = wheel.getwv_day();
					if (MainActivity.mCount == -1 || MainActivity.mCount == 0) {
						MainActivity.mCount = h * 3600 + m * 60 + s * 1;
					}
					if (MainActivity.mCount > 0) {
						SaveRun.setisdaojishi(true);
						btnselecttime.setText("暂停");
						alarmManager.cancel(pendingIntent);
						alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
								+ MainActivity.mCount * 1000, pendingIntent);
						Intent intent=new Intent();
						intent.setAction(MonitorService.STARTCOUNTACTION);
						context.sendBroadcast(intent);
						
					}
				} else {
					try {
						SaveRun.setisdaojishi(false);
						btnselecttime.setText("继续");
						alarmManager.cancel(pendingIntent);
						Intent intent=new Intent();
						intent.setAction(MonitorService.PAUSECOUNTACTION);
						context.sendBroadcast(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		Log.i("Alarm", "save");
		super.onSaveInstanceState(outState);
	}
	@Override
	public void onDestroy() {
		try {
			context.unregisterReceiver(sendReciever);
		} catch (Exception e) {
			Log.i("service", e.getLocalizedMessage());
		}
		if (alarmManager != null && pendingIntent != null) {
			alarmManager.cancel(pendingIntent);
		}
		super.onDestroy();
	}
	
	public class SendReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			handler.sendEmptyMessage(333);
		}}
}
