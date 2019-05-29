package com.yunfei.wh.ui.JSBridge.functions;

import android.content.Intent;

import com.prj.sdk.app.AppContext;
import com.yunfei.wh.broatcast.UnLoginBroadcastReceiver;
import com.thunisoft.jsbridge.wvjb.WVJBResponseCallback;
import com.thunisoft.jsbridge.wvjb.handler.WVJBHandler;

/**
 * 打开登录界面
 */
public class showLoginModule implements WVJBHandler {
	@Override
	public void request(Object data, WVJBResponseCallback callback) {
		AppContext.mAppContext.sendBroadcast(new Intent(UnLoginBroadcastReceiver.ACTION_NAME));
	}

}
