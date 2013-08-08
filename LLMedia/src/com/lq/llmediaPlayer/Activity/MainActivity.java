package com.lq.llmediaPlayer.Activity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.lq.llmediaPlayer.R;
import com.lq.llmediaPlayer.Adapters.PagerAdapter;
import com.lq.llmediaPlayer.Config.Constants;
import com.lq.llmediaPlayer.Fragment.RecentlyAddedFragment;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initView();
		
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void initView() {
		//init actionbar
		ActionBar actionBar = getActionBar();
		int upId = Resources.getSystem().getIdentifier("up", "id", "android");
		ImageView actionBarUp = (ImageView) findViewById(upId);
		
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		
		//init viewpager
		PagerAdapter mPagerAdapter = new PagerAdapter(getFragmentManager());
		Bundle bundle = new Bundle();
		bundle.putString(Constants.MIME_TYPE, Audio.Playlists.CONTENT_TYPE);
		bundle.putLong(BaseColumns._ID, Constants.PLAYLIST_RECENTLY_ADDED);
		
		new RecentlyAddedFragment(bundle);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
//		case R.id.:
//			
//			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onPrepareOptionsMenu(menu);
	}

}
