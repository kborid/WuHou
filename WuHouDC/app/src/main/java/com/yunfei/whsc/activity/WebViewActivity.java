package com.yunfei.whsc.activity;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.yunfei.whsc.custom.CommonLoadingWidget;
import com.yunfei.whsc.R;

public class WebViewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String CSS_STYLE = "<style>* {font-size:40px;padding:10px;}</style>";
    private WebView webview;
    private CommonLoadingWidget common_loading_widget;
    private String URL, mTitle, loginUrl;
    private TextView tv_left_title_back, tv_left_title_close, tv_center_title;
    private int paddingValue = 0;
    private int CLOSEWIDTH = 0;
    private int BACKWIDTH = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_webview_layout);

        findViews();
        dealIntent();
        initParams();
        setClickListener();
    }

    private void findViews() {
        webview = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setSupportZoom(false);
        webSettings.setUseWideViewPort(true);// 将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true);// 充满全屏。
        webSettings.setAllowFileAccess(true);// 允许访问文件数据
        webSettings.setDomStorageEnabled(true);// 开启Dom存储Api(启用地图、定位之类的都需要)
        webSettings.setAppCacheEnabled(true);// 开启 Application Caches 功能
//        webSettings.setAppCachePath(getFolderDir("webCache"));
        webSettings.setDatabaseEnabled(true);// 启用数据库
//        webSettings.setDatabasePath(getFolderDir("webDatabase"));
        webSettings.setGeolocationEnabled(true); // 启用地理定位
        webSettings.setDefaultTextEncodingName("utf-8");

        try {
            StringBuilder sb = new StringBuilder();
            String pkName = this.getPackageName();
            String versionName = this.getPackageManager().getPackageInfo(pkName, 0).versionName;
            sb.append(webSettings.getUserAgentString()).append(" android/").append("autodevice").append("/").append(versionName);// 名字+wuhou+版本号
            webSettings.setUserAgentString(sb.toString());// 追加修改ua特征标识（名字+包名+版本号）使得web端正确判断
        } catch (Exception e) {
        }

        tv_left_title_back = (TextView) findViewById(R.id.tv_left_title_back);
        tv_left_title_close = (TextView) findViewById(R.id.tv_left_title_close);
        tv_center_title = (TextView) findViewById(R.id.tv_center_title);
        common_loading_widget = (CommonLoadingWidget) findViewById(R.id.common_loading_widget);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        BACKWIDTH = tv_left_title_back.getWidth();
        CLOSEWIDTH = tv_left_title_close.getWidth();
        paddingValue = BACKWIDTH;
        tv_left_title_close.setVisibility(View.GONE);
    }

    private void dealIntent() {
        if (getIntent().getExtras() != null && getIntent().getExtras().getString("path") != null) {
            URL = getIntent().getExtras().getString("path");
            if (URL != null && !URL.startsWith("http")) {
                URL = "http://" + URL;
            }
        }
        if (getIntent().getExtras() != null && getIntent().getExtras().getString("title") != null) {
            mTitle = getIntent().getExtras().getString("title");
        }
    }

    private void initParams() {
        MyWebViewClient webViewClient = new MyWebViewClient();
        webview.setWebViewClient(webViewClient);
    }

    private void setClickListener() {
        tv_left_title_back.setOnClickListener(this);
        tv_left_title_close.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        webview.loadUrl(URL);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left_title_back:
                if (webview.canGoBack()) {
                    webview.goBack();
                } else {
                    finish();
                }
                break;
            case R.id.tv_left_title_close:
                finish();
                break;
            default:
                break;
        }
    }

    class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(final WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            common_loading_widget.startLoading();
            tv_center_title.setPadding(paddingValue, 0, paddingValue, 0);
            tv_center_title.setText(mTitle);
            webview.setEnabled(false);
        }

        @Override
        public void onPageFinished(WebView view, final String url) {
            super.onPageFinished(view, url);
            URL = url;
            if (view.canGoBack()) {
                tv_left_title_close.setVisibility(View.VISIBLE);
                paddingValue = BACKWIDTH + CLOSEWIDTH;
            } else {
                tv_left_title_close.setVisibility(View.GONE);
                paddingValue = BACKWIDTH;
            }
            tv_center_title.setPadding(paddingValue, 0, paddingValue, 0);
            String title = view.getTitle();

            common_loading_widget.closeLoading();
            if (!view.getSettings().getLoadsImagesAutomatically()) {
                view.getSettings().setLoadsImagesAutomatically(true);
            }
            webview.setEnabled(true);
        }

        public void onReceivedError(WebView webview, int errorCode, String description, String failingUrl) {
            // super.onReceivedError(webview, errorCode, description, failingUrl);
            try {
                webview.stopLoading();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (webview.canGoBack()) {
                webview.goBack();
            }
            if (errorCode == WebViewClient.ERROR_CONNECT || errorCode == WebViewClient.ERROR_TIMEOUT || errorCode == WebViewClient.ERROR_HOST_LOOKUP) {
                webview.loadDataWithBaseURL(null, CSS_STYLE + "哎呀，出错了，请检查网络并关闭重试！", "text/html", "utf-8", null);// 显示空白，屏蔽显示出错的网络地址url
            }
        }

        // 当load有ssl层的https页面时，如果这个网站的安全证书在Android无法得到认证，WebView就会变成一个空白页，而并不会像PC浏览器中那样跳出一个风险提示框
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            // 忽略证书的错误继续Load页面内容
            Toast.makeText(WebViewActivity.this, "已忽略证书信息继续加载", Toast.LENGTH_SHORT).show();
            handler.proceed();// 忽略证书信息继续加载
            // handler.cancel(); // Android默认的处理方式
            // handleMessage(Message msg); // 进行其他处理
            // super.onReceivedSslError(view, handler, error);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webview.stopLoading();
        webview.removeAllViews();
        webview.clearAnimation();
        webview.clearFormData();
        webview.clearHistory();
        webview.clearMatches();
        webview.clearSslPreferences();
        webview.destroy();
        common_loading_widget.closeLoading();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
