package jp.co.sae.android.banana.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import android.os.IBinder;

public class ServiceManager {
	
	public ServiceManager() {}
	
	/**
	 * サービス取得
	 * @param name サービス名
	 * @return サービス
	 */
	public static IBinder getService(String name) {
		
		IBinder binder = null;
		
		try {
			Class<?> cls = getClassLoader().loadClass("android.os.ServiceManager");
			Constructor<?> con = cls.getDeclaredConstructor(new Class[0]);
			con.setAccessible(true);
			Method method = cls.getMethod("getService", new Class[]{String.class});
			binder = (IBinder)method.invoke(
					con.newInstance(new Object[0]), new Object[]{name});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return binder;
	}
	
	private static final ClassLoader getClassLoader() {
		return ServiceManager.class.getClassLoader();
	}

}
