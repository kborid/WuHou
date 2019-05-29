package com.yunfei.wh.ui.JSBridge.functions;

import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunfei.wh.R;
import com.yunfei.wh.common.pay.alipay.AlipayUtil;
import com.yunfei.wh.common.pay.wxpay.WXPayUtils;
import com.prj.sdk.util.StringUtil;
import com.yunfei.wh.common.AppConst;
import com.yunfei.wh.ui.activity.HtmlActivity;
import com.thunisoft.jsbridge.wvjb.WVJBResponseCallback;
import com.thunisoft.jsbridge.wvjb.handler.WVJBHandler;

/**
 * 注入三方支付
 */
public class payOrder implements WVJBHandler {

    private Context mContext;
    private WVJBResponseCallback mCallback;
    private String mItemName;
    private BroadcastReceiver mBroadcastReceiver;

    public payOrder(Context context) {
        mContext = context;
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    String action = intent.getAction();
                    if (AppConst.ACTION_PAY_STATUS.equals(action)) {
                        // {"code":"相应平台的相应的支付状态的code","msg":"支付成功/支付失败"}
                        JSONObject mJson = new JSONObject();
                        mJson.put("code", intent.getExtras().getString("code"));
                        mJson.put("msg", intent.getExtras().getString("msg"));
                        mCallback.callback(mJson.toString());
                        unregisterReceiver();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void request(Object data, WVJBResponseCallback callback) {
        try {

            if (callback != null && data != null) {

                this.mCallback = callback;

                JSONObject mJson = JSON.parseObject(data.toString());
                String alipayOrder = mJson.getString("alipayOrder");
                String wechatOrder = mJson.getString("wechatOrder");

                if (StringUtil.notEmpty(alipayOrder) && StringUtil.notEmpty(wechatOrder)) {// 2者
                    String[] array = new String[]{"支付宝", "微信"};
                    selectPayType(array, alipayOrder, wechatOrder);
                } else if (StringUtil.notEmpty(alipayOrder)) {// 阿里支付
//					String[] array = new String[]{"支付宝"};
//					selectPayType(array, alipayOrder, wechatOrder);
                    aliPay(alipayOrder);
                } else if (StringUtil.notEmpty(wechatOrder)) {// 微信支付
//					String[] array = new String[]{"微信"};
//					selectPayType(array, alipayOrder, wechatOrder);
                    wechatPay(wechatOrder);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 选择支付方式
     */
    private void selectPayType(final String[] array, final String alipayOrder, final String wechatOrder) {

        Builder builder = new Builder(mContext);
        // 设置对话框的标题
        builder.setTitle("请选择支付方式");
        builder.setSingleChoiceItems(array, 0, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mItemName = array[which];
            }
        });

        builder.setPositiveButton(R.string.ok, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (mItemName == null) {
                    mItemName = array[0];
                }
                try {
                    if ("支付宝".equals(mItemName)) {
                        aliPay(alipayOrder);
                    } else {
                        wechatPay(wechatOrder);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        builder.setNegativeButton("取消", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                JSONObject mJson = new JSONObject();
                mJson.put("code", "-999");
                mJson.put("msg", "用户取消");
                mCallback.callback(mJson.toString());
                unregisterReceiver();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }


    /**
     * 阿里支付
     */
    private void aliPay(String alipayOrder) {
        alipayOrderBean temp = JSON.parseObject(alipayOrder, alipayOrderBean.class);

        AlipayUtil ali = new AlipayUtil((HtmlActivity) mContext);
        String orderInfo = ali.getOrderInfo(temp.subject, temp.body, temp.seller_id, temp.total_fee, temp.out_trade_no, temp.notify_url);
        ali.pay(orderInfo);
        registerReceiver();
    }

    /**
     * 微信支付
     */
    private void wechatPay(String wechatOrder) {
        WechatOrderBean temp = JSON.parseObject(wechatOrder, WechatOrderBean.class);

        WXPayUtils wx = new WXPayUtils((HtmlActivity) mContext);
        if (wx.isSupport()) {
            wx.sendPayReq(temp.nonceStr, temp.sign, temp.prepayID, temp.timeStamp);
            registerReceiver();
        }

    }


    /**
     * 注册广播,获取支付结果
     */
    private void registerReceiver() {
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(AppConst.ACTION_PAY_STATUS);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastReceiver, mIntentFilter);

    }

    /**
     * 注销广播
     */
    private void unregisterReceiver() {
        try {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mBroadcastReceiver);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}

/**
 * 阿里支付实体
 *
 * @author LiaoBo
 */
class alipayOrderBean {
    public String total_fee;        // 1
    public String subject;        // productName
    public String seller_id;        // sellerID
    public String notify_url;    // notifyURL
    public String out_trade_no;    // tradeNO
    public String body;            // productDescription
}

/**
 * 微信支付实体
 *
 * @author LiaoBo
 */
class WechatOrderBean {
    public String nonceStr;    // 12345678909876543212345678909876",
    public String sign;        // asdkjasdjk1k2j312kj31kj2h3",
    public String prepayID;    // 112223332"
    public String timeStamp;
}