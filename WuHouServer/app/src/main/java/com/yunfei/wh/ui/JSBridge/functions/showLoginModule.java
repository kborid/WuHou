package com.yunfei.wh.ui.JSBridge.functions;

import android.content.Intent;

import com.prj.sdk.app.AppContext;
import com.yunfei.wh.broatcast.UnLoginBroadcastReceiver;
import com.yunfei.wh.ui.JSBridge.WVJBWebViewClient;

/**
 * 打开登录界面
 * 
 * @author LiaoBo
 * 
 */
public class showLoginModule implements WVJBWebViewClient.WVJBHandler {
	@Override
	public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
		AppContext.mAppContext.sendBroadcast(new Intent(UnLoginBroadcastReceiver.ACTION_NAME));
	}

}
