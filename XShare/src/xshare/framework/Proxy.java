package xshare.framework;

import android.app.Activity;

public abstract class Proxy {
		
	
	public abstract void share(ProxyParams params);
	
	public Proxy(Activity activity) {
	}
}
