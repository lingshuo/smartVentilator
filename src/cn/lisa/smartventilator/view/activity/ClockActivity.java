package cn.lisa.smartventilator.view.activity;

import cn.lisa.smartventilator.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class ClockActivity extends Activity {
	private SoundPool sp;
	private int num;
	private boolean ring;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clock);
		RelativeLayout mLayout=(RelativeLayout) findViewById(R.id.clock_layout);
		sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		num = sp.load(ClockActivity.this, R.raw.in_call_alarm, 1);
		SharedPreferences sp1=getSharedPreferences("smartventilator.preferences", 0);
		ring=sp1.getBoolean("ring", true);
		if (ring) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					sp.play(num, 1, 1, 0, 1, 1);
				}
			}, 200);
		}
		mLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();				
			}
		});
	}
}
