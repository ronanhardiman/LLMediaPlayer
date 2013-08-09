package com.lq.llmediaPlayer.Widgets;

import android.content.Context;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * 
 * @author lq
 *
 */
public class ScrollAbleTabView extends HorizontalScrollView implements OnPageChangeListener{
	
	private LinearLayout mContainer;

	public ScrollAbleTabView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		mContainer = new LinearLayout(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		mContainer.setLayoutParams(params);
		mContainer.setOrientation(LinearLayout.HORIZONTAL);
		
		this.addView(mContainer);
	}

	public ScrollAbleTabView(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}

	public ScrollAbleTabView(Context context) {
		this(context,null);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		// TODO Auto-generated method stub
		
	}

}
