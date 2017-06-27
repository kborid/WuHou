package com.yunfei.wh.ui.custom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prj.sdk.app.AppContext;
import com.prj.sdk.constants.Const;
import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.net.image.ImageLoader;
import com.prj.sdk.util.LogUtil;
import com.prj.sdk.util.StringUtil;
import com.yunfei.wh.BuildConfig;
import com.yunfei.wh.R;
import com.yunfei.wh.broatcast.UnLoginBroadcastReceiver;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.net.RequestBeanBuilder;
import com.yunfei.wh.ui.activity.AboutActivity;
import com.yunfei.wh.ui.activity.AccountInfoActivity;
import com.yunfei.wh.ui.activity.CustomerServiceActivity;
import com.yunfei.wh.ui.activity.HtmlActivity;
import com.yunfei.wh.ui.activity.MyNotifyActivity;

/**
 * @author kborid
 * @date 2016/7/7
 */
public class UserCenterLayout extends RelativeLayout implements View.OnClickListener, DataCallback {
    private Context context;

    private LinearLayout lay_login_detail, lay_login_detail2;
    private RelativeLayout lay_unlogin_note;

    private CircleImageViewWithBound iv_photo;
    private TextView tv_username, tv_userid;
    private TextView tv_myorder, tv_myemail, tv_mydealbyother;
    private TextView tv_account_info, tv_msg, tv_server, tv_about;
    private TextView tv_msg_count;

    private Button btn_login;

    private String photoUrl = null;

    public UserCenterLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public UserCenterLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public UserCenterLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.user_center_layout, this);
        findViews();
        setListeners();
        updateUserInfo();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.UPDATE_USERINFO);
        context.registerReceiver(receiver, intentFilter);
    }

    private void findViews() {
        lay_login_detail = (LinearLayout) findViewById(R.id.detail_lay);
        lay_login_detail2 = (LinearLayout) findViewById(R.id.detail_lay2);
        lay_unlogin_note = (RelativeLayout) findViewById(R.id.note_lay);

        iv_photo = (CircleImageViewWithBound) findViewById(R.id.iv_photo);
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_userid = (TextView) findViewById(R.id.tv_userid);

        tv_myorder = (TextView) findViewById(R.id.tv_myorder);
        tv_myemail = (TextView) findViewById(R.id.tv_myemail);
        tv_mydealbyother = (TextView) findViewById(R.id.tv_mydealbyorther);

        tv_account_info = (TextView) findViewById(R.id.tv_account_info);
        tv_msg = (TextView) findViewById(R.id.tv_message);
        tv_server = (TextView) findViewById(R.id.tv_server);
        View line_server = findViewById(R.id.line_server);
        tv_about = (TextView) findViewById(R.id.tv_about);

        tv_msg_count = (TextView) findViewById(R.id.tv_msg_count);

        TextView tv_note = (TextView) findViewById(R.id.tv_note);
        tv_note.setText(R.string.note2);
        if (BuildConfig.FLAVOR.equals("liangjiang")) {
            tv_note.setVisibility(INVISIBLE);
            tv_server.setVisibility(GONE);
            line_server.setVisibility(GONE);
        }
        btn_login = (Button) findViewById(R.id.btn_login);
    }

    private void setListeners() {
        tv_myorder.setOnClickListener(this);
        tv_myemail.setOnClickListener(this);
        tv_mydealbyother.setOnClickListener(this);
        tv_account_info.setOnClickListener(this);
        tv_msg.setOnClickListener(this);
        tv_server.setOnClickListener(this);
        tv_about.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    public void updateUserInfo() {
        LogUtil.d("dw", "updateUserInfo()");
        if (SessionContext.isLogin()) {
            tv_username.setVisibility(VISIBLE);
            tv_userid.setVisibility(GONE);
            if (BuildConfig.FLAVOR.equals("liangjiang")) {
                lay_login_detail.setVisibility(GONE);
            } else if (BuildConfig.FLAVOR.equals("wuhou")) {
                lay_login_detail.setVisibility(VISIBLE);
            }
            lay_login_detail2.setVisibility(VISIBLE);
            lay_unlogin_note.setVisibility(GONE);

            tv_username.setText(StringUtil.isEmpty(SessionContext.mUser.USERBASIC.nickname) ? SessionContext.mUser.USERBASIC.username : SessionContext.mUser.USERBASIC.nickname);
            tv_userid.setText(SessionContext.mUser.USERBASIC.id);
            final String url = SessionContext.mUser.USERBASIC.getPhotoUrl();
            AppContext.mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (!StringUtil.isEmpty(url)) {
                        if (url.equals(photoUrl)) {
                            iv_photo.setImageBitmap(ImageLoader.getInstance().getCacheBitmap(photoUrl));
                        } else {
                            photoUrl = url;
                            if (photoUrl != null && photoUrl.length() > 0) {
                                ImageLoader.getInstance().loadBitmap(new ImageLoader.ImageCallback() {
                                    @Override
                                    public void imageCallback(Bitmap bm, String url, String imageTag) {
                                        iv_photo.setImageBitmap(bm);
                                    }
                                }, photoUrl);
                            }
                        }
                    }
                }
            });
            // 更新消息数量
            requestNotifyCount();
        } else {
            iv_photo.setImageResource(R.drawable.def_photo_gary);
            photoUrl = "";
            tv_username.setVisibility(GONE);
            tv_userid.setVisibility(GONE);
            lay_login_detail.setVisibility(GONE);
            lay_login_detail2.setVisibility(GONE);
            lay_unlogin_note.setVisibility(VISIBLE);
        }
        LogUtil.d("dw", "updateUserInfo complete");
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        if (v.getId() != R.id.tv_about) {
            if (!SessionContext.isLogin()) {
                context.sendBroadcast(new Intent(UnLoginBroadcastReceiver.ACTION_NAME));
                return;
            }
        }

        switch (v.getId()) {
            case R.id.tv_myorder:
                if (!StringUtil.isEmpty(NetURL.MY_ORDER)) {
                    intent = new Intent(context, HtmlActivity.class);
                    intent.putExtra("path", NetURL.MY_ORDER);
                }
                break;
            case R.id.tv_myemail:
                if (!StringUtil.isEmpty(NetURL.MY_ISSUER)) {
                    intent = new Intent(context, HtmlActivity.class);
                    intent.putExtra("path", NetURL.MY_ISSUER);
                }
                break;
            case R.id.tv_mydealbyorther:
                if (!StringUtil.isEmpty(NetURL.MY_DELEGATE)) {
                    intent = new Intent(context, HtmlActivity.class);
                    intent.putExtra("path", NetURL.MY_DELEGATE);
                }
                break;
            case R.id.tv_account_info:
                intent = new Intent(context, AccountInfoActivity.class);
                break;
            case R.id.tv_message:
                intent = new Intent(context, MyNotifyActivity.class);
                if (tv_msg_count.isShown()) {
                    tv_msg_count.setVisibility(GONE);
                }
                break;
            case R.id.tv_server:
                intent = new Intent(context, CustomerServiceActivity.class);
                break;
            case R.id.tv_about:
                intent = new Intent(context, AboutActivity.class);
                break;
            default:
                break;
        }

        if (null != intent) {
            context.startActivity(intent);
        }
    }

    private void requestNotifyCount() {
        RequestBeanBuilder b = RequestBeanBuilder.create(true);
        ResponseData d = b.syncRequest(b);
        d.path = NetURL.NOTIFY_COUNT;
        d.flag = 0;
        DataLoader.getInstance().loadData(this, d);
    }

    @Override
    public void preExecute(ResponseData request) {

    }

    @Override
    public void notifyMessage(ResponseData request, ResponseData response) throws Exception {
        if (request.flag == 0) {
            String str = response.body.toString();
            if (str != null && str.length() > 0) {
                if (str.equals("0")) {
                    tv_msg_count.setVisibility(View.GONE);
                } else {
                    tv_msg_count.setVisibility(View.VISIBLE);
                    tv_msg_count.setText(str);
                }
            } else {
                tv_msg_count.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void notifyError(ResponseData request, ResponseData response, Exception e) {

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(Const.UPDATE_USERINFO)) {
                updateUserInfo();
            }
        }
    };

    public void unregisterBroadReceiver() {
        if (receiver != null) {
            context.unregisterReceiver(receiver);
        }
    }
}
