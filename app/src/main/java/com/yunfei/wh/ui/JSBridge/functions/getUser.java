package com.yunfei.wh.ui.JSBridge.functions;

import com.alibaba.fastjson.JSON;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.ui.JSBridge.WVJBWebViewClient;

/**
 * 2.7.	获取USER数据
 * 
 * @author LiaoBo
 */
public class getUser implements WVJBWebViewClient.WVJBHandler {
	@Override
	public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
		try {

			if (callback != null)
				callback.callback(JSON.toJSONString(SessionContext.mUser));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
