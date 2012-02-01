package jp.co.sae.android.banana.database;

import jp.co.sae.android.banana.common.LogView;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;

public class DatabaseAccess {

	/** コンテキスト */
	private Context 			context;

	/** ロック */
	private static boolean		lock;
	/** キーガード */
	private static boolean		keyguard;
	/** 自動起動  */
	private static boolean		autoRun;
	/** シェイク強度 */
	private static int			shake;
	/** 自動消灯時間 */
	private static int 			timeout;
	/** 使用時間  */
	private static boolean[]	useTime;
	/** 使用時間無効  */
	private static boolean		useTimeDis;
	
	private static final int	TIME_COUNT = 24;

	/**
	 * コンストラクタ
	 * @param context コンテキスト
	 * @param getPreference	初期設定(true=取得する,false=取得しない)
	 */
	public DatabaseAccess(Context context, boolean getPreference){
		this.context = context;

		if(getPreference){
			readTable();
		}
	}

	/**
	 * テーブル読み込み
	 */
	public void readTable() {
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(
	        		DatabasePreferences.BananaTable.TABLE_URI,
	        		new String[]{"*"},
	        		BaseColumns._ID + " = 1", null, null);
		} catch (Exception e) {
			LogView.e(e.toString());
			return;
		}

		cursor.moveToFirst();

		lock		= Boolean.parseBoolean(cursor.getString(1));
		keyguard	= Boolean.parseBoolean(cursor.getString(2));
		autoRun		= Boolean.parseBoolean(cursor.getString(3));
		shake		= cursor.getInt(4);
		timeout		= cursor.getInt(5);
		
		StringBuilder builder = new StringBuilder();
		builder.setLength(0);
		builder.append(cursor.getString(6));
		
		boolean[] check = new boolean[TIME_COUNT];
		
		for(int i = 0; i < TIME_COUNT; i++){
			char c = builder.charAt(i);
			switch (c) {
			case '0':
				check[i] = false;
				break;
				
			case '1':
				check[i] = true;
				break;

			default:
				break;
			}
		}
		useTime = check;
		
		useTimeDis	= Boolean.parseBoolean(cursor.getString(7));

		cursor.deactivate();
		cursor.close();
		cursor = null;
	}

	/**
	 * ロック設定
	 * @param lock ロック
	 */
	public void setLock(boolean lock){
		updateTable(DatabasePreferences.BananaTable.LOCK, String.valueOf(lock));
	}
	/**
	 * キーガード設定
	 * @param key キーガード
	 */
	public void setKeyguard(boolean key){
		updateTable(DatabasePreferences.BananaTable.KEYGUARD, String.valueOf(key));
	}
	/**
	 * 自動起動設定
	 * @param auto 起動（true=自動／false=手動）
	 */
	public void setAutoRun(boolean auto){
		updateTable(DatabasePreferences.BananaTable.AUTO_RUN, String.valueOf(auto));
	}
	/**
	 * シェイク強度設定
	 * @param str 強度
	 */
	public void setShake(int str){
		updateTable(DatabasePreferences.BananaTable.SHAKE, String.valueOf(str));
	}
	/**
	 * 自動消灯時間設定
	 * @param time 時間
	 */
	public void setTimeout(int time){
		updateTable(DatabasePreferences.BananaTable.TIMEOUT, String.valueOf(time));
	}
	/**
	 * 使用時間設定
	 * @param checkedItems アイテム
	 */
	public void setUseTime(boolean[] checkedItems){
		StringBuffer buffer = new StringBuffer();
		buffer.setLength(0);
		
		for(int i = 0; i < TIME_COUNT; i++){
			if (checkedItems[i]){
				buffer.append('1');
			} else {
				buffer.append('0');
			}
		}
		
		LogView.v(buffer.toString());
		updateTable(DatabasePreferences.BananaTable.USE_TIME, buffer.toString());
	}
	/**
	 * 使用時間無効設定
	 * @param dis 使用設定（true=無効／false=有効）
	 */
	public void setUseTimeDis(boolean dis){
		updateTable(DatabasePreferences.BananaTable.USE_TIME_DIS, String.valueOf(dis));
	}

	/** ロック設定取得 */
	public boolean getLock(){
		return lock;
	}
	/** キーガード設定取得 */
	public boolean getKeyguard(){
		return keyguard;
	}
	/** 自動起動設定取得 */
	public boolean getAutoRun(){
		return autoRun;
	}
	/** シェイク強度設定取得 */
	public int getShake(){
		return shake;
	}
	/** 自動消灯時間設定取得 */
	public int getTimeout(){
		return timeout;
	}
	/** 使用時間設定取得 */
	public boolean[] getUseTime(){
		return useTime;
	}
	/** 使用時間無効設定取得 */
	public boolean getUseTimeDis(){
		return useTimeDis;
	}

	/**
	 * 設定更新パラメータ設定
	 * @param AttributeName 属性名
	 * @param object 設定値
	 */
	private void updateTable(String AttributeName, String object){
		ContentValues values = new ContentValues();

		values.put(AttributeName, object);

		context.getContentResolver().update(
				DatabasePreferences.BananaTable.TABLE_URI,
				values, BaseColumns._ID + " = 1", null);

		readTable();
	}
}
