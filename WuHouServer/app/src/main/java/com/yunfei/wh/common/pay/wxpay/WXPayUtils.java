package com.yunfei.wh.common.pay.wxpay;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Xml;
import android.view.Gravity;
import android.widget.Toast;

import com.prj.sdk.util.LogUtil;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 微信支付操作工具类支持app端生成订单和服务端生成订单支付：1、createOrderSendPayReq()支持app端生成订单并支付；2、SendPayReq()服务端生成订单信息，app直接支付
 * 
 * @author LiaoBo
 */
public class WXPayUtils {
	private final String		TAG	= "WXpayUtil";
	private Activity			mActivity;
	private IWXAPI				msgApi;
	private Map<String, String>	resultunifiedorder;
	private GetPrepayIdTask		mGetPrepayIdTask;
	private PayReq				req;

	public WXPayUtils(Activity act) {
		mActivity = act;
		msgApi = WXAPIFactory.createWXAPI(act, null);
		msgApi.registerApp(Constants.APP_ID);
		req = new PayReq();
	}

	/**
	 * 由app端根据商品信息生成订单并支付
	 * 
	 * @param fee
	 *            总费用
	 * @param body
	 *            商品描述
	 * @param out_trade_no
	 *            订单号
	 * @return
	 */
	public void createOrderSendPayReq(String fee, String body, String out_trade_no) {
		if (!isSupport()) {
			return;
		}
		// 1、根据out_trade_no生成PrepayId
		if (mGetPrepayIdTask == null || mGetPrepayIdTask.getStatus() != AsyncTask.Status.RUNNING) {
			GetPrepayIdTask getPrepayId = new GetPrepayIdTask(fee, body, out_trade_no);
			getPrepayId.execute();
		}
	}

	/**
	 * 生成签名
	 */
	private String genPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);

		String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		LogUtil.e("orion", packageSign);
		return packageSign;
	}
	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);

		String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		LogUtil.e("genAppSign", "appSign:\n" + appSign + "genAppSign params" + sb.toString() + "\n\n");
		return appSign;
	}
	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<" + params.get(i).getName() + ">");

			sb.append(params.get(i).getValue());
			sb.append("</" + params.get(i).getName() + ">");
		}
		sb.append("</xml>");

		LogUtil.e("orion", sb.toString());
		return sb.toString();
	}

	/**
	 * app支付生成预支付订单
	 * 
	 * @author LiaoBo
	 */
	private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>> {
		private String	fee, body, out_trade_no;

		/**
		 * @param fee
		 *            总费用 （单位 分）
		 * @param body
		 *            商品描述
		 * @param out_trade_no
		 *            订单号
		 * @return
		 */
		public GetPrepayIdTask(String fee, String body, String out_trade_no) {
			this.fee = fee;
			this.body = body;
			this.out_trade_no = out_trade_no;
		}

		private ProgressDialog	diaLogUtil;

		@Override
		protected void onPreExecute() {
			diaLogUtil = ProgressDialog.show(mActivity, "提示", "正在获取预支付订单...");
		}

		@Override
		protected void onPostExecute(Map<String, String> result) {
			if (diaLogUtil != null) {
				diaLogUtil.dismiss();
			}
			LogUtil.d(TAG, "prepay_id\n" + result.get("prepay_id") + "\n\n");
			resultunifiedorder = result;
			genPayReq();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected Map<String, String> doInBackground(Void... params) {

			String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
			String entity = genProductArgs(fee, body, out_trade_no);

			LogUtil.e("orion", entity);

			byte[] buf = Util.httpPost(url, entity);

			String content = new String(buf);
			LogUtil.e("orion", content);
			Map<String, String> xml = decodeXml(content);

			return xml;
		}
	}

	public Map<String, String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName = parser.getName();
				switch (event) {
					case XmlPullParser.START_DOCUMENT :

						break;
					case XmlPullParser.START_TAG :

						if ("xml".equals(nodeName) == false) {
							// 实例化student对象
							xml.put(nodeName, parser.nextText());
						}
						break;
					case XmlPullParser.END_TAG :
						break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
			LogUtil.e("orion", e.toString());
		}
		return null;

	}

	/**
	 * 随机字符串
	 * 
	 * @return
	 */
	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}

	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	/**
	 * 获取预支付订单号:生成prepay_id,在服务器完成
	 * 
	 * @param fee
	 *            总费用
	 * @param body
	 *            商品描述
	 * @param out_trade_no
	 *            订单号
	 * @return
	 */
	private String genProductArgs(String fee, String body, String out_trade_no) {
		StringBuffer xml = new StringBuffer();

		try {
			String nonceStr = genNonceStr();

			xml.append("</xml>");
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid", Constants.APP_ID));
			packageParams.add(new BasicNameValuePair("body", body));
			packageParams.add(new BasicNameValuePair("mch_id", Constants.MCH_ID));
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			packageParams.add(new BasicNameValuePair("notify_url", "http://121.40.35.3/test"));
			packageParams.add(new BasicNameValuePair("out_trade_no", out_trade_no));
			packageParams.add(new BasicNameValuePair("spbill_create_ip", "127.0.0.1"));
			packageParams.add(new BasicNameValuePair("total_fee", fee));
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));

			String sign = genPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));

			String xmlstring = toXml(packageParams);

			return new String(xmlstring.toString().getBytes(), "ISO8859-1");// 把xml转码下，解决 body中文无法支付问题

			// return xmlstring;

		} catch (Exception e) {
			LogUtil.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
			return null;
		}

	}

	/**
	 * 2、生成app微信支付签名参数
	 */
	private void genPayReq() {
		req.appId = Constants.APP_ID;
		req.partnerId = Constants.MCH_ID;
		req.prepayId = resultunifiedorder.get("prepay_id");
		req.packageValue = "Sign=WXPay";
		req.nonceStr = genNonceStr();// 随机字符串 noncestr
		req.timeStamp = String.valueOf(genTimeStamp());

		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));// 扩展字段，暂填写固定值Sign=WXPay
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

		req.sign = genAppSign(signParams);

		LogUtil.d(TAG, "sign\n" + req.sign + "\n\n");

		LogUtil.e("genPayReq", signParams.toString());
		sendPayReq();
	}

	/**
	 * 3、发送支付请求
	 * 
	 */
	private void sendPayReq() {
		msgApi.registerApp(Constants.APP_ID);
		msgApi.sendReq(req);
	}

	/**
	 * 由服务端生成订单信息，app端直接发起支付
	 * 
	 * @param nonceStr
	 *            随机字符串
	 * @param sign
	 *            签名
	 * @param prepayID
	 *            prepayID，其中包含了订单的费用、描述等信息
	 * @param timeStamp
	 *            时间戳
	 */
	public void sendPayReq(String nonceStr, String sign, String prepayID, String timeStamp) {
		if (!isSupport()) {
			return;
		}
		req = new PayReq();
		req.appId = Constants.APP_ID;
		req.partnerId = Constants.MCH_ID;
		req.prepayId = prepayID;
		req.packageValue = "Sign=WXPay";
		req.nonceStr = nonceStr;
		req.timeStamp = timeStamp;
		req.sign = sign;

		msgApi.registerApp(Constants.APP_ID);
		msgApi.sendReq(req);
	}

	/**
	 * 检查是否支持
	 * 
	 * @return
	 */
	public boolean isSupport() {
		if (!msgApi.isWXAppInstalled()) {
			Toast toast = Toast.makeText(mActivity, "没有安装微信", 0);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		}
		if (msgApi.getWXAppSupportAPI() < Build.PAY_SUPPORTED_SDK_INT) {
			Toast toast = Toast.makeText(mActivity, "当前版本不支持支付功能", 0);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		}
		return true;
	}

}
