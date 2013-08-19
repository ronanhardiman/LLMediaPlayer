package com.lq.llmediaPlayer.Cache;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.LruCache;

public class ImageCache {
	private static final String TAG = ImageCache.class.getSimpleName();
	private LruCache<String, Bitmap> mLruCache;
	private static ImageCache instance;
	public ImageCache(Context applicationContext) {
		init(applicationContext);
	}
	private void init(Context applicationContext) {
		ActivityManager activityManager = (ActivityManager) applicationContext.getSystemService(Context.ACTIVITY_SERVICE);
		int lruCacheSize = Math.round(0.25f * activityManager.getMemoryClass()*1024*1024);
		mLruCache = new LruCache<String, Bitmap>(lruCacheSize){
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getByteCount();
			}
		};
	}
	
	public static final ImageCache findOrCreateCache(final Activity activity){
		FragmentManager nFragmentManager = activity.getFragmentManager();
		RetainFragment retainFragment = (RetainFragment) nFragmentManager.findFragmentByTag(TAG);
		if(retainFragment == null){
			retainFragment = new RetainFragment();
			nFragmentManager.beginTransaction().add(retainFragment, TAG).commit();
		}
		ImageCache cache = (ImageCache) retainFragment.getObject();
		if(cache == null){
			cache = getInstance(activity);
			retainFragment.setObject(cache);
		}
		return cache;
	}
	
	public final static ImageCache getInstance(Context context) {
		if(instance == null){
			instance  = new ImageCache(context.getApplicationContext());
		}
		return instance;
	}
	
	public static final class RetainFragment extends Fragment{
		private Object object;
		public RetainFragment() {
		}
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			//Make sure this Fragment is retained over a configuration change
			setRetainInstance(true);
		}
		public void setObject(Object object){
			this.object = object;
			
		}
		public Object getObject(){
			return object;
		}
	}

	public final Bitmap get(String data) {
		if(data == null){
			return null;
		}
		if(mLruCache != null){
			final Bitmap mBitmap = mLruCache.get(data);
			if(mBitmap != null){
				return mBitmap;
			}
		}
		return null;
	}
	public void remove(String key) {
		if(mLruCache != null){
			mLruCache.remove(key);
		}
	}

}
