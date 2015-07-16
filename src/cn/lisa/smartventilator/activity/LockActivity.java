package cn.lisa.smartventilator.activity;

import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.service.FloatWindowService;
import cn.lisa.smartventilator.util.BrightnessUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class LockActivity extends Activity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View decorView = getWindow().getDecorView();  
		// Hide the status bar.   
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION  
	              | View.SYSTEM_UI_FLAG_FULLSCREEN;  
		decorView.setSystemUiVisibility(uiOptions);
		setContentView(R.layout.activity_lock);
		
		RelativeLayout mLayout=(RelativeLayout)findViewById(R.id.lock_layout);
		mLayout.setOnClickListener(this);
		
		
		if(BrightnessUtil.isAutoBrightness(getContentResolver()))
			BrightnessUtil.stopAutoBrightness(this);
		
		BrightnessUtil.setBrightness(this, 0);		
		
	}

	@Override
	protected void onResume() {
		super.onResume();
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lock_layout:
			BrightnessUtil.startAutoBrightness(this);
			//启动桌面悬浮球
			Intent intent2=new Intent();
			intent2.setClass(this, FloatWindowService.class);
			startService(intent2);
			finish();
			break;
		default:
			break;
		}
	}
}
