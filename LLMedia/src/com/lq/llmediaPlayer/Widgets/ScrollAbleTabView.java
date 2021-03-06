package com.lq.llmediaPlayer.Widgets;

import java.lang.reflect.Array;
import java.util.ArrayList;

import com.lq.llmediaPlayer.Interface.TabAdapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * 
 * @author lq
 * 
 */
public class ScrollAbleTabView extends HorizontalScrollView implements
		OnPageChangeListener {

	private LinearLayout mContainer;
	private TabAdapter mAdapter = null;
	private ViewPager mPager;
	private ArrayList<View> mTabs = new ArrayList<View>();

	public ScrollAbleTabView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		this.setHorizontalScrollBarEnabled(false);
		mContainer = new LinearLayout(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		mContainer.setLayoutParams(params);
		mContainer.setOrientation(LinearLayout.HORIZONTAL);

		this.addView(mContainer);
	}

	public ScrollAbleTabView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScrollAbleTabView(Context context) {
		this(context, null);
	}

	public void setAdapter(TabAdapter adapter) {
		this.mAdapter = adapter;
		if (mPager != null && mAdapter != null) {
			initTabs();
		}
	}

	private void initTabs() {
		mContainer.removeAllViews();
		mTabs.clear();
		if (mAdapter == null)
			return;
		
		for (int i = 0; i < mPager.getAdapter().getCount(); i++) {
			final int index = i;
			View tab = mAdapter.getView(i);
			mContainer.addView(tab);
			tab.setFocusable(true);
			mTabs.add(tab);
			
			tab.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mPager.getCurrentItem() == index){
						selectTab(index);
					}else{
						mPager.setCurrentItem(index,true);
					}
				}
			});
		}
		selectTab(mPager.getCurrentItem());
	}

	protected void selectTab(int position) {
		for (int i = 0,pos = 0; i < mContainer.getChildCount(); i++, pos++) {
			View tab = mContainer.getChildAt(i);
			tab.setSelected(pos == position);
		}
		View selectedTab = mContainer.getChildAt(position);
		final int w = selectedTab.getMeasuredWidth();
        final int l = selectedTab.getLeft();

        final int x = l - this.getWidth() / 2 + w / 2;
		smoothScrollTo(x, this.getScrollY());
		
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if(changed && mPager != null){
			selectTab(mPager.getCurrentItem());
		}
	}
	
	public void setViewPager(ViewPager pager) {
		this.mPager = pager;
		mPager.setOnPageChangeListener(this);

		if (mPager != null && mAdapter != null) {
			initTabs();
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		selectTab(position);

	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

}
