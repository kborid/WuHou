package com.yunfei.wh.ui.JSBridge;

import android.content.Context;

import com.yunfei.wh.ui.JSBridge.functions.customerService;
import com.yunfei.wh.ui.JSBridge.functions.getCityInfo;
import com.yunfei.wh.ui.JSBridge.functions.getDeviceId;
import com.yunfei.wh.ui.JSBridge.functions.getPhone;
import com.yunfei.wh.ui.JSBridge.functions.getPicturesUpload;
import com.yunfei.wh.ui.JSBridge.functions.getStreetID;
import com.yunfei.wh.ui.JSBridge.functions.getUserId;
import com.yunfei.wh.ui.JSBridge.functions.getUserTicket;
import com.yunfei.wh.ui.JSBridge.functions.handleError;
import com.yunfei.wh.ui.JSBridge.functions.openURL;
import com.yunfei.wh.ui.JSBridge.functions.payOrder;
import com.yunfei.wh.ui.JSBridge.functions.showException;
import com.yunfei.wh.ui.JSBridge.functions.showNativeMap;

/**
 * 注册处理程序，使JavaScript可以调用
 *
 * @author LiaoBo
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
        // mWVJBWebViewClient.registerHandler("showLoginModule", new showLoginModule());
        // mWVJBWebViewClient.registerHandler("setCacheValue", new setCacheValue());
        // mWVJBWebViewClient.registerHandler("getNetworkStatus", new getNetworkStatus());
        // mWVJBWebViewClient.registerHandler("getCacheValue", new getCacheValue());
        // mWVJBWebViewClient.registerHandler("getCacheValue", new loadRequest());
        mWVJBWebViewClient.registerHandler("openURL", new openURL(mContext));
        mWVJBWebViewClient.registerHandler("getUserTicket", new getUserTicket());
        mWVJBWebViewClient.registerHandler("getPicturesUpload", new getPicturesUpload(mContext));
        mWVJBWebViewClient.registerHandler("getUserId", new getUserId());
        mWVJBWebViewClient.registerHandler("getPhone", new getPhone());
        mWVJBWebViewClient.registerHandler("getDeviceId", new getDeviceId());
        mWVJBWebViewClient.registerHandler("showException", new showException(mContext));
        mWVJBWebViewClient.registerHandler("getCityInfo", new getCityInfo());
        mWVJBWebViewClient.registerHandler("handleError", new handleError(mContext));
        mWVJBWebViewClient.registerHandler("payOrder", new payOrder(mContext));
        mWVJBWebViewClient.registerHandler("customerService", new customerService(mContext));
        mWVJBWebViewClient.registerHandler("showNativeMap", new showNativeMap(mContext));
        mWVJBWebViewClient.registerHandler("getStreetID", new getStreetID());
    }
}
