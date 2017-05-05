package com.prj.sdk.net.data;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.prj.sdk.app.AppContext;
import com.prj.sdk.constants.Const;
import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.cache.DiskLruCache;
import com.prj.sdk.net.cache.DiskLruCache.DiskCacheType;
import com.prj.sdk.net.cache.LruCache;
import com.prj.sdk.net.cache.LruCache.CacheType;
import com.prj.sdk.net.http.HttpHelper;
import com.prj.sdk.util.GUIDGenerator;
import com.prj.sdk.util.LogUtil;
import com.prj.sdk.util.NetworkUtil;
import com.prj.sdk.util.Utils;

import org.json.JSONObject;

import java.io.File;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据异步加载
 *
 * @author Liao
 */
public class DataLoader {

    private static final String TAG = DataLoader.class.getName();

    private static DataLoader mInstance = null;
    private final int mMaxTaskCount = 3;
    private volatile int mActiveTaskCount;
    private final List<Runnable> mRequests;
    private final Map<String, Runnable> mRunningRequests;
    private final Handler mDataHandler;
    private List<String> ids;

    public List<String> mCacheUrls = new ArrayList<String>();
    private final int DEFAULT_CACHE_SIZE = (int) Math.min(Runtime.getRuntime().maxMemory() / 4, 16 * 1024 * 1024);
    private final LruCache<String, byte[]> mMemCache;
//    private final DiskLruCache<String, byte[]> mDiskCache;

    private DataLoader() {
        ids = Collections.synchronizedList(new LinkedList<String>());
        mRequests = Collections.synchronizedList(new LinkedList<Runnable>());
        mRunningRequests = Collections.synchronizedMap(new HashMap<String, Runnable>());
        mDataHandler = new Handler(Looper.getMainLooper());

        mMemCache = new LruCache<String, byte[]>(DEFAULT_CACHE_SIZE, CacheType.SIZE);
//        mDiskCache = new DiskLruCache<String, byte[]>(new File(Utils.getFolderDir("dataCache")), 50 * 1024 * 1024, DiskCacheType.SIZE);

    }

    /**
     * 获得实例的唯一全局访问点
     *
     * @return
     */
    public static DataLoader getInstance() {
        if (mInstance == null) {
            // 增加类锁,保证只初始化一次
            synchronized (DataLoader.class) {
                if (mInstance == null) {
                    mInstance = new DataLoader();
                }
            }
        }
        return mInstance;
    }

    public String loadData(DataCallback callback, ResponseData request) {
        return loadData(callback, request, null);
    }

    /**
     * UI层调用 数据接口方法
     *
     * @param callback
     * @param request
     */
    public String loadData(DataCallback callback, ResponseData request, String id) {
        if (id == null || id.trim().equals("")) {
            id = GUIDGenerator.generate();
        }
        if (ids.contains(id)) {
            return id;
        }

        RequestTask mTask = new RequestTask(callback, request, id);
        ids.add(id);
        insertRequestQueue(mTask);
        return id;
    }

    public byte[] getCacheData(String mCacheUrl) {
        byte[] data = null;
        try {
            data = mMemCache.get(mCacheUrl);
//            if (data == null) {
//                data = mDiskCache.get(mCacheUrl);
//            }
            if (data != null) {
                mMemCache.put(mCacheUrl, data);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return data;
    }

    public void removeCacheData(String mCacheUrl) {
        if (mCacheUrl == null)
            return;
        try {
            mMemCache.remove(mCacheUrl);
//            mDiskCache.remove(mCacheUrl);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public boolean isHasTask(String id) {
        return ids.contains(id);
    }

    private synchronized void flushRequests() {
        try {
            while (mActiveTaskCount < mMaxTaskCount && !mRequests.isEmpty()) {
                mActiveTaskCount++;
                Runnable request = mRequests.get(0);
                mRunningRequests.put(((RequestTask) request).id, request);
                mRequests.remove(0);
                Thread mThread = new Thread(request);
                mThread.setDaemon(true);
                mThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertRequestQueue(Runnable request) {
        mRequests.add(0, request);
        flushRequests();
    }

    /**
     * 和服务器交互线程
     *
     * @author Liao
     */
    private class RequestTask implements Runnable {
        public String id;
        public HttpHelper mHttpHelper;
        public int count;            // 计数器
        private ResponseData request;
        private ResponseData response;
        private DataCallback callback;
        private Exception mException;

        public RequestTask(DataCallback callback, ResponseData request, String id) {
            this.callback = callback;
            this.request = request;
            this.id = id;
            mHttpHelper = new HttpHelper();
        }

        @Override
        public void run() {
            try {
                if (ids.contains(id)) {
                    preExecute();
                }

                if (ids.contains(id)) {
                    doExecute();
                }

            } catch (final Exception e) {
                e.printStackTrace();
                mException = e;
            } finally {
                if (ids.contains(id)) {
                    postExecute();
                }
                clear(id);
            }
        }

        private void preExecute() {
            mDataHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (callback != null) {
                            callback.preExecute(request);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private void doExecute() throws Exception {
            // String mCacheUrl = request.path + (request.data != null ? JSON.toJSONString(request.data) : "");
            String mCacheUrl = request.path;
            if (NetworkUtil.isNetworkAvailable()) {
                LogUtil.d(TAG, "Request:" + mCacheUrl);
                if (request.data != null) {
                    LogUtil.d(TAG, "Request:" + request.data.toString());
                }
                // mPostMethod = new PostMethod(mCacheUrl);
                // RequestEntity re = new StringRequestEntity(request.data.toString(), "application/json", "utf-8");
                // mPostMethod.setRequestEntity(re);
                // // mPostMethod.setRequestBody(request.data.toString());
                // // mPostMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
                // if (request.header != null) {
                // for (String key : request.header.keySet()) {
                // mPostMethod.setRequestHeader(key, String.valueOf(request.header.get(key)));// 指定请求内容的类型
                // }
                // }
                // mClient.getHttpConnectionManager().getParams().setConnectionTimeout(18 * 1000);
                // mClient.getHttpConnectionManager().getParams().setSoTimeout(18 * 1000);
                // mClient.executeMethod(mPostMethod);
                // String json = null;
                // if (mPostMethod.getStatusCode() == HttpStatus.SC_OK) {
                // json = mPostMethod.getResponseBodyAsString();
                // }

                byte[] data = getDataFromNet();
                if (data != null) {
                    if (request.isLocal) {
                        response = new ResponseData();
                        response.data = new String(data, "UTF-8");
                    } else {
                        String json = new String(data, "UTF-8");
                        if (LogUtil.isDebug() && mCacheUrl != null) {
                            LogUtil.d(TAG, "Response:" + mCacheUrl);
                            LogUtil.d(TAG, "Response:" + json);
                        }
                        response = JSON.parseObject(json, ResponseData.class);
                    }

                    String key = request.key;
                    if (key == null || key.equals("")) {
                        key = mCacheUrl;
                    }
                    if (mCacheUrls.contains(key)) {
                        mMemCache.put(key, data);
//                        if (Utils.isSDCardEnable()) {
//                            mDiskCache.put(key, data);
//                        }
                    }
                }
            } else {
                // if (mCacheUrls.contains(mCacheUrl)) {
                // byte[] data = getCacheData(mCacheUrl);
                // if (data != null) {
                // String json = new String(data, "UTF-8");
                // if (LogUtil.isDebug() && mCacheUrl != null) {
                // LogUtil.d(TAG, "rec:" + "path:" + mCacheUrl + "response:" + json);
                // }
                // response = JSON.parseObject(json, ResponseData.class);
                // }
                // } else {
                throw new ConnectException("网络连接失败，请检查网络");
                // }
            }

        }

        private void postExecute() {
            mDataHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (callback != null) {
                            if (request.isLocal) {
                                if (response != null) {// 本地html数据请求直接返回结果
                                    callback.notifyMessage(request, response);
                                } else {
                                    callback.notifyError(request, response, mException);
                                }
                            } else {
                                if (response != null && response.head != null) {
                                    JSONObject mJson = new JSONObject(response.head.toString());
                                    String code = mJson.getString("rtnCode");
                                    if (code != null && code.equals("000000")) {
                                        callback.notifyMessage(request, response);
                                    } else {
                                        response.code = code;// 将错误code赋值给code，方便使用
                                        if (mJson.has("rtnMsg")) {
                                            response.data = mJson.getString("rtnMsg");// 将错误描述赋值给data，方便调用
                                        }
                                        if (code != null && (code.equals("900902") || code.equals("310001"))) {// 900902 票据失效
                                            Intent intent = new Intent(Const.UNLOGIN_ACTION);
                                            intent.putExtra(Const.IS_SHOW_TIP_DIALOG, true);
                                            AppContext.mAppContext.sendBroadcast(intent);// 发送登录广播
                                            response.data = "登录超时,请重新登录";
                                        }

                                        callback.notifyError(request, response, mException);
                                    }
                                } else {
                                    callback.notifyError(request, response, mException);
                                }
                            }
                        }
                    } catch (final Exception e) {
                        e.printStackTrace();
                        mException = e;
                        try {
                            if (callback != null) {
                                callback.notifyError(request, response, mException);
                            }
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }

                    }
                }
            });
        }

        private byte[] getDataFromNet() {
            byte[] data = null;
            try {
                if (NetworkUtil.isNetworkAvailable()) {
                    data = mHttpHelper.executeHttpRequest(request.path, request.type, request.header, request.data, request.isForm);
                    if (data == null && request.retry > count) {
                        count++;
                        return getDataFromNet();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

    }

    /**
     * 清除请求队列中的任务
     */
    public void clearRequests() {
        try {
            if (mRunningRequests != null && mRunningRequests.size() > 0) {
                Set<String> mRunningIds = mRunningRequests.keySet();
                for (String id : mRunningIds) {
                    clear(id);
                }
            }
            ids.clear();
            mRequests.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消任务
     *
     * @param id
     */
    public synchronized void clear(String id) {
        if (ids.contains(id)) {
            ids.remove(id);
            mActiveTaskCount--;
            flushRequests();

            RequestTask request = (RequestTask) mRunningRequests.get(id);
            // 中断http数据请求
            if (request != null) {
                mRunningRequests.remove(id);
            }
        }
    }

}