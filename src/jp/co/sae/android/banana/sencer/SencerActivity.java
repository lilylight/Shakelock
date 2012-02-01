package jp.co.sae.android.banana.sencer;

import jp.co.sae.android.banana.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;

public class SencerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
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
		
		LinearLayout bottomLayout = new LinearLayout(this);
		bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
		bottomLayout.setLayoutParams(new LinearLayout.LayoutParams
				(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		overLayout.addView(bottomLayout);
		
		Button infoButton = new Button(this);
		infoButton.setId(R.string.Back);
		infoButton.setText(R.string.Back);
		infoButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		infoButton.setLayoutParams(new LinearLayout.LayoutParams
				(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		bottomLayout.addView(infoButton);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
