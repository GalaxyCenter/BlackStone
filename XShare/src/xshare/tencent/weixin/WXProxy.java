package xshare.tencent.weixin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

import xshare.framework.Proxy;
import xshare.framework.ProxyActivity;
import xshare.framework.ProxyParams;
import xshare.framework.utils.Util;

public class WXProxy extends Proxy {

	private IWXAPI mWXapi;
	private Activity mActivity;
	private String mAppkey = null;
	
	public WXProxy(Activity activity) {
		super(activity);
		
		mActivity = activity;	
		
		Properties props = null;
    	InputStream is = null;
    	
    	mActivity = activity;
    	props = new Properties();
    	try {
			is = activity.getAssets().open("xshare_api.properties");
			props.load(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mAppkey = props.getProperty("oauth.tx.weixin.app_key");
		
		init(null);
	}
	
	@Override
	public void share(ProxyParams params) {
		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = null;
		SendMessageToWX.Req req = null;
		WXParams params2 = (WXParams)params;
		
		if (WXParams.Type.WEB.equals(params2.getType())) {
			WXWebpageObject webpage = new WXWebpageObject();
			Bitmap thumb = params2.getImage();
			
			webpage.webpageUrl = params.getUrl();
			
			msg = new WXMediaMessage(webpage);
			msg.title = params2.getSubject();
			msg.description = params2.getText();
			msg.thumbData = Util.bmpToByteArray(thumb, true);
		}
		// 构造一个Req
		req = new SendMessageToWX.Req();
		req.transaction = params2.getType();
		req.message = msg;
		req.scene = params2.isTimeline()? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
		
		// 调用api接口发送数据到微信
		mWXapi.sendReq(req);
	}

	protected void init(ProxyActivity activty) {
		mWXapi = WXAPIFactory.createWXAPI(mActivity, mAppkey, false);
		mWXapi.registerApp(mAppkey);    
		
		mWXapi.handleIntent(mActivity.getIntent(), new IWXAPIEventHandler() {

			@Override
			public void onReq(BaseReq req) {
				
			}
			
			@Override
			public void onResp(BaseResp resp) {
				
			}
			
		});
	}
}
