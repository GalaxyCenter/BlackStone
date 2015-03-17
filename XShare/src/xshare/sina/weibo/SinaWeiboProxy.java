package xshare.sina.weibo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;


import xshare.framework.Proxy;
import xshare.framework.ProxyActivity;
import xshare.framework.ProxyParams;

public class SinaWeiboProxy extends Proxy {

	private Activity mActivity;
    private IWeiboShareAPI  mWeiboShareAPI = null;
	
    /** 微博 Web 授权类，提供登陆等功能  */
    private WeiboAuth mWeiboAuth;
    
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;

    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    
    private String mAppkey = null;
    private String mRedirectUrl = null;
    private String mScope = null;
	
    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {
        
        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {              
                // 保存 Token 到 SharedPreferences
                //AccessTokenKeeper.writeAccessToken(mActivity, mAccessToken);
                Toast.makeText(mActivity, 
                        "weibosdk_demo_toast_auth_success", Toast.LENGTH_SHORT).show();
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = "weibosdk_demo_toast_auth_failed";//getString(R.string.weibosdk_demo_toast_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(mActivity, 
                    "weibosdk_demo_toast_auth_canceled", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(mActivity, 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
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
		mRedirectUrl = props.getProperty("oauth.sina.redirect_url");
		mScope = props.getProperty("oauth.sina.scope");
    }
    
    public void init() {
    
    	// 创建微博分享接口实例
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mActivity, mAppkey);
        
        // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
        // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
        // NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
        mWeiboShareAPI.registerApp();
        
        mWeiboShareAPI.handleWeiboResponse(mActivity.getIntent(), new IWeiboHandler.Response(){

			@Override
			public void onResponse(BaseResponse baseResp) {
				 switch (baseResp.errCode) {
			        case WBConstants.ErrorCode.ERR_OK:
			            
			            break;
			        case WBConstants.ErrorCode.ERR_CANCEL:
			            
			            break;
			        case WBConstants.ErrorCode.ERR_FAIL:
			           
			            break;
			        }
			}
                	
        });
        
        auth();
    }
    
    protected void auth() {
    	Properties props = null;
    	InputStream is = null;
    	String app_key = null;
    	String redirect_url = null;
    	String scope = null;
    	
    	props = new Properties();
    	try {
			is = mActivity.getAssets().open("xshare_api.properties");
			props.load(is);
		} catch (IOException e) {
		}
		

		app_key = props.getProperty("oauth.sina.app_key");
		redirect_url = props.getProperty("oauth.sina.redirect_url");
		scope = props.getProperty("oauth.sina.scope");
		
		mWeiboAuth = new WeiboAuth(mActivity, app_key, redirect_url, scope);
		
		mSsoHandler = new SsoHandler(mActivity, mWeiboAuth);
        mSsoHandler.authorize(new AuthListener());
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
        mWeiboShareAPI.sendRequest(request);
	}

 
}
