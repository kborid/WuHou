package com.yunfei.wh.ui.dialog;

import android.app.Dialog;
import android.content.Context;

import com.yunfei.wh.R;
import com.yunfei.wh.ui.custom.ProgressWheel;

public class ProgressDialog extends Dialog {
	// private TextView mTextView;
	private Context			mContext;
	private ProgressWheel progress_wheel;

	public ProgressDialog(final Context context) {
		super(context, R.style.iphone_progress_dialog);
		// dialog添加视图
		setContentView(R.layout.iphone_progress_dialog);
		this.mContext = context;

		// mTextView = (TextView) findViewById(R.id.iphone_progress_dialog_txt);
		progress_wheel = (ProgressWheel) findViewById(R.id.progress_wheel);

	}

	public void setMessage(String msg) {
//		 if (msg == null || msg.length() == 0) {
//		 mTextView.setVisibility(View.GONE);
//		 } else {
//		 mTextView.setVisibility(View.VISIBLE);
//		 }
//		 mTextView.setText(msg);
	}

	public void setMessage(int msgId) {
		// mTextView.setText(msgId);
	}

	@Override
	public void dismiss() {
		super.dismiss();
		progress_wheel.stopSpinning();
	}

	@Override
	public void show() {
		super.show();
		System.gc();
		if (!progress_wheel.isSpinning()) {
			progress_wheel.spin();
		}
	}
}
