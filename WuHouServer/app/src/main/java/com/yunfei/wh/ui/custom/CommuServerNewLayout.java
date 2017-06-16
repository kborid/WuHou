package com.yunfei.wh.ui.custom;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prj.sdk.net.image.ImageLoader;
import com.prj.sdk.util.Utils;
import com.yunfei.wh.R;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.control.BundleNavi;
import com.yunfei.wh.control.ServerDispatchControl;
import com.yunfei.wh.net.bean.AppListBean;
import com.yunfei.wh.ui.activity.AllServiceActivity;

import java.util.List;

/**
 * @author kborid
 * @date 2016/7/15
 */
public class CommuServerNewLayout extends LinearLayout {
    private Context context;

    private LinearLayout server_item_layout;

    public CommuServerNewLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CommuServerNewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.commu_server_new_layout, this);
        findViews();
    }

    private void findViews() {
        server_item_layout = (LinearLayout) findViewById(R.id.server_item_lay);
    }

    public void notifyLayoutDataChange(String str, final List<AppListBean> appList) {
        if (appList != null && appList.size() > 0) {
            server_item_layout.removeAllViews();
            int length = appList.size();
            for (int i = 0; i < length; i++) {
                final AppListBean temp = appList.get(i);
                View view = LayoutInflater.from(context).inflate(R.layout.commu_server_item, null);

                LinearLayout.LayoutParams mlp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (i > 0) {
                    mlp.topMargin = Utils.dip2px(10);
                } else {
                    mlp.topMargin = 0;
                }

                ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
                TextView tv_describe = (TextView) view.findViewById(R.id.tv_describe);

                if ("AllServiceActivity".equals(temp.appurls)) {
                    iv_icon.setImageResource(R.drawable.ic_service_more);
                } else {
                    iv_icon.setImageResource(R.drawable.ic_logo_placeholder);
                    getItemIcon(temp.imgurls, iv_icon);
                }
                tv_name.setText(temp.appname);
                tv_describe.setText(temp.appdesc);
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if ("AllServiceActivity".equals(temp.appurls)) {
                            Intent intent = new Intent(context, AllServiceActivity.class);
                            intent.putExtra("FLAG", "community");
                            BundleNavi.getInstance().putInt("index", temp.pid);
                            context.startActivity(intent);
                        } else {
                            ServerDispatchControl.serverEventDispatcher(context, temp);
                        }
                    }
                });
                server_item_layout.addView(view, mlp);
            }
        }
    }

    private void getItemIcon(String imgurls, final ImageView iv_icon) {
        String url;
        if (imgurls != null) {
            url = NetURL.getWhHmApi() + imgurls;
            ImageLoader.getInstance().loadBitmap(new ImageLoader.ImageCallback() {
                @Override
                public void imageCallback(Bitmap bm, String url, String imageTag) {
                    if (bm != null) {
                        iv_icon.setImageBitmap(bm);
                    }
                }
            }, url, url);
        }
    }
}
