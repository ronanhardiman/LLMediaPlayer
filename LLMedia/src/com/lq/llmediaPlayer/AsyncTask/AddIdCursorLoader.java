package com.lq.llmediaPlayer.AsyncTask;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class AddIdCursorLoader extends AsyncTaskLoader<Cursor>{
	final ForceLoadContentObserver mObserver;

    Uri mUri;
    String[] mProjection;
    String mSelection;
    String[] mSelectionArgs;
    String mSortOrder;

    Cursor mCursor;
	public AddIdCursorLoader(Context context) {
		super(context);
		mObserver = new ForceLoadContentObserver();
	}
	
	/**
     * Creates a fully-specified CursorLoader.  See
     * {@link ContentResolver#query(Uri, String[], String, String[], String)
     * ContentResolver.query()} for documentation on the meaning of the
     * parameters.  These will be passed as-is to that call.
     */
    public AddIdCursorLoader(Context context, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        super(context);
        mObserver = new ForceLoadContentObserver();
        mUri = uri;
        mProjection = projection;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mSortOrder = sortOrder;
    }
	
	@Override
	public Cursor loadInBackground() {
		// TODO Auto-generated method stub
		return null;
	}

}
