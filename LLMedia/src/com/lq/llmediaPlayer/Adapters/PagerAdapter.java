package com.lq.llmediaPlayer.Adapters;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {
	private final ArrayList<Fragment> mFragments = new ArrayList<Fragment>();

	public PagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		return mFragments.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFragments.size();
	}

	@Override
	public long getItemId(int position) {
		return super.getItemId(position);
	}

}
