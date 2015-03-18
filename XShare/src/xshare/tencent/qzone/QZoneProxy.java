package xshare.tencent.qzone;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import xshare.framework.Proxy;
import xshare.framework.ProxyActionListener;
import xshare.framework.ProxyActivity;
import xshare.framework.ProxyParams;

public class QZoneProxy extends Proxy {

	public Tencent mTencent;
	private Activity mActivity;
	private String mAppkey = null;
	
	public QZoneProxy(Activity activity) {
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

		mAppkey = props.getProperty("oauth.tx.qzone.app_key");
	}

	protected void init() {
		mTencent = Tencent.createInstance(mAppkey, mActivity);
	}
	
	@Override
	public void share(ProxyParams params) {
		QzoneShare mQzoneShare = null;
		Bundle bundle = new Bundle();
		QZoneParams params2 = (QZoneParams) params;
		
		init();
		mQzoneShare = new QzoneShare(mActivity, mTencent.getQQToken());

		// 设置分享类型：图文并茂加链接。其他类型见帮助文档
		int shareType = QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT;
		bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, shareType);
		bundle.putString(QzoneShare.SHARE_TO_QQ_TITLE, params2.getSubject());
		bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, params2.getText());
		bundle.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, mAppkey);
		bundle.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, mAppkey);
		if (shareType != QzoneShare.SHARE_TO_QZONE_TYPE_APP) {
			// app分享不支持传目标链接
			bundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL,
					params2.getUrl());
		}
		 
		mQzoneShare.shareToQzone(mActivity, bundle, new IUiListener() {

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onComplete(Object arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(UiError arg0) {
				// TODO Auto-generated method stub

			}

		});
	}

	@Override
	public void setProxyActionListener(ProxyActionListener l) {
		// TODO Auto-generated method stub
		
	}
}
