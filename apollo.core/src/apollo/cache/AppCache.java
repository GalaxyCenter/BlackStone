package apollo.cache;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import android.support.v4.util.LruCache;
import android.util.Log;
import apollo.util.CompatibleUtil;
import apollo.util.FileUtil;
import apollo.util.StringUtil;

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
		String name = null;
		
		name = StringUtil.getMD5Str(key);
		if (FileUtil.exists("data", name) == false) {
			FileUtil.saveFile("data", name, value);
		}
		mCache.put(key, value);
	}

	public static void remove(String key) {
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
				
				data = FileUtil.getFileData("data", name);
				obj = FileUtil.data2Object(data);			
			}
		}
		return obj;
	}
}
