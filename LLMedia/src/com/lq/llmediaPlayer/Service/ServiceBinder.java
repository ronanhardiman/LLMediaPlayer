package com.lq.llmediaPlayer.Service;

import com.lq.llmediaPlayer.Utils.MusicUtils;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class ServiceBinder implements ServiceConnection{
	
	private final ServiceConnection mCallback;
	
	public ServiceBinder(ServiceConnection callback){
		mCallback = callback;
	}
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		MusicUtils.mService = MediaService.Stub.asInterface(service);
		if(mCallback != null){
			mCallback.onServiceConnected(name, service);
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		if(mCallback != null){
			mCallback.onServiceDisconnected(name);
		}
		MusicUtils.mService = null;
	}

}
