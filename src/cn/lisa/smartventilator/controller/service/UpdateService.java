package cn.lisa.smartventilator.controller.service;

import java.util.Timer;
import java.util.TimerTask;

import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.controller.manager.UpdateManager;
import cn.lisa.smartventilator.debug.Debug;
import cn.lisa.smartventilator.utility.system.DialogHelper;
import cn.lisa.smartventilator.view.activity.MainActivity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;

public class UpdateService extends Service {

	private UpdateManager updateMan;
	private ProgressDialog updateProgressDialog;
	private SharedPreferences setting;
	private Editor editor;
	private Timer timer;

	@Override
	public void onCreate() {
		updateMan = new UpdateManager(MainActivity.getMyActivityContext(), appUpdateCb);
		setting = getSharedPreferences("smartventilator.preferences", 0);
		editor = setting.edit();
		editor.putBoolean("hasupdate", false);
		// 提交设置
		editor.commit();
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 开启定时器，每隔0.5秒刷新一次
		if (timer == null) {
			timer = new Timer();
			timer.scheduleAtFixedRate(new UpdateTask(), 10 * 1000, 60 * 60 * 1000);
			timer.schedule(new UpdateTask(), 0);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	class UpdateTask extends TimerTask {

		@Override
		public void run() {
			Debug.info(Debug.DEBUG_UPDATE, "update", "", "update check");
			updateMan.checkUpdate();

		}

	}

	UpdateManager.UpdateCallback appUpdateCb = new UpdateManager.UpdateCallback() {

		public void downloadProgressChanged(int progress) {
			if (updateProgressDialog != null && updateProgressDialog.isShowing()) {
				updateProgressDialog.setProgress(progress);
			}

		}

		public void downloadCompleted(Boolean success, CharSequence errorMsg) {
			if (updateProgressDialog != null && updateProgressDialog.isShowing()) {
				updateProgressDialog.dismiss();
			}
			if (success) {
				updateMan.update();
			} else {
				DialogHelper.Confirm(MainActivity.getMyActivityContext(),
						R.string.dialog_error_title, R.string.dialog_downfailed_msg,
						R.string.dialog_downfailed_btndown, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								updateMan.downloadPackage();

							}
						}, R.string.dialog_downfailed_btnnext,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								timer.schedule(new UpdateTask(), 5 * 1000);

							}
						});
			}
		}

		public void downloadCanceled() {
			// TODO Auto-generated method stub

		}

		public void checkUpdateCompleted(Boolean hasUpdate, CharSequence updateInfo) {
			if(setting.getBoolean("hasupdate", false)!=hasUpdate){
				editor.putBoolean("hasupdate", hasUpdate);
				// 提交设置
				editor.commit();
			}
			if (hasUpdate) {				
				DialogHelper.Confirm(
						MainActivity.getMyActivityContext(),
						getText(R.string.dialog_update_title),
						getText(R.string.dialog_update_msg).toString() + updateInfo
								+ getText(R.string.dialog_update_msg2).toString(),
						getText(R.string.dialog_update_btnupdate),
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								updateProgressDialog = new ProgressDialog(MainActivity
										.getMyActivityContext());
								updateProgressDialog
										.setMessage(getText(R.string.dialog_downloading_msg));
								updateProgressDialog.setIndeterminate(false);
								updateProgressDialog
										.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
								updateProgressDialog.setMax(100);
								updateProgressDialog.setProgress(0);
								updateProgressDialog.show();

								updateMan.downloadPackage();
							}
						}, getText(R.string.dialog_update_btnnext),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								timer.schedule(new UpdateTask(), 5 * 1000);

							}
						});
			}
		}
	};

}
