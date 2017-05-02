package com.yunfei.wh.ui.JSBridge.functions;

import com.google.gson.Gson;
import com.prj.sdk.app.AppContext;
import com.prj.sdk.util.StringUtil;
import com.yunfei.wh.ui.JSBridge.WVJBWebViewClient;

/**
 * 缓存一个值，将在webView关闭时释放
 * 
 * @author LiaoBo
 * 
 */
public class setCacheValue implements WVJBWebViewClient.WVJBHandler {
	@Override
	public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
		if (StringUtil.isEmpty(data)) {
			return;
		}
		try {
			Gson gson = new Gson();
			Bean mJson = gson.fromJson(data.toString(), Bean.class);
			AppContext.mMemoryMap.put(mJson.key, mJson.value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class Bean {
		public String	key;
		public String	value;
	}

}