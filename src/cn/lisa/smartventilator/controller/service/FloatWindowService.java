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
	 * �������߳��д������Ƴ���������
	 */
	private Handler handler = new Handler();

	/**
	 * ��ʱ������ʱ���м�⵱ǰӦ�ô��������Ƴ���������
	 */
	private Timer timer;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// ������ʱ����ÿ��0.5��ˢ��һ��
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
		// Service����ֹ��ͬʱҲֹͣ��ʱ����������
		timer.cancel();
		timer = null;
	}

	class RefreshTask extends TimerTask {

		@Override
		public void run() {
			// ��ǰ���������棬��û����������ʾ���򴴽���������
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
