package cn.lisa.smartventilator.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent myintent = context.getPackageManager()
				.getLaunchIntentForPackage("cn.lisa.smartventilator");
		context.startActivity(myintent);
	}

}
