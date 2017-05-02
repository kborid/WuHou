package com.yunfei.wh.ui.JSBridge.functions;

import org.json.JSONObject;

import android.content.Intent;

import com.prj.sdk.app.AppContext;
import com.prj.sdk.util.StringUtil;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.broatcast.UnLoginBroadcastReceiver;
import com.yunfei.wh.ui.JSBridge.WVJBWebViewClient;
import com.yunfei.wh.ui.JSBridge.WVJBWebViewClient.WVJBResponseCallback;

/**
 * 2.5. 获取访问凭据，Null时为未登录
 * 
 * @author LiaoBo
 */
public class getUserTicket implements WVJBWebViewClient.WVJBHandler {
	@Override
	public void request(Object data, WVJBResponseCallback callback) {
		try {

			if (callback != null) {
				if (StringUtil.notEmpty(SessionContext.getTicket())) {
					JSONObject mJson = new JSONObject();
					mJson.put("userTicket", SessionContext.getTicket());
					callback.callback(mJson.toString());
				} else {
					AppContext.mAppContext.sendBroadcast(new Intent(UnLoginBroadcastReceiver.ACTION_NAME));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
