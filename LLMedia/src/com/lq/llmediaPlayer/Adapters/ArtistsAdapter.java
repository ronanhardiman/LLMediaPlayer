package com.lq.llmediaPlayer.Adapters;


import java.lang.ref.WeakReference;

import com.lq.llmediaPlayer.R;
import com.lq.llmediaPlayer.Cache.ImageInfo;
import com.lq.llmediaPlayer.Cache.ImageProvider;
import com.lq.llmediaPlayer.Config.Constants;
import com.lq.llmediaPlayer.Fragment.ArtistsFragment;
import com.lq.llmediaPlayer.Utils.MusicUtils;
import com.lq.llmediaPlayer.Views.ViewHolderGrid;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

public class ArtistsAdapter extends SimpleCursorAdapter{
	private AnimationDrawable mPeakOneAnimation, mPeakTwoAnimation;
	private ImageProvider mImageProvider;
	private WeakReference<ViewHolderGrid> holderWeakReference;
	private Context mContext; 
	public ArtistsAdapter(Context context,int layout,Cursor c,
			String[] from, int[] to,int flags) {
		super(context,layout,c,from,to,flags);
		this.mContext = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View view = super.getView(position, convertView, parent);
		Cursor mCursor = (Cursor) getItem(position);
		final ViewHolderGrid viewholder;
		
		if(view != null){
			viewholder = new ViewHolderGrid(view);
			holderWeakReference = new WeakReference<ViewHolderGrid>(viewholder);
			view.setTag(holderWeakReference.get());
		}else{
			viewholder = (ViewHolderGrid) convertView.getTag();
		}
		//Artist name
		String artistName = mCursor.getString(ArtistsFragment.mArtistNameIndex);
		holderWeakReference.get().mViewHolderLineOne.setText(artistName);
		
		//number of albums
		int albums_plural = mCursor.getInt(ArtistsFragment.mArtistNumAlbumsIndex);
		boolean unknown = artistName == null || artistName.equals(MediaStore.UNKNOWN_STRING);
		String numAlbums = MusicUtils.makeAlbumsLabel(mContext, albums_plural, 0, unknown);
		holderWeakReference.get().mViewHolderLineTwo.setText(numAlbums);
		
		ImageInfo mInfo = new ImageInfo();
        mInfo.type = Constants.TYPE_ARTIST;
        mInfo.size = Constants.SIZE_THUMB;
        mInfo.source = Constants.SRC_FIRST_AVAILABLE;
        mInfo.data = new String[]{ artistName };
        
        mImageProvider.loadImage( viewholder.mViewHolderImage, mInfo );
        
        //current artist ID
        long currentArtistId = MusicUtils.getCurrentArtistId();
        long artistId = mCursor.getLong(ArtistsFragment.mArtistIdIndex);
        if(currentArtistId == artistId){
        	holderWeakReference.get().mPeakOne.setImageResource(R.anim.peak_meter_1);
        	holderWeakReference.get().mPeakTwo.setImageResource(R.anim.peak_meter_2);
             mPeakOneAnimation = (AnimationDrawable)holderWeakReference.get().mPeakOne.getDrawable();
             mPeakTwoAnimation = (AnimationDrawable)holderWeakReference.get().mPeakTwo.getDrawable();
             try {
                 if (MusicUtils.mService.isPlaying()) {
                     mPeakOneAnimation.start();
                     mPeakTwoAnimation.start();
                 } else {
                     mPeakOneAnimation.stop();
                     mPeakTwoAnimation.stop();
                 }
             } catch (RemoteException e) {
                 e.printStackTrace();
             }
        	
        }else{
        	holderWeakReference.get().mPeakOne.setImageResource(0);
        	holderWeakReference.get().mPeakTwo.setImageResource(0);
        }
		return view;
	}
}
