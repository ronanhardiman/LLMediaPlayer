package com.lq.llmediaPlayer.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.text.GetChars;

import com.lq.llmediaPlayer.Cache.ImageInfo;
import com.lq.llmediaPlayer.Config.Constants;

public class ImageUtils {
	private static final String IMAGE_EXTENSION = ".img";
	
	public static String createShortTag(ImageInfo imageInfo) {
		String tag = null;
		if( imageInfo.type.equals( Constants.TYPE_ALBUM ) ){
    		//album id + album suffix 
    		tag = imageInfo.data[0] + Constants.ALBUM_SUFFIX;
    	}
    	else if (imageInfo.type.equals( Constants.TYPE_ARTIST )){
    		//artist name + album suffix
    		tag = imageInfo.data[0] + Constants.ARTIST_SUFFIX;
    	}
    	else if (imageInfo.type.equals( Constants.TYPE_GENRE )){
    		//genre name + genre suffix
    		tag = imageInfo.data[0] + Constants.GENRE_SUFFIX;
    	}
    	else if (imageInfo.type.equals( Constants.TYPE_PLAYLIST )){
    		//genre name + genre suffix
    		tag = imageInfo.data[0] + Constants.PLAYLIST_SUFFIX;
    	}
		Utils.escapeForFileSystem(tag);
		return tag;
	}

	public static File getImageFromMediaStore(Context context,
			ImageInfo imageInfo) {
		String mAlbum = imageInfo.data[0];
		String[] projection ={
				BaseColumns._ID,Audio.Albums._ID,Audio.Albums.ALBUM_ART,
				Audio.Albums.ALBUM
		};
		Uri uri = Audio.Albums.EXTERNAL_CONTENT_URI;
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(uri, projection, 
					BaseColumns._ID+ "=" + DatabaseUtils.sqlEscapeString(mAlbum), 
					null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int column_index = cursor.getColumnIndex(Audio.Albums.ALBUM_ART);
		if(cursor.getCount() > 0){
			cursor.moveToFirst();
			String albumArt = cursor.getString(column_index);
			if(albumArt != null){
				try {
					File orgFile = new File(albumArt);
					File newFile = new File(context.getExternalCacheDir(),createShortTag(imageInfo)+ IMAGE_EXTENSION);
					InputStream in = new FileInputStream(orgFile);
					OutputStream out = new FileOutputStream(newFile);
					byte[] buf = new byte[1024];
					int len = 0;
					while ((len = in.read(buf)) > 0) {
						out.write(buf,0,len);
					}
					in.close();
					out.close();
					cursor.close();
					return newFile;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static File getImageFromWeb(Context context, ImageInfo imageInfo) {
		
		return null;
	}

}
