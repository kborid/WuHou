package com.yunfei.wh.ui.JSBridge.functions;

import com.alibaba.fastjson.JSON;
import com.prj.sdk.app.AppContext;
import com.prj.sdk.util.StringUtil;
import com.thunisoft.jsbridge.wvjb.WVJBResponseCallback;
import com.thunisoft.jsbridge.wvjb.handler.WVJBHandler;

/**
 * 缓存一个值，将在webView关闭时释放
 */
public class setCacheValue implements WVJBHandler {
	@Override
	public void request(Object data, WVJBResponseCallback callback) {
		if (StringUtil.isEmpty(data)) {
			return;
		}
		try {
			Bean bean = JSON.parseObject(data.toString(), Bean.class);
			AppContext.mMemoryMap.put(bean.key, bean.value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class Bean {
		public String	key;
		public String	value;
	}

}