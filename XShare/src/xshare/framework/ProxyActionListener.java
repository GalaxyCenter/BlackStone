package xshare.framework;

import java.util.Map;

public interface ProxyActionListener {
	
	public abstract void onComplete(Proxy proxy, int code, Map<String, Object> map);

	public abstract void onError(Proxy proxy, int paramInt, Throwable throwable);

	public abstract void onCancel(Proxy proxy, int paramInt);
}
