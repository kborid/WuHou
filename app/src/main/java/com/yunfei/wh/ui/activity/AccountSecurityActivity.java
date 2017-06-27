package com.yunfei.wh.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.prj.sdk.app.AppContext;
import com.prj.sdk.constants.Const;
import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.util.SharedPreferenceUtil;
import com.prj.sdk.util.StringUtil;
import com.prj.sdk.widget.CustomToast;
import com.umeng.analytics.MobclickAgent;
import com.yunfei.wh.R;
import com.yunfei.wh.common.AppConst;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.net.RequestBeanBuilder;
import com.yunfei.wh.net.bean.ThirdPartyBindListBean;
import com.yunfei.wh.ui.base.BaseActivity;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;

/**
 * 账户安全
 *
 * @author LiaoBo
 */
public class AccountSecurityActivity extends BaseActivity implements DataCallback, DialogInterface.OnCancelListener {
    private TextView tv_certification, tv_phone_number, tv_email, tv_change_password;
    private Button btn_logout;
    private TableRow tr_certification, tr_phone_number, tr_email, tr_third_party, tr_change_password;
    private ImageView iv_qq, iv_wx, iv_wb;
    private final int MODIFY_THIRD_PARTY = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_account_security_act);
        initViews();
        initParams();
        initListeners();
    }

    @Override
    public void initViews() {
        super.initViews();
        tv_center_title.setText(R.string.uc_accountsafe);
        tv_certification = (TextView) findViewById(R.id.tv_certification);
        tv_phone_number = (TextView) findViewById(R.id.tv_phone_number);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_change_password = (TextView) findViewById(R.id.tv_change_password);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        tr_certification = (TableRow) findViewById(R.id.tr_certification);
        tr_phone_number = (TableRow) findViewById(R.id.tr_phone_number);
        tr_email = (TableRow) findViewById(R.id.tr_email);
        tr_third_party = (TableRow) findViewById(R.id.tr_third_party);
        tr_change_password = (TableRow) findViewById(R.id.tr_change_password);
        iv_qq = (ImageView) findViewById(R.id.iv_qq);
        iv_wx = (ImageView) findViewById(R.id.iv_wx);
        iv_wb = (ImageView) findViewById(R.id.iv_wb);
    }

    @Override
    public void initParams() {
        super.initParams();
        loadThirdPartyBindList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (SessionContext.isLogin()) {
                String level = SessionContext.mUser.USERBASIC.levelstatus;
                if ("04".equals(level)) {
                    tv_certification.setText(R.string.rejected);
                } else if ("02".equals(level)) {
                    tv_certification.setText(R.string.auditing);
                } else if ("03".equals(level)) {
                    tv_certification.setText(R.string.audited);
                } else {
                    tv_certification.setText(R.string.not_audited);
                }
                String phone = SessionContext.mUser.USERAUTH.mobilenum;
                if (StringUtil.notEmpty(phone)) {
                    tv_phone_number.setText(phone.substring(0, phone.length() - (phone.substring(3)).length()) + "****" + phone.substring(7));
                }
                String data = SharedPreferenceUtil.getInstance().getString(AppConst.THIRDPARTYBIND, null, false);
                if (StringUtil.notEmpty(data)) {
                    setThirdPartyBind(data, false);
                }
            } else {
                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initListeners() {
        super.initListeners();
        tr_certification.setOnClickListener(this);
        tr_phone_number.setOnClickListener(this);
        tr_email.setOnClickListener(this);
        tr_third_party.setOnClickListener(this);
        tr_change_password.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent mIntent = null;
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tr_certification:// 实名认证
//                mIntent = new Intent(this, IdentityVerificationActivity.class);
                mIntent = new Intent(this, HtmlActivity.class);
                mIntent.putExtra("title", "实名认证");
                mIntent.putExtra("path", NetURL.IDENTITY_VERIFICATE);
                startActivity(mIntent);
                break;
            case R.id.tr_phone_number:
                mIntent = new Intent(this, ChangePhoneNoActivity.class);
                mIntent.putExtra("num", tv_phone_number.getText().toString());
                startActivity(mIntent);
                break;
            case R.id.tr_email:

                break;
            case R.id.tr_third_party:
                mIntent = new Intent(this, BindThirdPartyActivity.class);
                startActivityForResult(mIntent, MODIFY_THIRD_PARTY);
                break;
            case R.id.tr_change_password:
                mIntent = new Intent(this, ChangePwdActivity.class);
                startActivity(mIntent);
                break;
            case R.id.btn_logout:
                cancellationTicket();
                break;

            default:
                break;
        }
    }

    /**
     * 退出登录
     */
    public void logout() {
        // 添加友盟自定义事件
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("userId", SessionContext.mUser.USERBASIC.id);
        MobclickAgent.onEvent(this, "UserLogoutSuccess", map);
        SessionContext.cleanUserInfo();
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("Tag", false) == true) {
            this.setResult(RESULT_OK, null);
        }
        this.finish();
    }

    /**
     * 注销票据
     */
    public void cancellationTicket() {

        RequestBeanBuilder builder = RequestBeanBuilder.create(false);
        builder.addBody("accessTicket", SessionContext.getTicket());

        ResponseData data = builder.syncRequest(builder);
        data.path = NetURL.REMOVE_TICKET;
        data.flag = 1;
        if (!isProgressShowing()) {
            showProgressDialog(getString(R.string.logging_out), true);
        }
        requestID = DataLoader.getInstance().loadData(this, data);
    }

    /**
     * 加载第三方的绑定列表
     */
    public void loadThirdPartyBindList() {
        RequestBeanBuilder builder = RequestBeanBuilder.create(true);

        ResponseData data = builder.syncRequest(builder);
        data.path = NetURL.BIND_LIST;
        data.flag = 2;

        if (!isProgressShowing()) {
            showProgressDialog(null, true);
        }
        requestID = DataLoader.getInstance().loadData(this, data);

    }

    @Override
    public void onCancel(DialogInterface dialog) {
        DataLoader.getInstance().clear(requestID);
        removeProgressDialog();
    }

    @Override
    public void preExecute(ResponseData request) {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyMessage(ResponseData request, ResponseData response) throws Exception {
        removeProgressDialog();
        if (request.flag == 1) {
            logout();
            AppContext.mAppContext.sendBroadcast(new Intent(Const.UPDATE_USERINFO));
        } else {
            setThirdPartyBind(response.body.toString(), true);
        }
    }

    @Override
    public void notifyError(ResponseData request, ResponseData response, Exception e) {
        removeProgressDialog();
        if (request.flag == 1) {
            logout();
        } else {
            String message;
            if (e != null && e instanceof ConnectException) {
                message = getString(R.string.dialog_tip_net_error);
            } else {
                message = response != null && response.data != null ? response.data.toString() : getString(R.string.dialog_tip_null_error);
            }
            CustomToast.show(message, Toast.LENGTH_LONG);
        }
    }

    /**
     * 设置三方绑定
     *
     * @param data
     * @param isSave
     */
    public void setThirdPartyBind(String data, boolean isSave) {
        List<ThirdPartyBindListBean> temp = JSON.parseArray(data, ThirdPartyBindListBean.class);
        if (temp != null && !temp.isEmpty()) {
            for (int i = 0; i < temp.size(); i++) {
                if ("01".equals(temp.get(i).platform)) {
                    iv_wb.setVisibility(View.VISIBLE);
                } else if ("02".equals(temp.get(i).platform)) {
                    iv_qq.setVisibility(View.VISIBLE);
                } else {
                    iv_wx.setVisibility(View.VISIBLE);
                }

            }
            if (isSave)
                SharedPreferenceUtil.getInstance().setString(AppConst.THIRDPARTYBIND, data, false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            super.onActivityResult(requestCode, resultCode, intent);
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            switch (requestCode) {
                case MODIFY_THIRD_PARTY://重置状态
                    iv_wb.setVisibility(View.GONE);
                    iv_qq.setVisibility(View.GONE);
                    iv_wx.setVisibility(View.GONE);
                    String data = SharedPreferenceUtil.getInstance().getString(AppConst.THIRDPARTYBIND, null, false);
                    if (StringUtil.notEmpty(data)) {
                        // 已绑定的列表
                        List<ThirdPartyBindListBean> mList = JSON.parseArray(data, ThirdPartyBindListBean.class);
                        if (mList != null && !mList.isEmpty()) {
                            for (int i = 0; i < mList.size(); i++) {
                                ThirdPartyBindListBean temp = mList.get(i);
                                if ("01".equals(temp.platform)) {
                                    iv_wb.setVisibility(View.VISIBLE);
                                } else if ("02".equals(temp.platform)) {
                                    iv_qq.setVisibility(View.VISIBLE);
                                } else {
                                    iv_wx.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
