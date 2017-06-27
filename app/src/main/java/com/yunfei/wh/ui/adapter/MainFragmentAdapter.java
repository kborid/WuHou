package com.yunfei.wh.ui.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * 主Tab导航适配器
 * 
 * @author LiaoBo
 */
public class MainFragmentAdapter extends FragmentPagerAdapter {
	private List<Fragment>	mList;

	public MainFragmentAdapter(FragmentManager fm, List<Fragment> list) {
		super(fm);
		this.mList = list;
	}

	@Override
	public Fragment getItem(int position) {
		return mList.get(position);
	}

	@Override
	public int getCount() {
		return mList.size();
	}
}
