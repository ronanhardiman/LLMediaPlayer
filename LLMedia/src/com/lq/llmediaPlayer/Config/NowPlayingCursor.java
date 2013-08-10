package com.lq.llmediaPlayer.Config;

import java.util.Arrays;

import com.lq.llmediaPlayer.Service.MediaService;
import com.lq.llmediaPlayer.Utils.MusicUtils;

import android.content.Context;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;

public class NowPlayingCursor extends AbstractCursor{
	private final String[] mProjection;

    private Cursor mCurrentPlaylistCursor;

    private int mSize;

    private long[] mNowPlaying;

    private long[] mCursorIdxs;

    private final Context context;

    private final MediaService mService;
    
	public NowPlayingCursor(MediaService service, String[] projection, Context c) {
        mProjection = projection;
        mService = service;
        makeNowPlayingCursor();
        context = c;
    }
	
	private void makeNowPlayingCursor() {
		mCurrentPlaylistCursor = null;
		
		try {
			mNowPlaying = mService.getQueue();
		} catch (RemoteException e) {
			e.printStackTrace();
			mNowPlaying = new long[0];
		}
		mSize = mNowPlaying.length;
		if(mSize == 0){
			return;
		}
		StringBuilder where = new StringBuilder();
        where.append(BaseColumns._ID + " IN (");
        for (int i = 0; i < mSize; i++) {
            where.append(mNowPlaying[i]);
            if (i < mSize - 1) {
                where.append(",");
            }
        }
        where.append(")");

        mCurrentPlaylistCursor = MusicUtils.query(context, Audio.Media.EXTERNAL_CONTENT_URI,
                mProjection, where.toString(), null, BaseColumns._ID);

        if (mCurrentPlaylistCursor == null) {
            mSize = 0;
            return;
        }
        
        int size = mCurrentPlaylistCursor.getCount();
        mCursorIdxs = new long[size];
        mCurrentPlaylistCursor.moveToFirst();
        int colidx = mCurrentPlaylistCursor.getColumnIndexOrThrow(BaseColumns._ID);
        for (int i = 0; i < size; i++) {
            mCursorIdxs[i] = mCurrentPlaylistCursor.getLong(colidx);
            mCurrentPlaylistCursor.moveToNext();
        }
        mCurrentPlaylistCursor.moveToFirst();
        
        
        try {
			int removed = 0;
			for (int i = mNowPlaying.length - 1; i >= 0; i--) {
			    long trackid = mNowPlaying[i];
			    int crsridx = Arrays.binarySearch(mCursorIdxs, trackid);
			    if (crsridx < 0) {
			        removed += mService.removeTrack(trackid);
			    }
			}
			if (removed > 0) {
			    mNowPlaying = mService.getQueue();
			    mSize = mNowPlaying.length;
			    if (mSize == 0) {
			        mCursorIdxs = null;
			        return;
			    }
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			mNowPlaying = new long[0];
		}
	}

	@Override
	public String[] getColumnNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDouble(int column) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getFloat(int column) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInt(int column) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLong(int column) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short getShort(int column) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getString(int column) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isNull(int column) {
		// TODO Auto-generated method stub
		return false;
	}

}
