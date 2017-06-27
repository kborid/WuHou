package com.yunfei.wh.ui.custom;

import android.content.ClipboardManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.prj.sdk.widget.CustomToast;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yunfei.wh.R;
import com.yunfei.wh.control.ShareControl;

import java.util.List;

/**
 * @author kborid
 * @date 2016/10/26 0026
 */
public class CustomShareView extends LinearLayout implements View.OnClickListener {
    private Context context;
    private int[] mRes = {/*R.drawable.iv_menu_save, */R.drawable.iv_menu_copy, R.drawable.iv_menu_refresh/*, R.drawable.iv_menu_fout*/};
    private String[] mTitle = {/*"收藏", */"复制链接", "刷新"/*, "调整字体"*/};

    private String title;
    private String url;

    private LinearLayout wx_lay;
    private LinearLayout circle_lay;
    private LinearLayout qq_lay;
    private LinearLayout sina_lay;
    //    private LinearLayout msg_lay;
    private LinearLayout share_layout;
    private View lineview;
    private LinearLayout menu_layout;
    private TextView tv_cancel;

    public CustomShareView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CustomShareView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public CustomShareView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.custom_share_layout, this);
        findViews();
        setOnClickListeners();
        ShareControl.getInstance(context).addWXPlatform();
        ShareControl.getInstance(context).addQQQZonePlatform();
        ShareControl.getInstance(context).addSinaPlatform();
    }

    private void findViews() {
        wx_lay = (LinearLayout) findViewById(R.id.wx_share);
        circle_lay = (LinearLayout) findViewById(R.id.circle_share);
        qq_lay = (LinearLayout) findViewById(R.id.qq_share);
        sina_lay = (LinearLayout) findViewById(R.id.sina_share);
//        msg_lay = (LinearLayout) findViewById(R.id.msg_share);

        ((ImageView) wx_lay.findViewById(R.id.iv_icon)).setImageResource(R.drawable.iv_wx);
        ((TextView) wx_lay.findViewById(R.id.tv_title)).setText("微信好友");

        ((ImageView) circle_lay.findViewById(R.id.iv_icon)).setImageResource(R.drawable.iv_wx_circle);
        ((TextView) circle_lay.findViewById(R.id.tv_title)).setText("微信朋友圈");

        ((ImageView) qq_lay.findViewById(R.id.iv_icon)).setImageResource(R.drawable.iv_qq);
        ((TextView) qq_lay.findViewById(R.id.tv_title)).setText("手机QQ");

        ((ImageView) sina_lay.findViewById(R.id.iv_icon)).setImageResource(R.drawable.iv_sina);
        ((TextView) sina_lay.findViewById(R.id.tv_title)).setText("新浪微博");

//        ((ImageView) msg_lay.findViewById(R.id.iv_icon)).setImageResource(R.drawable.iv_share_sms);
//        ((TextView) msg_lay.findViewById(R.id.tv_title)).setText("短信");

        share_layout = (LinearLayout) findViewById(R.id.share_layout);
        lineview = findViewById(R.id.line_view);
        menu_layout = (LinearLayout) findViewById(R.id.menu_layout);

        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
    }

    private void setOnClickListeners() {
        wx_lay.setOnClickListener(this);
        circle_lay.setOnClickListener(this);
        qq_lay.setOnClickListener(this);
        sina_lay.setOnClickListener(this);
//        msg_lay.setOnClickListener(this);

        tv_cancel.setOnClickListener(this);
    }

    public void updateThirdMenu(List<ShareBeanInfo> list, final String title, final String url) {
        this.title = title;
        this.url = url;
        // 更新分享的内容
        ShareControl.getInstance(context).setShareContent(url, title, url, R.drawable.icon);

        // 删除所有子view
        menu_layout.removeAllViews();
        menu_layout.setVisibility(GONE);
        lineview.setVisibility(GONE);

        share_layout.removeAllViews();

        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                View v = LayoutInflater.from(context).inflate(R.layout.lv_share_item, null);
                ImageView imageView = (ImageView) v.findViewById(R.id.iv_icon);
                TextView textView = (TextView) v.findViewById(R.id.tv_title);
                //TODO set image
                textView.setText(list.get(i).title);
                final int finalI = i;
                v.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.action(finalI);
                        }
                        dismiss();
                    }
                });
                if ("share".equals(list.get(i).type)) {
                    share_layout.addView(v);
                } else {
                    menu_layout.addView(v);
                }
            }
        }

        // 收藏
//        View save = LayoutInflater.from(context).inflate(R.layout.lv_share_item, null);
//        ImageView iv_save = (ImageView) save.findViewById(R.id.iv_icon);
//        TextView tv_save = (TextView) save.findViewById(R.id.tv_title);
//        iv_save.setImageResource(mRes[0]);
//        tv_save.setText(mTitle[0]);
//        save.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TODO onCLick
//                dismiss();
//            }
//        });
//        menu_layout.addView(save);

        // 复制链接
        View copy = LayoutInflater.from(context).inflate(R.layout.lv_share_item, null);
        ImageView iv_copy = (ImageView) copy.findViewById(R.id.iv_icon);
        TextView tv_copy = (TextView) copy.findViewById(R.id.tv_title);
        iv_copy.setImageResource(mRes[0]);
        tv_copy.setText(mTitle[0]);
        copy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(url);
                CustomToast.show("成功复制到剪贴板", Toast.LENGTH_SHORT);
                dismiss();
            }
        });
        menu_layout.addView(copy);

        // 刷新
        View refresh = LayoutInflater.from(context).inflate(R.layout.lv_share_item, null);
        ImageView iv_refresh = (ImageView) refresh.findViewById(R.id.iv_icon);
        TextView tv_refresh = (TextView) refresh.findViewById(R.id.tv_title);
        iv_refresh.setImageResource(mRes[1]);
        tv_refresh.setText(mTitle[1]);
        refresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.refresh();
                }
                dismiss();
            }
        });
        menu_layout.addView(refresh);

        // 调整字体
//        View font = LayoutInflater.from(context).inflate(R.layout.lv_share_item, null);
//        ImageView iv_font = (ImageView) font.findViewById(R.id.iv_icon);
//        TextView tv_font = (TextView) font.findViewById(R.id.tv_title);
//        iv_font.setImageResource(mRes[3]);
//        tv_font.setText(mTitle[3]);
//        font.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TODO onCLick
//                dismiss();
//            }
//        });
//        menu_layout.addView(font);

        lineview.setVisibility(VISIBLE);
        menu_layout.setVisibility(VISIBLE);
    }

    @Override
    public void onClick(View v) {
        SHARE_MEDIA sm = null;
        switch (v.getId()) {
            case R.id.wx_share:
                sm = SHARE_MEDIA.WEIXIN;
                break;
            case R.id.circle_share:
                sm = SHARE_MEDIA.WEIXIN_CIRCLE;
                break;
            case R.id.qq_share:
                sm = SHARE_MEDIA.QQ;
                break;
            case R.id.sina_share:
                sm = SHARE_MEDIA.SINA;
                break;
//            case R.id.msg_share:
//                ShareControl.getInstance(context).sendSMS(title + "\n" + url);
//                break;
            case R.id.tv_cancel:
                dismiss();
                break;
            default:
                break;
        }

        if (sm != null) {
            ShareControl.getInstance(context).postShare(sm);
//            ShareControl.getInstance(context).directShare(sm);
        }
        dismiss();
    }

    private void dismiss() {
        if (listener != null) {
            listener.dismiss();
        }
    }

    public interface OnFunctionListener {
        void dismiss();

        void action(int i);

        void refresh();
    }

    private OnFunctionListener listener = null;

    public void setOnDismissListener(OnFunctionListener listener) {
        this.listener = listener;
    }
}
