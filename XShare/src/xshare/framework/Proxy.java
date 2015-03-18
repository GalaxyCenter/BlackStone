package xshare.framework;

import android.app.Activity;

public abstract class Proxy {
		
	public Proxy(Activity activity) {
	}
	
	public abstract void share(ProxyParams params);
	
	public abstract void setProxyActionListener(ProxyActionListener l);
}
