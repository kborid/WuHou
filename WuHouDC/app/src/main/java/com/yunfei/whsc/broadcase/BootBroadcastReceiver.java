package com.yunfei.whsc.broadcase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yunfei.whsc.activity.MainActivity;

/**
 * @author kborid
 * @date 2017/1/23 0023
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);  // 要启动的Activity
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivityIntent);
        }
    }
}
