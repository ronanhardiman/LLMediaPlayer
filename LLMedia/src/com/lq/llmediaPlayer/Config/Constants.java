package com.lq.llmediaPlayer.Config;

public final class Constants {
	//Bundle & Intent type
	public final static String MIME_TYPE = "mimetpye",INTENT_ACTION = "action",DATA_SCHEME = "file" ;
	// Playlists
    public final static long PLAYLIST_UNKNOWN = -1, PLAYLIST_ALL_SONGS = -2, PLAYLIST_QUEUE = -3,
            PLAYLIST_NEW = -4, PLAYLIST_FAVORITES = -5, PLAYLIST_RECENTLY_ADDED = -6;
    
    // SharedPreferences
    public final static String APOLLO = "Apollo", APOLLO_PREFERENCES = "apollopreferences",
            ARTIST_KEY = "artist", ALBUM_KEY = "album", ALBUM_ID_KEY = "albumid", NUMALBUMS = "num_albums",
            GENRE_KEY = "genres", ARTIST_ID = "artistid", NUMWEEKS = "numweeks",
            PLAYLIST_NAME_FAVORITES = "Favorites", PLAYLIST_NAME = "playlist", WIDGET_STYLE="widget_type",
            THEME_PACKAGE_NAME = "themePackageName", THEME_DESCRIPTION = "themeDescription",
            THEME_PREVIEW = "themepreview", THEME_TITLE = "themeTitle", VISUALIZATION_TYPE="visualization_type", 
            UP_STARTS_ALBUM_ACTIVITY = "upStartsAlbumActivity", TABS_ENABLED = "tabs_enabled";
 
}
