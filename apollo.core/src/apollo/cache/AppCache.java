package apollo.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
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
			ByteArrayOutputStream bos = null;
			ObjectOutputStream oos = null;
			byte[] data = null;

			bos = new ByteArrayOutputStream();
			try {
				oos = new ObjectOutputStream(bos);
				oos.writeObject(value);
				oos.flush();
				data = bos.toByteArray();
			} catch (Exception ex) {
				Log.e("AppCache.add", ex.getMessage());
			} finally {
				if (oos  != null) {try{oos.close();}catch(Exception ex){}}
				if (bos  != null) {try{bos.close();}catch(Exception ex){}}
			}
			
			if (data != null) {
				FileUtil.saveFile("data", name, data);
			}
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
