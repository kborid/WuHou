package com.yunfei.wh.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yunfei.wh.R;
import com.yunfei.wh.ui.custom.CommonLoadingWidget;
import com.prj.sdk.widget.CustomToast;
import com.yunfei.wh.ui.base.BaseActivity;
import com.umeng.analytics.MobclickAgentJSInterface;

/**
 * 相关webview 公用页面；如：城市新鲜事、常见问题等
 * 
 * @author LiaoBo
 * 
 */
public class WebViewActivity extends BaseActivity {

	private WebView	mWebView;
	private String	mURL	= "file:///android_asset/BusList.html";
	private CommonLoadingWidget	common_loading_widget;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_city_hot);

		initViews();
		dealIntent();
		initParams();
		initListeners();
		// important , so that you can use js to call Uemng APIs
		new MobclickAgentJSInterface(this, mWebView, new MyWebClient());
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

		if (getIntent().getExtras() != null) {
			if (getIntent().getExtras().getString("path") != null) {
				mURL = getIntent().getExtras().getString("path");

			}
			if (getIntent().getExtras().getString("title") != null) {
				tv_center_title.setText(getIntent().getExtras().getString("title"));

			}
		}
	}

	@Override
	public void initParams() {
		super.initParams();

		mWebView.clearView();
		mWebView.loadUrl(mURL);

		WebSettings webSetting = mWebView.getSettings();
		webSetting.setJavaScriptEnabled(true);
		webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
		webSetting.setAllowFileAccess(true);// 允许访问文件数据
		webSetting.setRenderPriority(RenderPriority.HIGH); // 提高渲染的优先级
		// webSetting.setCacheMode(WebSettings.LOAD_CACHE_ONLY);
		webSetting.setLoadWithOverviewMode(true);// 充满全屏。
		mWebView.setHorizontalScrollBarEnabled(false);// 水平不显示
		mWebView.setVerticalScrollBarEnabled(false); // 垂直不显示
		String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
		webSetting.setDatabaseEnabled(true);// 启用数据库
		webSetting.setDatabasePath(dir);// 设置数据库路径
		webSetting.setGeolocationEnabled(true); // 启用地理定位
		webSetting.setGeolocationDatabasePath(dir);
		webSetting.setDomStorageEnabled(true);// 开启Dom存储Api(启用地图、定位什么之类的都需要)
		// // 设置支持各种不同的设备
		// webSetting
		// .setUserAgentString("Mozilla/5.0 (iPad; U; CPU OS 3_2 like Mac OS X;en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B334bSafari/531.21.10");

		if (Build.VERSION.SDK_INT >= 19) { // 控制图片加载处理，提高view加载速度
			webSetting.setLoadsImagesAutomatically(true);
		} else {
			webSetting.setLoadsImagesAutomatically(false);
		}
		// testGeolocationOK();
	}

	@Override
	public void initListeners() {
		super.initListeners();
		mWebView.setWebViewClient(new MymWebViewClient());
		mWebView.setWebChromeClient(new MyWebClient());
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
			// dealError();
			if ((url != null) && (!"".equals(url)) && (url.indexOf("/") < 0)) {
				CustomToast.show("链接地址不正确", 0);
				return false;
			}
			try {
				// 设置点击网页里面的链接还是在当前的webview里跳转
				view.loadUrl(url);
			} catch (Exception localException) {
				while (true) {
					localException.printStackTrace();
				}
			}
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

		public void onReceivedError(WebView webview, int errorCode, String description, String failingUrl) {
			// super.onReceivedError(webview, errorCode, description, failingUrl);
			try {
				webview.stopLoading();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (mWebView.canGoBack()) {
				mWebView.goBack();
			}
			if (errorCode == WebViewClient.ERROR_CONNECT || errorCode == WebViewClient.ERROR_TIMEOUT || errorCode == WebViewClient.ERROR_HOST_LOOKUP) {
				mWebView.loadDataWithBaseURL(null, "哎呀，出错了,请检查网络并关闭重试！", "text/html", "utf-8", null);// 显示空白，屏蔽显示出错的网络地址url
			}

		}

		// 当load有ssl层的https页面时，如果这个网站的安全证书在Android无法得到认证，WebView就会变成一个空白页，而并不会像PC浏览器中那样跳出一个风险提示框
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			// 忽略证书的错误继续Load页面内容
			CustomToast.show("已忽略证书信息继续加载", 0);
			handler.proceed();
			// handler.cancel(); // Android默认的处理方式
			// handleMessage(Message msg); // 进行其他处理
			// super.onReceivedSslError(view, handler, error);
		}

	}

	private class MyWebClient extends WebChromeClient {
		// 提示对话框
		@Override
		public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
			return super.onJsAlert(view, url, message, result);
		}

		// 带按钮的对话框
		@Override
		public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
			return super.onJsConfirm(view, url, message, result);
		}

		// 设置网页加载的进度条
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			WebViewActivity.this.getWindow().setFeatureInt(Window.FEATURE_PROGRESS, newProgress * 100);
			super.onProgressChanged(view, newProgress);
		}

		// 设置应用程序的标题
		@Override
		public void onReceivedTitle(WebView view, String title) {
			WebViewActivity.this.setTitle(title);
			super.onReceivedTitle(view, title);
		}

		// 扩充数据库的容量
		public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long estimatedSize, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
			quotaUpdater.updateQuota(estimatedSize * 2);
		}

		public void onGeolocationPermissionsHidePrompt() {
			super.onGeolocationPermissionsHidePrompt();
		}

		// 地理定位
		@Override
		public void onGeolocationPermissionsShowPrompt(final String origin, final Callback callback) {
			// callback.invoke(origin, true, false);
			AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
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
			super.onGeolocationPermissionsShowPrompt(origin, callback);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			mWebView.stopLoading();
			mWebView.removeAllViews();
			mWebView.clearAnimation();
//			mWebView.clearCache(true);
			mWebView.clearFormData();
			mWebView.clearHistory();
			mWebView.clearMatches();
			mWebView.clearSslPreferences();
			mWebView.destroy();
			common_loading_widget.closeLoading();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检测定位服务是否可用
	 */
	private void testGeolocationOK() {
		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		boolean gpsProviderOK = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean networkProviderOK = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		boolean geolocationOK = gpsProviderOK && networkProviderOK;
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
