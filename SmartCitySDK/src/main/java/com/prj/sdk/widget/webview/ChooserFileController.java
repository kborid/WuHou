package com.prj.sdk.widget.webview;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.ValueCallback;

/**
 * 选择文件上传控制器
 * 
 * @author LiaoBo
 */
public class ChooserFileController {
	final static int	FILE_SELECTED	= 44;

	Activity			mActivity;
	UploadHandler		mUploadHandler;

	public ChooserFileController(Activity browser) {
		mActivity = browser;
	}

	public Activity getActivity() {
		return mActivity;
	}

	public void onDestroy() {
		if (mUploadHandler != null && !mUploadHandler.handled()) {
			mUploadHandler.onResult(Activity.RESULT_CANCELED, null);
			mUploadHandler = null;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
			case FILE_SELECTED :
				if (null == mUploadHandler)
					break;
				mUploadHandler.onResult(resultCode, intent);
				break;
		}
	}

	public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
		mUploadHandler = new UploadHandler(this);
		mUploadHandler.openFileChooser(uploadMsg, acceptType, capture);
	}
}