package com.yunfei.wh.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meiqia.meiqiasdk.util.MQIntentBuilder;
import com.prj.sdk.util.StringUtil;
import com.yunfei.wh.R;
import com.yunfei.wh.broatcast.UnLoginBroadcastReceiver;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.ui.base.BaseActivity;

import java.util.HashMap;

/**
 * @author duanwei
 */
public class CustomerServiceActivity extends BaseActivity {

    private String[] sexArray = null;
    private LinearLayout note_lay;
    private ImageView iv_server_banner;
    private TextView tv_note;
    private Button btn_login;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_service_act);
        initViews();
        initParams();
        initListeners();
    }


    @Override
    public void initViews() {
        super.initViews();
        tv_center_title.setText(R.string.uc_server);
        note_lay = (LinearLayout) findViewById(R.id.note_lay);
        iv_server_banner = (ImageView) findViewById(R.id.iv_server_banner);
        tv_note = (TextView) findViewById(R.id.tv_note);
        btn_login = (Button) findViewById(R.id.btn_login);
    }

    private void refreshLayout() {
        if (SessionContext.isLogin()) {
            note_lay.setVisibility(View.GONE);
            iv_server_banner.setVisibility(View.GONE);

            HashMap<String, String> clientInfo = new HashMap<>();

            String nickName = SessionContext.mUser.USERBASIC.nickname;
            if (!StringUtil.isEmpty(nickName)) {
                clientInfo.put("name", nickName);
            }
            String sex = SessionContext.mUser.USERBASIC.sex;
            if (!StringUtil.isEmpty(sex)) {
                sex = sex.equals("01") ? sexArray[0] : sexArray[1];
                clientInfo.put("gender", sex);
            }
            String address = SessionContext.mUser.LOCALUSER.residence;
            if (!StringUtil.isEmpty(address)) {
                clientInfo.put("address", address);
            }
            String email = SessionContext.mUser.USERAUTH.email;
            if (!StringUtil.isEmpty(email)) {
                clientInfo.put("email", email);
            }
            String avatar = SessionContext.mUser.USERBASIC.getPhotoUrl();
            if (!StringUtil.isEmpty(avatar)) {
                clientInfo.put("avatar", avatar);
            }
            String tel = SessionContext.mUser.USERAUTH.mobilenum;
            if (!StringUtil.isEmpty(tel)) {
                clientInfo.put("tel", tel);
            }

            Intent intent = new MQIntentBuilder(this, CustomizedMQConversationActivity.class).setClientInfo(clientInfo).build();
            startActivity(intent);
            this.finish();
        } else {
            note_lay.setVisibility(View.VISIBLE);
            iv_server_banner.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initParams() {
        super.initParams();

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.width = getResources().getDisplayMetrics().widthPixels;
        lp.height = (int) ((float) lp.width / 2.25);
        iv_server_banner.setLayoutParams(lp);

        tv_note.setText(R.string.note1);
        sexArray = getResources().getStringArray(R.array.sex_array);
    }

    @Override
    public void initListeners() {
        super.initListeners();
        btn_login.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLayout();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_login:
                this.sendBroadcast(new Intent(UnLoginBroadcastReceiver.ACTION_NAME));
                break;
            default:
                break;
        }
    }
}