package cn.lisa.smartventilator.view.fragment;

import java.util.Timer;
import java.util.TimerTask;

import cn.lisa.smartventilator.controller.entity.SaveRun;
import cn.lisa.smartventilator.utility.system.ScreenInfo;
import cn.lisa.smartventilator.view.activity.ClockActivity;
import cn.lisa.smartventilator.view.activity.MainActivity;
import cn.lisa.smartventilator.view.view.SlipButton;
import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.view.view.Wheel;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

public class ClockFragment extends Fragment {

	View timepickerview;
	Context context;
	Wheel wheel;
	LinearLayout timepickerlin;
	Button btnselecttime, daojishijicubutton;
	RelativeLayout listjishi;
	private Timer timer = null;
	private TimerTask task = null;
	private Message msg = null;
	Animation rotateAnimation, secondrotateAnimation, hourrotateAnimation;
	float predegree = 0;
	float secondpredegree = 0;
	float hourpredegree = 0;
	LinearLayout hoursoflinear;
	int mlCount = -1;
	TextView tvTime, hours;
	private SlipButton ringtixing;
	boolean ring = true;
	static boolean screen = true;
	private Intent intent;
	private PendingIntent pendingIntent;
	private AlarmManager alarmManager;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
				MainActivity.mCount--;
				Log.i("MainActivity.mCount", "MainActivity.mCount:" + MainActivity.mCount);
				if (MainActivity.mCount <= 0) {
					enddaojishi();
				}

				int totalSec = 0;
				int yushu = 0;
				totalSec = (int) (MainActivity.mCount / 10);
				yushu = (int) (MainActivity.mCount % 10);
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
					tvTime.setText(String.format("%1$02d:%2$02d.%3$d", min, sec, yushu));
				} catch (Exception e) {
					tvTime.setText("" + min + ":" + sec + "." + yushu);
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
		
		View view = inflater.inflate(R.layout.fragment_clock, null);
		initView(view, inflater);
		return view;
	}

	private void initView(View view, LayoutInflater inflater) {
		timepickerlin = (LinearLayout) view.findViewById(R.id.timepickerlin);
		listjishi = (RelativeLayout) view.findViewById(R.id.daojishirelativ);

		btnselecttime = (Button) view.findViewById(R.id.daojishistartbutton);
		ringtixing = (SlipButton) view.findViewById(R.id.ringtixing);

		ringtixing.setChecked(true);


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
			task.cancel();
			task = null;
			timer.cancel();
			timer.purge();
			timer = null;
			handler.removeMessages(msg.what);
			listjishi.setVisibility(View.GONE);
			timepickerlin.setVisibility(View.VISIBLE);
			MainActivity.mCount = -1;
			btnselecttime.setText("开始");
			SaveRun.setisdaojishi(false);
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
			if (null == task) {
				task = new TimerTask() {
					@Override
					public void run() {
						if (null == msg) {
							msg = new Message();
						} else {
							msg = Message.obtain();
						}
						msg.what = 100;
						handler.sendMessage(msg);
					}
				};
			}
			timer = new Timer(true);
			timer.schedule(task, 100, 100);
			alarmManager.cancel(pendingIntent);
			alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
					+ MainActivity.mCount * 100, pendingIntent);
		}

		daojishijicubutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				predegree = 0;
				secondpredegree = 0;
				hourpredegree = 0;
				listjishi.setVisibility(View.GONE);
				timepickerlin.setVisibility(View.VISIBLE);
				MainActivity.mCount = -1;
				btnselecttime.setText("开始");
				tvTime.setText("00:00.0");
				hoursoflinear.setVisibility(View.INVISIBLE);
				SaveRun.setisdaojishi(false);
				alarmManager.cancel(pendingIntent);
				try {
					if (task != null) {
						task.cancel();
						task = null;
						timer.cancel();
						timer.purge();
						timer = null;
						handler.removeMessages(msg.what);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		btnselecttime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (null == timer) {
					int h = wheel.getwv_year();
					int m = wheel.getwv_month();
					int s = wheel.getwv_day();
					if (MainActivity.mCount == -1 || MainActivity.mCount == 0) {
						MainActivity.mCount = h * 36000 + m * 600 + s * 10;
					}
					if (MainActivity.mCount > 0) {
						SaveRun.setisdaojishi(true);
						btnselecttime.setText("暂停");
						listjishi.setVisibility(View.VISIBLE);
						timepickerlin.setVisibility(View.GONE);
						if (null == task) {
							task = new TimerTask() {
								@Override
								public void run() {
									if (null == msg) {
										msg = new Message();
									} else {
										msg = Message.obtain();
									}
									msg.what = 100;
									handler.sendMessage(msg);
								}
							};
						}
						timer = new Timer(true);
						timer.schedule(task, 100, 100);
						alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
								+ MainActivity.mCount * 100, pendingIntent);
					}
				} else {
					try {
						SaveRun.setisdaojishi(false);
						btnselecttime.setText("继续");
						task.cancel();
						task = null;
						timer.cancel();
						timer.purge();
						timer = null;
						handler.removeMessages(msg.what);
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
	public void onDestroy() {
		if (alarmManager != null && pendingIntent != null) {
			alarmManager.cancel(pendingIntent);
		}
		if (task != null) {
			task.cancel();
			task = null;
		}
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
		super.onDestroy();
	}
}
