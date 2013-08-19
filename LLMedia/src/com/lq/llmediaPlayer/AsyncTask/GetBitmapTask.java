package com.lq.llmediaPlayer.AsyncTask;

import java.io.File;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.lq.llmediaPlayer.R;
import com.lq.llmediaPlayer.Cache.ImageInfo;
import com.lq.llmediaPlayer.Config.Constants;
import com.lq.llmediaPlayer.Utils.ImageUtils;

public class GetBitmapTask extends AsyncTask<String, Integer, Bitmap> {
	
	private WeakReference<OnBitmapReadyListener> mListenerReference;
	
	private WeakReference<Context> mContextReference;
	
	private ImageInfo mImageInfo;
	
	private int mThumbSize;
	
	public GetBitmapTask(int thumbSize, ImageInfo imageInfo,
			OnBitmapReadyListener listener, Context context) {
		mListenerReference = new WeakReference<OnBitmapReadyListener>(listener);
		mContextReference = new WeakReference<Context>(context);
		mImageInfo = imageInfo;
		mThumbSize = thumbSize;
	}

	public static interface OnBitmapReadyListener {
		public void bitmapReady(Bitmap bitmap, String tag);
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		Context context = mContextReference.get();
		if(context == null){
			return null;
		}
		File nFile = null;
		if( mImageInfo.source.equals(Constants.SRC_FILE)  && !isCancelled()){
        	nFile = ImageUtils.getImageFromMediaStore( context, mImageInfo );
        }
        else if ( mImageInfo.source.equals(Constants.SRC_LASTFM)  && !isCancelled()){
        	nFile = ImageUtils.getImageFromWeb( context, mImageInfo );
        }
        else if ( mImageInfo.source.equals(Constants.SRC_GALLERY)  && !isCancelled()){
        	nFile = ImageUtils.getImageFromGallery( context, mImageInfo );
        }        	
        else if ( mImageInfo.source.equals(Constants.SRC_FIRST_AVAILABLE)  && !isCancelled()){
        	Bitmap bitmap = null;
        	if( mImageInfo.size.equals( Constants.SIZE_NORMAL ) ){
        		bitmap = ImageUtils.getNormalImageFromDisk( context, mImageInfo );
        	}
        	else if( mImageInfo.size.equals( Constants.SIZE_THUMB ) ){
        		bitmap = ImageUtils.getThumbImageFromDisk( context, mImageInfo, mThumbSize );
        	}
        	//if we have a bitmap here then its already properly sized
        	if( bitmap != null ){
        		return bitmap;
        	}
        	
        	if( mImageInfo.type.equals( Constants.TYPE_ALBUM ) ){
        		nFile = ImageUtils.getImageFromMediaStore( context, mImageInfo );
        	}
        	if( nFile == null && ( mImageInfo.type.equals( Constants.TYPE_ALBUM ) || mImageInfo.type.equals( Constants.TYPE_ARTIST ) ) )
        		nFile = ImageUtils.getImageFromWeb( context, mImageInfo );
        }
        if( nFile != null ){        	
        	// if requested size is normal return it
        	if( mImageInfo.size.equals( Constants.SIZE_NORMAL ) )
        		return BitmapFactory.decodeFile(nFile.getAbsolutePath());
        	//if it makes it here we want a thumbnail image
        	return ImageUtils.getThumbImageFromDisk( context, nFile, mThumbSize );
        }
		return null;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		super.onPostExecute(bitmap);
		OnBitmapReadyListener listener = mListenerReference.get();
        if(bitmap == null && !isCancelled()){
        	if(mImageInfo.size.equals(Constants.SIZE_THUMB))
        		bitmap = BitmapFactory.decodeResource(mContextReference.get().getResources(),
        													R.drawable.no_art_small);
        	else if(mImageInfo.size.equals(Constants.SIZE_NORMAL))
        		bitmap = BitmapFactory.decodeResource(mContextReference.get().getResources(),
        													R.drawable.no_art_normal);
        }
        if (bitmap != null && !isCancelled()) {
            if (listener != null) {
                	listener.bitmapReady(bitmap,  ImageUtils.createShortTag(mImageInfo) + mImageInfo.size );
            }
        }
	}
}
