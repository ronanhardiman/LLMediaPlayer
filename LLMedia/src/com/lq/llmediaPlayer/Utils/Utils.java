package com.lq.llmediaPlayer.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

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

}
