package cn.lisa.smartventilator.controller.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver{
	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context arg0, Intent data) {
		Log.d("Alarm","the time is up,start the alarm...");
//		Toast.makeText(arg0, "闹钟时间到了！", Toast.LENGTH_SHORT).show();
	}
}