package cn.lisa.smartventilator.activity;

import io.vov.vitamio.LibsChecker;

import java.util.ArrayList;
import java.util.List;

import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.fragment.*;
import cn.lisa.smartventilator.receiver.LockReceiver;
import cn.lisa.smartventilator.service.FloatWindowService;
import cn.lisa.smartventilator.service.MonitorService;
import cn.lisa.smartventilator.view.BottomTabView;
import android.app.Fragment;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

public class MainActivity extends FragmentActivity {

	private BottomTabView mBottomTabView;
	private List<Drawable> tabDrawables = null;
	public DevicePolicyManager policyManager;
	public ComponentName componentName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!LibsChecker.checkVitamioLibs(this))
			return;
//		CustomTitleBar ct = new CustomTitleBar();
//		ct.getTitleBar(this, "");
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
		tabTexts.add("监控");
		tabTexts.add("媒体");
		tabTexts.add("设置");
		tabTexts.add("联系我们");

		// 设置样式
		mBottomTabView.setTabTextColor(Color.rgb(100,100,100));
		mBottomTabView.setTabTextSize(18);
		// 选中后的字体的颜色
		mBottomTabView.setTabSelectColor(Color.rgb(0, 85, 166));
		// mBottomTabView.setTabBackgroundResource(R.drawable.tab_bg2);
		mBottomTabView.setTabBackgroundColor(Color.WHITE);
		// mBottomTabView.setTabLayoutBackgroundResource(R.drawable.tablayout_bg2);

		// 注意图片的顺序
		tabDrawables = new ArrayList<Drawable>();
		tabDrawables.add(this.getResources().getDrawable(
				R.drawable.monitor_normal));
		tabDrawables.add(this.getResources().getDrawable(
				R.drawable.monitor_pressed));
		tabDrawables.add(this.getResources()
				.getDrawable(R.drawable.media_normal));
		tabDrawables.add(this.getResources().getDrawable(
				R.drawable.media_pressed));
		tabDrawables.add(this.getResources().getDrawable(
				R.drawable.setting_normal));
		tabDrawables.add(this.getResources().getDrawable(
				R.drawable.setting_pressed));
		tabDrawables.add(this.getResources()
				.getDrawable(R.drawable.contact_normal));
		tabDrawables.add(this.getResources().getDrawable(
				R.drawable.contact_pressed));

		// 演示增加一组
		mBottomTabView.addItemViews(tabTexts, mFragments, tabDrawables);

		mBottomTabView.setTabPadding(2, 2, 2, 2);
		
		//启动监控服务
		Intent intent1 = new Intent();
		intent1.setClass(this,MonitorService.class);
		startService(intent1);
		
		//启动桌面悬浮球
		Intent intent2=new Intent();
		intent2.setClass(this, FloatWindowService.class);
		startService(intent2);
		
		//启动设备管理器
		policyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

		componentName = new ComponentName(this,LockReceiver.class);

		if (!policyManager.isAdminActive(componentName)) {
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
			startActivity(intent);
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
	protected void onDestroy() {
		Intent intent = new Intent();  
        intent.setClass(this, MonitorService.class);
        stopService(intent);  
        super.onDestroy();
	}
}
