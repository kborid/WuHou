package com.yunfei.wh.ui.JSBridge.functions;

import com.google.gson.Gson;
import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.util.StringUtil;
import com.yunfei.wh.ui.JSBridge.WVJBWebViewClient;

/**
 * 2.8. 加载一个request
 * 
 * @author LiaoBo
 * 
 */
public class loadRequest implements WVJBWebViewClient.WVJBHandler {
	@Override
	public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
		if (callback == null) {
			return;
		}
		if (StringUtil.isEmpty(data)) {
			return;
		}
		// 解析请求参数
		Gson gson = new Gson();
		Bean mJson = gson.fromJson(data.toString(), Bean.class);
		// 封装请求
		ResponseData request = new ResponseData();
		request.path = mJson.url;
		request.type = mJson.type;
		request.data = mJson.params;
		request.isLocal = true;

		DataLoader.getInstance().loadData(new DataCallback() {

			@Override
			public void preExecute(ResponseData request) {

			}

			@Override
			public void notifyMessage(final ResponseData request, final ResponseData response) throws Exception {
				// JSONObject mJson = JSON.parseObject(response.body.toString());
				callback.callback(response.body.toString());
			}

			@Override
			public void notifyError(ResponseData request, ResponseData response, Exception e) {
				callback.callback(response.body.toString());
			}
		}, request);
	}

	class Bean {
		public String	url;
		public String	type;
		public String	params;
	}

}