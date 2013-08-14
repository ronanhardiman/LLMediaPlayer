package com.lq.llmediaPlayer.Utils;

import com.lq.llmediaPlayer.R;
import com.lq.llmediaPlayer.Config.Constants;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.MediaStore.Audio;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class Utils {
	
	/**
	 * 
	 * @param artistName
	 * @param id
	 * @param artistId
	 * @param context
	 */
	public static void setArtistId(String artistName, long id, String artistId,
			Context context) {
		SharedPreferences settings = context.getSharedPreferences(artistId, 0);
		settings.edit().putLong(artistName, id).commit();
	}
	/*
	 *Replace the characters not allowed in file names with underscore 
	 */
	public static String escapeForFileSystem(String tag) {
		return tag.replaceAll("[\\\\/:*?\"<>|]+", "_");
	}
	
	/**
     * Header used in the track browser
     * 
     * @param fragment
     * @param view
     * @param string
     */
	public static void listHeader(Fragment fragment, View view,
			String header) {
		if(fragment.getArguments() != null){
			 TextView mHeader = (TextView)view.findViewById(R.id.title);
	            String mimetype = fragment.getArguments().getString(Constants.MIME_TYPE);
	            if (Audio.Artists.CONTENT_TYPE.equals(mimetype)) {
	                mHeader.setVisibility(View.VISIBLE);
	                mHeader.setText(header);
	            } else if (Audio.Albums.CONTENT_TYPE.equals(mimetype)) {
	                mHeader.setVisibility(View.VISIBLE);
	                mHeader.setText(header);
	            }
		}
		
	}

	/**
     * Sets the ListView paddingLeft for the header
     * 
     * @param fragment
     * @param mListView
     */
    public static void setListPadding(Fragment fragment, ListView mListView, int left, int top,
            int right, int bottom) {
    	
    }
}
