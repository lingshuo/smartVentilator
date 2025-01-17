package cn.lisa.smartventilator.controller.activity;

import io.vov.vitamio.LibsChecker;

import java.util.ArrayList;
import java.util.List;

import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.controller.entity.MachineID;
import cn.lisa.smartventilator.controller.service.FloatWindowService;
import cn.lisa.smartventilator.controller.service.MonitorService;
import cn.lisa.smartventilator.controller.service.UpdateService;
import cn.lisa.smartventilator.view.fragment.*;
import cn.lisa.smartventilator.view.view.BottomTabView;
import android.app.Fragment;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class MainActivity extends FragmentActivity {

	private BottomTabView mBottomTabView;
	private List<Drawable> tabDrawables = null;
	public DevicePolicyManager policyManager;
	public ComponentName componentName;
	public PowerManager.WakeLock mWakeLock;
	private String curVersion;
	private int curVersionCode;
	public static MachineID mID;
	private static Context context;
	public static int mCount=-1;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mID = new MachineID();
		mID.readId();
		if (mID.getMid().equals("die")) {
			// die
		}
		
		//get current version
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			curVersion = pInfo.versionName;
			curVersionCode = pInfo.versionCode;
		} catch (NameNotFoundException e) {
			Log.e("update", e.getMessage());
		}
		
		SharedPreferences sp = getSharedPreferences("smartventilator.preferences", 0);
		Editor editor = sp.edit();
		editor.putString("mID", mID.getMid());
		editor.putString("version", curVersion);
		editor.putInt("versionCode", curVersionCode);
		// 提交设置
		editor.commit();
		
		context=MainActivity.this;
		if (!LibsChecker.checkVitamioLibs(this))
			return;

		setContentView(R.layout.activity_main);

		mBottomTabView = (BottomTabView) findViewById(R.id.mBottomTabView);

		mBottomTabView.getViewPager().setOffscreenPageLimit(5);

		MonitorFragment page1 = new MonitorFragment();
		MediaFragment page2 = new MediaFragment();
		SettingFragment page3 = new SettingFragment();
		ContactFragment page4 = new ContactFragment();
		ClockFragment page5=new ClockFragment();
		List<Fragment> mFragments = new ArrayList<Fragment>();
		mFragments.add(page1);
		mFragments.add(page2);
		mFragments.add(page3);
		mFragments.add(page5);
		mFragments.add(page4);

		List<String> tabTexts = new ArrayList<String>();
		tabTexts.add("监控");
		tabTexts.add("媒体");
		tabTexts.add("设置");
		tabTexts.add("定时");
		tabTexts.add("联系我们");

		// 设置样式
		mBottomTabView.setTabTextColor(Color.rgb(100, 100, 100));
		mBottomTabView.setTabTextSize(18);
		// 选中后的字体的颜色
		mBottomTabView.setTabSelectColor(Color.rgb(0, 85, 166));
		// mBottomTabView.setTabBackgroundResource(R.drawable.tab_bg2);
		mBottomTabView.setTabBackgroundColor(Color.WHITE);
		// mBottomTabView.setTabLayoutBackgroundResource(R.drawable.tablayout_bg2);

		// 注意图片的顺序
		tabDrawables = new ArrayList<Drawable>();
		tabDrawables.add(this.getResources().getDrawable(R.drawable.monitor_normal));
		tabDrawables.add(this.getResources().getDrawable(R.drawable.monitor_pressed));
		tabDrawables.add(this.getResources().getDrawable(R.drawable.media_normal));
		tabDrawables.add(this.getResources().getDrawable(R.drawable.media_pressed));
		tabDrawables.add(this.getResources().getDrawable(R.drawable.setting_normal));
		tabDrawables.add(this.getResources().getDrawable(R.drawable.setting_pressed));
		tabDrawables.add(this.getResources().getDrawable(R.drawable.clock_normal));
		tabDrawables.add(this.getResources().getDrawable(R.drawable.clock_pressed));
		tabDrawables.add(this.getResources().getDrawable(R.drawable.contact_normal));
		tabDrawables.add(this.getResources().getDrawable(R.drawable.contact_pressed));

		// 演示增加一组
		mBottomTabView.addItemViews(tabTexts, mFragments, tabDrawables);

		mBottomTabView.setTabPadding(2, 2, 2, 2);

//		// 启动监控服务
//		Intent intent1 = new Intent();
//		intent1.setClass(this, MonitorService.class);
//		startService(intent1);
//		// 启动桌面悬浮球
//		Intent intent2 = new Intent();
//		intent2.setClass(this, FloatWindowService.class);
//		startService(intent2);
		
		// 发送快捷方式
		createShortCut();
		// 保持屏幕常亮
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");	
		//登录检测
		loginCheck();
	}

	public void createShortCut() {
		SharedPreferences setting = getSharedPreferences("smartventilator.preferences", 0);
		boolean firstStart = setting.getBoolean("FIRST_START", true);
		if (firstStart) {
			// 创建快捷方式的Intent
			Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
			// 不允许重复创建
			shortcutintent.putExtra("duplicate", false);
			// 需要现实的名称
			shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
			// 快捷图片
			Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(),
					R.drawable.ic_launcher);
			shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
			// 点击快捷图片，运行的程序主入口
			shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
					getApplicationContext(), MainActivity.class));
			// 发送广播。OK
			sendBroadcast(shortcutintent);
			// 将第一次启动的标识设置为false
			Editor editor = setting.edit();
			editor.putBoolean("FIRST_START", false);
			// 提交设置
			editor.commit();
		}
	}
	
	//登录检测
	private void loginCheck(){
		SharedPreferences sp = getSharedPreferences("smartventilator.preferences", 0);
		Intent intent=getIntent();
		if(!intent.getBooleanExtra("login", false)&&!sp.getBoolean("login", false)){//尚未登录
			intent.setClass(this, LoginActivity.class);
			startActivity(intent);
		}
					
		if(intent.getBooleanExtra("login", false)&&!sp.getBoolean("rememberUser", false)){//登陆时没有选择记住密码
			sp.edit().putBoolean("login", false).commit();
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
	
	public static Context getMyActivityContext(){
		return context;
	}

	@Override
	protected void onResume() {
		mWakeLock.acquire();
		// 启动监控服务
		Intent intent1 = new Intent();
		intent1.setClass(this, MonitorService.class);
		startService(intent1);
		// 启动桌面悬浮球
		Intent intent2 = new Intent();
		intent2.setClass(this, FloatWindowService.class);
		startService(intent2);
		// 检查更新
		Intent intent3=new Intent();
		intent3.setClass(this, UpdateService.class);
		startService(intent3);
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
	
	// 隐藏输入法
    // 获取点击事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isHideInput(view, ev)) {
                HideSoftInput(view.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    // 判定是否需要隐藏
    private boolean isHideInput(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (ev.getX() > left && ev.getX() < right && ev.getY() > top
                    && ev.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
    // 隐藏软键盘
    private void HideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
