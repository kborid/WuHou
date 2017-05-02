package com.prj.sdk.util;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.inputmethod.InputMethodManager;

import com.prj.sdk.app.AppContext;

import org.apache.http.NameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 全局公共类
 *
 * @author Liao
 */
public class Utils {

    /**
     * 屏幕宽
     */
    public static int mScreenWidth = 0;
    /**
     * 屏幕高
     */
    public static int mScreenHeight = 0;

    /**
     * 状态栏高度
     */
    public static int mStatusBarHeight = 0;

    /**
     * 导航栏高度
     */
    public static int mNavigationBarHeight = 0;

    /**
     * 作用：
     * <p>
     * 初始化设置手机屏幕大小
     * </p>
     *
     * @param mActivity
     */
    public static void initScreenSize(Activity mActivity) {
        Display dis = mActivity.getWindowManager().getDefaultDisplay();
        mScreenWidth = dis.getWidth();
        mScreenHeight = dis.getHeight();

        int statusBarResId = mActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (statusBarResId > 0) {
            mStatusBarHeight = mActivity.getResources().getDimensionPixelSize(statusBarResId);
        }

        int navigationBarResId = mActivity.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (navigationBarResId > 0) {
            mNavigationBarHeight = mActivity.getResources().getDimensionPixelSize(navigationBarResId);
        }
    }

    public static boolean hasNavBar() {
        int id = AppContext.mAppContext.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && AppContext.mAppContext.getResources().getBoolean(id);
    }

    public static int dip2px(float dipValue) {

        final float scale = AppContext.mAppContext.getResources().getDisplayMetrics().density;

        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dp(float pxValue) {
        final float scale = AppContext.mAppContext.getResources().getDisplayMetrics().density;

        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * @param subFolder cache的文件夹
     * @return
     */
    public static String getFolderDir(String subFolder) {
        String rootDir = null;
        if (subFolder == null) {
            subFolder = "";
        }
        if (isSDCardEnable()) {
            // SD-card available
            rootDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + AppContext.mAppContext.getPackageName() + File.separator + subFolder
                    + File.separator;

            // 创建文件目录
            File file = new File(rootDir);
            if (!file.exists()) // 创建目录
            {
                file.mkdirs();
            }
        } else {
            rootDir = AppContext.mAppContext.getCacheDir() + File.separator + subFolder + File.separator;
            // 创建文件目录
            File file = new File(rootDir);
            if (!file.exists()) // 创建目录
            {
                file.mkdirs();
            }
        }

        return rootDir;
    }

    /**
     * 判断文件是否存在
     *
     * @param subFolder
     * @return
     */
    public static boolean isFolderDir(String subFolder) {
        String rootDir = null;
        if (subFolder == null) {
            subFolder = "";
        }
        if (Utils.isSDCardEnable()) {
            // SD-card available
            rootDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + AppContext.mAppContext.getPackageName() + File.separator + subFolder
                    + File.separator;

            File file = new File(rootDir);
            if (!file.exists()) {
                return false;
            }
        } else {
            rootDir = AppContext.mAppContext.getCacheDir() + File.separator + subFolder + File.separator;
            File file = new File(rootDir);
            if (!file.exists()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断文件路径是否存在
     *
     * @param filePath
     * @return true 存在 false 不存在
     */
    public static boolean isExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public static String getAPKDir(String subFolder) {
        String rootDir = null;
        if (isSDCardEnable()) {
            // SD-card available
            rootDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/body/" + subFolder + File.separator;

            // 创建文件目录
            File file = new File(rootDir);
            if (!file.exists()) // 创建目录
            {
                file.mkdirs();
            }
        }

        return rootDir;
    }

    /**
     * SD是否可用
     *
     * @return
     */
    public static boolean isSDCardEnable() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * 验证url的合法性
     *
     * @param url
     * @return
     */
    public static boolean checkUrl(String url) {
        return url.matches("^[a-zA-z]+://[^\\s]*$");
    }

    /**
     * 验证手机号是否合法
     *
     * @param mobiles
     * @return boolean
     */
    public static boolean isMobile(String mobiles) {
//		Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$");
        Pattern p = Pattern.compile("^1\\d{10}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 验证邮箱是否合法
     *
     * @param strEmail
     * @return boolean
     */
    public static boolean isEmail(String strEmail) {
        String strPattern = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strEmail);
        return m.matches();
    }

    /**
     * 获取手机的imei号
     *
     * @return
     */
    public static final String getIMEI() {
        String android_id = "";
        try {
            TelephonyManager telManager = (TelephonyManager) AppContext.mAppContext.getSystemService(Context.TELEPHONY_SERVICE);
            android_id = telManager.getDeviceId();
            if (android_id == null) {
                android_id = android.provider.Settings.System.getString(AppContext.mAppContext.getContentResolver(), "android_id");
            }
        } catch (Exception e) {
        }
        return android_id;
    }

    /**
     * 是否为IMEI
     *
     * @param IMEI
     * @return
     */
    public static final boolean IsIMEI(String IMEI) {
        int s = IMEI.length();
        if (s != 15) {
            return false;
        }
        try {
            char[] imeiChar = IMEI.toCharArray();
            int resultInt = 0;
            for (int i = 0; i < 14; i++) {
                int a = Integer.parseInt(String.valueOf(imeiChar[i]));
                i++;
                final int temp = Integer.parseInt(String.valueOf(imeiChar[i])) * 2;
                final int b = temp < 10 ? temp : temp - 9;
                resultInt += a + b;
            }
            resultInt %= 10;
            resultInt = resultInt == 0 ? 0 : 10 - resultInt;
            if (resultInt == Integer.parseInt(String.valueOf(imeiChar[14]))) {
                return true;
            }
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return false;
    }

    /**
     * 获取WIFI MAC地址
     */
    public static String getWifiMac() {
        WifiInfo wifiInfo = null;
        try {
            WifiManager wifiManager = (WifiManager) AppContext.mAppContext.getSystemService(Context.WIFI_SERVICE);
            wifiInfo = wifiManager.getConnectionInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (wifiInfo != null && wifiInfo.getMacAddress() != null)
            return wifiInfo.getMacAddress(); // 获取WIFI_MAC地址
        else
            return "";
    }

    /*
     * 提取url中的参数
     */
    public static String getParameter(String url, String name) {
        if (url == null || name == null) {
            return null;
        }

        int start = url.indexOf(name + "=");
        if (start == -1)
            return null;
        int len = start + name.length() + 1;
        int end = url.indexOf("&", len);
        if (end == -1) {
            end = url.length();
        }

        return url.substring(len, end);
    }

    /**
     * 获取参数值
     *
     * @param params
     * @param name
     * @return
     */
    public static String getParamValue(List<NameValuePair> params, String name) {
        try {
            for (NameValuePair pair : params) {
                if (name.equals(pair.getName())) {
                    return pair.getValue();
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 获取拼接参数
     *
     * @param params
     * @return
     */
    public static String getParamsStr(List<NameValuePair> params) {
        try {
            if (params == null) {
                return "";
            }

            StringBuffer sb = new StringBuffer();
            sb.append("?");
            for (NameValuePair pair : params) {
                sb.append(pair.getName()).append("=").append(pair.getValue()).append("&");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * bitmap转换
     *
     * @param bm
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 校验银行卡卡号
     *
     * @param cardId
     * @return
     */
    public static boolean checkBankCard(String cardId) {
        if (cardId != null && !"".equals(cardId)) {
            char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
            if (bit == 'N') {
                return false;
            }
            return cardId.charAt(cardId.length() - 1) == bit;
        }
        return false;
    }

    /**
     * 从不含校验位的银行卡卡号采用Luhm校验算法获得校验位
     *
     * @param nonCheckCodeCardId
     * @return
     */
    public static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0 || !nonCheckCodeCardId.matches("\\d+")) {
            // 如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    /**
     * 启动web页面
     *
     * @param context
     * @param url
     * @return
     */
    public static final boolean startWebView(Context context, String url) {
        if (url != null && Utils.checkUrl(url)) {
            Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
            context.startActivity(viewIntent);
            return true;
        }
        return false;
    }

    /**
     * 关闭软键盘
     *
     * @param mContext
     */
    public static final void closeSoftInputMode(Activity mContext) {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            try {
                inputMethodManager.hideSoftInputFromWindow((mContext).getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public static boolean isNull(String str) {
        if (str == null || str.length() == 0)
            return true;
        return false;
    }

    /**
     * 判断GPS是否打开
     *
     * @return
     */
    public static final boolean isGPSOPen() {
        LocationManager locationManager = (LocationManager) AppContext.mAppContext.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps) {
            return true;
        }
        return false;
    }

    /**
     * GPS开关
     */
    public static final void openGPS() {
        Intent gpsIntent = new Intent();
        gpsIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
        gpsIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(AppContext.mAppContext, 0, gpsIntent, 0).send();
        } catch (CanceledException e) {
            e.printStackTrace();
        }
    }

    /**
     * 转化英文格式
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * 获取手机ip地址
     *
     * @return
     */
    public static String getLocalIpAddress() {
        WifiManager wifiManager = (WifiManager) AppContext.mAppContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        if (ipAddress == 0) {
            return null;
        }
        // 对int类型的ip地址做一次转换
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "." + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
    }
}