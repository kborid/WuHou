package com.yunfei.wh.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * ViewPager 滑动适配器
 * 
 * @author LiaoBo
 * @date 2014-7-17
 */
public class ViewPagerAdapter extends PagerAdapter {
	private List<View>	mListViews;
	
	public ViewPagerAdapter(List<View> list) {
		if (list != null) {
			this.mListViews = list;
		} else {
			this.mListViews = new ArrayList<View>();
		}
	}

	public ViewPagerAdapter(Context context, List<View> list) {
		if (list != null) {
			this.mListViews = list;
		} else {
			this.mListViews = new ArrayList<View>();
		}
	}

	// 销毁position位置的界面
	@Override
	public void destroyItem(View collection, int position, Object o) {
		View view = (View) o;
		((ViewPager) collection).removeView(view);
		view = null;
	}

	// 获取当前窗体界面数
	@Override
	public int getCount() {
		return mListViews.size();
	}

	// 初始化position位置的界面
	@Override
	public Object instantiateItem(View view, int position) {
		((ViewPager) view).addView(mListViews.get(position));
		return mListViews.get(position);
	}

	// 判断View和对象是否为同一个View
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {

	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {

	}

	@Override
	public void finishUpdate(View arg0) {

	}

	/**
	 * 跳转到每个页面都要执行的方法
	 */
	@Override
	public void setPrimaryItem(View container, int position, Object object) {
	}

}
