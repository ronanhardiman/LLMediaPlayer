package com.lq.llmediaPlayer.Service;

import java.io.IOException;
import java.lang.ref.WeakReference;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.net.rtp.AudioStream;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;

public class LLMediaService extends Service {
	
	public static final int PLAYBACKSERVICE_STATUS = 1;
	
	public static final String APOLLO_PACKAGE_NAME = "com.andrew.apolloMod";

    public static final String MUSIC_PACKAGE_NAME = "com.android.music";

    public static final String PLAYSTATE_CHANGED = "com.andrew.apolloMod.playstatechanged";

    public static final String META_CHANGED = "com.andrew.apolloMod.metachanged";

    public static final String FAVORITE_CHANGED = "com.andrew.apolloMod.favoritechanged";

    public static final String QUEUE_CHANGED = "com.andrew.apolloMod.queuechanged";

    public static final String REPEATMODE_CHANGED = "com.andrew.apolloMod.repeatmodechanged";

    public static final String SHUFFLEMODE_CHANGED = "com.andrew.apolloMod.shufflemodechanged";

    public static final String PROGRESSBAR_CHANGED = "com.andrew.apolloMod.progressbarchnaged";

    public static final String REFRESH_PROGRESSBAR = "com.andrew.apolloMod.refreshprogessbar";

    public static final String CYCLEREPEAT_ACTION = "com.andrew.apolloMod.musicservicecommand.cyclerepeat";

    public static final String TOGGLESHUFFLE_ACTION = "com.andrew.apolloMod.musicservicecommand.toggleshuffle";

    public static final String SERVICECMD = "com.andrew.apolloMod.musicservicecommand";

	
	private Notification status;

	private static final int TRACK_ENDED = 1;

	private static final int RELEASE_WAKELOCK = 2;

	private static final int SERVER_DIED = 3;

	private static final int FOCUSCHANGE = 4;

	private static final int FADEDOWN = 5;

	private static final int FADEUP = 6;

	private static final int TRACK_WENT_TO_NEXT = 7;

	private static final int MAX_HISTORY_SIZE = 100;

	private MultiPlayer mPlayer;

	private String mFileToPlay;

	private long[] mAutoShuffleList = null;

	private long[] mPlayList = null;
	private int mPlayListLen = 0;

	private Cursor mCursor;
	private int mPlayPos = -1;

	private int mNextPlayPos = -1;
	private int mOpenFailedCounter = 0;
	String[] mCursorCols = new String[] { "audio._id AS _id",
			MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
			MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
			MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.ALBUM_ID,
			MediaStore.Audio.Media.ARTIST_ID,
			MediaStore.Audio.Media.IS_PODCAST, MediaStore.Audio.Media.BOOKMARK };
	private final static int IDCOLIDX = 0;

	private final static int PODCASTCOLIDX = 8;

	private final static int BOOKMARKCOLIDX = 9;
	private BroadcastReceiver mUnmountReceiver = null;
	private WakeLock mWakeLock;

	private int mServiceStartId = -1;

	private boolean mServiceInUse = false;

	private boolean mIsSupposedToBePlaying = false;
	
	private boolean mQuietMode = false;

    private AudioManager mAudioManager;

    private boolean mQueueIsSaveable = true;
    
 // used to track what type of audio focus loss caused the playback to pause
    private boolean mPausedByTransientLossOfFocus = false;

    private SharedPreferences mPreferences;
    
 // interval after which we stop the service when idle
    private static final int IDLE_DELAY = 60000;
    
	private final IBinder mBinder = new ServiceStub(this);

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	static class ServiceStub extends MediaService.Stub {
		WeakReference<LLMediaService> mService;

		ServiceStub(LLMediaService service) {
			mService = new WeakReference<LLMediaService>(service);
		}

		@Override
		public void openFile(String path) throws RemoteException {
			mService.get().open(path);

		}

		@Override
		public void open(long[] list, int position) throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public long getIdFromPath(String path) throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getQueuePosition() throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean isPlaying() throws RemoteException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void stop() throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void pause() throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void play() throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void prev() throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void next() throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public long duration() throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public long position() throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public long seek(long pos) throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getTrackName() throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getAlbumName() throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getAlbumId() throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Bitmap getAlbumBitmap() throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getArtistName() throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getArtistId() throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void enqueue(long[] list, int action) throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public long[] getQueue() throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setQueuePosition(int index) throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public String getPath() throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getAudioId() throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void setShuffleMode(int shufflemode) throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void notifyChange(String what) throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public int getShuffleMode() throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int removeTracks(int first, int last) throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int removeTrack(long id) throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void setRepeatMode(int repeatmode) throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public int getRepeatMode() throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getMediaMountedCount() throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getAudioSessionId() throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void addToFavorites(long id) throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void removeFromFavorites(long id) throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isFavorite(long id) throws RemoteException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void toggleFavorite() throws RemoteException {
			// TODO Auto-generated method stub

		}

	}
	
	private final Handler mDelayedStopHandler = new Handler(){
		public void handleMessage(Message msg) {
			// Check again to make sure nothing is playing right now
            if (isPlaying() || mPausedByTransientLossOfFocus || mServiceInUse
                    || mMediaplayerHandler.hasMessages(TRACK_ENDED)) {
                return;
            }
            // save the queue again, because it might have changed
            // since the user exited the music app (because of
            // party-shuffle or because the play-position changed)
            saveQueue(true);
            stopSelf(mServiceStartId);
		};
	};
	
	private final Handler mMediaplayerHandler = new Handler(){
		
	};
	private class MultiPlayer {
		private MediaPlayer mCurrentMediaPlayer = new MediaPlayer();

		private MediaPlayer mNextMediaPlayer;

		private Handler mHandler;

		private boolean mIsInitialized = false;

		public MultiPlayer() {
			mCurrentMediaPlayer.setWakeMode(LLMediaService.this,
					PowerManager.PARTIAL_WAKE_LOCK);
		}

		public void setDataSource(String path) {
			mIsInitialized = setDataSourceImpl(mCurrentMediaPlayer, path);
			if (mIsInitialized) {
				setNextDataSource(null);
			}
		}

		private void setNextDataSource(Object object) {
			// TODO Auto-generated method stub

		}

		private boolean setDataSourceImpl(MediaPlayer player, String path) {
			try {
				player.reset();
				player.setOnPreparedListener(null);
				if (path.startsWith("content://")) {
					player.setDataSource(LLMediaService.this, Uri.parse(path));
				} else {
					player.setDataSource(path);
				}
				player.setAudioStreamType(AudioManager.STREAM_MUSIC);
				player.prepare();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return false;
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			player.setOnCompletionListener(listener);
			player.setOnErrorListener(errorListener);
			Intent i = new Intent(
					AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
			i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
			i.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
			sendBroadcast(i);

			// VisualizerUtils.initVisualizer( player );

			return true;
		}

		private int getAudioSessionId() {
			return mCurrentMediaPlayer.getAudioSessionId();
		}

		OnErrorListener errorListener = new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				switch (what) {
				case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
					mIsInitialized = false;
					mCurrentMediaPlayer.release();
					// Creating a new MediaPlayer and settings its wakemode
					// does not
					// require the media service, so it's OK to do this now,
					// while the
					// service is still being restarted
					mCurrentMediaPlayer = new MediaPlayer();
					mCurrentMediaPlayer.setWakeMode(LLMediaService.this,
							PowerManager.PARTIAL_WAKE_LOCK);
					mHandler.sendMessageDelayed(
							mHandler.obtainMessage(SERVER_DIED), 2000);
					return true;
				default:
					Log.d("MultiPlayer", "Error: " + what + "," + extra);
					break;
				}
				return false;
			}
		};
		OnCompletionListener listener = new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				if (mp == mCurrentMediaPlayer && mNextMediaPlayer != null) {
					mCurrentMediaPlayer.release();
					mCurrentMediaPlayer = mNextMediaPlayer;
					mNextMediaPlayer = null;
					mHandler.sendEmptyMessage(TRACK_WENT_TO_NEXT);
				} else {
					// Acquire a temporary wakelock, since when we return from
					// this callback the MediaPlayer will release its wakelock
					// and allow the device to go to sleep.
					// This temporary wakelock is released when the
					// RELEASE_WAKELOCK
					// message is processed, but just in case, put a timeout on
					// it.
					mWakeLock.acquire(30000);
					mHandler.sendEmptyMessage(TRACK_ENDED);
					mHandler.sendEmptyMessage(RELEASE_WAKELOCK);
				}
			}
		};

		public boolean isInitialized() {
			return mIsInitialized;
		}

		public void stop() {
			mCurrentMediaPlayer.reset();
			mIsInitialized = false;
		}
	}

	/**
	 * Opens the specified file and readies it for playback.
	 * 
	 * @param path
	 *            The full path of the file to be opened.
	 */
	public boolean open(String path) {
		synchronized (this) {
			if (path == null) {
				return false;
			}
			if (mCursor == null) {
				ContentResolver resolver = getContentResolver();
				Uri uri;
				String where;
				String selectionArgs[];
				if (path.startsWith("content://media/")) {
					uri = Uri.parse(path);
					where = null;
					selectionArgs = null;
				} else {
					// Remove schema for search in the database
					// Otherwise the file will not found
					String data = path;
					if (data.startsWith("file://")) {
						data = data.substring(7);
					}
					uri = MediaStore.Audio.Media.getContentUriForPath(path);
					where = MediaColumns.DATA + "=?";
					selectionArgs = new String[] { data };

				}

				try {
					mCursor = resolver.query(uri, mCursorCols, where,
							selectionArgs, null);
					if (mCursor != null) {
						if (mCursor.getCount() == 0) {
							mCursor.close();
							mCursor = null;
						} else {
							mCursor.moveToNext();
							ensurePlayListCapacity(1);
							mPlayListLen = 1;
							mPlayList[0] = mCursor.getLong(IDCOLIDX);
							mPlayPos = 0;

						}
					}
				} catch (UnsupportedOperationException e) {
					e.printStackTrace();
				}
				updateAlbumBitmap();

			}
			mFileToPlay = path;
			mPlayer.setDataSource(mFileToPlay);
			if (mPlayer.isInitialized()) {
				mOpenFailedCounter = 0;
				return true;
			}
			stop(true);
		}
		return false;
	}

	protected void saveQueue(boolean full) {
		
	}

	protected boolean isPlaying() {
		return mIsSupposedToBePlaying;
	}

	private void stop(boolean remove_status_icon) {
		if (mPlayer.isInitialized()) {
			mPlayer.stop();
		}
		mFileToPlay = null;
		if (mCursor != null) {
			mCursor.close();
			mCursor = null;
			updateAlbumBitmap();
		}
		if (remove_status_icon) {
			gotoIdleState();
		} else {
			stopForeground(false);
		}
		if (remove_status_icon) {
			mIsSupposedToBePlaying = false;
		}
	}

	private void gotoIdleState() {
		mDelayedStopHandler.removeCallbacksAndMessages(null);
		Message msg = mDelayedStopHandler.obtainMessage();
		mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);
		stopForeground(false);
		if (status != null) {
//			status.contentView.setImageViewResource(R.id.status_bar_play,
//					mIsSupposedToBePlaying ? R.drawable.apollo_holo_dark_play
//							: R.drawable.apollo_holo_dark_pause);
//			status.bigContentView.setImageViewResource(R.id.status_bar_play,
//					mIsSupposedToBePlaying ? R.drawable.apollo_holo_dark_play
//							: R.drawable.apollo_holo_dark_pause);
//			NotificationManager mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//			mManager.notify(PLAYBACKSERVICE_STATUS, status);
		}
	}

	private void updateAlbumBitmap() {
		// TODO Auto-generated method stub

	}

	private void ensurePlayListCapacity(int size) {
		if (mPlayList == null || size > mPlayList.length) {
			// reallocate at 2x requested size so we don't
			// need to grow and copy the array for every
			// insert
			long[] newlist = new long[size * 2];
			int len = mPlayList != null ? mPlayList.length : mPlayListLen;
			for (int i = 0; i < len; i++) {
				newlist[i] = mPlayList[i];
			}
			mPlayList = newlist;
		}
		// FIXME: shrink the array when the needed size is much smaller
		// than the allocated size
	}
}
