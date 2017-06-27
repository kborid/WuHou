package com.yunfei.wh.ui.JSBridge.functions;

import com.prj.sdk.util.SharedPreferenceUtil;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.ui.JSBridge.WVJBWebViewClient;

import org.json.JSONObject;

/**
 * @author kborid
 * @date 2017/6/6 0006
 */
public class getStreetID implements WVJBWebViewClient.WVJBHandler {
    @Override
    public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
        try {

            if (callback != null) {
                JSONObject mJson = new JSONObject();
                int streetId = SharedPreferenceUtil.getInstance().getInt("streetId", -1);
                mJson.put("streetID", String.valueOf(streetId));
                callback.callback(mJson.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
