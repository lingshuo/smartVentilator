package cn.lisa.smartventilator.view.view;

import java.util.ArrayList;
import java.util.List;

import cn.lisa.smartventilator.controller.adapter.SvFragmentPagerAdapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BottomTabView extends LinearLayout {
	/** The context. */
	private Context context;

	/** tab�����Բ���. */
	private LinearLayout mTabLayout = null;

	/** The m view pager. */
	private ViewPager mViewPager;

	/** The m listener. */
	@SuppressWarnings("unused")
	private ViewPager.OnPageChangeListener mListener;

	/** tab���б�. */
	private ArrayList<TextView> tabItemList = null;

	/** ���ݵ�View. */
	private ArrayList<Fragment> pagerItemList = null;

	/** tab������. */
	private List<String> tabItemTextList = null;

	/** tab��ͼ��. */
	private List<Drawable> tabItemDrawableList = null;

	/** ��ǰѡ�б��. */
	private int mSelectedTabIndex = 0;

	/** ���������������. */
	private SvFragmentPagerAdapter mFragmentPagerAdapter = null;

	/** tab�ı���. */
	private int tabBackgroundResource = -1;

	/** tab�����ִ�С. */
	private int tabTextSize = 15;

	/** tab��������ɫ. */
	private int tabTextColor = Color.BLACK;

	/** tab��ѡ��������ɫ. */
	private int tabSelectColor = Color.WHITE;

	/** The m tab click listener. */
	private OnClickListener mTabClickListener = new OnClickListener() {
		public void onClick(View view) {
			TabItemView tabView = (TabItemView) view;
			setCurrentItem(tabView.getIndex());
		}
	};

	/**
	 * Instantiates a new ab bottom tab view.
	 *
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 */
	public BottomTabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		this.setOrientation(LinearLayout.VERTICAL);
		// this.setBackgroundColor(Color.rgb(255, 255, 255));

		mTabLayout = new LinearLayout(context);
		mTabLayout.setOrientation(LinearLayout.HORIZONTAL);
		mTabLayout.setGravity(Gravity.CENTER);
		// ���ݵ�View������
		mViewPager = new ViewPager(context);
		// �ֶ�������ViewPager,�������setId()��������һ��id
		mViewPager.setId(1985);
		pagerItemList = new ArrayList<Fragment>();
		this.addView(mViewPager, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
		addView(mTabLayout, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		// ����Tab��
		tabItemList = new ArrayList<TextView>();
		tabItemTextList = new ArrayList<String>();
		tabItemDrawableList = new ArrayList<Drawable>();
		// Ҫ�������FragmentActivity��ʵ��
		if (!(this.context instanceof FragmentActivity)) {

		}

		FragmentManager mFragmentManager = ((FragmentActivity) this.context).getFragmentManager();
		mFragmentPagerAdapter = new SvFragmentPagerAdapter(mFragmentManager, pagerItemList);
		mViewPager.setAdapter(mFragmentPagerAdapter);
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		mViewPager.setOffscreenPageLimit(3);

	}

	/**
	 * The listener interface for receiving myOnPageChange events. The class
	 * that is interested in processing a myOnPageChange event implements this
	 * interface, and the object created with that class is registered with a
	 * component using the component's
	 * <code>addMyOnPageChangeListener<code> method. When
	 * the myOnPageChange event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see MyOnPageChangeEvent
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.support.v4.view.ViewPager.OnPageChangeListener#
		 * onPageScrollStateChanged(int)
		 */
		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrolled
		 * (int, float, int)
		 */
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected
		 * (int)
		 */
		@Override
		public void onPageSelected(int arg0) {
			setCurrentItem(arg0);
		}

	}

	/**
	 * ������������ʾ��һ��.
	 *
	 * @param index
	 *            the new current item
	 */
	@SuppressWarnings("deprecation")
	public void setCurrentItem(int index) {
		if (mViewPager == null) {
			throw new IllegalStateException("ViewPager has not been bound.");
		}
		mSelectedTabIndex = index;
		final int tabCount = mTabLayout.getChildCount();
		for (int i = 0; i < tabCount; i++) {
			final TabItemView child = (TabItemView) mTabLayout.getChildAt(i);
			final boolean isSelected = (i == index);
			child.setSelected(isSelected);
			if (isSelected) {
				child.setTabTextColor(tabSelectColor);
				if (tabBackgroundResource != -1) {
					child.setTabBackgroundResource(tabBackgroundResource);
				}
				if (tabItemDrawableList.size() >= tabCount * 2) {
					child.setTabCompoundDrawables(null, tabItemDrawableList.get(index * 2 + 1), null, null);
				} else if (tabItemDrawableList.size() >= tabCount) {
					child.setTabCompoundDrawables(null, tabItemDrawableList.get(index), null, null);
				}
				mViewPager.setCurrentItem(index);
			} else {
				if (tabBackgroundResource != -1) {
					child.setBackgroundDrawable(null);
				}
				if (tabItemDrawableList.size() >= tabCount * 2) {
					child.setTabCompoundDrawables(null, tabItemDrawableList.get(i * 2), null, null);
				}
				child.setTabTextColor(tabTextColor);
			}
		}
	}

	/**
	 * ����������һ���ⲿ�ļ�����.
	 *
	 * @param listener
	 *            the new on page change listener
	 */
	public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
		mListener = listener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.LinearLayout#onMeasure(int, int)
	 */
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * ���������õ���tab�ı���ѡ����.
	 *
	 * @param resid
	 *            the new tab background resource
	 */
	public void setTabBackgroundResource(int resid) {
		tabBackgroundResource = resid;
	}

	/**
	 * ����������Tab�ı���.
	 *
	 * @param resid
	 *            the new tab layout background resource
	 */
	public void setTabLayoutBackgroundResource(int resid) {
		this.mTabLayout.setBackgroundResource(resid);
	}

	public void setTabBackgroundColor(int color) {
		mTabLayout.setBackgroundColor(color);

	}

	/**
	 * Gets the tab text size.
	 *
	 * @return the tab text size
	 */
	public int getTabTextSize() {
		return tabTextSize;
	}

	/**
	 * Sets the tab text size.
	 *
	 * @param tabTextSize
	 *            the new tab text size
	 */
	public void setTabTextSize(int tabTextSize) {
		this.tabTextSize = tabTextSize;
	}

	/**
	 * ����������tab���ֵ���ɫ.
	 *
	 * @param tabColor
	 *            the new tab text color
	 */
	public void setTabTextColor(int tabColor) {
		this.tabTextColor = tabColor;
	}

	/**
	 * ����������ѡ�е���ɫ.
	 *
	 * @param tabColor
	 *            the new tab select color
	 */
	public void setTabSelectColor(int tabColor) {
		this.tabSelectColor = tabColor;
	}

	/**
	 * ����������һ��Tab.
	 *
	 * @param text
	 *            the text
	 * @param index
	 *            the index
	 */
	private void addTab(String text, int index) {
		addTab(text, index, null);
	}

	/**
	 * ����������һ��Tab.
	 *
	 * @param text
	 *            the text
	 * @param index
	 *            the index
	 * @param top
	 *            the top
	 */
	private void addTab(String text, int index, Drawable top) {

		TabItemView tabView = new TabItemView(this.context);

		if (top != null) {
			tabView.setTabCompoundDrawables(null, top, null, null);
		}
		tabView.setTabTextColor(tabTextColor);
		tabView.setTabTextSize(tabTextSize);

		tabView.init(index, text);
		tabItemList.add(tabView.getTextView());
		tabView.setOnClickListener(mTabClickListener);
		mTabLayout.addView(tabView, new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
	}

	/**
	 * ������tab�б仯ˢ��.
	 */
	public void notifyTabDataSetChanged() {
		mTabLayout.removeAllViews();
		tabItemList.clear();
		final int count = mFragmentPagerAdapter.getCount();
		for (int i = 0; i < count; i++) {
			if (tabItemDrawableList.size() >= count * 2) {
				addTab(tabItemTextList.get(i), i, tabItemDrawableList.get(i * 2));
			} else if (tabItemDrawableList.size() >= count) {
				addTab(tabItemTextList.get(i), i, tabItemDrawableList.get(i));
			} else {
				addTab(tabItemTextList.get(i), i);
			}
		}
		if (mSelectedTabIndex > count) {
			mSelectedTabIndex = count - 1;
		}
		setCurrentItem(mSelectedTabIndex);
		requestLayout();
	}

	/**
	 * ����������һ��������tab.
	 *
	 * @param tabTexts
	 *            the tab texts
	 * @param fragments
	 *            the fragments
	 */
	public void addItemViews(List<String> tabTexts, List<Fragment> fragments) {

		tabItemTextList.addAll(tabTexts);
		pagerItemList.addAll(fragments);

		mFragmentPagerAdapter.notifyDataSetChanged();
		notifyTabDataSetChanged();
	}

	/**
	 * ����������һ��������tab��������ͼƬ.
	 *
	 * @param tabTexts
	 *            the tab texts
	 * @param fragments
	 *            the fragments
	 * @param drawables
	 *            the drawables
	 */
	public void addItemViews(List<String> tabTexts, List<Fragment> fragments, List<Drawable> drawables) {

		tabItemTextList.addAll(tabTexts);
		pagerItemList.addAll(fragments);
		tabItemDrawableList.addAll(drawables);
		mFragmentPagerAdapter.notifyDataSetChanged();
		notifyTabDataSetChanged();
	}

	/**
	 * ����������һ��������tab.
	 *
	 * @param tabText
	 *            the tab text
	 * @param fragment
	 *            the fragment
	 */
	public void addItemView(String tabText, Fragment fragment) {
		tabItemTextList.add(tabText);
		pagerItemList.add(fragment);
		mFragmentPagerAdapter.notifyDataSetChanged();
		notifyTabDataSetChanged();
	}

	/**
	 * ����������һ��������tab.
	 *
	 * @param tabText
	 *            the tab text
	 * @param fragment
	 *            the fragment
	 * @param drawableNormal
	 *            the drawable normal
	 * @param drawablePressed
	 *            the drawable pressed
	 */
	public void addItemView(String tabText, Fragment fragment, Drawable drawableNormal, Drawable drawablePressed) {
		tabItemTextList.add(tabText);
		pagerItemList.add(fragment);
		tabItemDrawableList.add(drawableNormal);
		tabItemDrawableList.add(drawablePressed);
		mFragmentPagerAdapter.notifyDataSetChanged();
		notifyTabDataSetChanged();
	}

	/**
	 * ������ɾ��ĳһ��.
	 *
	 * @param index
	 *            the index
	 */
	public void removeItemView(int index) {

		mTabLayout.removeViewAt(index);
		pagerItemList.remove(index);
		tabItemList.remove(index);
		tabItemDrawableList.remove(index);
		mFragmentPagerAdapter.notifyDataSetChanged();
		notifyTabDataSetChanged();
	}

	/**
	 * ������ɾ������.
	 */
	public void removeAllItemViews() {
		mTabLayout.removeAllViews();
		pagerItemList.clear();
		tabItemList.clear();
		tabItemDrawableList.clear();
		mFragmentPagerAdapter.notifyDataSetChanged();
		notifyTabDataSetChanged();
	}

	/**
	 * ��������ȡ���View��ViewPager.
	 *
	 * @return the view pager
	 */
	public ViewPager getViewPager() {
		return mViewPager;
	}

	/**
	 * ����������ÿ��tab�ı߾�.
	 *
	 * @param left
	 *            the left
	 * @param top
	 *            the top
	 * @param right
	 *            the right
	 * @param bottom
	 *            the bottom
	 */
	public void setTabPadding(int left, int top, int right, int bottom) {
		for (int i = 0; i < tabItemList.size(); i++) {
			TextView tv = tabItemList.get(i);
			tv.setPadding(left, top, right, bottom);
		}
	}

}
