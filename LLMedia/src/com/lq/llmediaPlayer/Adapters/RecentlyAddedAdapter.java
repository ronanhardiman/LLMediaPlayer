package com.lq.llmediaPlayer.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

public class RecentlyAddedAdapter extends SimpleCursorAdapter{

	public RecentlyAddedAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
	}

}
