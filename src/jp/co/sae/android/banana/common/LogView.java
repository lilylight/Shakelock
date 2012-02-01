package jp.co.sae.android.banana.common;

import android.util.Log;

public class LogView {
	
	private static final String TAG = "ShakelockLog";
	
	private static final boolean DEBUG = false;
	
	public static void v(String s){
		if(DEBUG)Log.v(TAG, s);
	}
	
	public static void d(String s){
		if(DEBUG)Log.d(TAG, s);
	}
	
	public static void w(String s){
		if(DEBUG)Log.w(TAG, s);
	}
	
	public static void e(String s){
		if(DEBUG)Log.e(TAG, s);
	}

}
