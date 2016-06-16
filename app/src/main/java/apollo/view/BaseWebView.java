package apollo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BaseWebView extends WebView {
 
	class MyWebViewClient extends WebViewClient {

		public boolean shouldOverrideUrlLoading(WebView webview, String s) {
			return mOnLoadUrlListener == null ? super.shouldOverrideUrlLoading(
					webview, s) : mOnLoadUrlListener.shouldOverrideUrlLoading(
					webview, s);
		}

	}

	public interface OnLoadUrlListener {
		public abstract boolean shouldOverrideUrlLoading(WebView webview,
				String s);
	}

	private OnLoadUrlListener mOnLoadUrlListener;
	private WebViewClient mWebViewClient;

	public BaseWebView(Context context) {
		super(context);
		mOnLoadUrlListener = null;
		init();
	}

	public BaseWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mOnLoadUrlListener = null;
		init();
	}

	private void init() {
		getSettings().setJavaScriptEnabled(true);
		getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		this.mWebViewClient = new MyWebViewClient();
		setWebViewClient(this.mWebViewClient);
		setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View paramView) {
				return true;
			}
		});
	}

	public void resetProxy(int i) {
	//	disablePlatformNotifications();
	//	enablePlatformNotifications();
	}


	public void setOnLoadUrlListener(OnLoadUrlListener listener) {
		mOnLoadUrlListener = listener;
	}
}
