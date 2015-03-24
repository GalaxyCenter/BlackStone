package xshare.sina.weibo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import xshare.framework.IHandleListener;
import xshare.framework.Proxy;
import xshare.framework.ProxyActionListener;
import xshare.framework.ProxyParams;
import android.app.Activity;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;

public class SinaWeiboProxy extends Proxy {

	private Activity mActivity;
    private IWeiboShareAPI  mWeiboShareAPI = null;
    private String mAppkey = null;
    private ProxyActionListener mActionListener = null;
   
    
    public SinaWeiboProxy(Activity activity) {
    	super(activity);
    	
    	Properties props = null;
    	InputStream is = null;
    	
    	mActivity = activity;
    	
    	props = new Properties();
    	try {
			is = activity.getAssets().open("xshare_api.properties");
			props.load(is);
		} catch (IOException e) {
		}
		
		mAppkey = props.getProperty("oauth.sina.app_key");
		
		init();
    }
    
    protected void init() {
    
    	// 创建微博分享接口实例
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mActivity, mAppkey);
        
        // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
        // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
        // NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
        mWeiboShareAPI.registerApp();
    }
            
	@Override
	public void share(ProxyParams params) {
		SinaWeiboParams params2 = (SinaWeiboParams) params;
		
		// 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        
        weiboMessage.textObject = new TextObject();
        weiboMessage.textObject.text = params2.getText() + " " + params2.getUrl();
        
		if (params.getImage() != null) {
			weiboMessage.imageObject = new ImageObject();
			weiboMessage.imageObject.setImageObject(params.getImage());
		}
                
        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        
        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(mActivity, request);
	}

	@Override
	public void setProxyActionListener(ProxyActionListener l) {
		mActionListener = l;
	}

	@Override
	public void handleProxyResponse(Activity activity, IHandleListener l) {
		mWeiboShareAPI.handleWeiboResponse(activity.getIntent(), l);
	} 
}
