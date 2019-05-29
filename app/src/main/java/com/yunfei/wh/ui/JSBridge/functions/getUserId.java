package com.yunfei.wh.ui.JSBridge.functions;

import android.content.Intent;

import com.prj.sdk.app.AppContext;
import com.thunisoft.jsbridge.wvjb.WVJBResponseCallback;
import com.thunisoft.jsbridge.wvjb.handler.WVJBHandler;
import com.yunfei.wh.broatcast.UnLoginBroadcastReceiver;
import com.yunfei.wh.common.SessionContext;

import org.json.JSONObject;

/**
 * 获取用户ID,用户未登录时调用此接口框架会要求用户去登录
 */
public class getUserId implements WVJBHandler {

    @Override
    public void request(Object data, WVJBResponseCallback callback) {
        try {

            if (callback != null) {
                if (SessionContext.isLogin()) {
                    JSONObject mJson = new JSONObject();
                    mJson.put("userId", SessionContext.mUser.USERBASIC.id);
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
