package jp.co.sae.android.banana.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import android.content.Context;

/**
 * @author naraoka
 * ステータスバー表示クラス
 */
public class StatusBarManager {
	
	private Context context;

	public StatusBarManager(Context context)  {
		this.context = context;
	}
	
	/**
	 * ステータスバー表示
	 */
	public void expand() {
		try {
			Class<?> cls = context.getClassLoader().loadClass(
			"android.app.StatusBarManager");
			Constructor<?> con = cls.getDeclaredConstructor(
					new Class[]{Context.class});
			con.setAccessible(true);
			Method method = cls.getMethod("expand", new Class[0]);
			method.invoke(con.newInstance(new Object[]{context}), new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
