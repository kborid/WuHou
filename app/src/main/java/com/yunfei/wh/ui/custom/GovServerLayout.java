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
 * @date 2016/8/17
 */
public class GovServerLayout extends LinearLayout {

    private static final int COLUME = 2;
    private Context context;

    private ImageView iv_icon;
    private TextView tv_title;
    private LinearLayout server_lay;

    public GovServerLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public GovServerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public GovServerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.gov_server_layout, this);
        findViews();
        setClickListeners();
    }

    private void findViews() {
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        tv_title = (TextView) findViewById(R.id.tv_title);
        server_lay = (LinearLayout) findViewById(R.id.server_lay);
    }

    private void setClickListeners() {

    }

    public void updateServerDataChange(String str, final List<AppListBean> appList) {
        tv_title.setText(str);
        if (appList != null && appList.size() >= 0) {
            server_lay.removeAllViews();
//            iv_icon.setBackgroundColor(R.color.main_color_wh);
            int length = appList.size();
            int row = length / COLUME + length % COLUME;
            for (int i = 0; i < row; i++) {
                LinearLayout rowLay = new LinearLayout(context);
                rowLay.setMotionEventSplittingEnabled(false);
                rowLay.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                lp.setLayoutDirection(LinearLayout.HORIZONTAL);
                rowLay.setLayoutParams(lp);
                rowLay.removeAllViews();

                for (int j = 0; j < COLUME; j++) {
                    int index = i * COLUME + j;
                    if (index < length) {

                        final AppListBean temp = appList.get(index);
                        View itemView = LayoutInflater.from(context).inflate(R.layout.gov_server_item, null);
                        LinearLayout.LayoutParams itemLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        itemLp.weight = 1;
                        itemView.setLayoutParams(itemLp);
                        ImageView iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
                        TextView tv_name = (TextView) itemView.findViewById(R.id.tv_name);

                        if (temp.appurls.equals("AllServiceActivity")) {
                            iv_icon.setImageResource(R.drawable.ic_service_more);
                        } else {
                            iv_icon.setImageResource(R.drawable.ic_logo_placeholder);
                            getItemIcon(NetURL.API_LINK, temp.imgurls, iv_icon);
                        }
                        tv_name.setText(temp.appname);
                        itemView.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (temp.appurls.equals("AllServiceActivity")) {
                                    Intent intent = new Intent(context, AllServiceActivity.class);
                                    intent.putExtra("FLAG", "service");
                                    BundleNavi.getInstance().putInt("index", temp.pid);
                                    context.startActivity(intent);
                                } else {
                                    ServerDispatchControl.serverEventDispatcher(context, temp);
                                }
                            }
                        });
                        rowLay.addView(itemView);

                        View line = new View(context);
                        LinearLayout.LayoutParams lineLp = new LinearLayout.LayoutParams(Utils.dip2px(0.5F), Utils.dip2px(60));
                        line.setLayoutParams(lineLp);
                        line.setBackgroundColor(0xffe9e9e9);
                        rowLay.addView(line);
                    } else {
                        View empty = new View(context);
                        LinearLayout.LayoutParams emptyLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        emptyLp.weight = 1;
                        empty.setLayoutParams(emptyLp);
                        rowLay.addView(empty);
                    }
                }
                server_lay.addView(rowLay);

                View lineView2 = new View(context);
                LinearLayout.LayoutParams lineLp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(0.5F));
                lineView2.setLayoutParams(lineLp2);
                lineView2.setBackgroundColor(0xffe9e9e9);
                server_lay.addView(lineView2);
            }
        }
    }

    private void getItemIcon(String domain, String imgurls, final ImageView iv_icon) {
        String url;
        if (imgurls != null) {
            url = domain + imgurls;
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
