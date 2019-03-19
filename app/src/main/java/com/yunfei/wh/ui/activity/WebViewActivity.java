package com.yunfei.wh.ui.activity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yunfei.wh.R;
import com.yunfei.wh.ui.base.BaseActivity;
import com.yunfei.wh.ui.custom.CommonLoadingWidget;

/**
 * @author kborid
 */
public class WebViewActivity extends BaseActivity {

	private WebView	mWebView;
	private String	mURL;
	private CommonLoadingWidget	common_loading_widget;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_city_hot);
		initViews();
		initParams();
		initListeners();
	}

	@Override
	public void initViews() {
		super.initViews();
		mWebView = (WebView) findViewById(R.id.webview);
		tv_right_title.setBackgroundResource(R.drawable.m_refresh);
		tv_center_title.setText("");
		tv_right_title.setVisibility(View.GONE);
		common_loading_widget = (CommonLoadingWidget) findViewById(R.id.common_loading_widget);
	}

	@Override
	public void dealIntent() {
		super.dealIntent();

		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
            mURL = bundle.getString("path");
            tv_center_title.setText(bundle.getString("title"));
		}
	}

	@Override
	public void initParams() {
		super.initParams();
		WebSettings webSetting = mWebView.getSettings();
		webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
		webSetting.setLoadWithOverviewMode(true);// 充满全屏。
		mWebView.setHorizontalScrollBarEnabled(false);// 水平不显示
		mWebView.setVerticalScrollBarEnabled(false); // 垂直不显示
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 控制图片加载处理，提高view加载速度
			webSetting.setLoadsImagesAutomatically(true);
		} else {
			webSetting.setLoadsImagesAutomatically(false);
		}
        mWebView.setWebViewClient(new MymWebViewClient());
		mWebView.loadUrl(mURL);
	}

	@Override
	public void initListeners() {
		super.initListeners();
	}

	@Override
	public void onClick(View v) {
		// super.onClick(v);
		switch (v.getId()) {
			case R.id.tv_right_title :
				mWebView.reload();// 刷新
				break;
			case R.id.tv_left_title :
				if (mWebView.canGoBack()) {
					mWebView.goBack();
				} else {
					this.finish();
				}
				break;
			default :
				break;
		}
	}

	class MymWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			common_loading_widget.startLoading();
			mWebView.setEnabled(false);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			common_loading_widget.closeLoading();
			if (!view.getSettings().getLoadsImagesAutomatically()) {
				view.getSettings().setLoadsImagesAutomatically(true);
			}
			mWebView.setEnabled(true);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWebView.stopLoading();
		mWebView.destroy();
		common_loading_widget.closeLoading();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
