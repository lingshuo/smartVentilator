package cn.lisa.smartventilator.activity;

import cn.lisa.smartventilator.receiver.LockReceiver;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;

import android.os.Bundle;

import android.view.Window;
import android.view.WindowManager;

public class LockActivity extends Activity {

	DevicePolicyManager policyManager;
	ComponentName componentName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		policyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

		componentName = new ComponentName(this, LockReceiver.class);

		if (!policyManager.isAdminActive(componentName)) {
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
					componentName);
			startActivity(intent);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		systemLock();
	}

	/**
	 * 
	 */
	private void systemLock() {
		if (this.policyManager.isAdminActive(this.componentName)) {
			Window localWindow = getWindow();
			WindowManager.LayoutParams localLayoutParams = localWindow
					.getAttributes();
			localLayoutParams.screenBrightness = 0.05F;
			localWindow.setAttributes(localLayoutParams);
			this.policyManager.lockNow();
		}
		finish();
	}
	// /**
	// *
	// */
	// private void UninstallActivity(){
	// this.policyManager.removeActiveAdmin(this.componentName);
	// startActivity(new Intent("android.intent.action.DELETE",
	// Uri.parse("package:cn.lisa.smartventilator")));
	// finish();
	// }
}
