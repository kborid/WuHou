package com.yunfei.wh.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
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
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.yunfei.wh.R;
import com.yunfei.wh.common.AppConst;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.net.RequestBeanBuilder;
import com.yunfei.wh.net.bean.UserInfo;
import com.yunfei.wh.ui.base.BaseActivity;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录
 *
 * @author LiaoBo
 */
public class LoginActivity extends BaseActivity implements DataCallback, DialogInterface.OnCancelListener {

    private static onCancelLoginListener mCancelLogin;
    private UMSocialService mController = null;
    private String usertoken;

    private String mPlatform; //(01-新浪微博，02-腾讯QQ，03-微信，04-支付宝)
    private String thirdpartusername, thirdpartuserheadphotourl, openid, unionid;
    private RelativeLayout login_lay;
    private TextView tv_no_wx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_login);
        initViews();
        initParams();
        initListeners();
    }

    @Override
    public void initViews() {
        super.initViews();
        login_lay = (RelativeLayout) findViewById(R.id.login_lay);
        tv_no_wx = (TextView) findViewById(R.id.tv_no_wx);
    }

    @Override
    public void initParams() {
        super.initParams();
        findViewById(R.id.title_lay).setBackgroundResource(R.color.transparent);
        tv_center_title.setText(R.string.tologin);
//        SessionContext.cleanUserInfo();
        mController = UMServiceFactory.getUMSocialService("com.umeng.login");
        addWXPlatform();
    }

    @Override
    public void initListeners() {
        super.initListeners();
        login_lay.setOnClickListener(this);
        tv_no_wx.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//         super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_left_title:
                if (mCancelLogin != null) {
                    mCancelLogin.isCancelLogin(true);
                }
                this.finish();
                break;
            case R.id.login_lay:
                // 添加友盟自定义事件
                MobclickAgent.onEvent(this, "UserLogAction");

                if (WXAPIFactory.createWXAPI(this, null).isWXAppInstalled()) {
                    login(SHARE_MEDIA.WEIXIN);
                } else {
                    CustomToast.show(getString(R.string.not_install_wx), 0);
                }
                break;
            case R.id.tv_no_wx:
                Intent intent = new Intent(this, LoginAndBindActivity.class);
                intent.putExtra("index", 0);
                startActivity(intent);
                break;
        }
    }

    /**
     * 授权。如果授权成功，则获取用户信息</br>
     */
    private void login(final SHARE_MEDIA platform) {
        mController.doOauthVerify(this, platform, new UMAuthListener() {

            @Override
            public void onStart(SHARE_MEDIA platform) {
                CustomToast.show("授权开始", 0);
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                CustomToast.show("授权错误", Toast.LENGTH_SHORT);
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                String uid = value.getString("uid");
                openid = uid;
                unionid = value.getString("unionid");
                usertoken = value.getString("access_token");
                if (!TextUtils.isEmpty(uid)) {
                    // StringBuilder sb = new StringBuilder();
                    // for (String key : value.keySet()) {
                    // sb.append(";  " + key + ":" + value.getString(key));
                    // }
                    // 微博 2 用户名 screen_name，access_token，profile_image_url
                    // 微博1 头像 access_token ，userName
                    // qq 2 profile_image_url 头像 screen_name 名称
                    // qq 1 access_token，
                    // 微信 2 用户名 nickname, headimgurl
                    // 微信 1 access_token
                    getUserInfo(platform);
                } else {
                    CustomToast.show("授权失败", Toast.LENGTH_SHORT);
                }
                // CustomToast.show("授权完成", Toast.LENGTH_SHORT);
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                CustomToast.show("授权取消", Toast.LENGTH_SHORT);
            }
        });
    }

    /**
     * 获取授权平台的用户信息</br>
     */
    private void getUserInfo(final SHARE_MEDIA platform) {
        mController.getPlatformInfo(this, platform, new UMDataListener() {

            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(int status, Map<String, Object> info) {
                if (status == StatusCode.ST_CODE_SUCCESSED && info != null) {
                    try {
                        /*if (platform == SHARE_MEDIA.SINA) { // 新浪微博
                            mPlatform = "01";
                            thirdpartuserheadphotourl = info.get("profile_image_url").toString();
                            thirdpartusername = info.get("screen_name").toString();
                            if (StringUtil.isEmpty(usertoken)) {// 通过web方式登录，需要从用户信息中获取access_token
                                usertoken = info.get("access_token").toString();
                            }
                        } else if (platform == SHARE_MEDIA.QQ) { // QQ
                            mPlatform = "02";
                            thirdpartuserheadphotourl = info.get("profile_image_url").toString();
                            thirdpartusername = info.get("screen_name").toString();
                        } else */
                        if (platform == SHARE_MEDIA.WEIXIN) { // 微信
                            mPlatform = "03";
                            thirdpartuserheadphotourl = info.get("headimgurl").toString();
                            thirdpartusername = info.get("nickname").toString();
                            unionid = info.get("unionid").toString();
                        }

                        // StringBuilder sb = new StringBuilder();
                        // Set<String> keys = info.keySet();
                        // for (String key : keys) {
                        // sb.append(key + "=" + info.get(key).toString() + "\r\n");
                        // }
                        checkThirdLogin();
                    } catch (Exception e) {
                        CustomToast.show("获取用户信息失败", Toast.LENGTH_SHORT);
                    }
                } else {
                    CustomToast.show("获取用户信息失败", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    /**
     * 添加微信平台配置
     */
    private void addWXPlatform() {
        String appId = getString(R.string.wx_appid);
        String appSecret = getString(R.string.wx_appsecret);
        UMWXHandler wxHandler = new UMWXHandler(this, appId, appSecret);
        wxHandler.addToSocialSDK();
    }

    /**
     * 判断是否绑定了三方帐号
     */
    private void checkThirdLogin() {
        RequestBeanBuilder builder = RequestBeanBuilder.create(false);
        builder.addBody("openid", openid);// openid值为uid
        builder.addBody("unionid", unionid);
        builder.addBody("platform", mPlatform);
        builder.addBody("ip", Utils.getLocalIpAddress());
        ResponseData data = builder.syncRequest(builder);
        data.path = NetURL.BIND_CHECK;
        data.flag = 3;

        if (!isProgressShowing()) {
            showProgressDialog("正在登录，请稍候...", true);
        }
        requestID = DataLoader.getInstance().loadData(this, data);
    }

    /**
     * 获取用户信息
     *
     * @param ticket 票据
     */
    private void getUserInfo(String ticket) {
        RequestBeanBuilder builder = RequestBeanBuilder.create(true);

        ResponseData data = builder.syncRequest(builder);
        data.path = NetURL.GET_USER_INFO;
        data.flag = 2;
        requestID = DataLoader.getInstance().loadData(this, data);
    }

    @Override
    public void preExecute(ResponseData request) {
    }

    @Override
    public void notifyMessage(ResponseData request, ResponseData response) throws Exception {
        if (request.flag == 2) {
            removeProgressDialog();
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
            CustomToast.show("登录成功", 0);
            AppContext.mAppContext.sendBroadcast(new Intent(Const.UPDATE_USERINFO));
            if (mCancelLogin != null) {
                mCancelLogin.isCancelLogin(false);
            }
            this.finish();
            // 添加友盟自定义事件
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("userId", SessionContext.mUser.USERBASIC.id);
            MobclickAgent.onEvent(this, "UserLoginSuccess", map);
        } else if (request.flag == 3) {// 如果绑定，直接获取用户信息，没有绑定到绑定页面
            removeProgressDialog();
            JSONObject mJson = JSON.parseObject(response.body.toString());
            int flag = mJson.getInteger("flag");
            if (flag == 1) {// flag:0-未绑定 ，1-已绑定，当flag=1时，还返回accessTicket
                String accessTicket = mJson.getString("accessTicket");
                SharedPreferenceUtil.getInstance().setString(AppConst.ACCESS_TICKET, accessTicket, true);// 保存ticket
                SessionContext.setTicket(accessTicket);
                getUserInfo(accessTicket);
            } else {
                Intent intent = new Intent(this, LoginAndBindActivity.class);
                intent.putExtra("index", 1);
                intent.putExtra("thirdpartusername", thirdpartusername);
                intent.putExtra("thirdpartuserheadphotourl", thirdpartuserheadphotourl);
                intent.putExtra("openid", openid);
                intent.putExtra("unionid", unionid);
                intent.putExtra("platform", mPlatform);
                intent.putExtra("usertoken", usertoken);
                startActivity(intent);
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

    @Override
    public void onCancel(DialogInterface dialog) {
        DataLoader.getInstance().clear(requestID);
        removeProgressDialog();
        if (mCancelLogin != null) {
            mCancelLogin.isCancelLogin(true);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (mCancelLogin != null) {
                mCancelLogin.isCancelLogin(true);
            }
            this.finish();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 处理取消登录回调接口
     */
    public interface onCancelLoginListener {
        /**
         * @param isCancel true:取消登录；false:登录成功
         */
        public void isCancelLogin(boolean isCancel);
    }

    /**
     * 设置登录状态监听
     *
     * @param cancelLogin
     */
    public static final void setCancelLogin(onCancelLoginListener cancelLogin) {
        mCancelLogin = cancelLogin;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** 使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SessionContext.isLogin()) {
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCancelLogin != null) {
            mCancelLogin = null;
        }
    }
}
