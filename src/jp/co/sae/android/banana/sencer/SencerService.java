package jp.co.sae.android.banana.sencer;

import jp.co.sae.android.banana.Common;
import jp.co.sae.android.banana.Initialization;
import jp.co.sae.android.banana.MainActivity;
import jp.co.sae.android.banana.MessageDelayed;
import jp.co.sae.android.banana.R;
import jp.co.sae.android.banana.common.LogView;
import jp.co.sae.android.banana.database.DatabaseAccess;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

public class SencerService extends Service {

	/** 加速度センサ */
	private SencerManager mSencerManager = null;
	
	private MessageDelayed mMessageDelayed = null;
	
	private DatabaseAccess mDatabaseAccess = null;
	
	private Handler mMessageHandler = new MessageHandler();
	
	private ScreenReceiver mReceiver = new ScreenReceiver();
	
	private NotificationManager mNotificationManager = null;
	
	private boolean wakeLock = false;
	/** キーガード無効 */
	private boolean keyguard = false;
	/** シェイク強度 */
	private int shake = 0;
	/** 自動消灯時間 */
	private int timeout = 0;

	/* (非 Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		mSencerManager = new SencerManager(this, mMessageHandler);
		
		mDatabaseAccess = new DatabaseAccess(this, true);
		
		mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		mMessageDelayed = new MessageDelayed(this, mMessageHandler, getPackageName());
		mMessageDelayed.open();
		
		if (Initialization.INITIAL == false){
			// リソースの取得
			Initialization.TIMES_STRING = getResources().getStringArray(R.array.TimeString);
			Initialization.TIMES_VALUE = getResources().getIntArray(R.array.TimeValue);
			Initialization.TIME_CLOCK_STRING = getResources().getStringArray(R.array.TimeClockString);
			Initialization.TIME_CLOCK_VALUE = getResources().getIntArray(R.array.TimeClockValue);
			Initialization.SHAKE_STRING = getResources().getStringArray(R.array.ShakeString);
			Initialization.SHAKE_VALUE = getResources().getIntArray(R.array.ShakeValue);
			Initialization.INITIAL = true;
		}
		
		if (!mDatabaseAccess.getUseTimeDis()){
			Intent intent = new Intent();
			intent.setAction(Common.Timer.ACTION_START);
			sendBroadcast(intent);
		}
		
		// レシーバを登録
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		SensorWakeLock.cpuWakeLock(this);
		lighting(false);
		keyguard = mDatabaseAccess.getKeyguard();
		shake = Initialization.SHAKE_VALUE[mDatabaseAccess.getShake()];
		timeout = Initialization.TIMES_VALUE[mDatabaseAccess.getTimeout()];
		
		LogView.v("shake:" + shake);
		LogView.v("time:" + timeout);
		
		mSencerManager.setShakeForce(shake);
		mSencerManager.startScan();
		
		/*if (keyguard){
			SensorWakeLock.keyguardUnLock(SencerService.this);
		}*/
	}

	/* (非 Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		mNotificationManager.cancel(R.string.app_name);
		SensorWakeLock.keyguardLock();
		SensorWakeLock.releaseScreenWakeLock();
		SensorWakeLock.releaseCpuWakeLock();
		mSencerManager.stopScan();
		mSencerManager = null;
		mMessageHandler = null;
		mMessageDelayed.close();
		mMessageDelayed = null;
		mMessageHandler = null;
		mDatabaseAccess = null;
		mNotificationManager = null;
		
		unregisterReceiver(mReceiver);
	}

	/**
	 * メッセージ受信ハンドラ. 各クラスからのメッセージを受信し、処理を行う
	 */
	private class MessageHandler extends Handler {

		@Override
		public synchronized void handleMessage(Message msg) {

			if (null == mMessageHandler){
				return;
			}

			switch (msg.what) {
			case Common.Sencer.SHAKE_GENTLY:
				
				if (wakeLock == false){
					if (timeout != 0){
						mMessageDelayed.sendDelayMessage(
								Common.Timer.LIGHT_OFF_TIMER, timeout);
					}
					
					SensorWakeLock.screenWakeLock(SencerService.this);
					if (keyguard){
						SensorWakeLock.keyguardUnLock(SencerService.this);
					}
					wakeLock = true;
					lighting(true);
				} else {
					mMessageDelayed.removeDelayMessage(
							Common.Timer.LIGHT_OFF_TIMER);
					if (keyguard){
						SensorWakeLock.keyguardLock();
					}
					SensorWakeLock.releaseScreenWakeLock();
					wakeLock = false;
					lighting(false);
				}
				
				break;
				
			case Common.Timer.LIGHT_OFF_TIMER:
				if (keyguard){
					SensorWakeLock.keyguardLock();
				}
				SensorWakeLock.releaseScreenWakeLock();
				wakeLock = false;
				lighting(false);
				
				break;
			}
		}
	}
	
	private class ScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
				LogView.v("ACTION_SCREEN_OFF");
				if (keyguard){
					SensorWakeLock.keyguardLock();
				}
				SensorWakeLock.releaseScreenWakeLock();
				wakeLock = false;
				lighting(false);
			}
		}
	}
	
	/**
	 * 点灯通知変更
	 * @param light アイコン(true=点灯/false=消灯)
	 */
	private void lighting(boolean light){
		
		int icon = 0;
		int msg = 0;
		
		if (light){
			icon = R.drawable.light_on;
			msg = R.string.UnLock;
		} else {
			icon = R.drawable.light_off;
			msg = R.string.Lock;
		}
		
		// ノティフィケーションを閉じる
		mNotificationManager.cancel(R.string.app_name);
		
		// ノティフィケーションを開いたときに開始するインテント
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Notification notification = new Notification(icon, null, System.currentTimeMillis());
		
		// ボタンが押されたときにインテントを出す
		PendingIntent pendingIntent = PendingIntent.getActivity(
				this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		
		// ノティフィケーションの通知設定
		notification.setLatestEventInfo(this, 
				getString(R.string.app_name), 
				getString(msg), 
				pendingIntent);
		
		// ノティフィケーション通知
		mNotificationManager.notify(R.string.app_name, notification);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		LogView.v("onBind");
		if (ISencerService.class.getName().equals(intent.getAction())){
			return sencerServiceIf;
		}
		return null;
	}
	
	@Override
	public boolean onUnbind(Intent intent){
		LogView.v("onUnbind");
		return false;
	}
	
	@Override
	public void onRebind(Intent intent){
		LogView.v("onRebind");
	}
	
	private final ISencerService.Stub sencerServiceIf = new ISencerService.Stub() {
		
		@Override
		public String getPname() throws RemoteException {
			return Common.PACKAGE_NAME;
		}
		
		@Override
		public void setKeyGuard(boolean key) throws RemoteException {
			keyguard = key;
		}
		
		@Override
		public void setShakeForce(int force) throws RemoteException {
			mSencerManager.setShakeForce(force);
		}

		@Override
		public void setOffTime(int time) throws RemoteException {
			timeout = time;
		}
		
	};
}
