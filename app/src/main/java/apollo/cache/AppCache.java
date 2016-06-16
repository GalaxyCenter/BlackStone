package apollo.cache;

import java.io.Serializable;

import android.support.v4.util.LruCache;
import apollo.util.CompatibleUtil;
import apollo.util.DateTime;
import apollo.util.FileUtil;
import apollo.util.StringUtil;

class CachedObject implements Serializable {
	private static final long serialVersionUID = 1066906079017549460L;
	public DateTime expiration;
	public Object ref;
}

public class AppCache {
		
	private static LruCache<String, Object> mCache;

	private AppCache() {
	}

	static {
		int maxMem = CompatibleUtil.getMaxMemory();
		int cacheSize = maxMem / 8;

		mCache = new LruCache<String, Object>(cacheSize);
	}

	public static void add(String key, Object value) {
		add(key, value, true);
	}
	
	public static void add(String key, Object value, boolean persistent) {
		add(key, value, persistent ? 3600 : 0);
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @param seconds 可持久化时间
	 */
	public static void add(String key, Object value, int seconds) {
		String name = null;
		
		name = StringUtil.getMD5Str(key);
		if (seconds != 0 && FileUtil.exists("data", name) == false) {
			CachedObject obj = new CachedObject();
			obj.ref = value;
			obj.expiration = DateTime.now().addSeconds(seconds);
			
			FileUtil.saveFile("data", name, obj);
		}
		mCache.put(key, value);
	}

	public static void remove(String key) {
		String name = null;

		name = StringUtil.getMD5Str(key);
		FileUtil.deleteFile("data", name);

		mCache.remove(key);
	}

	public static void clear() {
		mCache.evictAll();
	}

	public static Object get(String key) {
		Object obj = null;

		obj = mCache.get(key);
		if (obj == null) {
			String name = null;
			
			name = StringUtil.getMD5Str(key);
			if (FileUtil.exists("data", name) == true) {
				byte[] data = null;
				CachedObject  cache_obj = null;
				
				data = FileUtil.getFileData("data", name);
				cache_obj = (CachedObject)FileUtil.data2Object(data);	
				if (DateTime.diff(DateTime.now(), cache_obj.expiration) < 0) 
					obj = cache_obj.ref;
				else
					remove(key);
			}
		}
		return obj;
	}
}
