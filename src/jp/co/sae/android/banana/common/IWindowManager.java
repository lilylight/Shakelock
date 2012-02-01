package jp.co.sae.android.banana.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import android.os.IBinder;
import android.view.KeyEvent;

public class IWindowManager extends Object {
	
	private IBinder binder;
	private Class<?> cls;
	
	public IWindowManager() {
		try {
			binder = ServiceManager.getService("window");
			cls = IWindowManager.Stub.asInterface(binder).getClass();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * キーイベント発生
	 * @param ev キーイベント
	 * @param sync 
	 * @return
	 */
	public boolean injectKeyEvent(KeyEvent ev, boolean sync) {
		
		boolean key = false;
		
		try {
			Method method = cls.getMethod("injectKeyEvent", 
					new Class[]{KeyEvent.class, boolean.class});
			Constructor<?> con = cls.getDeclaredConstructor(new Class[]{IBinder.class});
			con.setAccessible(true);
			key = (Boolean)method.invoke(con.newInstance(
					new Object[]{binder}), new Object[]{ev, sync});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return key;
	}
	
	private static class Stub {
		
		public static Object asInterface(IBinder obj) throws Exception {
			Class<?> cls = getClassLoader().loadClass("android.view.IWindowManager$Stub");
			Method method = cls.getMethod("asInterface", new Class[]{IBinder.class});
			return method.invoke(null, new Object[]{obj});
		}
		
	}
	
	private static final ClassLoader getClassLoader() {
		return IWindowManager.class.getClassLoader();
	}

}
