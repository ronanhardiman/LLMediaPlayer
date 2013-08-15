package com.lq.llmediaPlayer.Utils;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Audio.Playlists;
import android.provider.MediaStore.Audio.PlaylistsColumns;
import android.widget.Toast;

import com.lq.llmediaPlayer.R;
import com.lq.llmediaPlayer.Config.Constants;
import com.lq.llmediaPlayer.Service.LLMediaService;
import com.lq.llmediaPlayer.Service.MediaService;
import com.lq.llmediaPlayer.Service.ServiceBinder;
import com.lq.llmediaPlayer.Service.ServiceToken;

/**
 * Various methods used to help with specific music statements
 * 
 * @author lq
 */
public class MusicUtils {

	// Used to make number of albums/songs/time strings
	private final static StringBuilder sFormatBuilder = new StringBuilder();

	private final static Formatter sFormatter = new Formatter(sFormatBuilder,
			Locale.getDefault());

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
	public static Cursor query(Context context, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		return query(context, uri, projection, selection, selectionArgs,
				sortOrder, 0);
	}

	/**
	 * @param context
	 * @param name
	 * @param def
	 * @return number of weeks used to create the Recent tab
	 */
	public static int getIntPref(Context context, String name, int def) {
		SharedPreferences prefs = context.getSharedPreferences(
				context.getPackageName(), Context.MODE_PRIVATE);
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
	public static Cursor query(Context context, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder,
			int limit) {
		try {
			ContentResolver resolver = context.getContentResolver();
			if (resolver == null) {
				return null;
			}
			if (limit > 0) {
				uri = uri.buildUpon().appendQueryParameter("limit", "" + limit)
						.build();
			}
			return resolver.query(uri, projection, selection, selectionArgs,
					sortOrder);
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
	private static void playAll(Context context, Cursor cursor, int position,
			boolean force_shuffle) {

		long[] list = getSongListForCursor(cursor);
		playAll(context, list, position, force_shuffle);
	}

	/**
	 * @param cursor
	 * @return
	 */
	private static long[] getSongListForCursor(Cursor cursor) {
		if (cursor == null) {
			return sEmptyList;
		}
		int len = cursor.getCount();
		long[] list = new long[len];
		cursor.moveToNext();
		int colindex = -1;

		try {
			colindex = cursor
					.getColumnIndexOrThrow(Audio.Playlists.Members.AUDIO_ID);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			colindex = cursor.getColumnIndexOrThrow(BaseColumns._ID);
		}
		for (int i = 0; i < len; i++) {
			list[i] = cursor.getLong(colindex);
			cursor.moveToNext();
		}

		return list;
	}

	private static void playAll(Context context, long[] list, int position,
			boolean force_shuffle) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @param context
	 * @param callBack
	 */
	public static ServiceToken bindToService(Context context,
			ServiceConnection callBack) {
		Activity realActivity = ((Activity) context).getParent();
		if (realActivity == null) {
			realActivity = (Activity) context;
		}
		ContextWrapper cw = new ContextWrapper(realActivity);
		cw.startService(new Intent(cw, LLMediaService.class));
		ServiceBinder sb = new ServiceBinder(callBack);
		if (cw.bindService(new Intent().setClass(cw, LLMediaService.class), sb,
				0)) {
			sConnectionMap.put(cw, sb);
			return new ServiceToken(cw);
		}
		return null;
	}

	/**
	 * 
	 * @param mToken
	 */
	public static void unbindFromService(ServiceToken mToken) {
		if (mToken == null) {
			return;
		}
		ContextWrapper cw = mToken.contextWrapper;
		ServiceBinder sb = sConnectionMap.remove(cw);
		if (sb == null) {
			return;
		}
		cw.unbindService(sb);
		if (sConnectionMap.isEmpty()) {
			mService = null;
		}

	}

	/**
	 * 
	 * @param mContext
	 * @param numalbums
	 * @param numsongs
	 * @param unknown
	 * @return a string based on the number of albums for an artist or songs for
	 *         an album
	 */
	public static String makeAlbumsLabel(Context mContext, int numalbums,
			int numsongs, boolean isUnknown) {
		StringBuilder songs_albums = new StringBuilder();

		Resources r = mContext.getResources();
		if (isUnknown) {
			String f = r.getQuantityText(R.plurals.Nsongs, numsongs).toString();
			sFormatBuilder.setLength(0);// 清空 StringBuilder
			sFormatter.format(f, Integer.valueOf(numsongs));
			songs_albums.append(sFormatBuilder);
		} else {
			String f = r.getQuantityText(R.plurals.Nalbums, numalbums)
					.toString();
			sFormatBuilder.setLength(0);
			sFormatter.format(f, Integer.valueOf(numalbums));
			songs_albums.append(sFormatBuilder);
			songs_albums.append("\n");
		}
		return songs_albums.toString();
	}

	/**
	 * 
	 * @return current artist ID
	 */
	public static long getCurrentArtistId() {
		if (MusicUtils.mService != null) {
			try {
				return mService.getArtistId();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	/**
	 * @param context
	 * @param id
	 * @return
	 */
	public static long[] getSongListForAlbum(Context context, long id) {
		final String[] projection = new String[] { BaseColumns._ID };
		String selection = AudioColumns.ALBUM_ID + "=" + id + " AND "
				+ AudioColumns.IS_MUSIC + "=1";
		String sortOrder = AudioColumns.TRACK;
		Uri uri = Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor cursor = query(context, uri, projection, selection, null,
				sortOrder);
		if (cursor != null) {
			long[] list = getSongListForCursor(cursor);
			cursor.close();
			return list;
		}
		return sEmptyList;
	}

	public static void playAll(Activity activity, long[] list, int i) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param context
	 * @param id
	 * @return
	 */
	public static long[] getSongListForArtist(Context context, long id) {
		String[] projection = new String[] { BaseColumns._ID };
		String selection = AudioColumns.ARTIST_ID + "=" + id + " AND "
				+ AudioColumns.IS_MUSIC + "=1";
		String sortOrder = AudioColumns.ALBUM_KEY + "," + AudioColumns.TRACK;
		Uri uri = Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor cursor = query(context, uri, projection, selection, null,
				sortOrder);
		if (cursor != null) {
			long[] list = getSongListForCursor(cursor);
			cursor.close();
			return list;
		}
		return sEmptyList;
	}
	/*
	 * Create a search choose;
	 */
	public static void doSearch(Context mContext, Cursor mCursor,
			int index) {
		CharSequence title = null;
		Intent i = new Intent();
		i.setAction(MediaStore.INTENT_ACTION_MEDIA_SEARCH);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		String query = mCursor.getString(index);
		title = "";
		i.putExtra("", query);
		title = title + " " + query;
		title = "Search " + title;
		i.putExtra(SearchManager.QUERY, query);
		mContext.startActivity(Intent.createChooser(i, title));
	}

	public static long[] getQueue() {
		if(mService == null){
			return sEmptyList;
		}
		try {
			return mService.getQueue();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return sEmptyList;
	}

	public static long getFavoritesId(Context context) {
		long favorites_id = -1;
        String favorites_where = PlaylistsColumns.NAME + "='" + "Favorites" + "'";
        String[] favorites_cols = new String[] {
            BaseColumns._ID
        };
        Uri favorites_uri = Audio.Playlists.EXTERNAL_CONTENT_URI;
        Cursor cursor = query(context, favorites_uri, favorites_cols, favorites_where, null, null);
        if (cursor.getCount() <= 0) {
            favorites_id = createPlaylist(context, "Favorites");
        } else {
            cursor.moveToFirst();
            favorites_id = cursor.getLong(0);
            cursor.close();
        }
		return favorites_id;
	}

	private static long createPlaylist(Context context, String name) {
		if (name != null && name.length() > 0) {
            ContentResolver resolver = context.getContentResolver();
            String[] cols = new String[] {
                PlaylistsColumns.NAME
            };
            String whereclause = PlaylistsColumns.NAME + " = '" + name + "'";
            Cursor cur = resolver.query(Audio.Playlists.EXTERNAL_CONTENT_URI, cols, whereclause,
                    null, null);
            if (cur.getCount() <= 0) {
                ContentValues values = new ContentValues(1);
                values.put(PlaylistsColumns.NAME, name);
                Uri uri = resolver.insert(Audio.Playlists.EXTERNAL_CONTENT_URI, values);
                return Long.parseLong(uri.getLastPathSegment());
            }
            return -1;
        }
		return -1;
	}

	public static void setRingtone(Context context, long id) {
		ContentResolver resolver = context.getContentResolver();
        // Set the flag in the database to mark this as a ringtone
        Uri ringUri = ContentUris.withAppendedId(Audio.Media.EXTERNAL_CONTENT_URI, id);
        try {
            ContentValues values = new ContentValues(2);
            values.put(AudioColumns.IS_RINGTONE, "1");
            values.put(AudioColumns.IS_ALARM, "1");
            resolver.update(ringUri, values, null, null);
        } catch (UnsupportedOperationException ex) {
            // most likely the card just got unmounted
            return;
        }

        String[] cols = new String[] {
                BaseColumns._ID, MediaColumns.DATA, MediaColumns.TITLE
        };

        String where = BaseColumns._ID + "=" + id;
        Cursor cursor = query(context, Audio.Media.EXTERNAL_CONTENT_URI, cols, where, null, null);
        try {
            if (cursor != null && cursor.getCount() == 1) {
                // Set the system setting to make this the current ringtone
                cursor.moveToFirst();
                Settings.System.putString(resolver, Settings.System.RINGTONE, ringUri.toString());
                String message = context.getString(R.string.set_as_ringtone, cursor.getString(2));
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
		
	}

	public static int removeTrack(long id) {
		if (mService == null)
            return 0;

        try {
            return mService.removeTrack(id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
	}

	public static void removeFromFavorites(Context context, long id) {
		long favorites_id;
        if (id < 0) {
        } else {
            ContentResolver resolver = context.getContentResolver();
            String favorites_where = PlaylistsColumns.NAME + "='" + Constants.PLAYLIST_NAME_FAVORITES + "'";
            String[] favorites_cols = new String[] {
                BaseColumns._ID
            };
            Uri favorites_uri = Audio.Playlists.EXTERNAL_CONTENT_URI;
            Cursor cursor = resolver.query(favorites_uri, favorites_cols, favorites_where, null,
                    null);
            if (cursor.getCount() <= 0) {
                favorites_id = createPlaylist(context, Constants.PLAYLIST_NAME_FAVORITES);
            } else {
                cursor.moveToFirst();
                favorites_id = cursor.getLong(0);
                cursor.close();
            }
            Uri uri = Playlists.Members.getContentUri(Constants.EXTERNAL, favorites_id);
            resolver.delete(uri, Playlists.Members.AUDIO_ID + "=" + id, null);
        }
	}

	public static int getQueuePosition() {
		 if (mService == null)
	            return 0;
	        try {
	            return mService.getQueuePosition();
	        } catch (RemoteException e) {
	        }
		return 0;
	}
}
