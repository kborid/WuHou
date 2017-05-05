package com.prj.sdk.widget.webview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * 扩展WebChromeClient，使其支持JsAlert、选择文件、定位
 * 
 * @author LiaoBo
 */
public class WebChromeClientCompat extends WebChromeClient {
	private ChooserFileController	mCtrl;
	private Context					mContext;
	private TextView				mTitleView;

	public WebChromeClientCompat(Context context, ChooserFileController mCtrl) {
		this.mCtrl = mCtrl;
		this.mContext = context;
	}
	public WebChromeClientCompat(Context context, ChooserFileController mCtrl,TextView view) {
		this.mCtrl = mCtrl;
		this.mContext = context;
		this.mTitleView = view;
	}
	
	// 提示对话框
	@Override
	public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage(message);
		builder.setPositiveButton("OK", new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				result.confirm();
			}
		});
		builder.setCancelable(false);
		builder.create();
		builder.show();
		return true;
	}

	// 带按钮的对话框
	@Override
	public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("提示");
		builder.setMessage(message);
		builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				result.confirm();
			}
		});
		builder.setNeutralButton(android.R.string.cancel, new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				result.cancel();
			}
		});
		builder.setCancelable(false);
		builder.create();
		builder.show();
		return true;
	}

	// 设置网页加载的进度条
	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		// mContext.getWindow().setFeatureInt(Window.FEATURE_PROGRESS, newProgress * 100);
		super.onProgressChanged(view, newProgress);
	}

	// 设置应用程序的标题
	@Override
	public void onReceivedTitle(WebView view, String title) {
		super.onReceivedTitle(view, title);
		if(mTitleView != null){
			mTitleView.setText(title);
		}
	}

	public void onGeolocationPermissionsHidePrompt() {
		super.onGeolocationPermissionsHidePrompt();
	}

	// 地理定位
	@Override
	public void onGeolocationPermissionsShowPrompt(final String origin, final Callback callback) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage("是否允许接入位置信息?");
		OnClickListener dialogButtonOnClickListener = new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int clickedButton) {
				if (DialogInterface.BUTTON_POSITIVE == clickedButton) {
					callback.invoke(origin, true, true);
				} else if (DialogInterface.BUTTON_NEGATIVE == clickedButton) {
					callback.invoke(origin, false, false);
				}
			}
		};
		builder.setPositiveButton("运行", dialogButtonOnClickListener);
		builder.setNegativeButton("拒绝", dialogButtonOnClickListener);
		builder.show();
		// callback.invoke(origin, true, false);
		super.onGeolocationPermissionsShowPrompt(origin, callback);

	}

	// Android 2.x
	public void openFileChooser(ValueCallback<Uri> uploadMsg) {
		openFileChooser(uploadMsg, "");
	}

	// Android 3.0
	public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
		openFileChooser(uploadMsg, "", "filesystem");
	}

	// Android 4.1
	public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
		if (null == mCtrl) {
			throw new IllegalStateException("Controller is null");
		}
		mCtrl.openFileChooser(uploadMsg, acceptType, capture);
	}

	@Override
	public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
		String[] acceptTypes = fileChooserParams.getAcceptTypes();
		String acceptType = "";
		for (int i = 0; i < acceptTypes.length; i++) {
			String type = acceptTypes[i];
			if (!TextUtils.isEmpty(type)) {
				acceptType.concat(type).concat(";");
			}
		}
		if (TextUtils.isEmpty(acceptType)) {
			acceptType = "*/*";
		}
		final ValueCallback<Uri[]> finalFilePathCallback = filePathCallback;
		ValueCallback<Uri> vc = new ValueCallback<Uri>() {
			@Override
			public void onReceiveValue(Uri value) {
				Uri[] result;
				if (value != null) {
					result = new Uri[]{value};
				} else {
					result = null;
				}
				finalFilePathCallback.onReceiveValue(result);
			}
		};
		openFileChooser(vc, acceptType, "*");
		return true;
	}
}
