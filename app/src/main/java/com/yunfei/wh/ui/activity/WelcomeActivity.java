package com.yunfei.wh.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.prj.sdk.app.AppContext;
import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.net.image.ImageLoader;
import com.prj.sdk.net.image.ImageLoader.ImageCallback;
import com.prj.sdk.util.ActivityTack;
import com.prj.sdk.util.DateUtil;
import com.prj.sdk.util.LogUtil;
import com.prj.sdk.util.SharedPreferenceUtil;
import com.prj.sdk.util.StringUtil;
import com.prj.sdk.util.Utils;
import com.prj.sdk.widget.CustomToast;
import com.umeng.analytics.MobclickAgent;
import com.yunfei.wh.R;
import com.yunfei.wh.app.PRJApplication;
import com.yunfei.wh.common.AppConst;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.control.BundleNavi;
import com.yunfei.wh.control.UpdateControl;
import com.yunfei.wh.net.RequestBeanBuilder;
import com.yunfei.wh.net.bean.AdvertisementBean;
import com.yunfei.wh.net.bean.AppInfoBean;
import com.yunfei.wh.net.bean.CommuInfoBean;
import com.yunfei.wh.net.bean.DiscoveryChannelBean;
import com.yunfei.wh.permission.PermissionsActivity;
import com.yunfei.wh.permission.PermissionsDef;
import com.yunfei.wh.ui.base.BaseActivity;
import com.yunfei.wh.ui.dialog.CustomDialog;

import java.net.ConnectException;
import java.util.Collections;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * 欢迎页面
 *
 * @author Liao
 */
public class WelcomeActivity extends BaseActivity implements DataCallback {
    private static final String TAG = "WelcomeActivity";
    private long start = 0;                                // 记录启动时间
    private final long LOADING_TIME = 1500;
    private final long AD_TIME = 3000;                            // 等待广告加载时间
    private ImageView iv_advertisement;
    private FrameLayout layoutAd;
    private Button btn_skip;
    private AdvertisementBean mAdvertBean;
    private boolean isBreak;                                            // 点击广告，终止本页面跳转流程

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_welcome);
        start = SystemClock.elapsedRealtime();
        initViews();
        initParams();
        initListeners();
//        initStatistical();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);// 用于“用户使用时长”，“活跃用户”，“用户打开次数”的统计，并上报到服务器，在 Portal 上展示给开发者

        // 缺少权限时, 进入权限配置页面
        if (PRJApplication.getPermissionsChecker(this).lacksPermissions(PermissionsDef.LAUNCH_REQUIRE_PERMISSIONS)) {
            PermissionsActivity.startActivityForResult(this, PermissionsDef.PERMISSION_REQ_CODE, PermissionsDef.LAUNCH_REQUIRE_PERMISSIONS);
            return;
        }

        loadData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);// 用于“用户使用时长”，“活跃用户”，“用户打开次数”的统计，并上报到服务器，在 Portal 上展示给开发者
    }

    private void requestCommunityStreetList() {
        RequestBeanBuilder b = RequestBeanBuilder.create(false);

        ResponseData d = b.syncRequest(b);
        d.path = NetURL.COMMUNITY_LIST;
        d.flag = 0;

        requestID = DataLoader.getInstance().loadData(this, d);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void initViews() {
        super.initViews();
        iv_advertisement = (ImageView) findViewById(R.id.iv_advertisement);
        layoutAd = (FrameLayout) findViewById(R.id.layoutAd);
        btn_skip = (Button) findViewById(R.id.btn_skip);
        layoutAd.setVisibility(View.GONE);
    }

    public void initParams() {
        super.initParams();
        SessionContext.initUserInfo();
        SessionContext.setAreaCode(getString(R.string.areaCode), getString(R.string.areaName));
        Utils.initScreenSize(this);// 设置手机屏幕大小
    }

    private void loadData() {
        loadCacheData();
        loadAppInfo();
        loadAd();
        loadDiscoverChannel();
        requestCommunityStreetList();
    }

    /**
     * 获取app信息（分享、升级）
     */
    public void loadAppInfo() {
        RequestBeanBuilder builder = RequestBeanBuilder.create(false);
        builder.addBody("getConfForMgr", "YES");
        builder.addBody("platform", "0");// 配置类型，0为安卓端，1为苹果端
        builder.addBody("cityCode", SessionContext.getAreaInfo(1));// 城市编码

        ResponseData data = builder.syncRequest(builder);
        data.path = NetURL.APP_INFO;
        data.flag = 1;

        requestID = DataLoader.getInstance().loadData(this, data);
    }

    /**
     * 加载广告信息
     */
    public void loadAd() {
        RequestBeanBuilder builder = RequestBeanBuilder.create(false);
        builder.addBody("getConfForMgr", "YES");
        builder.addBody("adStatus", "1");// 1上架0下架
        builder.addBody("startingTime", DateUtil.getCurDateStr("yyyy-MM-dd"));// 开始时间
        builder.addBody("endTime", DateUtil.getCurDateStr("yyyy-MM-dd"));// 结束时间

        ResponseData data = builder.syncRequest(builder);
        data.path = NetURL.ADVERTISEMENT;
        data.flag = 2;

        requestID = DataLoader.getInstance().loadData(this, data);
    }

    private void loadDiscoverChannel() {
        LogUtil.d("dw", "loadDiscoverChannel()");
        RequestBeanBuilder b = RequestBeanBuilder.create(false);
        ResponseData d = b.syncRequest(b);
        d.path = NetURL.DIS_CHANNEL;
        d.flag = 3;
        requestID = DataLoader.getInstance().loadData(this, d);
    }

    private void parseDiscoveryChannel(ResponseData response) {
        if (response != null && response.body != null) {
            JSONObject mJson = JSON.parseObject(response.body.toString());
            String json = mJson.getString("dataMap");
            JSONObject mmJson = JSON.parseObject(json);
            String jjson = mmJson.getString("data");
            List<DiscoveryChannelBean> temp = JSON.parseArray(jjson, DiscoveryChannelBean.class);
            DiscoveryChannelBean head = new DiscoveryChannelBean();
            head.name = getString(R.string.all_channel);
            head.id = "0";
            temp.add(0, head);
            SessionContext.setChannelList(temp);
        }
    }

    @Override
    public void initListeners() {
        super.initListeners();
        btn_skip.setOnClickListener(this);
        iv_advertisement.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_skip:
                intentActivity();
                break;
            case R.id.iv_advertisement:
                isBreak = true;
                Intent mIntent = new Intent(this, HtmlActivity.class);
                mIntent.putExtra("id", mAdvertBean.Id);
                mIntent.putExtra("title", mAdvertBean.adName);
                mIntent.putExtra("path", mAdvertBean.linkaddress);// temp.entry
                mIntent.putExtra("goBack", "Main");// Html返回处理
                startActivity(mIntent);
                this.finish();
                // 添加友盟自定义事件
                MobclickAgent.onEvent(this, "OpeningAdTouched");
                break;

            default:
                break;
        }
    }

    /**
     * 预加载首页缓存数据
     */
    public void loadCacheData() {
        LogUtil.d("dw", "loadCacheData()");
        Collections.addAll(DataLoader.getInstance().mCacheUrls, NetURL.CACHE_URL);

        try {
            byte[] channeldata = DataLoader.getInstance().getCacheData(NetURL.DIS_CHANNEL);
            if (channeldata != null) {
                String json = new String(channeldata, "UTF-8");
                ResponseData response = JSON.parseObject(json, ResponseData.class);
                parseDiscoveryChannel(response);
            }

            // 获取广告
            String ad = SharedPreferenceUtil.getInstance().getString(AppConst.ADVERTISEMENT_INFO, "", false);
            if (StringUtil.notEmpty(ad)) {
                mAdvertBean = JSON.parseObject(ad, AdvertisementBean.class);
                loadImage(iv_advertisement, mAdvertBean.picture);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转到相应Activity
     */
    public void intentActivity() {
        if (isBreak) {
            return;
        }

        Intent intent = new Intent(WelcomeActivity.this, MainFragmentActivity.class);
        String value = BundleNavi.getInstance().getString("path");
        if (value != null && !value.equals("")) {
            BundleNavi.getInstance().putString("path", value);
        }

        // schema 跳转
        // uri 传入 MainFragmentActivity
        Intent intentScheme = getIntent();
        Uri uri =intentScheme.getData();
        if (uri != null) {
            String path = uri.getPath();
            if (!TextUtils.isEmpty(path)) {
                intent.setData(uri);
            }
        }
        startActivity(intent);
        finish();
    }

    /**
     * 是否显示广告
     */
    private boolean isShowAdvert() {
        // 数据初始化完成 并且有广告图片，显示广告
        return iv_advertisement.getTag() != null;
    }

    /**
     * 显示广告
     */
    public void showAd() {
        long end = SystemClock.elapsedRealtime();

        if (end - start > LOADING_TIME) {
            if (isShowAdvert()) {// 显示隐藏广告界面
                layoutAd.setVisibility(View.VISIBLE);
            }
        } else {
            AppContext.mMainHandler.postDelayed(new Runnable() {// 延迟加载，显示LOADING_TIME时长的加载页

                @Override
                public void run() {
                    if (isShowAdvert()) {// 显示隐藏广告界面
                        layoutAd.setVisibility(View.VISIBLE);
                    }
                }

            }, LOADING_TIME - (end - start));
        }
    }

    /**
     * 跳转到下一个页面
     */
    private void goToNextActivity() {
        long end = SystemClock.elapsedRealtime();

        if (end - start < LOADING_TIME) {
            // 延迟加载
            AppContext.mMainHandler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    AppContext.mMainHandler.postDelayed(new Runnable() {// 延迟加载3s，主要是做显示广告

                        @Override
                        public void run() {
                            intentActivity();
                        }
                    }, AD_TIME);

                }

            }, LOADING_TIME - (end - start));
        } else {
            intentActivity();
        }
    }

    @Override
    public void preExecute(ResponseData request) {
    }

    @Override
    public void notifyMessage(ResponseData request, ResponseData response) throws Exception {
        if (request.flag == 0) {
            JSONObject mJson = JSON.parseObject(response.body.toString());
            if (mJson.containsKey("list_catalog")) {
                String mmJson = mJson.getString("list_catalog");
                List<CommuInfoBean> temp = JSON.parseArray(mmJson, CommuInfoBean.class);
                SessionContext.setCommunityStreetList(temp);
            }
        }else if (request.flag == 1) {
            AppInfoBean mAppInfo = JSON.parseObject(response.body.toString(), AppInfoBean.class);
            LogUtil.d("dw", "AppInfo json[" + response.body.toString() + "]");
            String currentVersion = UpdateControl.getInstance().getCurVersionName();
            String remoteVersion = mAppInfo.vsid;
            if (mAppInfo.isforce == 1 && UpdateControl.getInstance().compareVersion(currentVersion, remoteVersion) < 0) {
                showUpdateDialog(mAppInfo.apkurls, mAppInfo.updesc);
                return;
            }
            // 缓存APP信息
            SharedPreferenceUtil.getInstance().setString(AppConst.APP_INFO, response.body.toString(), false);
            goToNextActivity();
        } else if (request.flag == 2) {
            mAdvertBean = JSON.parseObject(response.body.toString(), AdvertisementBean.class);
            loadImage(iv_advertisement, mAdvertBean.picture);
            SharedPreferenceUtil.getInstance().setString(AppConst.ADVERTISEMENT_INFO, response.body.toString(), false);
        } else if (request.flag == 3) {
            parseDiscoveryChannel(response);
        }
    }

    @Override
    public void notifyError(ResponseData request, ResponseData response, Exception e) {
        removeProgressDialog();
        String message;
        if (e != null && e instanceof ConnectException) {
            message = getString(R.string.dialog_tip_net_error);
        } else {
            message = response != null && response.data != null ? response.data.toString() : getString(R.string.dialog_tip_null_error);
        }
        CustomToast.show(message, Toast.LENGTH_LONG);
        goToNextActivity();
    }

    /**
     * 版本更新对话框
     *
     * @param url
     * @param description
     */
    private void showUpdateDialog(final String url, final String description) {
        CustomDialog dialog = new CustomDialog(this);
        dialog.setTitle(R.string.version_update);
        dialog.setMessage(description);
        dialog.setNegativeButton(R.string.download, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.startWebView(WelcomeActivity.this, url);
            }
        });
        dialog.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityTack.getInstanse().exit();
            }
        });
        dialog.setDismiss(false);
        dialog.show();
    }

    /***
     * 加载广告图片
     */
    public void loadImage(final ImageView iView, String url) {
        url = NetURL.API_LINK + url;// 拼接广告图片路径
        ImageLoader.getInstance().loadBitmap(new ImageCallback() {
            @Override
            public void imageCallback(Bitmap bm, String url, String imageTag) {
                if (bm != null) {
                    iView.setImageBitmap(bm);
                    iView.setTag("Y");
                    showAd();
                }
            }

        }, url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            DataLoader.getInstance().clear(requestID);
            ActivityTack.getInstanse().exit();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 初始化北京平台埋点
     */
//    private void initStatistical() {
//        try {
//            PackageManager manager = this.getPackageManager();
//            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
//            String version = info.versionName;
//            String cityId = SessionContext.getAreaInfo(1);
//
//            InitParameter param = new InitParameter();
//            param.setUrl("http://uas.scity.cn/analy/upload");// http://uas.scity.com/analy/upload
//            param.setDl("AndroidServer");// 预先在后台定义的下载渠道
//            // String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dcStatistic/" + getPackageName();
//            param.setLogDirPath(Utils.getFolderDir("dcStatistic"));// 离线日志存储目录
//            param.setMfv(version);// 主框架版本
//            param.setStaAppkey("lctz" + SessionContext.getAreaInfo(1));// 平台埋点key
//
//            StatisticProxy.init(AppContext.mAppContext, param);
//            StatisticProxy.getInstance().enableRealTimeUpload(true);// 开启/关闭实时上传功能
//            StatisticProxy.getInstance().setLog(true);// 开启或关闭日志功能
//
//            boolean isFirst = SharedPreferenceUtil.getInstance().getBoolean("dc_smart_city_statistical_first", true);
//            LogUtil.d("dw", "StatisticProxy's sharepreference is " + isFirst);
//            if (isFirst) {
//                StatisticProxy.getInstance().onFirst(this, "first-categoryId", cityId);// 发送首次访问分析事件，应用在首次启动时调用。
//                SharedPreferenceUtil.getInstance().setBoolean("dc_smart_city_statistical_first", false);
//            } else {
//                StatisticProxy.getInstance().onLaunch(this, "launch-categoryId", cityId);// 发送除首次外应用启动的访问分析事件，在每次（除首次）应用启动时调用
//            }
//
//            StatisticProxy.getInstance().postClientFileDatas();// 上传统计分析日志，在系统需要上传本地统计分析日志时调用。
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
