package cn.lisa.smartventilator.view.activity;

import cn.lisa.smartventilator.R;
import android.annotation.SuppressLint;
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

public class ClockActivity extends Activity implements SoundPool.OnLoadCompleteListener{
	private SoundPool sp;
	private int num;
	private boolean ring;

	protected Message msg;
	private static final int SOUND=100000;
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg){
    		switch( msg.what){
    		case SOUND:    			
    			sp.play(num, (float)1.0,(float)1.0, 16, -1, (float)1.0);
    			break;
    		}
    	}
	};
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clock);
		RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.clock_layout);
		SharedPreferences sp1 = getSharedPreferences("smartventilator.preferences", 0);
		ring = sp1.getBoolean("ring", true);
		sp = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
		sp.setOnLoadCompleteListener(this);
		
		num = sp.load(ClockActivity.this, R.raw.in_call_alarm, 1);

		
		mLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
    public void onDestroy()
    {   
    	sp.stop(num);
    	sp.release() ;
    	super.onDestroy();
    }
	@Override
	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
		if(ring)
			handler.sendEmptyMessage(SOUND);
	}
}
