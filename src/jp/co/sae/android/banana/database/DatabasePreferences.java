package jp.co.sae.android.banana.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabasePreferences extends SQLiteOpenHelper {
	
	/** データベース名 */
	public static final String DB_NAME = "banana.db";
	
	/** パッケージ名 */
	public static final String AUTHORITIES = "jp.co.sae.android.banana";
	
	/** データベースバージョン */
	public static final int DB_VERSION = 2;
	
	public static final class BananaTable implements BaseColumns {
		
		/** テーブル名 */
		public static final String TABLE_NAME = "Banana";
		
		/** テーブルを示すURI */
		public static final Uri TABLE_URI = Uri.parse("content://" + AUTHORITIES + "/" + TABLE_NAME);
		
		/** ロック */
		public static final String LOCK = "lock";
		/** キーガード */
		public static final String KEYGUARD = "keyguard";
		/** 自動起動 */
		public static final String AUTO_RUN = "auto_run";
		/** シェイク強度 */
		public static final String SHAKE = "shake";
		/** 自動消灯時間 */
		public static final String TIMEOUT = "timeout";
		/** 使用時間 */
		public static final String USE_TIME = "use_time";
		/** 使用時間無効 */
		public static final String USE_TIME_DIS = "use_time_dis";
	}
	
	/**
	 * コンストラクタ
	 * @param context コンテキスト
	 */
	DatabasePreferences(Context context) {
		super(context, DB_NAME, null, DB_VERSION );
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(
				"create table if not exists " + BananaTable.TABLE_NAME +
				"(" + BaseColumns._ID + " integer primary key autoincrement," +
				BananaTable.LOCK + " text default 'false'," +
				BananaTable.KEYGUARD + " text default 'false'," +
				BananaTable.AUTO_RUN + " text default 'false'," +
				BananaTable.SHAKE + " text default '2'," +
				BananaTable.TIMEOUT + " text default '0'," +
				BananaTable.USE_TIME + " text default '111111111111111111111111'," +
				BananaTable.USE_TIME_DIS + " text default 'true')");
		
		db.execSQL("insert into " + BananaTable.TABLE_NAME + " default values");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// すでにテーブルが存在していたら削除
		db.execSQL("drop table if exists " + BananaTable.TABLE_NAME);
		onCreate(db);
	}
}
