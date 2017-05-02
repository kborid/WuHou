package com.yunfei.wh.broatcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.prj.sdk.util.NetworkUtil;
import com.yunfei.wh.control.UpdateControl;

/**
 * @author kborid
 * @date 2016/8/11
 */
public class WifiStatusBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {//wifi连接上与否
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
            } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
//                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (NetworkUtil.isNetworkAvailable()) {
                    UpdateControl.getInstance().downloadAPKFile();
                }
            }

        } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {//wifi打开与否
            int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
            if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
            } else if (wifistate == WifiManager.WIFI_STATE_ENABLED) {
            }
        }
    }
}
