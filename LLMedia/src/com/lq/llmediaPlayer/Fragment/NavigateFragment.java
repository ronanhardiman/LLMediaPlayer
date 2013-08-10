package com.lq.llmediaPlayer.Fragment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.lq.llmediaPlayer.R;
import com.lq.llmediaPlayer.Activity.NavigateActivity;
import com.lq.llmediaPlayer.Adapters.PagerAdapter;
import com.lq.llmediaPlayer.Adapters.ScrollingTabsAdapter;
import com.lq.llmediaPlayer.Config.Constants;
import com.lq.llmediaPlayer.Widgets.ScrollAbleTabView;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class NavigateFragment extends Fragment{
	private NavigateActivity navigateActivity;
	private ViewPager mViewPager;
	private ScrollAbleTabView mScrollingTabs;
	public NavigateFragment() {
	}
	
	@Override
	public void onAttach(Activity activity) {
		this.navigateActivity = (NavigateActivity) activity;
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_main, null);
		mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
		mScrollingTabs = (ScrollAbleTabView)view.findViewById(R.id.scrollingTabs);
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		PagerAdapter mPagerAdapter = new PagerAdapter(getFragmentManager());
		Bundle bundle = new Bundle();
		bundle.putString(Constants.MIME_TYPE, Audio.Playlists.CONTENT_TYPE);
		bundle.putLong(BaseColumns._ID, Constants.PLAYLIST_RECENTLY_ADDED);
	
		Set<String> defaults = new HashSet<String>(Arrays.asList(getResources().getStringArray(R.array.tab_titles)));
		
		if(defaults.contains(getResources().getString(R.string.tab_recent))){
			mPagerAdapter.addFragment(new RecentlyAddedFragment(bundle));
			mPagerAdapter.addFragment(new RecentlyAddedFragment(bundle));
			mPagerAdapter.addFragment(new RecentlyAddedFragment(bundle));
			mPagerAdapter.addFragment(new RecentlyAddedFragment(bundle));
			mPagerAdapter.addFragment(new RecentlyAddedFragment(bundle));
		}
		mViewPager.setAdapter(mPagerAdapter);
		initScrollAbleTabs(mViewPager);
	}
	private void initScrollAbleTabs(ViewPager mViewPager) {
		
		ScrollingTabsAdapter scrollingTabsAdapter = new ScrollingTabsAdapter(navigateActivity);
		mScrollingTabs.setAdapter(scrollingTabsAdapter);
		mScrollingTabs.setViewPager(mViewPager);
		
	}
}
