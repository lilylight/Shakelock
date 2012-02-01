package jp.co.sae.android.banana.widget;

import jp.co.sae.android.banana.Common;
import jp.co.sae.android.banana.R;
import jp.co.sae.android.banana.common.LogView;
import jp.co.sae.android.banana.database.DatabaseAccess;
import jp.co.sae.android.banana.sencer.SencerService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class LockWidget extends AppWidgetProvider {
	
	public static final String LOCK_ON_ACTION = Common.PACKAGE_NAME + ".on";
	
	public static final String LOCK_OFF_ACTION = Common.PACKAGE_NAME + ".off";
	
	private static final String WIDGET_CLICK_ACTION = Common.PACKAGE_NAME + ".click";
	
	private static DatabaseAccess mAccess = null;
	
	private static boolean isLocked = false;
	
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		LogView.v("onDisabled");
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds){
		super.onDeleted(context, appWidgetIds);
		LogView.v("onDeleted");
	}
	
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		LogView.v("onEnabled");
	}
	
	@Override
	public void onReceive(Context context, Intent intent){
		super.onReceive(context, intent);
		LogView.v("onReceive");
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		LogView.v("onUpdate");
		
		mAccess = new DatabaseAccess(context, true);
		isLocked = mAccess.getLock();
		
		if (isLocked){
			widgetUpdate(context, R.drawable.btn_toggle_on, R.string.UnLock);
		} else {
			widgetUpdate(context, R.drawable.btn_toggle_off, R.string.Lock);
		}
	}

	private static void widgetUpdate(Context context, int imageId, int textId) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_lock);

		Intent clickIntent = new Intent(WIDGET_CLICK_ACTION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 画像部分のクリックを有効とする
		remoteViews.setOnClickPendingIntent(R.id.LockImageButton, pendingIntent);
		
		if (imageId != 0){
			remoteViews.setImageViewResource(R.id.LockImageButton, imageId);
		}
		
		if (textId != 0){
			remoteViews.setTextViewText(R.id.LockTextView, context.getString(textId));
		}

		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		ComponentName thisWidget = new ComponentName(context, LockWidget.class);
		manager.updateAppWidget(thisWidget, remoteViews);
	}

	/**
	 * ブロードキャストレシーバ
	 */
	public static class LockWidgetReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			LogView.v("onReceive:" + intent.getAction());
			
			if (intent.getAction().equals(WIDGET_CLICK_ACTION)) {
				intent = new Intent(context, SencerService.class);
				
				if (mAccess == null){
					mAccess = new DatabaseAccess(context, true);
				}
				
				if (isLocked){
					context.stopService(intent);
					mAccess.setLock(false);
					
					widgetUpdate(context, R.drawable.btn_toggle_off, R.string.Lock);
					isLocked = false;
				} else {
					context.startService(intent);
					mAccess.setLock(true);
					
					widgetUpdate(context, R.drawable.btn_toggle_on, R.string.UnLock);
					isLocked = true;
				}
			} else if (intent.getAction().equals(LOCK_ON_ACTION)){
				widgetUpdate(context, R.drawable.btn_toggle_on, R.string.UnLock);
				isLocked = true;
			} else if (intent.getAction().equals(LOCK_OFF_ACTION)){
				widgetUpdate(context, R.drawable.btn_toggle_off, R.string.Lock);
				isLocked = false;
			}
		}
	}
}
