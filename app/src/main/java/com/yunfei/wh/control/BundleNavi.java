package com.yunfei.wh.control;


import java.io.Serializable;

import android.app.Activity;
import android.os.Bundle;

public class BundleNavi {
	
	private BundleUnit bundle;
	private BundleUnit preBundle;
	
	private static final BundleNavi instance = new BundleNavi();
	
	private BundleNavi() {
		bundle = BundleUnit.nullBundleUnit;
		preBundle = BundleUnit.nullBundleUnit;
	}
	
	public static BundleNavi getInstance(){
		return instance;
	}
	
	/*package*/ void updateBundle(Activity activity) {
		if(bundle.hashcode == activity.hashCode()){
			return;
		}
		preBundle = bundle;
		bundle = BundleUnit.create(activity.getClass(),activity.hashCode());
	}
	
	public boolean containKey(String key){
		return preBundle.bundle.containsKey(key);
	}
	
	public String getString(String key){
		String temp = preBundle.bundle.getString(key);
		preBundle.bundle.remove(key);
		return temp;
	}
	
	public int getInt(String key){
		int temp = preBundle.bundle.getInt(key);
		preBundle.bundle.remove(key);
		return temp;
	}
	
	public int getInt(String key, int defaultValue){
		int temp = preBundle.bundle.getInt(key, defaultValue);
		preBundle.bundle.remove(key);
		return temp;
	}
	
	public float getFloat(String key){
		float temp = preBundle.bundle.getFloat(key);
		preBundle.bundle.remove(key);
		return temp;
	}
	
	public boolean getBoolean(String key){
		boolean temp = preBundle.bundle.getBoolean(key);
		preBundle.bundle.remove(key);
		return temp;
	}
	
	public Object get(String key){
		Object temp = preBundle.bundle.get(key);
		preBundle.bundle.remove(key);
		return temp;
	}
	
	public void putString(String key, String value){
		bundle.bundle.putString(key, value);
	}
	
	public void putInt(String key, int value){
		bundle.bundle.putInt(key, value);
	}
	
	public void putFloat(String key, float value) {
		bundle.bundle.putFloat(key, value);
	}
	
	public void putBoolean(String key, boolean value){
		bundle.bundle.putBoolean(key, value);
	}
	
	public void put(String key, Serializable value){
		bundle.bundle.putSerializable(key, value);
	}
	
	public Class<? extends Activity> getPreviousActivityClass(){
		return preBundle.activityClass;
	}
	
	public Class<? extends Activity> getCurrentActivityClass(){
		return bundle.activityClass;
	}
	
	private static class BundleUnit{
		private static final BundleUnit nullBundleUnit = new BundleUnit(Activity.class,0);
		
		private final Bundle bundle;
		private final Class<? extends Activity> activityClass;
		private int hashcode;
		private BundleUnit(Class<? extends Activity> activityClass,int hashcode){
			this.activityClass = activityClass;
			this.hashcode = hashcode;
			bundle = new Bundle();
		}
		
		private static BundleUnit create(Class<? extends Activity> activityClass,int hashcode){
			return new BundleUnit(activityClass,hashcode);
		}
	}

}
