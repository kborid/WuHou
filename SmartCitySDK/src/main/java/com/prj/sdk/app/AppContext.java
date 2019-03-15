package com.prj.sdk.app;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.prj.sdk.db.DBManager;
import com.prj.sdk.net.data.DataLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * 记录应用全局信息
 *
 * @author LiaoBo
 */
public final class AppContext {

    public static final Handler mMainHandler = new Handler(Looper.getMainLooper()); // 公共Handler
    public static Map<String, Object> mMemoryMap = null; // 提供调用memory存取值
    public static DBManager mDBManager = null;
    /*
     * 初始化上下文
     */
    public static Context mAppContext = null; // Application的上下文

    public static void init(Context context) {
        mAppContext = context.getApplicationContext();
        mMemoryMap = new HashMap<>();
        mDBManager = DBManager.getInstance(context, null);
    }

    /**
     * 销毁全局变量
     */
    public static void destory() {
        DataLoader.getInstance().clearRequests();
        mMemoryMap.clear();
        mDBManager = null;
    }

}
