package com.yunfei.wh.control;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.prj.sdk.util.StringUtil;
import com.umeng.analytics.MobclickAgent;
import com.yunfei.wh.R;
import com.yunfei.wh.net.bean.AppListBean;
import com.yunfei.wh.ui.activity.CustomerServiceActivity;
import com.yunfei.wh.ui.activity.DiscoveryActivity;
import com.yunfei.wh.ui.activity.HtmlActivity;
import com.yunfei.wh.ui.activity.MapActivity;
import com.yunfei.wh.ui.activity.OrderScreensActivity;
import com.yunfei.wh.ui.dialog.CustomDialog;

import java.util.HashMap;

/**
 * @author kborid
 * @date 2016/9/26
 */
public class ServerDispatchControl {

    public static void serverEventDispatcher(Context context, final AppListBean bean) {

        Intent mIntent = null;
        String url = bean.appurls;

        // 添加友盟自定义事件
        HashMap<String, String> map = new HashMap<>();
        map.put("url", url);
        MobclickAgent.onEvent(context, "ServiceDidTapped", map);

        if (StringUtil.isEmpty(url)) {
            return;
        }

        if (url.startsWith("WuHou://Native/CustomerService")) {
            mIntent = new Intent(context, CustomerServiceActivity.class);
        } else if (url.startsWith("WuHou://Native/Setting/prefs:root=WIFI")) {
            mIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        } else if (url.startsWith("WuHou://Native/Tel")) {
            String num = url.substring("WuHou://Native/Tel/".length());
            Uri dialUri = Uri.parse("tel:" + num);
            mIntent = new Intent(Intent.ACTION_DIAL, dialUri);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else if (url.startsWith("WuHou://Native/Msg")) {
            String num = url.substring("WuHou://Native/Msg/".length());
            Uri smsToUri = Uri.parse("smsto:");
            mIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
            mIntent.putExtra("address", num); // 电话号码，这行去掉的话，默认就没有电话
            mIntent.setType("vnd.android-dir/mms-sms");
        } else if (url.startsWith("WuHou://Native/Developing")) {
            CustomDialog dialog = new CustomDialog(context);
            dialog.setMessage(R.string.developing);
            dialog.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else if (url.startsWith("WuHou://Native/Discovery")) {
            mIntent = new Intent(context, DiscoveryActivity.class);
        } else if (url.startsWith("WuHou://Native/TableList")) {
            mIntent = new Intent(context, OrderScreensActivity.class);
            String path = url.substring("WuHou://Native/TableList/".length());
            BundleNavi.getInstance().putString("path", path);
        } else if (url.startsWith("WuHou://Native/Map/School")) {
            mIntent = new Intent(context, MapActivity.class);
        } else {
            mIntent = new Intent(context, HtmlActivity.class);
            mIntent.putExtra("title", bean.appname);
            mIntent.putExtra("path", bean.appurls);
            mIntent.putExtra("id", String.valueOf(bean.id));
        }

        if (null != mIntent) {
            context.startActivity(mIntent);
        }
    }
}
