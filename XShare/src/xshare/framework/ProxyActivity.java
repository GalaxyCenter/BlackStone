package xshare.framework;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.tauth.UiError;

public class ProxyActivity extends Activity implements IHandleListener {

	protected Proxy mProxy;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		///setContentView(R.layout.activity_proxy);		
	}

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		mProxy.handleProxyResponse(this, this);
	}

	// 实现weibo接口
	@Override
	public void onResponse(BaseResponse baseResp) {
		switch (baseResp.errCode) {
        case WBConstants.ErrorCode.ERR_OK:
            Toast.makeText(this, "weibosdk_demo_toast_share_success", Toast.LENGTH_LONG).show();
            break;
        case WBConstants.ErrorCode.ERR_CANCEL:
            Toast.makeText(this, "weibosdk_demo_toast_share_canceled", Toast.LENGTH_LONG).show();
            break;
        case WBConstants.ErrorCode.ERR_FAIL:
            Toast.makeText(this, 
                    " weibosdk_demo_toast_share_failed Error Message: " + baseResp.errMsg, 
                    Toast.LENGTH_LONG).show();
            break;
		}
	}

	// 实现微信接口
	@Override
	public void onReq(BaseReq arg0) {
		Toast.makeText(this, " wx.onreq", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onResp(BaseResp arg0) {
		Toast.makeText(this, " wx.onResp", Toast.LENGTH_LONG).show();
	}

	// 实现QQ空间接口
	@Override
	public void onCancel() {
		Toast.makeText(this, " qzone.onCancel", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onComplete(Object arg0) {
		Toast.makeText(this, " qzone.onComplete", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onError(UiError arg0) {
		Toast.makeText(this, " qzone.onError", Toast.LENGTH_LONG).show();
	}
}
