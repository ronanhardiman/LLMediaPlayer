package com.lq.llmediaPlayer.Adapters;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.lq.llmediaPlayer.R;
import com.lq.llmediaPlayer.Config.Constants;
import com.lq.llmediaPlayer.Interface.TabAdapter;

public class ScrollingTabsAdapter implements TabAdapter {
	private Activity activity;
	public ScrollingTabsAdapter(Activity activity) {
		this.activity = activity;
	}

	@Override
	public View getView(int position) {
		Button tab = (Button) activity.getLayoutInflater().inflate(R.layout.tabs, null);
		
		//Get default values for tab visibility preferences
        final String[] mTitles = activity.getResources().getStringArray(R.array.tab_titles);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        Set<String> defaults = new HashSet<String>(Arrays.asList(mTitles));
        Set<String> tabs_set = sp.getStringSet(Constants.TABS_ENABLED,defaults);
        
        if(tabs_set.size()==0)
        	tabs_set = defaults;
        String[] tabs_new = new String[tabs_set.size()];
        int count = 0;
        for (int i = 0; i < mTitles.length; i++) {
			if(tabs_set.contains(mTitles[i])){
				tabs_new[count] = mTitles[i];
				count++;
			}
		}
        if(position < tabs_new.length){
        	tab.setText(tabs_new[position].toUpperCase());
        }
		return tab;
	}

}
