package com.yunfei.wh.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.prj.sdk.app.AppContext;
import com.prj.sdk.constants.Const;
import com.prj.sdk.util.ActivityTack;
import com.prj.sdk.util.LogUtil;
import com.prj.sdk.util.SharedPreferenceUtil;
import com.prj.sdk.util.Utils;
import com.prj.sdk.widget.CustomToast;
import com.prj.sdk.widget.webview.ChooserFileController;
import com.prj.sdk.widget.webview.WebChromeClientCompat;
import com.umeng.socialize.sso.UMSsoHandler;
import com.yunfei.wh.BuildConfig;
import com.yunfei.wh.R;
import com.yunfei.wh.broatcast.UnLoginBroadcastReceiver;
import com.yunfei.wh.common.AppConst;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.control.ShareControl;
import com.yunfei.wh.ui.JSBridge.RegisterHandler;
import com.yunfei.wh.ui.JSBridge.WVJBWebViewClient;
import com.yunfei.wh.ui.activity.LoginActivity.onCancelLoginListener;
import com.yunfei.wh.ui.base.BaseActivity;
import com.yunfei.wh.ui.custom.CommonLoadingWidget;
import com.yunfei.wh.ui.custom.CustomShareView;
import com.yunfei.wh.ui.custom.ShareBeanInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 中间件处理，加载html5应用数据
 *
 * @author LiaoBo
 * @date 2014-7-8
 */
public class HtmlActivity extends BaseActivity implements onCancelLoginListener {

    private static final String TAG = "HtmlActivity";
    private static final String CSS_STYLE = "<style>* {font-size:40px;padding:10px;}</style>";

    private WebView mWebView;
    private CommonLoadingWidget common_loading_widget;
    private String URL, mTitle, loginUrl;
    private ActivityResult mActivityForResult;
    private String mID;
    private TextView tv_left_title_back, tv_left_title_close;
    private ChooserFileController mCtrl;
    private int paddingValue = 0;
    private int CLOSEWIDTH = 0;
    private int BACKWIDTH = 0;
    private MyWebViewClient myWebViewClient = null;
    private RelativeLayout btn_menu;
    private PopupWindow popupWindow = null;
    private CustomShareView customShareView;
    private List<ShareBeanInfo> list = new ArrayList<>();

    private LoginReceiver loginReceiver;


    // 登陆成功 刷新
    class LoginReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mWebView.reload();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_web_act);
        createController();
        initViews();
        initParams();
        initListeners();
        initReceiver();
        if (AppConst.ISDEVELOP) {
            if (getIntent().getBooleanExtra("ISDEVELOP", false)) {
                initDevelop();
            }
        }

        if (AppConst.ISDEVELOP) {
            if (getIntent().getBooleanExtra("ISJS", false)) {
                mWebView.loadUrl("file:///android_asset/ExampleApp.html");
            }
        }
    }

    private void createController() {
        mCtrl = new ChooserFileController(this);
    }

    /**
     * 开发者测试
     */
    public void initDevelop() {
        tv_center_title.setText("环境切换");
        btn_menu.setVisibility(View.GONE);
        LinearLayout layout_test = (LinearLayout) findViewById(R.id.layout_test);
        final EditText et_url = (EditText) findViewById(R.id.et_url);
        Button btn_go = (Button) findViewById(R.id.btn_go);
        TextView tv_cur = (TextView) findViewById(R.id.tv_cur);
        StringBuilder sb = new StringBuilder();
        sb.append("\nCurrent Environment（")
                .append(SharedPreferenceUtil.getInstance().getInt(AppConst.APPTYPE, 0))
                .append("：")
                .append(NetURL.getApi())
                .append("）");
        tv_cur.setText(sb);
        tv_cur.setVisibility(View.VISIBLE);
        layout_test.setVisibility(View.VISIBLE);
        btn_go.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                URL = et_url.getText().toString().trim();
                if ("0".equals(URL) || "1".equals(URL) || "2".equals(URL)) {
                    SharedPreferenceUtil.getInstance().setInt(AppConst.APPTYPE, Integer.parseInt(URL));// 保存切换地址类型

                    AppContext.mMainHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SessionContext.cleanUserInfo();
                            SessionContext.destroy();
                            SharedPreferenceUtil.getInstance().setString(AppConst.MAIN_IMG_DATA, "", false);
                            ActivityTack.getInstanse().exit();
                        }
                    }, 2000);
                    CustomToast.show("切换成功，即将退出，请手动重启", 0);
                    return;
//                } else if ("4".equals(URL)) {
//                    Intent intent = new Intent(HtmlActivity.this, EasyARActivity.class);
//                    startActivity(intent);
                }

                mWebView.loadUrl(URL);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        BACKWIDTH = tv_left_title_back.getWidth();
        CLOSEWIDTH = tv_left_title_close.getWidth();
        paddingValue = BACKWIDTH;
        tv_left_title_close.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left_title_back:
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    goBack();
                }
                break;
            case R.id.tv_left_title_close:
                goBack();
                break;
            case R.id.btn_menu:
                showPopupWindow();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void initViews() {
        super.initViews();
        mWebView = (WebView) findViewById(R.id.webview);
        tv_left_title_back = (TextView) findViewById(R.id.tv_left_title_back);
        common_loading_widget = (CommonLoadingWidget) findViewById(R.id.common_loading_widget);
        tv_left_title_close = (TextView) findViewById(R.id.tv_left_title_close);
        btn_menu = (RelativeLayout) findViewById(R.id.btn_menu);
        customShareView = new CustomShareView(this);
    }

    @Override
    public void dealIntent() {
        super.dealIntent();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getString("path") != null) {
            URL = bundle.getString("path");
            if (URL != null && !URL.startsWith("http")) {
                URL = "http://" + URL;
            }
        }
        if (bundle != null && bundle.getString("title") != null) {
            mTitle = bundle.getString("title");
        }
        if (bundle != null && bundle.getString("id") != null) {
            mID = bundle.getString("id");
        }
    }

    public void initParams() {
        super.initParams();
        LoginActivity.setCancelLogin(this);

        WebSettings webSetting = mWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSetting.setSupportZoom(false);
        webSetting.setUseWideViewPort(true);// 将图片调整到适合webview的大小
        webSetting.setLoadWithOverviewMode(true);// 充满全屏。
        mWebView.setHorizontalScrollBarEnabled(false);// 水平不显示
        mWebView.setVerticalScrollBarEnabled(false); // 垂直不显示
        webSetting.setDomStorageEnabled(true);// 开启Dom存储Api(启用地图、定位之类的都需要)
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);// 提高渲染的优先级
        // 应用可以有缓存
        webSetting.setAppCacheEnabled(true);// 开启 Application Caches 功能
        webSetting.setAppCachePath(Utils.getFolderDir("webCache"));
        webSetting.setGeolocationEnabled(true); // 启用地理定位

        StringBuilder sb = new StringBuilder();
        sb.append(webSetting.getUserAgentString()).append(" Android/").append(BuildConfig.FLAVOR).append("/").append(BuildConfig.VERSION_NAME);// 名字+wuhou+版本号
        webSetting.setUserAgentString(sb.toString());// 追加修改ua特征标识（名字+包名+版本号）使得web端正确判断
        LogUtil.i(TAG, "WebView's UserAgent:" + webSetting.getUserAgentString());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 控制图片加载处理，提高view加载速度
            webSetting.setLoadsImagesAutomatically(true);
        } else {
            webSetting.setLoadsImagesAutomatically(false);
        }
        mWebView.loadUrl(URL);
    }

    public void initListeners() {
        super.initListeners();
        myWebViewClient = new MyWebViewClient(mWebView);
        mWebView.setWebViewClient(myWebViewClient);
        mWebView.setWebChromeClient(new WebChromeClientCompat(this, mCtrl, tv_center_title));
        tv_left_title_back.setOnClickListener(this);
        tv_left_title_close.setOnClickListener(this);
        btn_menu.setOnClickListener(this);
        customShareView.setOnDismissListener(listener);
    }

    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.LOGIN_SUCCESS);
        loginReceiver = new LoginReceiver();
        registerReceiver(loginReceiver, intentFilter);
    }

    /**
     * 处理返回，如果是广告，就跳首页
     */
    public void goBack() {
        if (getIntent().getExtras() != null && getIntent().getExtras().getString("goBack") != null) {
            Intent intent = new Intent(this, MainFragmentActivity.class);
            startActivity(intent);
        } else {
            this.finish();
        }
    }

    class MyWebViewClient extends WVJBWebViewClient {

        public MyWebViewClient(WebView webView) {
            super(webView);
            new RegisterHandler(this, HtmlActivity.this).init();
        }


        @Override
        public void onPageStarted(final WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            common_loading_widget.startLoading();
            try {
                // 拦截H5应用跳转网页版登录，使用app登录页面登录，登录成功加载loginUrl的链接
                if (url != null && (url.contains("rtnCode=900902") || url.contains("rtnCode=310001"))) {
                    if (loginUrl != null) {
                        goBack();
                    } else {
                        loginUrl = Uri.parse(url).getQueryParameter("loginUrl");
                        sendBroadcast(new Intent(UnLoginBroadcastReceiver.ACTION_NAME));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            tv_center_title.setPadding(paddingValue, 0, paddingValue, 0);
            tv_center_title.setText(mTitle);

            mWebView.setEnabled(false);
        }

        @Override
        public void onPageFinished(WebView view, final String url) {
            super.onPageFinished(view, url);
            if (view.canGoBack()) {
                tv_left_title_close.setVisibility(View.VISIBLE);
                paddingValue = BACKWIDTH + CLOSEWIDTH;
            } else {
                tv_left_title_close.setVisibility(View.GONE);
                paddingValue = BACKWIDTH;
            }
            tv_center_title.setPadding(paddingValue, 0, paddingValue, 0);
            String title = view.getTitle();
            LogUtil.d(TAG, "html's title = " + title);
            if (title != null
                    && !title.startsWith(NetURL.getApi().replace("http://", ""))
                    && !title.startsWith(NetURL.getWhHmApi().replace("http://", ""))
                    && !title.contains(".html")
                    && !title.contains(".htm")
                    && !title.contains(".jsp")
                    && !title.contains(".aspx")
                    && !title.contains(".do")) {
                mTitle = title;
                tv_center_title.setText(mTitle);// 点击后退，设置标题
            }

            common_loading_widget.closeLoading();
            if (!view.getSettings().getLoadsImagesAutomatically()) {
                view.getSettings().setLoadsImagesAutomatically(true);
            }
            mWebView.setEnabled(true);

//            if (url != null) {
//                // 拦截巴士公交进行原生定位,向js注入定位结果
//                if (url.startsWith("http://m.basbus.cn")) {
//                    if (mLatitude != 0 && mLongitude != 0) {
//                        final StringBuilder script = new StringBuilder();
//                        script.append("getic(").append(mLongitude).append(",").append(mLatitude).append(")");
//                        executeJavascript(script.toString());
//                    } else if (!AMapLocationControl.getInstance().isStart()) {
//                        AMapLocationControl.getInstance().startLocationOnce(HtmlActivity.this, new AMapLocationControl.LocationCallback(){
//
//                            @Override
//                            public void onLocationInfo(AMapLocation locationInfo) {
//                                if (locationInfo == null) {
//                                    return;
//                                }
//                                try {
//                                    mLatitude = locationInfo.getLatitude();
//                                    mLongitude = locationInfo.getLongitude();
//                                    StringBuilder script = new StringBuilder();
//                                    script.append("getic(").append(mLongitude).append(",").append(mLatitude).append(")");
//                                    executeJavascript(script.toString());
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                    }
//                }
//            }

            callHandler("addActionMethodsToNative", null, new WVJBResponseCallback() {
                @Override
                public void callback(Object data) {
                    JSONArray jsonArray = JSON.parseArray(data.toString());
                    if (list != null && list.size() > 0) {
                        list.clear();
                    }
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject mmjson = (JSONObject) jsonArray.get(i);
                        ShareBeanInfo info = new ShareBeanInfo();
                        if (mmjson.containsKey("title")) {
                            info.title = mmjson.getString("title");
                        }
                        if (mmjson.containsKey("imgURL")) {
                            info.imagePath = mmjson.getString("imgURL");
                        }
                        if (mmjson.containsKey("action")) {
                            info.action = mmjson.getString("action");
                        }
                        if (mmjson.containsKey("type")) {
                            info.type = mmjson.getString("type");
                        }
                        list.add(info);
                    }
                    customShareView.updateThirdMenu(list, mTitle, url);
                }
            });
            customShareView.updateThirdMenu(list, mTitle, url);
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
                mWebView.loadDataWithBaseURL(null, CSS_STYLE + "哎呀，出错了，请检查网络并关闭重试！", "text/html", "utf-8", null);// 显示空白，屏蔽显示出错的网络地址url
            }
        }

        // 当load有ssl层的https页面时，如果这个网站的安全证书在Android无法得到认证，WebView就会变成一个空白页，而并不会像PC浏览器中那样跳出一个风险提示框
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            // 忽略证书的错误继续Load页面内容
            CustomToast.show("已忽略证书信息继续加载", 0);
            handler.proceed();// 忽略证书信息继续加载
            // handler.cancel(); // Android默认的处理方式
            // handleMessage(Message msg); // 进行其他处理
            // super.onReceivedSslError(view, handler, error);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        destroyView();
        LoginActivity.setCancelLogin(null);
        common_loading_widget.closeLoading();
        unregisterReceiver(loginReceiver);
    }

    /**
     * 销毁视图
     */
    public void destroyView() {
        mWebView.stopLoading();
        mWebView.destroy();
        AppContext.mMemoryMap.clear();// 清空提供给网页的缓存
        mCtrl.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mActivityForResult != null) {
            mActivityForResult.onActivityResult(requestCode, resultCode, data);
        }
        mCtrl.onActivityResult(requestCode, resultCode, data);

        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = ShareControl.getInstance(this).getUMServerController().getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * Activity回调数据
     *
     * @author LiaoBo
     */
    public interface ActivityResult {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    public void setActivityForResult(ActivityResult mResult) {
        mActivityForResult = mResult;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                goBack();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void isCancelLogin(boolean isCancel) {
        if (isCancel) {
            this.finish();
        } else {
            if (loginUrl != null) {// 如果拦截到网页登录，登录成功则跳转到loginUrl
                mWebView.loadUrl(loginUrl);
                // loginUrl = null;
            } else {
                mWebView.reload();// 刷新
            }
        }
    }

    private void showPopupWindow() {
        if (null == popupWindow) {
            popupWindow = new PopupWindow(customShareView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        }
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setAnimationStyle(R.style.share_anmi);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.alpha = 1.0f;
                getWindow().setAttributes(params);
            }
        });
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.8f;
        getWindow().setAttributes(params);
        int value = 0;
//        if (Utils.hasNavBar()) {
//            value = Utils.mNavigationBarHeight;
//        }
        popupWindow.showAtLocation(btn_menu, Gravity.BOTTOM, 0, value);
    }

    private CustomShareView.OnFunctionListener listener = new CustomShareView.OnFunctionListener() {
        @Override
        public void dismiss() {
            AppContext.mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    popupWindow.dismiss();
                }
            }, 250);
        }

        @Override
        public void action(int i) {
            myWebViewClient.callHandler(list.get(i).action, null, new WVJBWebViewClient.WVJBResponseCallback() {
                @Override
                public void callback(Object data) {
                    if (data != null) {
                        LogUtil.d(TAG, "data = " + data.toString());
                    }
                }
            });
        }

        @Override
        public void refresh() {
            mWebView.reload();
        }
    };
}
