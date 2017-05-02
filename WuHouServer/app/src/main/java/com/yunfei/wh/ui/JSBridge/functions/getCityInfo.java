package com.yunfei.wh.ui.JSBridge.functions;

import org.json.JSONObject;

import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.ui.JSBridge.WVJBWebViewClient;

/**
 * 3.5 获取城市信息
 * 
 * @author LiaoBo
 */
public class getCityInfo implements WVJBWebViewClient.WVJBHandler {

	@Override
	public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
		try {

			if (callback != null) {
				//{“cityCode”:”500000”,”cityName”:”重庆"}
				JSONObject mJson = new JSONObject();
				mJson.put("cityCode", SessionContext.getAreaInfo(1));
				mJson.put("cityName", SessionContext.getAreaInfo(2));
				callback.callback(mJson.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
