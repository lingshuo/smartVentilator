package cn.lisa.smartventilator.controller.service;

import java.util.Timer;
import java.util.TimerTask;

import cn.lisa.smartventilator.controller.manager.MyWindowManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class FloatWindowService extends Service {

	/**
	 * 用于在线程中创建或移除悬浮窗。
	 */
	private Handler handler = new Handler();

	/**
	 * 定时器，定时进行检测当前应该创建还是移除悬浮窗。
	 */
	private Timer timer;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 开启定时器，每隔0.5秒刷新一次
		if (timer == null) {
			timer = new Timer();
			timer.scheduleAtFixedRate(new RefreshTask(), 0, 500);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (MyWindowManager.isWindowShowing()) {
			MyWindowManager.removeBigWindow(getApplicationContext());
			MyWindowManager.removeSmallWindow(getApplicationContext());
		}
		// Service被终止的同时也停止定时器继续运行
		timer.cancel();
		timer = null;
	}

	class RefreshTask extends TimerTask {

		@Override
		public void run() {
			// 当前界面是桌面，且没有悬浮窗显示，则创建悬浮窗。
			if (!MyWindowManager.isWindowShowing()) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						MyWindowManager
								.createSmallWindow(getApplicationContext());
					}
				});
			}
		}

	}

}
