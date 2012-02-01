package jp.co.sae.android.banana;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.SystemClock;

public class MessageDelayed {
	
	private Context context;
	private Handler handler;
	private String action;
	private DelayedReceiver receiver = new DelayedReceiver();
	
	public MessageDelayed(Context context, Handler handler, String action){
		this.context = context;
		this.handler = handler;
		this.action = action;
	}
	
	public void open(){
		IntentFilter filter = new IntentFilter(action);
		context.registerReceiver(receiver, filter);	
	}
	
	public void close(){
		context.unregisterReceiver(receiver);
	}
	
	/**
	 * 遅延メッセージ送信
	 * @param msg メッセージ
	 * @param time 遅延時間(ms)
	 */
	public void sendDelayMessage(int msg, long time){
		Intent intent = new Intent();
		intent.setAction(action);
		intent.setPackage(String.valueOf(msg));
		intent.putExtra(action, msg);
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		AlarmManager manager = (AlarmManager)
				context.getSystemService(Context.ALARM_SERVICE);
		
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime() + time, pendingIntent);
	}
	
	/**
	 * 遅延メッセージ削除
	 * @param msg メッセージ
	 */
	public void removeDelayMessage(int msg){
		Intent intent = new Intent(action);
		intent.setPackage(String.valueOf(msg));
		intent.putExtra(action, msg);
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		AlarmManager manager = (AlarmManager)
				context.getSystemService(Context.ALARM_SERVICE);
		
		manager.cancel(pendingIntent);
	}
	
	private class DelayedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int extraMessage = intent.getIntExtra(action, 0);
			
			handler.sendEmptyMessage(extraMessage);
		}
	}
}
