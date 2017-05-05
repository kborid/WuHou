package com.prj.sdk.util;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.prj.sdk.app.AppContext;

/**
 * 文本工具
 * 
 * @author LiaoBo
 * @date 2014-7-28
 */
public class TextDrawableUtil {

	/**
	 * 设置文本左侧图片
	 * 
	 * @param view
	 * @param drawableId
	 */
	public static void setTextDrawableLeft(TextView textView, int drawableId) {
		setTextDrawableLeft(textView, drawableId, null);
	}

	/**
	 * 设置文本左侧图片
	 * 
	 * @param view
	 * @param drawable
	 */
	public static void setTextDrawableLeft(TextView textView, Drawable drawable) {
		setTextDrawableLeft(textView, drawable, null);
	}
	
	
	/**
	 * 设置文本以及图片
	 * 
	 * @param textView
	 * @param drawableId
	 * @param text
	 */
	public static void setTextDrawableLeft(TextView textView, Drawable drawable, String text) {
		// / 这一步必须要做,否则不会显示.
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		textView.setCompoundDrawables(drawable, null, null, null);
		if (text != null) {
			textView.setText(text);
		}
	}
	
	/**
	 * 设置文本以及图片
	 * 
	 * @param textView
	 * @param drawableId
	 * @param text
	 */
	public static void setTextDrawableLeft(TextView textView, int drawableId, String text) {
		Drawable drawable = AppContext.mAppContext.getResources().getDrawable(drawableId);
		// / 这一步必须要做,否则不会显示.
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		textView.setCompoundDrawables(drawable, null, null, null);
		if (text != null) {
			textView.setText(text);
		}
	}
	

	/**
	 * 设置底部图片
	 * 
	 * @param view
	 * @param drawableId
	 */
	public static void setTextDrawableBotton(TextView textView, Drawable drawable) {
		if (drawable != null) {
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			textView.setCompoundDrawables(null, null, null, drawable);
		} else {
			textView.setCompoundDrawables(null, null, null, null);
		}
	}


}
