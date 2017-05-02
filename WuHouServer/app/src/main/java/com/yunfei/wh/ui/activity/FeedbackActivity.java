package com.yunfei.wh.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.prj.sdk.constants.Const;
import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.util.StringUtil;
import com.prj.sdk.widget.CustomToast;
import com.yunfei.wh.R;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.net.RequestBeanBuilder;
import com.yunfei.wh.ui.base.BaseActivity;

import java.net.ConnectException;

/**
 * 意见反馈
 *
 * @author LiaoBo
 */
public class FeedbackActivity extends BaseActivity implements DataCallback, DialogInterface.OnCancelListener {
    private EditText et_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_feedback_act);

        initViews();
        initParams();
        initListeners();

    }

    @Override
    public void initViews() {
        super.initViews();
        tv_center_title.setText(R.string.uc_suggest_feedback);
        tv_right_title.setText(R.string.suggest_submit);
        et_content = (EditText) findViewById(R.id.et_content);
    }

    @Override
    public void initParams() {
        super.initParams();
    }

    @Override
    public void initListeners() {
        super.initListeners();
        tv_right_title.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_right_title:
                if (!SessionContext.isLogin()) {
                    sendBroadcast(new Intent(Const.UNLOGIN_ACTION));
                    return;
                }
                if (!StringUtil.notEmpty(et_content.getText().toString().trim())) {
                    CustomToast.show("内容不允许为空", 0);
                    return;
                }
                loadData();
                break;
            default:
                break;
        }
    }

    /**
     * 加载数据
     */
    private void loadData() {
        RequestBeanBuilder builder = RequestBeanBuilder.create(false);
        builder.addBody("content", et_content.getText().toString().trim()).addBody("login", StringUtil.doEmpty(SessionContext.mUser.USERAUTH.login));

        ResponseData requster = builder.syncRequest(builder);
        requster.flag = 1;
        requster.path = NetURL.WH_FEEDBACK;

        if (!isProgressShowing()) {
            showProgressDialog(getString(R.string.submitting), true);
        }
        requestID = DataLoader.getInstance().loadData(this, requster);
    }

    @Override
    public void preExecute(ResponseData request) {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyMessage(ResponseData request, ResponseData response) throws Exception {
        removeProgressDialog();
        CustomToast.show("提交成功", 0);
        this.finish();
    }

    @Override
    public void notifyError(ResponseData request, ResponseData response, Exception e) {
        removeProgressDialog();

        String message;
        if (e != null && e instanceof ConnectException) {
            message = getString(R.string.dialog_tip_net_error);
        } else {
            message = response != null && response.data != null ? response.data.toString() : getString(R.string.dialog_tip_null_error);
        }
        CustomToast.show(message, Toast.LENGTH_LONG);

    }

    @Override
    public void onCancel(DialogInterface dialog) {
        DataLoader.getInstance().clear(requestID);
        removeProgressDialog();
    }

}
