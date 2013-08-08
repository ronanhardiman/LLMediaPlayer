package com.lq.llmediaPlayer.Fragment;


import com.lq.llmediaPlayer.Adapters.RecentlyAddedAdapter;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

public class RecentlyAddedFragment extends Fragment{
	
	// Adapter
    private RecentlyAddedAdapter mRecentlyAddedAdapter;

    // ListView
    private ListView mListView;

    // Cursor
    private Cursor mCursor;

    // Audio columns
    public static int mTitleIndex, mAlbumIndex, mAlbumIdIndex, mArtistIndex, mMediaIdIndex;

	
	public RecentlyAddedFragment() {
	}
	public RecentlyAddedFragment(Bundle bundle) {
		setArguments(bundle);
	}
}
