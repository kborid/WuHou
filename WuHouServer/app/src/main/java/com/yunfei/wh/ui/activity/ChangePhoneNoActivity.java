package com.yunfei.wh.ui.activity;

import java.net.ConnectException;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.widget.CustomToast;
import com.yunfei.wh.R;
import com.yunfei.wh.net.RequestBeanBuilder;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.tools.SHA1;
import com.yunfei.wh.ui.base.BaseActivity;

/**
 * 更改手机号
 * 
 * @author LiaoBo
 * 
 */
public class ChangePhoneNoActivity extends BaseActivity implements DataCallback, DialogInterface.OnCancelListener {
	private EditText	et_pwd;
	private Button		btn_sbmit;
	private TextView	tv_phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_change_phone_number_act);

		initViews();
		initParams();
		initListeners();

	}

	@Override
	public void initViews() {
		super.initViews();
		tv_center_title.setText("更改手机号");
		tv_right_title.setVisibility(View.GONE);
		et_pwd = (EditText) findViewById(R.id.et_pwd);
		btn_sbmit = (Button) findViewById(R.id.btn_sbmit);
		tv_phone = (TextView) findViewById(R.id.tv_phone);
	}

	@Override
	public void initParams() {
		super.initParams();
		if (getIntent().getExtras() != null && getIntent().getExtras().getString("num") != null) {
			tv_phone.setText(getIntent().getExtras().getString("num"));
		}
	}

	@Override
	public void initListeners() {
		super.initListeners();
		btn_sbmit.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.btn_sbmit :
				
				Intent intent = new Intent(this,ChangePhoneNoBindActivity.class);
				startActivity(intent);
				
//				if (StringUtil.notEmpty(et_pwd.getText().toString().trim())) {
//					loadData();
//				} else {
//					CustomToast.show("密码不允许为空", 0);
//				}

				break;

			default :
				break;
		}

	}

	/**
	 * 加载数据
	 */
	private void loadData() {
		RequestBeanBuilder builder = RequestBeanBuilder.create(true);
		SHA1 sha1 = new SHA1();
		String pwd = sha1.getDigestOfString(et_pwd.getText().toString().trim().getBytes());
//		builder.addBody("PASSWORD", pwd).addBody("USER_ID", StringUtil.doEmpty(SessionContext.mUser.USERBASIC.userid));
		builder.addBody("USER_PWD", pwd);
		
		ResponseData data = builder.syncRequest(builder);
		data.path = NetURL.VERIFY_PWD;

		if (!isProgressShowing()) {
			showProgressDialog("正在提交，请稍候...", true);
		}
		requestID = DataLoader.getInstance().loadData(this, data);
	}

	@Override
	public void preExecute(ResponseData request) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyMessage(ResponseData request, ResponseData response) throws Exception {
		removeProgressDialog();
		if (response.body instanceof Boolean && (Boolean)response.body == true) {
			CustomToast.show("验证成功", 0);
			Intent intent = new Intent();
			intent.setClass(this, ChangePhoneNoBindActivity.class);
			startActivity(intent);
		} else {
			CustomToast.show("密码错误,请重试", 0);
		}
	}
	@Override
	public void notifyError(ResponseData request, ResponseData response, Exception e) {
		removeProgressDialog();

		String message;
		if (e != null && e instanceof ConnectException) {
			message = getString(R.string.dialog_tip_net_error);
		} else {
			message = response != null && response.data != null ? response.data.toString() : getString(R.string.dialog_tip_null_error);
		}
		CustomToast.show(message, Toast.LENGTH_LONG);

	}

	@Override
	public void onCancel(DialogInterface dialog) {
		DataLoader.getInstance().clear(requestID);
		removeProgressDialog();
	}

}
