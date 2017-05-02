package com.yunfei.wh.control;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.alibaba.fastjson.JSON;
import com.prj.sdk.util.LogUtil;
import com.prj.sdk.util.NetworkUtil;
import com.prj.sdk.util.SharedPreferenceUtil;
import com.prj.sdk.util.StringUtil;
import com.prj.sdk.util.Utils;
import com.yunfei.wh.common.AppConst;
import com.yunfei.wh.net.bean.AppInfoBean;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author kborid
 * @date 2016/8/11
 */
public class UpdateControl {

    private static final String TAG = "UpdateControl";
    private static final String DOWNLOADDIR = "download";
    private static final String TMPNAME = "WuHou.apk.tmp";
    private static final String APKNAME = "WuHou";
    private static final int TIME_OUT = 1000;
    private Context context;
    private static UpdateControl instance = null;
    private File apkFile = null;
    private AppInfoBean info = null;
    private boolean isDownloading = false;

    private UpdateControl() {
    }

    public static UpdateControl getInstance() {
        if (null == instance) {
            synchronized (UpdateControl.class) {
                if (null == instance) {
                    instance = new UpdateControl();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
    }

    public void update() {
        String appInfo = SharedPreferenceUtil.getInstance().getString(AppConst.APP_INFO, "", false);
        if (StringUtil.notEmpty(appInfo)) {
            info = JSON.parseObject(appInfo, AppInfoBean.class);
        }
        apkFile = new File(getDownloadApkPath());
    }

    public String getRemoteVersion() {
        if (info != null) {
            return info.vsid;
        }
        return null;
    }

    public String getRemoteUrl() {
        if (null != info) {
            return info.apkurls;
//            return "http://qiniu-app-cdn.pgyer.com/7f76b94109a68736be98d6afe4ee5464.apk?e=1470979653&attname=WuHouService_v2.1.2_20160728-release.apk&token=6fYeQ7_TVB5L0QSzosNFfw2HU8eJhAirMF5VxV9G:3X1F4dNJu8s7n7L9xiNxD7qphMw=";
        }
        return null;
    }

    public String getDownloadApkPath() {
        return Utils.getFolderDir(DOWNLOADDIR) + APKNAME + getRemoteVersion() + ".apk";
    }

    /**
     * 比较系统版本号
     *
     * @return
     * 0--v1等于v2；
     * -1--v1小于v2；
     * 1--v1大于v2
     */
    public int compareVersion(String v1, String v2) {
        if (v1 == null || v2 == null) {
            return 0;
        }

        if (v1.equals(v2)) {
            return 0;
        }

        String[] arrayStr1 = v1.split("\\.");
        String[] arrayStr2 = v2.split("\\.");

        int leng1 = arrayStr1.length;
        int leng2 = arrayStr2.length;
        int leng = (leng1 > leng2) ? leng2 : leng1;

        int result = 0;
        for (int i = 0; i < leng; i++) {
            result = arrayStr1[i].length() - arrayStr2[i].length();
            if (result != 0) {
                return result > 0 ? 1 : -1;
            }
            result = arrayStr1[i].compareTo(arrayStr2[i]);
            if (result != 0) {
                return result > 0 ? 1 : -1;
            }
        }

        if (leng1 > leng2) {
            result = 1;
        } else if (leng1 < leng2) {
            result = -1;
        }
        return result;
    }

    public String getCurVersionName() {
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public boolean isCanInstall() {
        LogUtil.d(TAG, "==== In isCanInstall() ===");
        if (compareVersion(getRemoteVersion(), getCurVersionName()) > 0) {
            boolean isIgnore = SharedPreferenceUtil.getInstance().getBoolean("IgnoreInstall", false);
            LogUtil.d(TAG, "========= isIgnore = !" + isIgnore + " && " + apkFile.exists());
            return apkFile.exists() && !isIgnore;
        }
        LogUtil.d(TAG, "======== return false, can't install");
        return false;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void downloadAPKFile() {
        LogUtil.d(TAG, "==== In downloadAPKFile() ===");
        if (apkFile == null) {
            return;
        }
        if (apkFile.exists()) {
            LogUtil.d(TAG, "========== apk file is exist-----need't download");
            return;
        }

        if (getRemoteUrl() == null || getRemoteUrl().equals("")) {
            LogUtil.d(TAG, "========== remote url is NULL, return");
            return;
        }

        if (getRemoteVersion() == null || getRemoteVersion().equals("")) {
            LogUtil.d(TAG, "========== remote version is NULL, return");
            return;
        }

        if (compareVersion(getRemoteVersion(), getCurVersionName()) <= 0) {
            LogUtil.d(TAG, "============== " + getRemoteVersion() + " <= " + getCurVersionName());
            return;
        }

        if (isDownloading()) {
            LogUtil.d(TAG, "========== thread is downloading-----");
            return;
        }

        new DownLoadTask().start();
    }

    private class DownLoadTask extends Thread {
        private int startPosition = 0;
        private int currentPosition = 0;
        private int totalLength = 0;
        private File tmpFile = null;
        private RandomAccessFile file = null;

        public DownLoadTask() {
        }

        @Override
        public void run() {
            try {
                isDownloading = true;
                SharedPreferenceUtil.getInstance().setBoolean("IgnoreInstall", false);
                URL url = new URL(getRemoteUrl());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(TIME_OUT * 4);
                conn.setReadTimeout(TIME_OUT * 24);  //设置读取流的等待时间,必须设置该参数
                totalLength = conn.getContentLength();
                InputStream is = conn.getInputStream();
                String tmpName = Utils.getFolderDir(DOWNLOADDIR) + TMPNAME;
                tmpFile = new File(tmpName);
                file = new RandomAccessFile(tmpFile, "rwd");
                file.seek(startPosition);
                byte[] buffer = new byte[1024 * 4];
                int len;
                //当前子线程的下载位置
                while (NetworkUtil.isNetworkAvailable() && NetworkUtil.isWifi()) {
                    len = is.read(buffer);
                    if (len == -1) {
                        break;
                    }
                    //把下载数据数据写入文件
                    file.write(buffer, 0, len);
                    currentPosition += len;
                    listener.onProgress(currentPosition);
                    if (currentPosition % (1024 * 1024) == 0) {
                        LogUtil.d(TAG, "============== currentPosition = " + currentPosition);
                    }
                }
                file.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
                listener.onFail();
            } finally {
                if (currentPosition == totalLength) {
                    LogUtil.d(TAG, "============== currentPosition = " + currentPosition + ", length = " + totalLength);
                    tmpFile.renameTo(apkFile);
                    listener.onComplete();
                }
                isDownloading = false;
                System.gc();
            }
        }
    }

    /**
     * 安装apk
     */
    public void installApk() {
        File apkfile = new File(getDownloadApkPath());
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    private DownloadListener listener = null;

    public void setDownloadlistener(DownloadListener listener) {
        this.listener = listener;
    }

    public interface DownloadListener {
        void onComplete();

        void onProgress(int progress);

        void onFail();
    }
}
