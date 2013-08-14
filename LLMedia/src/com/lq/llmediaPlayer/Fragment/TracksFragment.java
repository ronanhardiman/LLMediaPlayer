package com.lq.llmediaPlayer.Fragment;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.lq.llmediaPlayer.R;
import com.lq.llmediaPlayer.Adapters.TrackAdapter;
import com.lq.llmediaPlayer.Config.Constants;
import com.lq.llmediaPlayer.Config.NowPlayingCursor;
import com.lq.llmediaPlayer.Utils.MusicUtils;
import com.lq.llmediaPlayer.Utils.Utils;

public class TracksFragment extends RefreshableFragment implements LoaderCallbacks<Cursor> , OnItemClickListener{

	//adapter 
	private TrackAdapter mTrackAdapter;
	//listview
	private ListView mListView;
	//cursor
	private Cursor mCursor;
	
	//PlayList ID
	private long mPlayListId = -1;
	
	//Selected position
	private int mSelectedPosition;
	
	//Used to set ringtone
	private long mSelectedId;
	
	//Options
	private final int PALY_SELECTION = 6;
	
	//
	private final int USE_AS_RINGTONE = 7;
	
	//
	private final int ADD_T0_PLAYLIST = 8;
	private final int SEARCH = 9;
	private final int REMOVE = 10;
	private boolean mEditMode = false;
	//Audio columns
	public static int mTitleIndex,mAlbumIndex,mArtistIndex,mMediaIdIndex;
	
	public TracksFragment() {
	}
	//Bundle
	public TracksFragment(Bundle args){
		setArguments(args);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.listview, container, false);
		// Align the track list with the header, in other words,OCD.
        TextView mHeader = (TextView)root.findViewById(R.id.title);
        int eight = (int)getActivity().getResources().getDimension(
                R.dimen.list_separator_padding_left_right);
        mHeader.setPadding(eight, 0, 0, 0);
        
        // Set the header while in @TracksBrowser
        String header = getActivity().getResources().getString(R.string.track_header);
        int left = getActivity().getResources().getInteger(R.integer.listview_padding_left);
        int right = getActivity().getResources().getInteger(R.integer.listview_padding_right);

        Utils.listHeader(this, root, header);
		return root;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initEditMode();
		
		//
		mTrackAdapter = new TrackAdapter(getActivity(), R.layout.listview_items, null,
                new String[] {}, new int[] {}, 0);
		mListView.setOnCreateContextMenuListener(this);
		mListView.setOnItemClickListener(this);
		mListView.setAdapter(mTrackAdapter);
		
		getLoaderManager().initLoader(0, null, this);
	}
	
	/**
	 * Check if we're viewing the contents of a playlist
	 */
	private void initEditMode() {
		if(getArguments() != null){
			String mimeTpye = getArguments().getString(Constants.MIME_TYPE);
			if(Audio.Playlists.CONTENT_TYPE.equals(mimeTpye)){
				mPlayListId = getArguments().getLong(BaseColumns._ID);
				switch ((int)mPlayListId) {
				case (int)Constants.PLAYLIST_QUEUE:
					mEditMode = true;
					break;
				case (int)Constants.PLAYLIST_FAVORITES:
					mEditMode = true;
					break;
				default:
					if(mPlayListId > 0){
						mEditMode = true;
					}
					break;
				}
			}
		}
	}
	@Override
	public void refresh() {
		if(mListView != null){
			getLoaderManager().restartLoader(0, null, this);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parents, View V, int position, long id) {
		if(mCursor instanceof NowPlayingCursor){
			if(MusicUtils.mService != null){
				MusicUtils.setQueuePosition(position);
				return;
			}
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		
	}

}
