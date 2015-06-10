package cn.lisa.smartventilator.activity;

import io.vov.vitamio.LibsChecker;

import java.util.ArrayList;
import java.util.List;

import cn.lisa.smartventilator.R;
import cn.lisa.smartventilator.fragment.*;
import cn.lisa.smartventilator.view.BottomTabView;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
public class MainActivity extends FragmentActivity {

	private BottomTabView mBottomTabView;
	private List<Drawable> tabDrawables = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		mBottomTabView.setTabTextColor(Color.BLACK);
		// ѡ�к���������ɫ
		mBottomTabView.setTabSelectColor(Color.rgb(155, 190, 79));
//		mBottomTabView.setTabBackgroundResource(R.drawable.tab_bg2);
		mBottomTabView.setTabBackgroundColor(Color.WHITE);
//		mBottomTabView.setTabLayoutBackgroundResource(R.drawable.tablayout_bg2);

		// ע��ͼƬ��˳��
		tabDrawables = new ArrayList<Drawable>();
		tabDrawables.add(this.getResources().getDrawable(
				R.drawable.collect_normal));
		tabDrawables.add(this.getResources().getDrawable(
				R.drawable.collect_pressed));
		tabDrawables.add(this.getResources()
				.getDrawable(R.drawable.find_normal));
		tabDrawables.add(this.getResources().getDrawable(
				R.drawable.find_pressed));
		tabDrawables.add(this.getResources().getDrawable(
				R.drawable.group_normal));
		tabDrawables.add(this.getResources().getDrawable(
				R.drawable.group_pressed));
		tabDrawables.add(this.getResources()
				.getDrawable(R.drawable.mine_normal));
		tabDrawables.add(this.getResources().getDrawable(
				R.drawable.mine_pressed));

		// ��ʾ����һ��
		mBottomTabView.addItemViews(tabTexts, mFragments, tabDrawables);

		 mBottomTabView.setTabPadding(2,2, 2, 2);
 
	}

}
