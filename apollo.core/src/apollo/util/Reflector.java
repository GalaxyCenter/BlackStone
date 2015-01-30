package apollo.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class Reflector {

	public static Field getDeclaredField(Object obj, String name) {
		Field field = null;
		Class<?> clazz = null;

		clazz = obj.getClass();
		try {
			field = clazz.getDeclaredField(name);
			field.setAccessible(true);
		} catch (Exception ex) {
		}
		return field;
	}

	public static Object getDeclaredFieldRef(Object obj, String name) {
		Field field = null;
		Object ref = null;

		field = getDeclaredField(obj, name);
		try {
			ref = field.get(obj);
		} catch (Exception ex) {
		}
		return ref;
	}
}
