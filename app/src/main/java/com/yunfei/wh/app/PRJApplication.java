package com.yunfei.wh.app;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.webkit.WebView;

import com.meiqia.core.callback.OnInitCallback;
import com.meiqia.meiqiasdk.uilimageloader.UILImageLoader;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.prj.sdk.algo.Algorithm3DES;
import com.prj.sdk.algo.AlgorithmData;
import com.prj.sdk.app.AppContext;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.util.ActivityTack;
import com.prj.sdk.util.LogUtil;
import com.prj.sdk.util.SharedPreferenceUtil;
import com.umeng.analytics.MobclickAgent;
import com.yunfei.wh.R;
import com.yunfei.wh.broatcast.UnLoginBroadcastReceiver;
import com.yunfei.wh.broatcast.WifiStatusBroadcastReceiver;
import com.yunfei.wh.common.AppConst;
import com.yunfei.wh.common.CrashHandler;
import com.yunfei.wh.control.AMapLocationControl;
import com.yunfei.wh.control.UpdateControl;
import com.yunfei.wh.permission.PermissionsChecker;

import cn.jpush.android.api.JPushInterface;

/**
 * 应用全文
 *
 * @author LiaoBo
 */
public class PRJApplication extends Application {
    UnLoginBroadcastReceiver mReceiver = new UnLoginBroadcastReceiver();
    WifiStatusBroadcastReceiver wifiReceiver = new WifiStatusBroadcastReceiver();

    private PermissionsChecker mPermissionsChecker;

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext.init(this);
        CrashHandler.getInstance().init(this);
        MobclickAgent.setDebugMode(AppConst.ISDEVELOP);// 普通测试流程，打开调试模式
        MobclickAgent.updateOnlineConfig(this);// 友盟统计发送策略，在线参数如配置时间、开关等
        MobclickAgent.openActivityDurationTrack(false); // 禁止默认的页面统计方式
        boolean mEnable = SharedPreferenceUtil.getInstance().getBoolean(AppConst.PUSH_ENABLE, true);
        if (mEnable) {
            JPushInterface.setDebugMode(AppConst.ISDEVELOP); // 设置开启日志,发布时请关闭日志
            JPushInterface.init(this); // 初始化 JPush
        }
        UpdateControl.getInstance().init(this);

        // 动态注册登录广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UnLoginBroadcastReceiver.ACTION_NAME);
        registerReceiver(mReceiver, intentFilter);
        registerReceiver(wifiReceiver, new IntentFilter());
        initMeiChat();
//        LeakCanary.install(this);
        AMapLocationControl.getInstance().startLocationOnce(this);

        if (LogUtil.isDebug()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }

        mPermissionsChecker = new PermissionsChecker(this);

        AlgorithmData data = new AlgorithmData();
        String sourceStr = "I am your father yyyyyyyyyyyy!";
        data.setDataMing(sourceStr);
        Algorithm3DES.encryptMode(data);
        System.out.println(data.toString());
        String encodedStr = data.getDataMi();
        AlgorithmData data2 = new AlgorithmData();
        data2.setDataMi(encodedStr);
        data2.setKey(data.getKey());
        Algorithm3DES.decryptMode(data2);
        System.out.println(data2.toString());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActivityTack.getInstanse().exit();
        DataLoader.getInstance().clearRequests();
    }

    private void initMeiChat() {
        MQConfig.init(AppContext.mAppContext, getString(R.string.MeiChatAppKey), new UILImageLoader(), new OnInitCallback() {
            @Override
            public void onSuccess(String s) {
                configMeiChat();
            }

            @Override
            public void onFailure(int i, String s) {
            }
        });
    }

    private void configMeiChat() {
        // 配置自定义信息
        MQConfig.ui.backArrowIconResId = R.drawable.m_back;
        MQConfig.ui.titleBackgroundResId = R.color.main_color_wh;
        MQConfig.ui.titleTextColorResId = R.color.white;
        MQConfig.ui.rightChatBubbleColorResId = R.color.main_color_wh;
        MQConfig.ui.rightChatTextColorResId = R.color.white;
    }

    public static PermissionsChecker getPermissionsChecker(Context context) {
        PRJApplication app = (PRJApplication) context.getApplicationContext();
        return app.mPermissionsChecker;
    }
}
