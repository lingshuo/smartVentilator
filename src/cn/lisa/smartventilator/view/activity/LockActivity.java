package cn.lisa.smartventilator.view.activity;


import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.controller.manager.MyWindowManager;
import cn.lisa.smartventilator.controller.service.FloatWindowService;
import cn.lisa.smartventilator.utility.system.BrightnessUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class LockActivity extends Activity implements OnClickListener{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View decorView = getWindow().getDecorView();
		// Hide the status bar.
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		setContentView(R.layout.activity_lock);

		RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.lock_layout);
		mLayout.setOnClickListener(this);

		if (BrightnessUtil.isAutoBrightness(getContentResolver()))
			BrightnessUtil.stopAutoBrightness(this);

		BrightnessUtil.setBrightness(this, 0);

	}

	@Override
	protected void onResume() {
		if(MyWindowManager.isWindowShowing())
			MyWindowManager.removeSmallWindow(getApplicationContext());
		Intent intent1 = new Intent();
		intent1.setClass(this, FloatWindowService.class);
		stopService(intent1);
		Log.i("lock", "onResume:remove");
		super.onResume();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
	}
	
	@Override
	protected void onPause() {
		// 启动桌面悬浮球
		Intent intent2 = new Intent();
		intent2.setClass(this, FloatWindowService.class);
		startService(intent2);
		Log.i("lock", "onPause:show");
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lock_layout:
			BrightnessUtil.startAutoBrightness(this);
			// 启动桌面悬浮球
			Intent intent2 = new Intent();
			intent2.setClass(this, FloatWindowService.class);
			startService(intent2);
			Log.i("lock", "click:show");
			finish();
			break;
		default:
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode>=0)
			 return true;
		return super.onKeyDown(keyCode, event);
	}
	
}
