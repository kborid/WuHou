package com.yunfei.wh.ui.custom;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prj.sdk.util.StringUtil;
import com.prj.sdk.util.Utils;
import com.umeng.analytics.MobclickAgent;
import com.yunfei.wh.R;
import com.yunfei.wh.ui.activity.SearchActivity;

import java.util.HashMap;

/**
 * @author kborid
 * @date 2016/7/17
 */
public class CommonTitleLayout extends LinearLayout {

    private static final int ALPHA_MAX = 255;
    private Context context;
    private LinearLayout rootView;
    private RelativeLayout userInfoLay;
    private RelativeLayout qrCodeLay;
    private RelativeLayout titleLay;
    private TextView tv_title;
//    private TextView tv_street;
    private ImageView iv_title;
    private RelativeLayout search_lay;
    private TextView tv_search;

    public void setOnClickListeners(OnClickListener l) {
        if (l != null) {
            userInfoLay.setOnClickListener(l);
            qrCodeLay.setOnClickListener(l);
            titleLay.setOnClickListener(l);
        }
        tv_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 添加友盟自定义事件
                HashMap<String, String> map = new HashMap<>();
//                map.put("userId", SessionContext.mUser.USERBASIC.id);
                MobclickAgent.onEvent(context, "Search", map);

                Intent intent = new Intent(context, SearchActivity.class);
                intent.putExtra("type", SearchActivity.GLOBAL_SEARCH);
                context.startActivity(intent);
            }
        });
    }

    public CommonTitleLayout(Context context) {
        this(context, null);
    }

    public CommonTitleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonTitleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.comm_main_title, this);
        findViews();
    }


    private void findViews() {
        rootView = (LinearLayout) findViewById(R.id.rootview);
        rootView.getBackground().mutate().setAlpha(0);
        tv_title = (TextView) findViewById(R.id.tv_title);
//        tv_street = (TextView) findViewById(R.id.tv_street);
        iv_title = (ImageView) findViewById(R.id.iv_title);
        userInfoLay = (RelativeLayout) findViewById(R.id.userinfo_lay);
        qrCodeLay = (RelativeLayout) findViewById(R.id.code_lay);
        titleLay = (RelativeLayout) findViewById(R.id.title_lay);
        titleLay.setAlpha(150 / ALPHA_MAX);
        tv_search = (TextView) findViewById(R.id.tv_search);
        tv_search.getBackground().mutate().setAlpha(150);
        search_lay = (RelativeLayout) findViewById(R.id.search_lay);
    }

    public void relizeTitleLayout(String title, boolean isPull, boolean hasSearch) {
        if (hasSearch) {
            search_lay.setVisibility(VISIBLE);
            titleLay.setVisibility(GONE);
        } else {
            search_lay.setVisibility(GONE);
            titleLay.setVisibility(VISIBLE);
            tv_title.setText(title);
            if (isPull) {
                iv_title.setVisibility(VISIBLE);
            } else {
                iv_title.clearAnimation();
                iv_title.setVisibility(GONE);
            }
        }
    }

    public void setBackgroundAlpha(int scroll) {
        int alphaValue;
        int height = Utils.dip2px(216 - 56) - Utils.mStatusBarHeight;
        alphaValue = scroll < height ? (int) ((float) scroll / height * ALPHA_MAX) : ALPHA_MAX;
        rootView.getBackground().mutate().setAlpha(alphaValue);
        titleLay.setAlpha((float) (alphaValue < 150 ? 150 : alphaValue) / ALPHA_MAX);
        tv_search.getBackground().mutate().setAlpha(alphaValue < 150 ? 150 : alphaValue);
    }

//    public void setTitleSummary(String summary) {
//        if (StringUtil.notEmpty(summary)) {
//            tv_street.setVisibility(VISIBLE);
//            tv_street.setText(summary);
//        } else {
//            tv_street.setVisibility(GONE);
//        }
//    }

    public void setTitle(String title) {
        if (StringUtil.notEmpty(title)) {
            tv_title.setText(title);
        }
    }
}
