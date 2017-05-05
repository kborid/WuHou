package com.yunfei.whsc.app;

import android.app.Application;

import com.prj.sdk.app.AppContext;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.util.ActivityTack;
import com.prj.sdk.util.Utils;
import com.yunfei.whsc.common.CrashHandler;

/**
 * 应用全文
 *
 * @author LiaoBo
 */
public class PRJAppplcation extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext.init(this);
        CrashHandler.getInstance().init(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActivityTack.getInstanse().exit();
        DataLoader.getInstance().clearRequests();
    }
}
