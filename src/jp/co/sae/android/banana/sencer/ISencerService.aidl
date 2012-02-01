package jp.co.sae.android.banana.sencer;

/**
 * サービスのインターフェイス.
 */
interface ISencerService {
	
	/** パッケージ名取得 */
	String getPname();
	
	/** シェイク強度設定 */
	void setShakeForce(int force);
	
	/** 自動消灯時間設定 */
	void setOffTime(int time);
	
	/** キーガードの設定 */
	void setKeyGuard(boolean key);
	
}