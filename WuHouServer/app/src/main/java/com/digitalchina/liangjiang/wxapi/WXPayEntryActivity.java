package com.digitalchina.liangjiang.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.prj.sdk.util.LogUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yunfei.wh.common.AppConst;
import com.yunfei.wh.common.pay.wxpay.Constants;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private static final String	TAG	= "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI				api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.pay_result);

		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);

		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		// 0 成功 -1 失败 -2取消支付
		LogUtil.d(TAG, "onPayFinish, errCode = " + resp.errCode);

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			int code = resp.errCode;
			String msg = "";
			switch (code) {
				case 0 :
					msg = "支付成功";
					break;
				case -1 :
					msg = "支付失败";
					break;
				case -2 :
					msg = "支付取消";
					break;

				default :
					msg = "支付失败";
					break;
			}
			
			Intent mIntent = new Intent(AppConst.ACTION_PAY_STATUS);
			mIntent.putExtra("code", code);
			mIntent.putExtra("msg", msg);
			LocalBroadcastManager.getInstance(this).sendBroadcast(mIntent);
			this.finish();
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle("提示");
//			builder.setMessage(msg);
//			builder.setNegativeButton("确定", new OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					dialog.dismiss();
//					WXPayEntryActivity.this.finish();
//				}
//			});
//			builder.show();
		}
	}
}