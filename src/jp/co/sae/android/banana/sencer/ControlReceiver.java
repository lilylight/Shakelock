package jp.co.sae.android.banana.sencer;

import java.util.Calendar;

import jp.co.sae.android.banana.Common;
import jp.co.sae.android.banana.common.LogView;
import jp.co.sae.android.banana.database.DatabaseAccess;
import jp.co.sae.android.banana.widget.LockWidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ControlReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(Common.Timer.ACTION_START)){
			setNextAlarm(context);
		} else if (action.equals(Common.Timer.ACTION_STOP)){
			removeAlarm(context);
		} else if (action.equals(Common.Timer.ACTION_CONTROL)){
			DatabaseAccess access = new DatabaseAccess(context, true);
			
			if (access.getUseTimeDis()){
				return;
			}
			
			boolean[] check = access.getUseTime();
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			intent = new Intent();
			intent.setClass(context, SencerService.class);
			
			LogView.v("hour:" + hour + " start:" + check[hour]);
			
			if (check[hour]){
				context.startService(intent);
				access.setLock(true);
				
				intent = new Intent(LockWidget.LOCK_ON_ACTION);
				context.sendBroadcast(intent);
			} else {
				context.stopService(intent);
				access.setLock(false);
				
				intent = new Intent(LockWidget.LOCK_OFF_ACTION);
				context.sendBroadcast(intent);
			}
			
			setNextAlarm(context);
		} else if (action.equals(Intent.ACTION_TIME_CHANGED)){
			setNextAlarm(context);
		} /*else if (action.equals(Intent.ACTION_DATE_CHANGED)){
			setNextAlarm(context);
		} */else if (action.equals(Intent.ACTION_TIMEZONE_CHANGED)){
			setNextAlarm(context);
		}
	}
	
	private void setNextAlarm(Context context){
		Intent intent = new Intent();
		intent.setAction(Common.Timer.ACTION_CONTROL);
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		AlarmManager manager = (AlarmManager)
				context.getSystemService(Context.ALARM_SERVICE);
		
		manager.set(
				AlarmManager.RTC_WAKEUP, 
				System.currentTimeMillis() + getNextHour(), 
				pendingIntent);
	}
	
	private void removeAlarm(Context context){
		Intent intent = new Intent();
		intent.setAction(Common.Timer.ACTION_CONTROL);
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		AlarmManager manager = (AlarmManager)
				context.getSystemService(Context.ALARM_SERVICE);
		
		manager.cancel(pendingIntent);
	}
	
	private int getNextHour(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis() + 1);
		
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		
		int current = (minute * 60 + second) * 1000;
		
		return (int) (AlarmManager.INTERVAL_HOUR - current);
	}

}
