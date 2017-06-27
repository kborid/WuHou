package com.yunfei.wh.ui.custom;


import android.content.Context;
import android.util.AttributeSet;

import com.yunfei.wh.R;


public class MultiChoiceLayout extends CheckedRelativeLayout {

	public MultiChoiceLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setImageResouce(R.drawable.check, R.drawable.checked);
	}
}
