package cn.lisa.smartventilator.view.activity;

import io.vov.vitamio.LibsChecker;

import java.util.ArrayList;
import java.util.List;

import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.controller.entity.MachineID;
import cn.lisa.smartventilator.controller.service.FloatWindowService;
import cn.lisa.smartventilator.controller.service.MonitorService;
import cn.lisa.smartventilator.view.fragment.*;
import cn.lisa.smartventilator.view.view.BottomTabView;
import android.app.Fragment;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends FragmentActivity {

	private BottomTabView mBottomTabView;
	private List<Drawable> tabDrawables = null;
	public DevicePolicyManager policyManager;
	public ComponentName componentName;
	public PowerManager.WakeLock mWakeLock;
	public static MachineID mID;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mID = new MachineID();
		mID.readId();
		if (mID.getMid().equals("die")) {
			// die
		}
		SharedPreferences sp = getSharedPreferences("smartventilator.preferences", 0);
		Editor editor = sp.edit();
		editor.putString("mID", mID.getMid());
		editor.putString("version", "1.0.1");
		// �ύ����
		editor.commit();

		if (!LibsChecker.checkVitamioLibs(this))
			return;

		setContentView(R.layout.activity_main);

		mBottomTabView = (BottomTabView) findViewById(R.id.mBottomTabView);

		mBottomTabView.getViewPager().setOffscreenPageLimit(5);

		MonitorFragment page1 = new MonitorFragment();
		MediaFragment page2 = new MediaFragment();
		SettingFragment page3 = new SettingFragment();
		ContactFragment page4 = new ContactFragment();

		List<Fragment> mFragments = new ArrayList<Fragment>();
		mFragments.add(page1);
		mFragments.add(page2);
		mFragments.add(page3);
		mFragments.add(page4);

		List<String> tabTexts = new ArrayList<String>();
		tabTexts.add("���");
		tabTexts.add("ý��");
		tabTexts.add("����");
		tabTexts.add("��ϵ����");

		// ������ʽ
		mBottomTabView.setTabTextColor(Color.rgb(100, 100, 100));
		mBottomTabView.setTabTextSize(18);
		// ѡ�к���������ɫ
		mBottomTabView.setTabSelectColor(Color.rgb(0, 85, 166));
		// mBottomTabView.setTabBackgroundResource(R.drawable.tab_bg2);
		mBottomTabView.setTabBackgroundColor(Color.WHITE);
		// mBottomTabView.setTabLayoutBackgroundResource(R.drawable.tablayout_bg2);

		// ע��ͼƬ��˳��
		tabDrawables = new ArrayList<Drawable>();
		tabDrawables.add(this.getResources().getDrawable(R.drawable.monitor_normal));
		tabDrawables.add(this.getResources().getDrawable(R.drawable.monitor_pressed));
		tabDrawables.add(this.getResources().getDrawable(R.drawable.media_normal));
		tabDrawables.add(this.getResources().getDrawable(R.drawable.media_pressed));
		tabDrawables.add(this.getResources().getDrawable(R.drawable.setting_normal));
		tabDrawables.add(this.getResources().getDrawable(R.drawable.setting_pressed));
		tabDrawables.add(this.getResources().getDrawable(R.drawable.contact_normal));
		tabDrawables.add(this.getResources().getDrawable(R.drawable.contact_pressed));

		// ��ʾ����һ��
		mBottomTabView.addItemViews(tabTexts, mFragments, tabDrawables);

		mBottomTabView.setTabPadding(2, 2, 2, 2);

		// ������ط���
		Intent intent1 = new Intent();
		intent1.setClass(this, MonitorService.class);
		startService(intent1);
		// ��������������
		Intent intent2 = new Intent();
		intent2.setClass(this, FloatWindowService.class);
		startService(intent2);
		// ���Ϳ�ݷ�ʽ
		createShortCut();
		// ������Ļ����
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
	}

	public void createShortCut() {
		SharedPreferences setting = getSharedPreferences("smartventilator.preferences", 0);
		boolean firstStart = setting.getBoolean("FIRST_START", true);
		if (firstStart) {
			// ������ݷ�ʽ��Intent
			Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
			// �������ظ�����
			shortcutintent.putExtra("duplicate", false);
			// ��Ҫ��ʵ������
			shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
			// ���ͼƬ
			Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(),
					R.drawable.ic_launcher);
			shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
			// ������ͼƬ�����еĳ��������
			shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
					getApplicationContext(), MainActivity.class));
			// ���͹㲥��OK
			sendBroadcast(shortcutintent);
			// ����һ�������ı�ʶ����Ϊfalse
			Editor editor = setting.edit();
			editor.putBoolean("FIRST_START", false);
			// �ύ����
			editor.commit();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		try {
			super.onConfigurationChanged(newConfig);
			if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				// land
			} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				// port
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		mWakeLock.acquire();
		super.onResume();
	}

	@Override
	protected void onPause() {
		mWakeLock.release();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		Intent intent = new Intent();
		intent.setClass(this, MonitorService.class);
		stopService(intent);
		super.onDestroy();
	}
}
