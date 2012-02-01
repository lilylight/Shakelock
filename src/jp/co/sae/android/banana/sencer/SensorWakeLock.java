package jp.co.sae.android.banana.sencer;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;

public class SensorWakeLock {
	
	private static PowerManager.WakeLock cpuWakeLock = null;
	private static PowerManager.WakeLock screenWakeLock = null;
	private static KeyguardManager.KeyguardLock keyguardLock = null;
	private static boolean isKeyguard = false;
	
	/**
	 * CPUを起動させる
	 * @param context コンテキスト
	 */
	public static void cpuWakeLock(Context context){
		if (cpuWakeLock != null){
			return;
		}
		
		PowerManager manager = (PowerManager)
			context.getSystemService(Context.POWER_SERVICE);
		
		cpuWakeLock = manager.newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP
				/*| PowerManager.ON_AFTER_RELEASE*/, context.getPackageName());
		cpuWakeLock.acquire();
	}
	
	/**
	 * 画面を起動させる
	 * @param context コンテキスト
	 */
	public static void screenWakeLock(Context context){
		if (screenWakeLock != null){
			return;
		}
		
		PowerManager manager = (PowerManager)
			context.getSystemService(Context.POWER_SERVICE);
		
		screenWakeLock = manager.newWakeLock(
				PowerManager.SCREEN_BRIGHT_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP
				/*| PowerManager.ON_AFTER_RELEASE*/, context.getPackageName());
		screenWakeLock.acquire();
	}
	
	/**
	 * キーガード解除
	 * @param context コンテキスト
	 */
	public static void keyguardUnLock(Context context){
		if (keyguardLock != null){
			return;
		}
		
		KeyguardManager manager = (KeyguardManager)
			context.getSystemService(Context.KEYGUARD_SERVICE);
		
		keyguardLock = manager.newKeyguardLock(context.getPackageName());
		
		if (manager.inKeyguardRestrictedInputMode()){
			keyguardLock.disableKeyguard();
			isKeyguard = true;
		} else {
			isKeyguard = false;
		}
	}
	
	public static void releaseCpuWakeLock(){
		if (cpuWakeLock != null){
			cpuWakeLock.release();
			cpuWakeLock = null;
		}
	}
	
	public static void releaseScreenWakeLock(){
		if (screenWakeLock != null){
			screenWakeLock.release();
			screenWakeLock = null;
		}
	}
	
	public static void keyguardLock(){
		if (keyguardLock != null){
			if (isKeyguard){
				keyguardLock.reenableKeyguard();
				isKeyguard = false;
			}
			keyguardLock = null;
		}
	}
}
