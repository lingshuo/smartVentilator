package cn.lisa.smartventilator.controller.adapter;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

public class SvFragmentPagerAdapter extends FragmentPagerAdapter {
	private ArrayList<Fragment> mFragmentList = null;

	public SvFragmentPagerAdapter(FragmentManager mFragmentManager,
			ArrayList<Fragment> fragmentList) {
		super(mFragmentManager);
		mFragmentList = fragmentList;
	}

	@Override
	public int getCount() {
		return mFragmentList.size();
	}

	@Override
	public Fragment getItem(int position) {

		Fragment fragment = null;
		if (position < mFragmentList.size()) {
			fragment = mFragmentList.get(position);
		} else {
			fragment = mFragmentList.get(0);
		}
		return fragment;

	}
}
