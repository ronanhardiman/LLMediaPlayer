package com.lq.llmediaPlayer.Cache;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.lq.llmediaPlayer.AsyncTask.GetBitmapTask;
import com.lq.llmediaPlayer.AsyncTask.GetBitmapTask.OnBitmapReadListener;
import com.lq.llmediaPlayer.Config.Constants;
import com.lq.llmediaPlayer.Utils.ImageUtils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.widget.ImageView;

public class ImageProvider implements GetBitmapTask.OnBitmapReadListener{
	private static ImageProvider mInstance;
	private Context mContext;
	private ImageCache mCache;
	private int thumbSize;
	private Map<String, Set<ImageView>> pendingImagesMap = new HashMap<String, Set<ImageView>>();

	private Set<String> unavailable = new HashSet<String>();
	
	public ImageProvider(Activity activity) {
		mContext = activity;
		mCache = ImageCache.getInstance(activity);
		Resources resources = mContext.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		thumbSize = (int) ( ( 153 * (metrics.densityDpi/160f) ) + 0.5f );
	}
	
	public void loadImage(ImageView imageView, ImageInfo imageInfo) {
		String tag = ImageUtils.createShortTag(imageInfo) + imageInfo.size;
		if( imageInfo.source.equals(Constants.SRC_FILE) || imageInfo.source.equals(Constants.SRC_LASTFM) || imageInfo.source.equals(Constants.SRC_GALLERY)){
    		clearFromMemoryCache( ImageUtils.createShortTag(imageInfo) );
    		asyncLoad( tag, imageView, new GetBitmapTask( thumbSize, imageInfo, this, imageView.getContext() ) );
		}
    	if(!setCachedBitmap(imageView, tag)){
            asyncLoad( tag, imageView, new GetBitmapTask( thumbSize, imageInfo, this, imageView.getContext() ) );
        }
	}

	private void asyncLoad(String tag, ImageView imageView,
			GetBitmapTask getBitmapTask) {
		
	}

	private boolean setCachedBitmap(ImageView imageView, String tag) {
		if(unavailable.contains(tag)){
			handleBitmapUnavailable(imageView, tag);
		}
		Bitmap bitmap = mCache.get(tag);
		if(bitmap == null){
			return false;
		}
		imageView.setTag(tag);
		imageView.setImageBitmap(bitmap);
		return true;
	}

	private void handleBitmapUnavailable(ImageView imageView, String tag) {
		imageView.setTag(tag);
        imageView.setImageDrawable(null);
	}

	private void clearFromMemoryCache(String tag) {
		if(unavailable.contains(tag + Constants.SIZE_THUMB)){
			unavailable.remove(tag + Constants.SIZE_THUMB);
		}
		if(pendingImagesMap.get(tag + Constants.SIZE_THUMB) != null){
			pendingImagesMap.remove(tag + Constants.SIZE_THUMB);
		}
		if(mCache.get(tag + Constants.SIZE_THUMB) != null){
			mCache.remove(tag + Constants.SIZE_THUMB);
		}
		if(unavailable.contains(tag + Constants.SIZE_NORMAL)){
			unavailable.remove(tag + Constants.SIZE_NORMAL);
		}
		if(pendingImagesMap.get(tag + Constants.SIZE_NORMAL) != null){
			pendingImagesMap.remove(tag + Constants.SIZE_NORMAL);
		}
		if(mCache.get(tag + Constants.SIZE_NORMAL) != null){
			mCache.remove(tag + Constants.SIZE_NORMAL);
		}
	}

	public static final ImageProvider getInstance(Activity activity) {
		if(mInstance == null){
			mInstance = new ImageProvider(activity);
		}
		return null;
	}

	@Override
	public void bitmapReady(Bitmap bitmap, String tag) {
		
	}

}
