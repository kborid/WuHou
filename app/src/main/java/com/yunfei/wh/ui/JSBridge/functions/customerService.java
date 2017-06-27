package com.yunfei.wh.ui.JSBridge.functions;

import android.content.Context;
import android.content.Intent;

import com.prj.sdk.app.AppContext;
import com.yunfei.wh.ui.JSBridge.WVJBWebViewClient;
import com.yunfei.wh.ui.activity.CustomerServiceActivity;

/**
 * Created by kborid on 2016/6/8.
 * 跳转客服
 */
public class customerService implements WVJBWebViewClient.WVJBHandler {
    private Context context;

    public customerService(Context context) {
        this.context = context;
    }

    @Override
    public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
        Intent intent = new Intent(context, CustomerServiceActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
