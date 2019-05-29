package com.yunfei.wh.ui.JSBridge.functions;

import org.json.JSONObject;

import android.content.Intent;

import com.prj.sdk.app.AppContext;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.broatcast.UnLoginBroadcastReceiver;
import com.thunisoft.jsbridge.wvjb.WVJBResponseCallback;
import com.thunisoft.jsbridge.wvjb.handler.WVJBHandler;

/**
 * 获取手机号,用户未登录时调用此接口框架会要求用户去登录
 */
public class getPhone implements WVJBHandler {

    @Override
    public void request(Object data, WVJBResponseCallback callback) {
        try {

            if (callback != null) {
                if (SessionContext.isLogin()) {
                    JSONObject mJson = new JSONObject();
                    mJson.put("phone", SessionContext.mUser.USERAUTH.mobilenum);
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