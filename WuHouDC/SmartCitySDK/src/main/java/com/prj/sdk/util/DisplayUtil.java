package com.prj.sdk.util;

import android.util.DisplayMetrics;

import com.prj.sdk.app.AppContext;

/**
 * dp、sp 转换为 px 的工具类
 * 
 * @author LiaoBo
 * 
 */
public class DisplayUtil {

	/**
	 * 获得字体的缩放密度
	 * 
	 * @return
	 */
	public static float getScaledDensity() {
		DisplayMetrics dm = AppContext.mAppContext.getResources().getDisplayMetrics();
		return dm.scaledDensity;
	}

	/**
	 * 将px值转换为dip或dp值，保证尺寸大小不变
	 * 
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(float pxValue) {
		final float scale = AppContext.mAppContext.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 * 
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(float dipValue) {
		final float scale = AppContext.mAppContext.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @return
	 */
	public static int px2sp(float pxValue) {
		final float fontScale = AppContext.mAppContext.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @return
	 */
	public static int sp2px(float spValue) {
		final float fontScale = AppContext.mAppContext.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}
}
