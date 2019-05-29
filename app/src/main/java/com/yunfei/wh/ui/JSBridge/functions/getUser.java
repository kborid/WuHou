package com.yunfei.wh.ui.JSBridge.functions;

import com.alibaba.fastjson.JSON;
import com.yunfei.wh.common.SessionContext;
import com.thunisoft.jsbridge.wvjb.WVJBResponseCallback;
import com.thunisoft.jsbridge.wvjb.handler.WVJBHandler;

/**
 * 2.7.	获取USER数据
 */
public class getUser implements WVJBHandler {
	@Override
	public void request(Object data, WVJBResponseCallback callback) {
		try {

			if (callback != null)
				callback.callback(JSON.toJSONString(SessionContext.mUser));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
