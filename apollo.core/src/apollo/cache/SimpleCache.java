package apollo.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import apollo.util.DateTime;

class CachedObject {
	public DateTime expiration;
	public Object ref;
}

class SimpleCache implements ICache {
	private Map<String, CachedObject> cache;

	public SimpleCache() {
		cache = new HashMap<String, CachedObject>();
	}

	public void add(String key, Object value) {
		this.add(key, value, ICache.MINUTE_FACTOR);
	}

	public void add(String key, Object value, int seconds) {
		CachedObject obj = new CachedObject();
		obj.ref = value;
		obj.expiration = DateTime.now().addSeconds(seconds);
		this.cache.put(key, obj);
	}

	public void add(String key, String group, Object value) {
		add(key, group, value, ICache.MINUTE_FACTOR);
	}

	public void add(String key, String group, Object value, int seconds) {
	}

	public void add(String key, String[] groups, Object value) {
	}

	public void add(String key, String[] groups, Object value, int seconds) {
	}

	public Object get(String key) {
		CachedObject obj = (CachedObject) this.cache.get(key);
		Object ref = null;

		if (obj != null) {
			if (DateTime.diff(DateTime.now(), obj.expiration) < 0) {
				ref = obj.ref;
			} else {
				this.cache.remove(key);
			}
		}
		return ref;
	}

	public Collection<String> getKeys() {
		return this.cache.keySet();
	}

	public Collection<Object> getValues() {
		ArrayList<Object> raws = new ArrayList<Object>();
		Collection<CachedObject> objs = (Collection<CachedObject>) this.cache
				.values();

		for (CachedObject obj : objs) {
			raws.add(obj.ref);
		}
		return raws;
	}

	public void remove(String key) {
		this.cache.remove(key);
	}

	public void clear() {
		this.cache.clear();
	}

	public void clear(String group) {

	}
}
