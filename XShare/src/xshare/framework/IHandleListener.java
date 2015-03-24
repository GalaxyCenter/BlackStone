package xshare.framework;

import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.tauth.IUiListener;

public interface IHandleListener extends IWeiboHandler.Response, IWXAPIEventHandler, IUiListener {

}
