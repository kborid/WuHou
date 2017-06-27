package com.yunfei.wh.ui.JSBridge.functions;

import com.prj.sdk.util.NetworkUtil;
import com.yunfei.wh.ui.JSBridge.WVJBWebViewClient;

/**
 * 2.4. 获取当前的网络状态 : none = 0,// 不能访问网络
 * 						wwan = 1,// 使用移动网络 
 * 						wifi = 2,// 使用wifi
 * 
 * @author LiaoBo
 * 
 */
public class getNetworkStatus implements WVJBWebViewClient.WVJBHandler {

	@Override
	public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
		if (callback == null)
			return;
		int status = 0;// 不能访问网络
		if (NetworkUtil.isNetworkAvailable()) {// 网络可用
			if (NetworkUtil.isWifi()) {
				status = 2;// 使用wifi
			} else {
				status = 1;// 使用移动网络
			}
		}

		callback.callback(status);
	}

}