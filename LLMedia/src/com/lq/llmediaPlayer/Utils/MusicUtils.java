package com.lq.llmediaPlayer.Utils;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import com.lq.llmediaPlayer.Service.MediaService;
import com.lq.llmediaPlayer.Service.ServiceBinder;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
/**
 * Various methods used to help with specific music statements
 * @author lq
 */
public class MusicUtils {
	
	// Used to make number of albums/songs/time strings
    private final static StringBuilder sFormatBuilder = new StringBuilder();

    private final static Formatter sFormatter = new Formatter(sFormatBuilder, Locale.getDefault());

    public static MediaService mService = null;

    private static HashMap<Context, ServiceBinder> sConnectionMap = new HashMap<Context, ServiceBinder>();

    private final static long[] sEmptyList = new long[0];

    private static final Object[] sTimeArgs = new Object[5];

    private static ContentValues[] sContentValuesCache = null;
	 /**
     * @param context
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    public static Cursor query(Context context, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        return query(context, uri, projection, selection, selectionArgs, sortOrder, 0);
    }
	
	/**
     * @param context
     * @param name
     * @param def
     * @return number of weeks used to create the Recent tab
     */
    public static int getIntPref(Context context, String name, int def) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(),
                Context.MODE_PRIVATE);
        return prefs.getInt(name, def);
    }
    /**
     * @param context
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @param limit
     * @return
     */
    public static Cursor query(Context context, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder, int limit) {
        try {
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return null;
            }
            if (limit > 0) {
                uri = uri.buildUpon().appendQueryParameter("limit", "" + limit).build();
            }
            return resolver.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (UnsupportedOperationException ex) {
            return null;
        }
    }
    
    /**
     * 
     * @param position
     */
	public static void setQueuePosition(int index) {
		if (mService == null)
            return;
        try {
            mService.setQueuePosition(index);
        } catch (RemoteException e) {
        }
	}
	/**
	 * @param activity
	 * @param mCursor
	 * @param position
	 */
	public static void playAll(Context context, Cursor cursor, int position) {
		playAll(context, cursor, position, false);
	}
	
	/**
     * @param context
     * @param cursor
     * @param position
     * @param force_shuffle
     */
    private static void playAll(Context context, Cursor cursor, int position, boolean force_shuffle) {

        long[] list = getSongListForCursor(cursor);
        playAll(context, list, position, force_shuffle);
    }

	private static long[] getSongListForCursor(Cursor cursor) {
		return null;
	}

	private static void playAll(Context context, long[] list, int position,
			boolean force_shuffle) {
		// TODO Auto-generated method stub
		
	}
}
