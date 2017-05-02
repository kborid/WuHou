package com.prj.sdk.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.prj.sdk.app.AppContext;

/**
 * 网络状态判断类
 * 
 * @author Liao
 * 
 */
public class NetworkUtil {

	private static String LOG_TAG = "NetworkUtil";
	
	/**
	 * 网络是否可用
	 * 
	 * @return
	 */
	public static boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) AppContext.mAppContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = cm.getActiveNetworkInfo();
		if (network != null) {
			return network.isAvailable();
		}
		return false;
	}

	/**
	 * 判断当前网络是否是wifi网络
	 */
	public static boolean isWifi() {
		ConnectivityManager connectivityManager = (ConnectivityManager) AppContext.mAppContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * 判断当前网络是否是3G网络
	 * 
	 */
	public static boolean is3G() {
		ConnectivityManager connectivityManager = (ConnectivityManager) AppContext.mAppContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断网络是否为漫游
	 */
	public static boolean isNetworkRoaming() {
		ConnectivityManager connectivity = (ConnectivityManager) AppContext.mAppContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			LogUtil.w(LOG_TAG, "couldn't get connectivity manager");
		} else {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
				TelephonyManager tm = (TelephonyManager) AppContext.mAppContext.getSystemService(Context.TELEPHONY_SERVICE);
				if (tm != null && tm.isNetworkRoaming()) {
					LogUtil.d(LOG_TAG, "network is roaming");
					return true;
				} else {
					LogUtil.d(LOG_TAG, "network is not roaming");
				}
			} else {
				LogUtil.d(LOG_TAG, "not using mobile network");
			}
		}
		return false;
	}
	
	/**
	 * 判断MOBILE网络是否可用
	 * 
	 * @throws Exception
	 */
	public static boolean isMobileDataEnable() throws Exception {
		ConnectivityManager connectivityManager = (ConnectivityManager) AppContext.mAppContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isMobileDataEnable = false;

		isMobileDataEnable = connectivityManager.getNetworkInfo(
				ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();

		return isMobileDataEnable;
	}

}
