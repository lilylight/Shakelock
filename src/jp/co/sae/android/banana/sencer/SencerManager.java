package jp.co.sae.android.banana.sencer;

import java.util.List;

import jp.co.sae.android.banana.Common;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

public class SencerManager implements SensorEventListener {

	/** 生成元コンテキスト */
	private Context mContext = null;
	/** メッセージ送信先ハンドラ */
	private Handler mHandler = null;
	/** センサマネージャ */
	private SensorManager mSensorManager = null;
	/** 加速度センサ */
	private Sensor mAccelerate = null;
	
	private float shakeFort = 0.0f;
	
	private float[] currentOrientationValues = {0.0f, 0.0f, 0.0f};
	private float[] currentAccelerationValues = {0.0f, 0.0f, 0.0f};
	
	//private ArrayList<Float> valueArray = new ArrayList<Float>();
	
	private boolean waitFlag = false;
	
	private Handler processHandler = new Handler();
	
	private Runnable processRunnable = new Runnable() {
		public void run() {
			waitFlag = false;
		}
	};
	
	//private int currentRotation = 0;

	/**
	 * コンストラクタ
	 * @param c	コンテキスト
	 * @param h	ハンドラ
	 */
	SencerManager(Context c, Handler h) {
		mContext = c;
		mHandler = h;

		// センサーマネージャの取得
		mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
	}

	/**
	 * センサ検出開始
	 */
	public void startScan() {
		List<Sensor> list;
		// 加速度センサーの取得
		list = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (list.size() > 0) {
			mAccelerate = list.get(0);
		}
		
		waitFlag = true;
		processHandler.postDelayed(processRunnable, 2000);
		
		if (mAccelerate != null) {
			mSensorManager.registerListener(this, mAccelerate,SensorManager.SENSOR_DELAY_NORMAL);
		}
	}
	
	/**
	 * シェイク検出値設定
	 * @param fort 検出値
	 */
	public void setShakeForce(int fort){
		shakeFort = fort;
	}
	
	/**
	 * シェイク検出値取得
	 * @return 検出値
	 */
	public float getShakeForce(){
		return shakeFort;
	}

	/**
	 * センサ検出停止
	 */
	public void stopScan() {
		mSensorManager.unregisterListener(this);
		mAccelerate = null;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// 加速度の取得
		if (event.sensor == mAccelerate) {
			// シェイク検出
			currentOrientationValues[0] = event.values[0] * 0.1f + currentOrientationValues[0] * (1.0f - 0.1f);
			currentOrientationValues[1] = event.values[1] * 0.1f + currentOrientationValues[1] * (1.0f - 0.1f);
			currentOrientationValues[2] = event.values[2] * 0.1f + currentOrientationValues[2] * (1.0f - 0.1f);

			currentAccelerationValues[0] = event.values[0] - currentOrientationValues[0];
			currentAccelerationValues[1] = event.values[1] - currentOrientationValues[1];
			currentAccelerationValues[2] = event.values[2] - currentOrientationValues[2];

			float targetValue = Math.abs(currentAccelerationValues[0])
					+ Math.abs(currentAccelerationValues[1])
					+ Math.abs(currentAccelerationValues[2]);

			if (targetValue > shakeFort) {
				if (false == waitFlag) {
					//valueArray.clear();
					//valueArray.add(targetValue);
					waitFlag = true;
					executeShake();
					processHandler.postDelayed(processRunnable, 2000);
				} 
				//else {
				//	valueArray.add(targetValue);
				//}
			}

			/*// 回転検出
			int rotation = currentRotation;
			if (Math.abs(currentOrientationValues[0]) > 7.0f) {
				// 横
				rotation = 1;
			} else if (Math.abs(currentOrientationValues[1]) > 7.0f) {
				// 縦
				rotation = 2;
			} else if (Math.abs(currentOrientationValues[2]) > 7.0f) {
				// 水平
				rotation = 3;
			} else {
				// ?
			}
			if (currentRotation != rotation) {
				//mHandler.sendMessage(mHandler.obtainMessage(ThreadMessages.Sencer.ROTATION));
				//Log.v("Sensor", "ROTATION");
			}
			currentRotation = rotation;*/
		}
	}

	private void executeShake() {
		//Collections.sort(valueArray);
		//float result = valueArray.get(valueArray.size() - 1);
		//if (result > 25.0f) {
			//mHandler.sendMessage(mHandler.obtainMessage(ThreadMessages.Sencer.SHAKE_STRONGLY));
			//Log.v("Sensor", "shake is STONGLY");
		//} else {
			mHandler.sendMessage(mHandler.obtainMessage(Common.Sencer.SHAKE_GENTLY));
		//}
	}
}
