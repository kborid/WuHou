package com.yunfei.wh.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.prj.sdk.net.image.ImageLoader;
import com.prj.sdk.net.image.ImageLoader.ImageCallback;
import com.prj.sdk.util.SharedPreferenceUtil;
import com.prj.sdk.util.StringUtil;
import com.yunfei.wh.R;
import com.yunfei.wh.common.AppConst;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.net.bean.AppInfoBean;
import com.yunfei.wh.ui.base.BaseActivity;

/**
 * 我的二维码
 */
public class MyQRCodeActivity extends BaseActivity {
    private ImageView iv_qr_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_mycode_layout);
        initViews();
        initParams();
        initListeners();
    }

    @Override
    public void initViews() {
        super.initViews();
        iv_qr_code = (ImageView) findViewById(R.id.iv_qr_code);
    }

    @Override
    public void initParams() {
        super.initParams();
        tv_center_title.setText(R.string.my_code);
        String app_info = SharedPreferenceUtil.getInstance().getString(AppConst.APP_INFO, "", false);
        if (StringUtil.notEmpty(app_info)) {
            AppInfoBean json = JSON.parseObject(app_info, AppInfoBean.class);
            String inviteLink = json.invitationLink + SessionContext.mUser.USERBASIC.id;// 拼接邀请链接
            setQR(inviteLink);
        }
    }

    @Override
    public void initListeners() {
        super.initListeners();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    /**
     * 生成邀请二维码，使用联图二维码开放平台
     */
    private void setQR(String url) {
        try {
            if (StringUtil.notEmpty(url)) {
                url = url.replace("&", "%26").replaceAll("\n", "%0A");// x内容出现 & 符号时，请用 %26 代替,换行符使用 %0A

                StringBuilder sbUrl = new StringBuilder();
                sbUrl.append("http://qr.liantu.com/api.php?text=").append(url).append("&m=10");
                System.out.println("sburl = " + sbUrl);
                ImageLoader.getInstance().loadBitmap(new ImageCallback() {
                    @Override
                    public void imageCallback(Bitmap bm, String url, String imageTag) {
                        if (bm != null) {
                            iv_qr_code.setImageBitmap(bm);
                        }
                    }

                }, sbUrl.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
