package com.yunfei.wh.ui.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.util.StringUtil;
import com.prj.sdk.util.Utils;
import com.prj.sdk.widget.CustomToast;
import com.yunfei.wh.R;
import com.yunfei.wh.net.RequestBeanBuilder;
import com.yunfei.wh.common.AppConst;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.tools.SHA1;
import com.yunfei.wh.ui.base.BaseActivity;

import java.net.ConnectException;

/**
 * 忘记密码
 *
 * @author LiaoBo
 */
public class ForgetPwdActivity extends BaseActivity implements DataCallback, DialogInterface.OnCancelListener {
    private EditText et_phone, et_yzm, et_password, et_password2;
    private Button btn_reset, btn_getYZM;
    private String mPhoneNum;
    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_forget_pwd_act);
        initViews();
        initParams();
        initListeners();
    }

    @Override
    public void initViews() {
        super.initViews();
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_yzm = (EditText) findViewById(R.id.et_yzm);
        et_password = (EditText) findViewById(R.id.et_password);
        et_password2 = (EditText) findViewById(R.id.et_password2);
        btn_getYZM = (Button) findViewById(R.id.btn_getYZM);
        btn_reset = (Button) findViewById(R.id.btn_reset);
    }

    @Override
    public void initParams() {
        super.initParams();
        tv_center_title.setText(R.string.reset_pwd);
        findViewById(R.id.title_lay).setBackgroundResource(R.color.transparent);
        setCountDownTimer(60 * 1000, 1000);
    }

    @Override
    public void initListeners() {
        super.initListeners();
        btn_reset.setOnClickListener(this);
        btn_getYZM.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_getYZM:
                mPhoneNum = et_phone.getText().toString().trim();
                if (StringUtil.notEmpty(mPhoneNum)) {
                    if (Utils.isMobile(mPhoneNum)) {
                        loadYZM();
                    } else {
                        CustomToast.show("请输入正确的手机号", 0);
                    }
                } else {
                    CustomToast.show("请输入手机号", 0);
                }

                break;
            case R.id.btn_reset:
                String phoneNumber = et_phone.getText().toString().trim();
                String checkCode = et_yzm.getText().toString().trim();
                String pwd1 = et_password.getText().toString().trim();
                String pwd2 = et_password2.getText().toString().trim();

                if (StringUtil.isEmpty(phoneNumber)) {
                    CustomToast.show("请输入手机号码", 0);
                    return;
                }
                if (!Utils.isMobile(phoneNumber)) {
                    CustomToast.show("请输入正确的手机号码", 0);
                    return;
                }
                if (StringUtil.isEmpty(checkCode)) {
                    CustomToast.show("请输入验证码", 0);
                    return;
                }
                if (StringUtil.isEmpty(pwd1)) {
                    CustomToast.show("请输入密码", 0);
                    return;
                }
                if (pwd1.length() < 6 && pwd1.length() > 20) {
                    CustomToast.show("请输入6-20个字符的密码", 0);
                    return;
                }
                if (!pwd1.equals(pwd2)) {
                    CustomToast.show("两次密码不一致", 0);
                    return;
                }
                requestResetPwd();
                break;
            default:
                break;
        }
    }

    /**
     * 加载验证码
     */
    private void loadYZM() {
        RequestBeanBuilder b = RequestBeanBuilder.create(false);
        b.addBody("BUSINESSTYPE", getResources().getString(R.string.businessType_findPwd));
        b.addBody("MOBILENUM", mPhoneNum);

        ResponseData d = b.syncRequest(b);
        d.path = NetURL.GET_YZM;
        d.flag = 1;

        if (!isProgressShowing()) {
            showProgressDialog("正在加载，请稍候...", true);
        }
        requestID = DataLoader.getInstance().loadData(this, d);
    }

    private void requestResetPwd() {
        RequestBeanBuilder b = RequestBeanBuilder.create(false);
        b.addBody("PWDSTRENGTH", "1");
        b.addBody("FINDTYPE", "mobile");
        b.addBody("CODE", et_yzm.getText().toString());
        b.addBody("LOGIN", et_phone.getText().toString());
        SHA1 sha1 = new SHA1();
        b.addBody("PASSWORD", sha1.getDigestOfString(et_password.getText().toString().getBytes()));
        b.addBody("BUSINESSTYPE", getResources().getString(R.string.businessType_findPwd));

        ResponseData d = b.syncRequest(b);
        d.path = NetURL.FORGET_PWD;
        d.flag = 2;

        if (!isProgressShowing()) {
            showProgressDialog("正在提交，请稍候...", true);
        }
        requestID = DataLoader.getInstance().loadData(this, d);
    }

    @Override
    public void preExecute(ResponseData request) {
    }

    @Override
    public void notifyMessage(ResponseData request, ResponseData response) throws Exception {
        removeProgressDialog();
        if (request.flag == 1) {
            CustomToast.show("验证码已发送，请稍后...", 0);
            btn_getYZM.setEnabled(false);
            mCountDownTimer.start();// 启动倒计时
        } else if (request.flag == 2) {
            CustomToast.show("密码已修改", 0);
            this.finish();
        } else {

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

    /**
     * 设置倒计时
     *
     * @param millisInFuture
     * @param countDownInterval
     */
    private void setCountDownTimer(long millisInFuture, long countDownInterval) {
        mCountDownTimer = new CountDownTimer(millisInFuture, countDownInterval) {

            @Override
            public void onTick(long millisUntilFinished) {
                btn_getYZM.setText(getString(R.string.get_checknumber) + "(" + millisUntilFinished / 1000 + "s)");
            }

            @Override
            public void onFinish() {
                btn_getYZM.setEnabled(true);
                btn_getYZM.setText(R.string.get_checknumber);
            }
        };
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        DataLoader.getInstance().clear(requestID);
        removeProgressDialog();
    }
}
