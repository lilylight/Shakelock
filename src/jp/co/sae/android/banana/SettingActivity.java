package jp.co.sae.android.banana;

import jp.co.sae.android.banana.common.LogView;
import jp.co.sae.android.banana.database.DatabaseAccess;
import jp.co.sae.android.banana.sencer.ISencerService;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;

public class SettingActivity extends PreferenceActivity 
	implements OnPreferenceClickListener, OnClickListener, OnMultiChoiceClickListener {
	
	private ISencerService sencerServiceIf;
	
	private AlertDialog shakeDialog;
	
	private AlertDialog offTimeDialog;
	
	private AlertDialog useTimeDialog;
	
	private DatabaseAccess mAccess;
	
	private boolean[] checked;
	
	private boolean[] subchecked;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mAccess = new DatabaseAccess(this, true);
		
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
		
		// 設定メニュー画面生成
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);
        screen.setTitle(R.string.SettingButton);
        setPreferenceScreen(screen);
        
        CheckBoxPreference keyguardBox = new CheckBoxPreference(this);
        keyguardBox.setTitle(R.string.KEYGUARD_BOX_TITLE);
        keyguardBox.setSummary(R.string.KEYGUARD_BOX_SUMMARY);
        keyguardBox.setKey(Initialization.KEYGUARD_KEY);
        keyguardBox.setChecked(mAccess.getKeyguard());
        keyguardBox.setOnPreferenceClickListener(this);
        screen.addPreference(keyguardBox);
        
        CheckBoxPreference autorunBox = new CheckBoxPreference(this);
        autorunBox.setTitle(R.string.AUTO_RUN_BOX_TITLE);
        autorunBox.setSummary(R.string.AUTO_RUN_BOX_SUMMARY);
        autorunBox.setKey(Initialization.AUTORUN_KEY);
        autorunBox.setChecked(mAccess.getAutoRun());
        autorunBox.setOnPreferenceClickListener(this);
        screen.addPreference(autorunBox);
        
        Preference setShake = new Preference(this);
        setShake.setTitle(R.string.SHAKE_DIALOG_TITLE);
        setShake.setSummary(R.string.SHAKE_DIALOG_SUMMARY);
        setShake.setKey(Initialization.SHAKE_KEY);
        setShake.setOnPreferenceClickListener(this);
        screen.addPreference(setShake);
        
        shakeDialog = createDialog(R.string.SHAKE_DIALOG_TITLE, 
        		Initialization.SHAKE_STRING, mAccess.getShake());
        
        Preference setOffTime = new Preference(this);
        setOffTime.setTitle(R.string.TIMEOUT_DIALOG_TITLE);
        setOffTime.setSummary(R.string.TIMEOUT_DIALOG_SUMMARY);
        setOffTime.setKey(Initialization.TIMEOUT_KEY);
        setOffTime.setOnPreferenceClickListener(this);
        screen.addPreference(setOffTime);
        
		offTimeDialog = createDialog(R.string.TIMEOUT_DIALOG_TITLE, 
				Initialization.TIMES_STRING, mAccess.getTimeout());
		
		CheckBoxPreference setUseTimeDis = new CheckBoxPreference(this);
		setUseTimeDis.setChecked(mAccess.getUseTimeDis());
        setUseTimeDis.setTitle(R.string.USE_TIME_DIS_DIALOG_TITLE);
        setUseTimeDis.setSummary(R.string.USE_TIME_DIS_DIALOG_SUMMARY);
        setUseTimeDis.setKey(Initialization.USE_TIME_DIS_KEY);
        setUseTimeDis.setDisableDependentsState(true);
        setUseTimeDis.setOnPreferenceClickListener(this);
        screen.addPreference(setUseTimeDis);
        
        PreferenceScreen setUseTime = getPreferenceManager().createPreferenceScreen(this);
		//Preference setOnTime = new Preference(this);
		setUseTime.setTitle(R.string.TIME_CLOCK_DIALOG_TITLE);
		setUseTime.setSummary(R.string.TIME_CLOCK_DIALOG_SUMMARY);
		setUseTime.setKey(Initialization.TIME_CLOCK_KEY);
		setUseTime.setOnPreferenceClickListener(this);
		setUseTime.setDependency(Initialization.USE_TIME_DIS_KEY);
        screen.addPreference(setUseTime);
        
        checked = mAccess.getUseTime();
        
		Intent intent = new Intent(ISencerService.class.getName());
		bindService(intent, serviceConnection, 0);
	}
	
	private AlertDialog createDialog(int title, String[] strings, int position){
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(title);
		dialogBuilder.setNeutralButton(R.string.Cancel, this);
		dialogBuilder.setSingleChoiceItems(strings, position, this);
		
		return dialogBuilder.create();
	}
	
	@Override
	public void onPause(){
		super.onPause();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		mAccess = null;
		unbindService(serviceConnection);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		
		if (preference.getKey().equals(Initialization.KEYGUARD_KEY)){
			boolean check = ((CheckBoxPreference)preference).isChecked();
			try {
				if (sencerServiceIf != null){
					sencerServiceIf.setKeyGuard(check);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			mAccess.setKeyguard(check);
		} else if (preference.getKey().equals(Initialization.AUTORUN_KEY)){
			boolean check = ((CheckBoxPreference)preference).isChecked();
			mAccess.setAutoRun(check);
		} else if (preference.getKey().equals(Initialization.SHAKE_KEY)) {
			shakeDialog.show();
		} else if (preference.getKey().equals(Initialization.TIMEOUT_KEY)){
			offTimeDialog.show();
		} else if (preference.getKey().equals(Initialization.USE_TIME_DIS_KEY)){
			boolean check = ((CheckBoxPreference)preference).isChecked();
			mAccess.setUseTimeDis(check);
			if (check){
				Intent intent = new Intent();
				intent.setAction(Common.Timer.ACTION_STOP);
				sendBroadcast(intent);
			} else {
				Intent intent = new Intent();
				intent.setAction(Common.Timer.ACTION_START);
				sendBroadcast(intent);
			}
		} else if (preference.getKey().equals(Initialization.TIME_CLOCK_KEY)){
			subchecked = checked.clone();
			
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
	        dialogBuilder.setTitle(R.string.TIME_CLOCK_DIALOG_TITLE);
	        dialogBuilder.setNeutralButton(R.string.Cancel, this);
	        dialogBuilder.setPositiveButton(R.string.OK, this);
	        dialogBuilder.setMultiChoiceItems(
	        		Initialization.TIME_CLOCK_STRING, subchecked, this);
	        useTimeDialog = dialogBuilder.create();
			useTimeDialog.show();
		}
		
		return false;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		LogView.v("which:" + which);
		switch (which) {
		case DialogInterface.BUTTON_NEUTRAL:
			
			break;

		default:
			if (dialog.equals(shakeDialog)){
				try {
					if (sencerServiceIf != null){
						sencerServiceIf.setShakeForce(Initialization.SHAKE_VALUE[which]);
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
				// データベースへの書き込み
				mAccess.setShake(which);
				
				shakeDialog.dismiss();
			}
			
			if (dialog.equals(offTimeDialog)){
				try {
					if (sencerServiceIf != null){
						sencerServiceIf.setOffTime(Initialization.TIMES_VALUE[which]);
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
				// データベースへの書き込み
				mAccess.setTimeout(which);
				
				offTimeDialog.dismiss();
			}
			
			if (dialog.equals(useTimeDialog)){
				checked = subchecked;
				mAccess.setUseTime(checked);
			}
			
			break;
		}
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		//subchecked[which] = isChecked;
	}

	/**
     * Serviceに接続･切断したときに呼ばれる
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			sencerServiceIf = ISencerService.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			sencerServiceIf = null;
		}
    	
    };
}
