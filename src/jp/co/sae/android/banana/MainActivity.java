package jp.co.sae.android.banana;

import jp.co.sae.android.banana.common.InfoDialog;
import jp.co.sae.android.banana.common.LogView;
import jp.co.sae.android.banana.database.DatabaseAccess;
import jp.co.sae.android.banana.sencer.SencerService;
import jp.co.sae.android.banana.widget.LockWidget;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView.ScaleType;

public class MainActivity extends Activity implements 
			View.OnClickListener, OnCheckedChangeListener{

	private DatabaseAccess mAccess;
	
	private ImageView mImageView;
	
	/* (非 Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// ディスプレイの情報を取得
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        float density = displayMetrics.scaledDensity;
		
		mAccess = new DatabaseAccess(this, true);
		
		FrameLayout baseLayout = new FrameLayout(this);
		setContentView(baseLayout);
		
		LinearLayout underLayout = new LinearLayout(this);
		underLayout.setGravity(Gravity.CENTER);
		baseLayout.addView(underLayout);
		
		ImageView backImage = new ImageView(this);
		backImage.setScaleType(ScaleType.CENTER);
		backImage.setImageResource(R.drawable.android_wallpaper);
		underLayout.addView(backImage);
		
		LinearLayout overLayout = new LinearLayout(this);
		overLayout.setOrientation(LinearLayout.VERTICAL);
		baseLayout.addView(overLayout);
		
		LinearLayout topLayout = new LinearLayout(this);
		topLayout.setOrientation(LinearLayout.VERTICAL);
		topLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		topLayout.setLayoutParams(new LinearLayout.LayoutParams
				(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT, 1));
		overLayout.addView(topLayout);
		
		ImageView shakelock =  new ImageView(this);
		shakelock.setImageResource(R.drawable.shakelock);
		/*shakelock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, SencerActivity.class);
				startActivity(intent);
			}
		});*/
		topLayout.addView(shakelock);
		
		mImageView = new ImageView(this);
		mImageView.setPadding(0, (int) (20 * density), 0, (int) (20 * density));
		topLayout.addView(mImageView);
		
		if (mAccess.getLock()){
			mImageView.setImageResource(R.drawable.ic_jog_dial_unlock);
		} else {
			mImageView.setImageResource(R.drawable.ic_jog_dial_lock);
		}
		
		ToggleButton lockButton = new ToggleButton(this);
		lockButton.setTextOn(getString(R.string.UnLock));
		lockButton.setTextOff(getString(R.string.Lock));
		lockButton.setChecked(mAccess.getLock());
		lockButton.setTextSize(20);
		lockButton.setWidth((int) (100 * density));
		lockButton.setHeight((int) (100 * density));
		lockButton.setOnCheckedChangeListener(this);
		lockButton.setLayoutParams(new LinearLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		topLayout.addView(lockButton);
		
		LinearLayout bottomLayout = new LinearLayout(this);
		bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
		bottomLayout.setLayoutParams(new LinearLayout.LayoutParams
				(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		overLayout.addView(bottomLayout);
		
		Button infoButton = new Button(this);
		infoButton.setId(R.string.InfoButton);
		infoButton.setText(R.string.InfoButton);
		infoButton.setOnClickListener(this);
		infoButton.setLayoutParams(new LinearLayout.LayoutParams
				(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT, 1));
		bottomLayout.addView(infoButton);
		
		Button settingButton = new Button(this);
		settingButton.setId(R.string.SettingButton);
		settingButton.setText(R.string.SettingButton);
		settingButton.setOnClickListener(this);
		settingButton.setLayoutParams(new LinearLayout.LayoutParams
				(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT, 1));
		bottomLayout.addView(settingButton);
	}
	
	@Override
	public void onPause(){
		super.onPause();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		mAccess = null;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.string.InfoButton:
			String version = null;
	        try {
				PackageManager packageManager = getPackageManager();
				PackageInfo packageInfo = packageManager.getPackageInfo(Common.PACKAGE_NAME, 0);
				version = packageInfo.versionName;
			} catch (NameNotFoundException e) {
				LogView.e(e.toString());
				version = "0.0";
			}
			InfoDialog dialog = new InfoDialog(this);
			dialog.show("Version " + version, R.array.InfoDialog);
			break;
			
		case R.string.SettingButton:
			Intent intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Intent intent = new Intent(this, SencerService.class);
		
		if (isChecked){
			startService(intent);
			
			intent = new Intent(LockWidget.LOCK_ON_ACTION);
			sendBroadcast(intent);
			
			mImageView.setImageResource(R.drawable.ic_jog_dial_unlock);
		} else {
			stopService(intent);
			
			intent = new Intent(LockWidget.LOCK_OFF_ACTION);
			sendBroadcast(intent);
			
			mImageView.setImageResource(R.drawable.ic_jog_dial_lock);
		}
		
		mAccess.setLock(isChecked);
	}
}