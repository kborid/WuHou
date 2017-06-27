package com.yunfei.wh.ui.activity;

import java.net.ConnectException;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.util.StringUtil;
import com.prj.sdk.widget.CustomToast;
import com.yunfei.wh.R;
import com.yunfei.wh.net.RequestBeanBuilder;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.tools.SHA1;
import com.yunfei.wh.ui.base.BaseActivity;

/**
 * 更改登录密码
 * 
 * @author LiaoBo
 * 
 */
public class ChangePwdActivity extends BaseActivity implements DataCallback, DialogInterface.OnCancelListener {
	private EditText	et_old_pwd, et_new_pwd, et_new_pwd2;
	private Button		btn_save;
	private String		mPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_change_pwd_act);

		initViews();
		initParams();
		initListeners();

	}

	@Override
	public void initViews() {
		super.initViews();
		tv_center_title.setText("更改登录密码");
		tv_right_title.setVisibility(View.GONE);

		et_old_pwd = (EditText) findViewById(R.id.et_old_pwd);
		btn_save = (Button) findViewById(R.id.btn_save);
		et_new_pwd = (EditText) findViewById(R.id.et_new_pwd);
		et_new_pwd2 = (EditText) findViewById(R.id.et_confirm_pwd);
	}

	@Override
	public void initParams() {
		super.initParams();
	}

	@Override
	public void initListeners() {
		super.initListeners();
		btn_save.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_save:
			loadData();
			break;

		default:
			break;
		}

	}

	/**
	 * 加载数据
	 */
	private void loadData() {

		String OLDPWD = et_old_pwd.getText().toString().trim();
		mPassword = et_new_pwd.getText().toString().trim();
		String NEWPWD2 = et_new_pwd2.getText().toString().trim();
		if (StringUtil.isEmpty(OLDPWD)) {
			CustomToast.show("请输入原密码", 0);
			return;
		}
		if (StringUtil.isEmpty(mPassword)) {
			CustomToast.show("请输入新密码", 0);
			return;
		}
		if (mPassword.length() < 6 || mPassword.length() > 20) {
			CustomToast.show("请输入6-20个字符的新密码", 0);
			return;
		}
		if (!mPassword.equals(NEWPWD2)) {
			CustomToast.show("两次密码不一致", 0);
			return;
		}
		if(OLDPWD.equals("mPassword")){
			CustomToast.show("新密码和原密码不能一致", 0);
			return;
		}
		
		RequestBeanBuilder builder = RequestBeanBuilder.create(true);
		
		builder.addBody("PWDSTRENGTH", "1");//密码强弱
		SHA1 sha1 = new SHA1();
		builder.addBody("OLDPASSWORD", sha1.getDigestOfString(OLDPWD.getBytes()));
		builder.addBody("NEWPASSWORD", sha1.getDigestOfString(mPassword.getBytes()));

		ResponseData data = builder.syncRequest(builder);
		data.path = NetURL.UPDATA_LOGIN_PWD;
		data.flag = 1;

		if (!isProgressShowing()) {
			showProgressDialog(getString(R.string.loading), true);
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
		if (request.flag == 1) {
			CustomToast.show("修改成功", 0);
			this.finish();
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
