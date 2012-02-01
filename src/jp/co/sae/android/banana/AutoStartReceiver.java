package jp.co.sae.android.banana;

import jp.co.sae.android.banana.database.DatabaseAccess;
import jp.co.sae.android.banana.sencer.SencerService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStartReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			DatabaseAccess mAccess = new DatabaseAccess(context, true);
			
			if (mAccess.getAutoRun() == true){
				intent = new Intent(context, SencerService.class);
				context.startService(intent);
				mAccess.setLock(true);
			}
			mAccess = null;
		}
	}
}
