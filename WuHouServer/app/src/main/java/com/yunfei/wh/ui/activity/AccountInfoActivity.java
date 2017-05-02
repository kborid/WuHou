package com.yunfei.wh.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yunfei.wh.R;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.ui.base.BaseActivity;

/**
 * @author kborid
 * @date 2016/11/8 0008
 */
public class AccountInfoActivity extends BaseActivity {

    private TextView tv_userinfo;
    private TextView tv_account;
    private TextView tv_auth;
    private TextView tv_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_accountinfo_layout);
        initViews();
        initParams();
        initListeners();
    }

    @Override
    public void initViews() {
        super.initViews();
        tv_userinfo = (TextView) findViewById(R.id.tv_userinfo);
        tv_account = (TextView) findViewById(R.id.tv_account_safe);
        tv_auth = (TextView) findViewById(R.id.tv_auth);
        tv_address = (TextView) findViewById(R.id.tv_address);
    }

    @Override
    public void initParams() {
        super.initParams();
        tv_center_title.setText(R.string.uc_account_info);
    }

    @Override
    public void initListeners() {
        super.initListeners();
        tv_userinfo.setOnClickListener(this);
        tv_account.setOnClickListener(this);
        tv_auth.setOnClickListener(this);
        tv_address.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent = null;
        switch (v.getId()) {
            case R.id.tv_userinfo:
                intent = new Intent(this, PersonalDataActivity.class);
                break;
            case R.id.tv_account_safe:
                intent = new Intent(this, AccountSecurityActivity.class);
                break;
            case R.id.tv_auth:
                intent = new Intent(this, IdentityVerificationActivity.class);
                break;
            case R.id.tv_address:
                intent = new Intent(this, AddressManageActivity.class);
                break;
            default:
                break;
        }

        if (null != intent) {
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!SessionContext.isLogin()) {
            this.finish();
        }
    }
}
