package com.yunfei.wh.ui.JSBridge.functions;

import java.security.MessageDigest;

import org.json.JSONObject;

import com.prj.sdk.util.Utils;
import com.yunfei.wh.ui.JSBridge.WVJBWebViewClient;

/**
 * 3.3 获取设备标识
 * 
 * @author LiaoBo
 */
public class getDeviceId implements WVJBWebViewClient.WVJBHandler {

	@Override
	public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
		try {

			if (callback != null) {
				JSONObject mJson = new JSONObject();
				mJson.put("deviceId", getUdid());
				callback.callback(mJson.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成设备Udid
	 * 
	 * @return
	 */
	public String getUdid() {
		try {
			String imei = Utils.getIMEI();
			String mac = Utils.getWifiMac();
			String content = imei + mac;
			MessageDigest md;
			md = MessageDigest.getInstance("MD5");
			md.update(content.getBytes("UTF-8"));
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}