package jp.co.sae.android.banana;

public final class Common {

	public static final String PACKAGE_NAME = "jp.co.sae.android.banana";
	
	/**
	 * センサ関連
	 */
	public static final class Sencer {
		/** シェイク強 */
		public static final int SHAKE_STRONGLY = 0x7f010001;
		/** シェイク弱 */
		public static final int SHAKE_GENTLY = 0x7f010002;
		/** 回転 */
		public static final int ROTATION = 0x7f010003;
	}
	
	/**
	 * タイマ関連
	 */
	public static final class Timer {
		/** 消灯 */
		public static final int LIGHT_OFF_TIMER = 0x7f010004;
		/** 起動制御開始 */
		public static final String  ACTION_START = PACKAGE_NAME + ".start";
		/** 起動制御停止 */
		public static final String  ACTION_STOP = PACKAGE_NAME + ".stop";
		/** 起動制御受信 */
		public static final String  ACTION_CONTROL = PACKAGE_NAME + ".control";
	}
}
