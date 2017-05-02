package com.yunfei.wh.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 *  重写ListView解决item高度不一致的问题 ,listview全部显示，使scrollview与listview共存，以及listview嵌套listview
 *
 * @author LiaoBo
 * @date 2014-6-23
 */
public class MyListViewWidget extends ListView {

	public MyListViewWidget(Context context) {
		super(context);
	}

	public MyListViewWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyListViewWidget(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
	            MeasureSpec.AT_MOST);
	    super.onMeasure(widthMeasureSpec, expandSpec);
	}
}