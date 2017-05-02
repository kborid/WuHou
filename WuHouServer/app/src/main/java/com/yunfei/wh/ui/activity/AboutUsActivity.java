package com.yunfei.wh.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunfei.wh.R;
import com.yunfei.wh.common.AppConst;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.ui.base.BaseActivity;

public class AboutUsActivity extends BaseActivity {
    private Button btnAbout;
    private RelativeLayout test_lay;
    private Button btn_develop, btn_js;
    private TextView tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_about_act);
        initViews();
        initParams();
        initListeners();
    }

    @Override
    public void initViews() {
        super.initViews();
        btnAbout = (Button) findViewById(R.id.btn_about);
        tv_version = (TextView) findViewById(R.id.tv_version);

        test_lay = (RelativeLayout) findViewById(R.id.test_lay);
        btn_develop = (Button) findViewById(R.id.btn_develop);
        btn_js = (Button) findViewById(R.id.btn_js);
    }

    @Override
    public void initParams() {
        super.initParams();
        tv_center_title.setText(R.string.uc_about_us);
        try {
            tv_version.setText(getVersion());// 设置版本
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (AppConst.ISDEVELOP) {
            test_lay.setVisibility(View.VISIBLE);
        } else {
            test_lay.setVisibility(View.GONE);
        }
    }

    @Override
    public void initListeners() {
        super.initListeners();
        btnAbout.setOnClickListener(this);
        btn_develop.setOnClickListener(this);
        btn_js.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent mIntent = null;
        switch (v.getId()) {
            case R.id.btn_about:
                mIntent = new Intent(this, WebViewActivity.class);
                mIntent.putExtra("path", NetURL.ABOUT_AS);
                mIntent.putExtra("title", getString(R.string.uc_about));
                startActivity(mIntent);
                break;
            case R.id.btn_develop:
                mIntent = new Intent(this, HtmlActivity.class);
                mIntent.putExtra("ISDEVELOP", AppConst.ISDEVELOP);
                mIntent.putExtra("title", getString(R.string.develop_test));
                startActivity(mIntent);
                break;
            case R.id.btn_js:
                mIntent = new Intent(this, HtmlActivity.class);
                mIntent.putExtra("ISJS", true);
                mIntent.putExtra("title", getString(R.string.js_test));
                startActivity(mIntent);
                break;
            default:
                break;
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    private String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            StringBuilder sb = new StringBuilder();
            sb.append("Version：").append(version);
            if (AppConst.ISDEVELOP) {
                sb.append(" 渠道名：").append(getAppMetaData(this, "UMENG_CHANNEL"));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取渠道名
     *
     * @param ctx 此处习惯性的设置为activity，实际上context就可以
     * @return 如果没有获取成功，那么返回值为空
     */
    private static String getChannelName(Activity ctx) {
        if (ctx == null) {
            return null;
        }
        String channelName = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                // 注意此处为ApplicationInfo 而不是 ActivityInfo,因为友盟设置的meta-data是在application标签中，而不是某activity标签中，所以用ApplicationInfo
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        channelName = applicationInfo.metaData.getString("");
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return channelName;
    }

    /**
     * 获取application中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return resultData;
    }
}
