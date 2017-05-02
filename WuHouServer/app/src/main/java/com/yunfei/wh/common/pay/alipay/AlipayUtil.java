package com.yunfei.wh.common.pay.alipay;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.yunfei.wh.common.AppConst;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * alipay操作工具类
 * 
 * @author LiaoBo
 */
public class AlipayUtil {
	private static final int	SDK_PAY_FLAG	= 1;

	// 商户PID
	private final String				PARTNER = "2088811019618951";
	// 商户收款账号
//	private final String				SELLER = "admin@zaichengdu.com";
	// 商户私钥，pkcs8格式
	private final String				RSA_PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKy7JzB+ayEuf/ulXc0Sis4fZht12OUKk2Aj3sbckSc8Wf9WjU+dUyNDMLbelgrvN3jJqSZGTbu+Jef5mTTMotbL7Wt8i5uI9sBDrLYDfr18qF/susSQb7F3zqT8L31b8xYKZnPQFBE4Y/RDIqyWrR+VffRnO9Nw+xd+2GKUBoD5AgMBAAECgYBSiuUBYYgkRSpXwQCAPTojohGTUc+iNFmU+c89zK14yHhhcqT4xL+z/CnNWbTt6L7jc8rnRXVkIEvQ/Q/fkWLG5PGFCtmNbaG47RSKt73BOwbfptYwA7KTCvxBoyB0l8YWmq9CIC1weV3ES0iT2VOkEqj+oq2/CarEIr8zGw78+QJBAOPM1Ln04fHNn89OxONO7V889GcG1wbfJC0M/95GuO/U8Er0bPjiRs3GjCSzNHJx0HAitMr0J3xUb23c/Fvf+68CQQDCHSO2lYuspkDJAdH2m947y0xIZcaDvWO86CgjXm3fNIJOady7TsE42Sm5hpwCwRfek0vwmh5/XnamJocT2y/XAkAUy9tTyq5h49LGGzvQQKFdA3SQf0hcITztO/WT9kLl6juxrBTSt92c5ZZft1h/ZjKQOirlKFr+d7rgfb8wKAeTAkBxDXmUSEqd1gIbT5USdLscxg16iNPyPCZPikJ+PuvsuG8/7gXhjT+ZtVjIta49KGqXdeAflBM5CTI/GGdJWDnnAkAaXiM9ntbL1OzgCjnC9LbFGTSV13stnVQBClKcWhDdGO68aOQG5XKvDF3tAZJLaXTR2PfxhpB6rUaSX304aGpO";
    
	private Activity			mContext;
	private Handler				mHandler;

	public AlipayUtil(Activity context) {
		mContext = context;
		mHandler = new Handler(new MyCallBack());
	}

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 * @param orderInfo
	 *            订单
	 */
	public void pay(String orderInfo) {
		// if (!check()) {
		// Toast.makeText(mContext, "没有支护宝，请安装支护宝", Toast.LENGTH_SHORT).show();
		// return;
		// }

		// 对订单做RSA 签名
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(mContext);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				Message msg = mHandler.obtainMessage();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				msg.sendToTarget();
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}
	/**
	 * check whether the device has authentication alipay account. 查询终端设备是否存在支付宝认证账户
	 * 
	 * @return 是否存在支护宝app
	 */
	public boolean check() {
		boolean isExist = false;
		try {

			// 构造PayTask 对象
			PayTask payTask = new PayTask(mContext);
			// 调用查询接口，获取查询结果
			isExist = payTask.checkAccountIfExist();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isExist;

	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	private void getSDKVersion() {
		PayTask payTask = new PayTask(mContext);
		String version = payTask.getVersion();
		Toast.makeText(mContext, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 生成订单信息
	 * 
	 * @param subject
	 *            商品名名称
	 * @param body
	 *            商品详情
	 * @param seller_id
	 *            商户id
	 * @param total_fee
	 *            总金额
	 * @param out_trade_no
	 *            订单号
	 * @param notify_url
	 *            服务器异步通知页面路径
	 * @return
	 */
	public String getOrderInfo(String subject, String body, String seller_id, String total_fee, String out_trade_no, String notify_url) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + seller_id + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + out_trade_no + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + total_fee + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + notify_url + "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	private String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	private String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

	/**
	 * 支护宝回调
	 * 
	 * @author LiaoBo
	 */
	private class MyCallBack implements Handler.Callback {

		@Override
		public boolean handleMessage(Message msg) {
			String resultStatus = "", status = "";
			try {
				switch (msg.what) {
					case SDK_PAY_FLAG : {
						PayResult payResult = new PayResult((String) msg.obj);

						// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
						// String resultInfo = payResult.getResult();

						resultStatus = payResult.getResultStatus();
						// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
						if (TextUtils.equals(resultStatus, "9000")) {
							Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
							status = "支付成功";
						} else {
							// 判断resultStatus 为非“9000”则代表可能支付失败
							// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
							if (TextUtils.equals(resultStatus, "8000")) {
								Toast.makeText(mContext, "支付结果确认中", Toast.LENGTH_SHORT).show();
								status = "支付结果确认中";
							} else {
								// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
								Toast.makeText(mContext, "支付失败", Toast.LENGTH_SHORT).show();
								status = "支付失败";
							}
						}
						break;
					}
					default :
						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				Intent mIntent = new Intent(AppConst.ACTION_PAY_STATUS);
				mIntent.putExtra("code", resultStatus);
				mIntent.putExtra("msg", status);
				LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent);
			}

			return true;
		}

	}

}
