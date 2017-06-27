package com.prj.sdk.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.prj.sdk.algo.Aes;
import com.prj.sdk.app.AppContext;

/**
 * SharedPreferences操作 进行数据保存
 * 
 * @author LiaoBo
 * 
 */
public class SharedPreferenceUtil {

	private Context						mContext			= AppContext.mAppContext;

	private final static String			CONFIG_FILE_NAME	= "config";
	private static SharedPreferenceUtil	sInstance;

	public static SharedPreferenceUtil getInstance() {
		if (sInstance == null) {
			sInstance = new SharedPreferenceUtil();
		}
		return sInstance;
	}

	/**
	 * 获取SharedPreferences.Editor
	 * 
	 * @return
	 */
	private SharedPreferences.Editor getEditor() {
		if (mContext != null) {
			SharedPreferences settings = mContext.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
			return settings.edit();
		}
		return null;
	}

	/**
	 * 获取SharedPreferences
	 * 
	 * @return
	 */
	private SharedPreferences getSettings() {
		if (mContext != null) {
			return mContext.getSharedPreferences(CONFIG_FILE_NAME, 0);
		}
		return null;
	}

	/**
	 * 获取字符串
	 * 
	 * @param configName
	 * @param def
	 * @param encrypt 获取的数据是否为加密数据
	 * @return
	 */
	public String getString(String configName, String def, boolean encrypt) {
		String value = getSettings().getString(configName, def);
		try {
			if (encrypt) {
				if (!value.equals(def))
					value = Aes.DecryptToString(Conversion.hexStringToBytes(value), Aes.AES_KEY);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 保存字符串
	 * 
	 * @param configName
	 * @param value
	 * @param encrypt
	 *            是否加密
	 * @return
	 */
	public boolean setString(String configName, String value, boolean encrypt) {
		try {
			if (value != null) {
				if (encrypt) {
					value = Conversion.bytesToHexString(Aes.Encrypt(value, Aes.AES_KEY));
				}
			} else {
				value = "";
			}
		} catch (Exception e) {
		}
		return getEditor().putString(configName, value).commit();
	}

	public boolean removeValue(String configName) {
		return getEditor().remove(configName).commit();
	}

	/**
	 * 获取int
	 * 
	 * @param configName
	 * @param def
	 * @return
	 */
	public int getInt(String configName, int def) {
		return getSettings().getInt(configName, def);
	}

	/**
	 * 设置int
	 * 
	 * @param configName
	 * @param def
	 * @return
	 */
	public boolean setInt(String configName, int def) {
		return getEditor().putInt(configName, def).commit();
	}

	public boolean setLong(String configName, long def) {
		return getEditor().putLong(configName, def).commit();
	}

	public long getLong(String configName, long def) {
		return getSettings().getLong(configName, def);
	}

	public boolean setFloat(String configName, Float def) {
		return getEditor().putFloat(configName, def).commit();
	}

	public Float getFloat(String configName, Float def) {
		return getSettings().getFloat(configName, def);
	}

	/**
	 * 布尔值处理
	 * 
	 * @param configName
	 * @param def
	 * @return
	 */
	public boolean getBoolean(String configName, boolean def) {
		return getSettings().getBoolean(configName, def);
	}

	public boolean setBoolean(String configName, boolean value) {
		return getEditor().putBoolean(configName, value).commit();
	}

	/**
	 * 清除所有数据
	 */
	public void clearData() {
		getEditor().clear().commit();
	}

}