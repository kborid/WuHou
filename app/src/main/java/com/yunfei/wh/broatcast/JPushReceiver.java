package com.yunfei.wh.broatcast;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSONObject;
import com.prj.sdk.app.AppContext;
import com.prj.sdk.util.LogUtil;
import com.yunfei.wh.control.BundleNavi;
import com.yunfei.wh.ui.activity.HtmlActivity;

import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == intent) {
            return;
        }
        String action = intent.getAction();
        LogUtil.d(TAG, "onReceive - " + action);
        Bundle bundle = intent.getExtras();
        LogUtil.d(TAG, "Extras: " + printBundle(bundle));

        if (action.equals(JPushInterface.ACTION_REGISTRATION_ID)) {
            String id = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            LogUtil.d(TAG, "Regist Success! ID : " + id);
        } else if (action.equals(JPushInterface.ACTION_MESSAGE_RECEIVED)) {
            String msg = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            LogUtil.d(TAG, "Received Message: " + msg);

        } else if (action.equals(JPushInterface.ACTION_NOTIFICATION_RECEIVED)) {
            String notify = bundle.getString(JPushInterface.EXTRA_ALERT);
            LogUtil.d(TAG, "Received Notification: " + notify);

        } else if (action.equals(JPushInterface.ACTION_NOTIFICATION_OPENED)) {
            LogUtil.d(TAG, "Open Notification");
            // 上报用户的通知栏被打开，或者用于上报用户自定义消息被展示等客户端需要统计的事件。
            JPushInterface.reportNotificationOpened(context,
                    bundle.getString(JPushInterface.EXTRA_MSG_ID));
            openNotification(context, bundle);
        } else if (action.equals(JPushInterface.ACTION_RICHPUSH_CALLBACK)) {
            LogUtil.d(
                    TAG,
                    "Received RICHPUSH_CALLBACK: "
                            + bundle.getString(JPushInterface.EXTRA_EXTRA));
        } else if (action.equals(JPushInterface.ACTION_CONNECTION_CHANGE)) {
            boolean connected = intent.getBooleanExtra(
                    JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            LogUtil.w(TAG, "Connect status changed : " + connected);
        } else {
            LogUtil.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    private void openNotification(Context context, Bundle bundle) {
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Intent intent = null;
        JSONObject json = JSONObject.parseObject(extras);
        String value = "";
        if (json.containsKey("url")) {
            value = json.getString("url");
        }

        if (!isRunActivity(context, AppContext.mAppContext.getPackageName())) {
            intent = context.getPackageManager().getLaunchIntentForPackage(
                    AppContext.mAppContext.getPackageName());
            if (value != null && !value.equals("")) {
                BundleNavi.getInstance().putString("path", value);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            if (value != null && !value.equals("")) {
                intent = new Intent(context, HtmlActivity.class);
                intent.putExtra("path", value);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        }

        if (null != intent) {
            context.startActivity(intent);
        }
    }

    private boolean isRunActivity(Context context, String packageName) {
        ActivityManager __am = (ActivityManager) context
                .getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> __list = __am.getRunningTasks(100);
        if (__list.size() == 0)
            return false;
        for (RunningTaskInfo task : __list) {
            if (task.topActivity.getPackageName().equals(packageName)) {

                Intent activityIntent = new Intent();
                activityIntent.setComponent(task.topActivity);
                activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(activityIntent);
                return true;
            }
        }
        return false;
    }
}
