package com.yunfei.wh.broatcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.prj.sdk.app.AppContext;
import com.prj.sdk.constants.Const;
import com.prj.sdk.util.ActivityTack;
import com.yunfei.wh.R;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.ui.activity.LoginActivity;
import com.yunfei.wh.ui.dialog.CustomDialog;

/**
 * 未登录广播
 *
 * @author LiaoBo
 */
public class UnLoginBroadcastReceiver extends BroadcastReceiver {

    private Context context;
    public static final String ACTION_NAME = Const.UNLOGIN_ACTION;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action = intent.getAction();
        if (!ACTION_NAME.equals(action)) {
            return;
        }

        boolean isShowDialog = intent.getBooleanExtra(Const.IS_SHOW_TIP_DIALOG, false);
        if (isShowDialog) {
            showLoginDialog();
        } else {
            intentLogin();
        }
    }

    private void showLoginDialog() {

        String msg;
        if (SessionContext.isLogin()) {
            msg = "登录信息过期，是否重新登录？";
        } else {
            msg = "您还没有登录，是否立即登录？";
        }
        // 注销登录状态
        SessionContext.cleanUserInfo();


        CustomDialog dialog = new CustomDialog(ActivityTack.getInstanse().getCurrentActivity());
        dialog.setCancelable(false);
        dialog.setMessage(msg);
        dialog.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intentLogin();
            }
        });

        dialog.show();
    }

    /**
     * 跳转登录页面
     */
    private void intentLogin() {
        Intent mIntent = new Intent(context, LoginActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mIntent);
        AppContext.mAppContext.sendBroadcast(new Intent(Const.UPDATE_USERINFO));
    }
}
