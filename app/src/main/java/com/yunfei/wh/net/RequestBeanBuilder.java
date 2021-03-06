package com.yunfei.wh.net;

import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.prj.sdk.algo.MD5;
import com.prj.sdk.app.AppContext;
import com.prj.sdk.constants.InfoType;
import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.util.StringUtil;
import com.yunfei.wh.broatcast.UnLoginBroadcastReceiver;
import com.yunfei.wh.common.AppConst;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.control.UpdateControl;
import com.prj.sdk.algo.Algorithm3DES;
import com.prj.sdk.algo.AlgorithmData;
import com.yunfei.wh.tools.Base64;

import java.util.HashMap;
import java.util.Map;

/**
 * 构建请求处理
 * 
 * @author LiaoBo
 */
public class RequestBeanBuilder {

	private Map<String, Object>	head;
	private Map<String, Object>	body;

	private RequestBeanBuilder(boolean isNeedTicket) {
		head = new HashMap<String, Object>();
		body = new HashMap<String, Object>();
		if (isNeedTicket) {
			if (StringUtil.isEmpty(SessionContext.getTicket())) {// 如果ticket为空则发送登录广播
				AppContext.mAppContext.sendBroadcast(new Intent(UnLoginBroadcastReceiver.ACTION_NAME));
			}
			addHeadToken(SessionContext.getTicket());
		}
	}

	/**
	 * 构建请求
	 * 
	 * @param isNeedTicket
	 *            是否需要ticket ,如果需要登录就需要ticket
	 * @return
	 */
	public static RequestBeanBuilder create(boolean isNeedTicket) {
		return new RequestBeanBuilder(isNeedTicket);
	}

	public RequestBeanBuilder addHeadToken(String token) {
		return addHead("accessTicket", token);
	}

	public RequestBeanBuilder addHead(String key, Object value) {
		head.put(key, value);
		return this;
	}

	public RequestBeanBuilder addBody(String key, Object value) {
		body.put(key, value);
		return this;
	}

	private String sign() {
		AlgorithmData data = new AlgorithmData();
		try {
			// 先对body进行base64
			String bodyText = JSON.toJSONString(body);
			// 对报文进行BASE64编码，避免中文处理问题
			String base64Text = new String(Base64.encodeBase64((AppConst.APPID + bodyText).getBytes("utf-8"), false));
			// MD5摘要，生成固定长度字符串用于加密
			String destText = MD5.getMD5(base64Text);
			data.setDataMing(destText);
			data.setKey(AppConst.APPKEY);
			// 3DES加密
			Algorithm3DES.encryptMode(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data.getDataMi();
	}

	/**
	 * 获取访问mgr的签名
	 * 
	 * @return
	 */
	private String signRequestForMgr() {
		String destText = "";
		try {

			String srcText = JSON.toJSONString(body);

			String base64Text = new String(Base64.encodeBase64((AppConst.APPID + srcText + AppConst.APPKEY).getBytes("utf-8"), false));

			destText = MD5.getMD5(base64Text);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return destText;
	}

	/**
	 * 请求数据的json字符串
	 * 
	 * @return
	 */
	public String toJson(boolean isMgr) {
		HashMap<String, Object> json = new HashMap<String, Object>();
		json.put("head", head);
		json.put("body", body);

		head.put("appid", AppConst.APPID);
		if (isMgr) {
			head.put("sign", signRequestForMgr());
		} else {
			head.put("sign", sign());
		}
		head.put("version", AppConst.VERSION);
		head.put("siteid", SessionContext.getAreaInfo(1));
		head.put("appversion", UpdateControl.getInstance().getCurVersionName());
		return JSON.toJSONString(json);
	}

	/**
	 * 请求数据
	 * 
	 * @return
	 */
	public ResponseData syncRequest(RequestBeanBuilder builder) {
		ResponseData data = new ResponseData();
		data.data = builder.toJson(false);
		data.type = InfoType.POST_REQUEST.toString();
		return data;
	}

//	/**
//	 * 请求mgr接口数据
//	 * 
//	 * @return
//	 */
//	public ResponseData syncRequestMgr(RequestBeanBuilder builder) {
//		ResponseData data = new ResponseData();
//		data.data = builder.toJson(true);
//		data.type = InfoType.POST_REQUEST.toString();
//		return data;
//	}

}
