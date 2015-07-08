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
		tabTexts.add("���");
		tabTexts.add("ý��");
		tabTexts.add("����");
		tabTexts.add("��ϵ����");

		// ������ʽ
		mBottomTabView.setTabTextColor(Color.rgb(100,100,100));
		mBottomTabView.setTabTextSize(18);
		// ѡ�к���������ɫ
		mBottomTabView.setTabSelectColor(Color.rgb(0, 85, 166));
		// mBottomTabView.setTabBackgroundResource(R.drawable.tab_bg2);
		mBottomTabView.setTabBackgroundColor(Color.WHITE);
		// mBottomTabView.setTabLayoutBackgroundResource(R.drawable.tablayout_bg2);

		// ע��ͼƬ��˳��
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

		// ��ʾ����һ��
		mBottomTabView.addItemViews(tabTexts, mFragments, tabDrawables);

		mBottomTabView.setTabPadding(2, 2, 2, 2);
		
		//������ط���
		Intent intent1 = new Intent();
		intent1.setClass(this,MonitorService.class);
		startService(intent1);
		
		//��������������
		Intent intent2=new Intent();
		intent2.setClass(this, FloatWindowService.class);
		startService(intent2);
		
		//�����豸������
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
