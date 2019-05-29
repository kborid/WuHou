package com.yunfei.wh.ui.JSBridge;

import android.content.Context;

import com.thunisoft.jsbridge.WVJBWebViewClient;
import com.yunfei.wh.ui.JSBridge.functions.getDeviceId;
import com.yunfei.wh.ui.JSBridge.functions.getIDCardPic;
import com.yunfei.wh.ui.JSBridge.functions.getSelfPic;
import com.yunfei.wh.ui.JSBridge.functions.getUserId;
import com.yunfei.wh.ui.JSBridge.functions.showException;

/**
 * 注册处理程序
 */
public class RegisterHandler {

    private WVJBWebViewClient mWVJBWebViewClient;
    private Context mContext;

    /**
     * 构造函数
     *
     * @param mContext
     */
    public RegisterHandler(WVJBWebViewClient mWVJBWebViewClient, Context mContext) {
        this.mWVJBWebViewClient = mWVJBWebViewClient;
        this.mContext = mContext;
    }

    /**
     * 初始化，注册处理程序
     */
    public void init() {
        mWVJBWebViewClient.registerHandler("getUserId", new getUserId());
        mWVJBWebViewClient.registerHandler("getDeviceId", new getDeviceId());
        mWVJBWebViewClient.registerHandler("showException", new showException(mContext));
        mWVJBWebViewClient.registerHandler("getIDCardPic", new getIDCardPic());
        mWVJBWebViewClient.registerHandler("getSelfPic", new getSelfPic());
    }
}
