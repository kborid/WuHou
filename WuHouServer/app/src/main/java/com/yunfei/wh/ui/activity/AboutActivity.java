package com.yunfei.wh.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.prj.sdk.constants.Const;
import com.yunfei.wh.R;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.ui.base.BaseActivity;

/**
 * @author kborid
 * @date 2016/11/8 0008
 */
public class AboutActivity extends BaseActivity {

    private TextView tv_access;
    private TextView tv_feedback;
    private TextView tv_about_us;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_aboutus_layout);
        initViews();
        initParams();
        initListeners();
    }

    @Override
    public void initViews() {
        super.initViews();
        tv_access = (TextView) findViewById(R.id.tv_access);
        tv_feedback = (TextView) findViewById(R.id.tv_feedback);
        tv_about_us = (TextView) findViewById(R.id.tv_about);
    }

    @Override
    public void initParams() {
        super.initParams();
        tv_center_title.setText(R.string.uc_about);
    }

    @Override
    public void initListeners() {
        super.initListeners();
        tv_access.setOnClickListener(this);
        tv_feedback.setOnClickListener(this);
        tv_about_us.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_access: {
                try {
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(Intent.createChooser(intent, "请选择应用市场"));
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "未找到应用商店", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.tv_feedback: {
                if (!SessionContext.isLogin()) {
                    sendBroadcast(new Intent(Const.UNLOGIN_ACTION));
                    return;
                }
                Intent intent = new Intent(this, FeedbackActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.tv_about: {
                Intent intent = new Intent(this, AboutUsActivity.class);
                startActivity(intent);
                break;
            }
            default:
                break;
        }
    }
}
