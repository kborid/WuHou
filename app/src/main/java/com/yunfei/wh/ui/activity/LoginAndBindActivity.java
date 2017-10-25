package com.yunfei.wh.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.prj.sdk.app.AppContext;
import com.prj.sdk.constants.Const;
import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.util.DateUtil;
import com.prj.sdk.util.LogUtil;
import com.prj.sdk.util.SharedPreferenceUtil;
import com.prj.sdk.util.StringUtil;
import com.prj.sdk.util.Utils;
import com.prj.sdk.widget.CustomToast;
import com.umeng.analytics.MobclickAgent;
import com.yunfei.wh.R;
import com.yunfei.wh.common.AppConst;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.net.RequestBeanBuilder;
import com.yunfei.wh.net.bean.UserInfo;
import com.yunfei.wh.ui.base.BaseActivity;

import java.net.ConnectException;
import java.util.HashMap;

/**
 * @author kborid
 * @date 2016/10/20 0020
 */
public class LoginAndBindActivity extends BaseActivity implements DataCallback {

    private int index = 0;
    private CountDownTimer mCountDownTimer;
    private String mPlatform; //(01-新浪微博，02-腾讯QQ，03-微信，04-支付宝)
    private String thirdpartusername, thirdpartuserheadphotourl, openid, unionid, usertoken;
    private EditText et_phone, et_yzm;
    private Button btn_yzm;
    private Button btn_submit;
    private LinearLayout agreement_layout;
    private RelativeLayout checkBox_lay;
    private CheckBox checkBox;
    private TextView tv_agreement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_bind_activity);
        initViews();
        initParams();
        initListeners();
    }

    @Override
    public void initViews() {
        super.initViews();
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_yzm = (EditText) findViewById(R.id.et_yzm);
        btn_yzm = (Button) findViewById(R.id.btn_getYZM);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        agreement_layout = (LinearLayout) findViewById(R.id.agreement_layout);
        checkBox_lay = (RelativeLayout) findViewById(R.id.checkBox_lay);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        tv_agreement = (TextView) findViewById(R.id.tv_agreement);
    }

    @Override
    public void dealIntent() {
        super.dealIntent();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            index = bundle.getInt("index");
            thirdpartusername = bundle.getString("thirdpartusername");
            thirdpartuserheadphotourl = bundle.getString("thirdpartuserheadphotourl");
            openid = bundle.getString("openid");
            unionid = bundle.getString("unionid");
            mPlatform = bundle.getString("platform");
            usertoken = bundle.getString("usertoken");
        }
    }

    @Override
    public void initParams() {
        super.initParams();
        if (index == 0) {
            agreement_layout.setVisibility(View.GONE);
            tv_center_title.setText(R.string.tologin);
            btn_submit.setText(R.string.login);
        } else {
            agreement_layout.setVisibility(View.VISIBLE);
            tv_center_title.setText(R.string.bind_phone);
            btn_submit.setText(R.string.bind);
        }
        findViewById(R.id.title_lay).setBackgroundResource(R.color.transparent);
        setCountDownTimer(60 * 1000, 1000);
    }

    @Override
    public void initListeners() {
        super.initListeners();
        btn_yzm.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        tv_agreement.setOnClickListener(this);
        checkBox_lay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_getYZM: {
                String phone = et_phone.getText().toString().trim();
                if (StringUtil.isEmpty(phone)) {
                    CustomToast.show("请输入手机号", 0);
                    return;
                }
                if (!Utils.isMobile(phone)) {
                    CustomToast.show("请输入正确的手机号", 0);
                    return;
                }
                requestYZM();
                break;
            }
            case R.id.checkBox_lay:
                checkBox.setChecked(!checkBox.isChecked());
                break;
            case R.id.tv_agreement:
                Intent mIntent = new Intent(this, WebViewActivity.class);
                mIntent.putExtra("title", "注册协议");
                mIntent.putExtra("path", NetURL.REGISTER_AGEMENNT);
                startActivity(mIntent);
                break;
            case R.id.btn_submit: {
                String phone = et_phone.getText().toString().trim();
                String code = et_yzm.getText().toString().trim();
                if (StringUtil.isEmpty(phone)) {
                    CustomToast.show("请输入手机号", 0);
                    return;
                }
                if (!Utils.isMobile(phone)) {
                    CustomToast.show("请输入正确的手机号", 0);
                    return;
                }
                if (StringUtil.isEmpty(code)) {
                    CustomToast.show("请输入验证码", Toast.LENGTH_SHORT);
                    return;
                }
                if (index == 0) {
                    requestLogin();
                } else {
                    if (!checkBox.isChecked()) {
                        CustomToast.show("请阅读并同意注册协议", Toast.LENGTH_SHORT);
                        return;
                    }
                    requestBindWXPhone();
                }
                break;
            }
            default:
                break;
        }
    }

    private void requestYZM() {
        RequestBeanBuilder b = RequestBeanBuilder.create(false);
        b.addBody("BUSINESSTYPE", getResources().getString(R.string.businessType_loginYZM));
        b.addBody("MOBILENUM", et_phone.getText().toString().trim());

        ResponseData d = b.syncRequest(b);
        d.path = NetURL.GET_YZM;
        d.flag = 0;

        if (!isProgressShowing()) {
            showProgressDialog("正在加载，请稍候...", true);
        }
        requestID = DataLoader.getInstance().loadData(this, d);
    }

    private void requestLogin() {
        RequestBeanBuilder b = RequestBeanBuilder.create(false);
        b.addBody("mobile", et_phone.getText().toString().trim());
        b.addBody("yzm", et_yzm.getText().toString().trim());
        b.addBody("businesstype", getResources().getString(R.string.businessType_loginYZM));
        b.addBody("CHANNELID", "2");

        ResponseData d = b.syncRequest(b);
        d.path = NetURL.YZM_LOGIN;
//        d.path = "http://192.168.1.25:8080/wh_portal/service/CW9008";
        d.flag = 1;

        if (!isProgressShowing()) {
            showProgressDialog(null, false);
        }
        requestID = DataLoader.getInstance().loadData(this, d);
    }

    private void requestBindWXPhone() {
        RequestBeanBuilder b = RequestBeanBuilder.create(false);
        b.addBody("mobile", et_phone.getText().toString().trim());
        b.addBody("yzm", et_yzm.getText().toString().trim());
        b.addBody("businesstype", getResources().getString(R.string.businessType_loginYZM));
        b.addBody("CHANNELID", "2");
        b.addBody("siteid", SessionContext.getAreaInfo(1));
        b.addBody("platform", mPlatform);
        b.addBody("thirdpartusername", thirdpartusername);
        b.addBody("thirdpartuserheadphotourl", thirdpartuserheadphotourl);
        b.addBody("openid", openid);
        b.addBody("unionid", unionid);
        b.addBody("usertoken", usertoken);

        ResponseData d = b.syncRequest(b);
        d.path = NetURL.BIND_WX;
//        d.path = "http://192.168.1.25:8080/wh_portal/service/CW90261";
        d.flag = 2;

        if (!isProgressShowing()) {
            showProgressDialog(null, false);
        }
        requestID = DataLoader.getInstance().loadData(this, d);
    }

    private void requestUserInfo() {
        RequestBeanBuilder builder = RequestBeanBuilder.create(true);

        ResponseData data = builder.syncRequest(builder);
        data.path = NetURL.GET_USER_INFO;
        data.flag = 3;
        requestID = DataLoader.getInstance().loadData(this, data);
    }

    @Override
    public void preExecute(ResponseData request) {

    }

    @Override
    public void notifyMessage(ResponseData request, ResponseData response) throws Exception {
        removeProgressDialog();
        if (response != null && !response.equals("")) {
            String res = response.body.toString();
            if (request.flag == 0) {
                CustomToast.show("验证码已发送，请稍候...", Toast.LENGTH_LONG);
                btn_yzm.setEnabled(false);
                mCountDownTimer.start();// 启动倒计时
            } else if (request.flag == 1 || request.flag == 2) {
                JSONObject mJson = JSON.parseObject(response.body.toString());
                String accessTicket = mJson.getString("accessTicket");
                SharedPreferenceUtil.getInstance().setString(AppConst.ACCESS_TICKET, accessTicket, true);// 保存ticket
                SessionContext.setTicket(accessTicket);
                requestUserInfo();
            } else if (request.flag == 3) {
                if (StringUtil.isEmpty(response.body.toString()) || response.body.toString().equals("{}")) {
                    CustomToast.show("获取用户信息失败，请重试", 0);
                    return;
                }
                SessionContext.mUser = JSON.parseObject(response.body.toString(), UserInfo.class);
                LogUtil.d(getClass().getSimpleName(), response.body.toString());

                if (SessionContext.mUser == null || StringUtil.isEmpty(SessionContext.mUser.USERBASIC)) {
                    CustomToast.show("获取用户信息失败，请重试", 0);
                    return;
                }
                SharedPreferenceUtil.getInstance().setString(AppConst.LAST_LOGIN_DATE, DateUtil.getCurDateStr(null), false);// 保存登录时间
                SharedPreferenceUtil.getInstance().setString(AppConst.USER_PHOTO_URL, SessionContext.mUser.USERBASIC != null ? SessionContext.mUser.USERBASIC.getPhotoUrl() : "", false);
                SharedPreferenceUtil.getInstance().setString(AppConst.USER_INFO, response.body.toString(), true);
                LogUtil.d("UserInfo", response.body.toString());
                // SharedPreferenceUtil.getInstance().setString(AppConst.THIRDPARTYBIND, "", false);//置空第三方绑定信息，需要在详情页面重新获取
                CustomToast.show(index == 0 ? "登录成功" : "绑定成功", 0);
                AppContext.mAppContext.sendBroadcast(new Intent(Const.LOGIN_SUCCESS));
                AppContext.mAppContext.sendBroadcast(new Intent(Const.UPDATE_USERINFO));
                this.finish();
                // 添加友盟自定义事件
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("userId", SessionContext.mUser.USERBASIC.id);
                MobclickAgent.onEvent(this, "UserLoginSuccess", map);
            }
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
                btn_yzm.setText(getString(R.string.get_checknumber) + "(" + millisUntilFinished / 1000 + "s)");
            }

            @Override
            public void onFinish() {
                btn_yzm.setEnabled(true);
                btn_yzm.setText(R.string.get_checknumber);
            }
        };
    }
}
