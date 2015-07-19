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

	/** tab的线性布局. */
	private LinearLayout mTabLayout = null;

	/** The m view pager. */
	private ViewPager mViewPager;

	/** The m listener. */
	@SuppressWarnings("unused")
	private ViewPager.OnPageChangeListener mListener;

	/** tab的列表. */
	private ArrayList<TextView> tabItemList = null;

	/** 内容的View. */
	private ArrayList<Fragment> pagerItemList = null;

	/** tab的文字. */
	private List<String> tabItemTextList = null;

	/** tab的图标. */
	private List<Drawable> tabItemDrawableList = null;

	/** 当前选中编号. */
	private int mSelectedTabIndex = 0;

	/** 内容区域的适配器. */
	private SvFragmentPagerAdapter mFragmentPagerAdapter = null;

	/** tab的背景. */
	private int tabBackgroundResource = -1;

	/** tab的文字大小. */
	private int tabTextSize = 15;

	/** tab的文字颜色. */
	private int tabTextColor = Color.BLACK;

	/** tab的选中文字颜色. */
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
		// 内容的View的适配
		mViewPager = new ViewPager(context);
		// 手动创建的ViewPager,必须调用setId()方法设置一个id
		mViewPager.setId(1985);
		pagerItemList = new ArrayList<Fragment>();
		this.addView(mViewPager, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
		addView(mTabLayout, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		// 定义Tab栏
		tabItemList = new ArrayList<TextView>();
		tabItemTextList = new ArrayList<String>();
		tabItemDrawableList = new ArrayList<Drawable>();
		// 要求必须是FragmentActivity的实例
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
	 * 描述：设置显示哪一个.
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
	 * 描述：设置一个外部的监听器.
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
	 * 描述：设置单个tab的背景选择器.
	 *
	 * @param resid
	 *            the new tab background resource
	 */
	public void setTabBackgroundResource(int resid) {
		tabBackgroundResource = resid;
	}

	/**
	 * 描述：设置Tab的背景.
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
	 * 描述：设置tab文字的颜色.
	 *
	 * @param tabColor
	 *            the new tab text color
	 */
	public void setTabTextColor(int tabColor) {
		this.tabTextColor = tabColor;
	}

	/**
	 * 描述：设置选中的颜色.
	 *
	 * @param tabColor
	 *            the new tab select color
	 */
	public void setTabSelectColor(int tabColor) {
		this.tabSelectColor = tabColor;
	}

	/**
	 * 描述：创造一个Tab.
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
	 * 描述：创造一个Tab.
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
	 * 描述：tab有变化刷新.
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
	 * 描述：增加一组内容与tab.
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
	 * 描述：增加一组内容与tab附带顶部图片.
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
	 * 描述：增加一个内容与tab.
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
	 * 描述：增加一个内容与tab.
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
	 * 描述：删除某一个.
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
	 * 描述：删除所有.
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
	 * 描述：获取这个View的ViewPager.
	 *
	 * @return the view pager
	 */
	public ViewPager getViewPager() {
		return mViewPager;
	}

	/**
	 * 描述：设置每个tab的边距.
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
