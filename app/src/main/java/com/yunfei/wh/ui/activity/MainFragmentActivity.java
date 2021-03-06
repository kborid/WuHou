package com.yunfei.wh.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.prj.sdk.app.AppContext;
import com.prj.sdk.constants.Const;
import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.util.ActivityTack;
import com.prj.sdk.util.DateUtil;
import com.prj.sdk.util.LogUtil;
import com.prj.sdk.util.SharedPreferenceUtil;
import com.prj.sdk.util.StringUtil;
import com.prj.sdk.widget.CustomToast;
import com.umeng.analytics.MobclickAgent;
import com.yunfei.wh.R;
import com.yunfei.wh.app.PRJApplication;
import com.yunfei.wh.codescan.control.CaptureActivity;
import com.yunfei.wh.common.AppConst;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.control.BundleNavi;
import com.yunfei.wh.control.UpdateControl;
import com.yunfei.wh.net.RequestBeanBuilder;
import com.yunfei.wh.net.bean.UserInfo;
import com.yunfei.wh.permission.PermissionsActivity;
import com.yunfei.wh.permission.PermissionsDef;
import com.yunfei.wh.ui.adapter.MainFragmentAdapter;
import com.yunfei.wh.ui.base.BaseFragmentActivity;
import com.yunfei.wh.ui.custom.CommonTitleLayout;
import com.yunfei.wh.ui.custom.CustomViewPager;
import com.yunfei.wh.ui.custom.UserCenterLayout;
import com.yunfei.wh.ui.fragment.TabCommunityFragment;
import com.yunfei.wh.ui.fragment.TabHomeFragment;
import com.yunfei.wh.ui.fragment.TabServerCenterFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainFragmentActivity extends BaseFragmentActivity implements OnPageChangeListener, View.OnClickListener, DataCallback {

    private RadioGroup radioGroup;
    private CustomViewPager viewPager;
    private long exitTime = 0;
    private CommonTitleLayout commonTitleLayout;
    private UserCenterLayout userCenterLayout;
    private DrawerLayout drawerLayout;
//    private SystemBarTintManager mTintManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main_tab);
        initViews();
        dealIntent();
        initParams();
        initListeners();
    }

    public void initViews() {
        super.initViews();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        userCenterLayout = (UserCenterLayout) findViewById(R.id.usercenterlayout);
        commonTitleLayout = (CommonTitleLayout) findViewById(R.id.commontitlelayout);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        if (radioGroup != null) {
            radioGroup.setOnCheckedChangeListener(this);
        }
        viewPager = (CustomViewPager) findViewById(R.id.viewPager);
        if (viewPager != null) {
            viewPager.setSlidingEnabled(false);
            viewPager.addOnPageChangeListener(this);
        }
    }

    @Override
    public void initParams() {
        super.initParams();
        if (SessionContext.isLogin()) {// 登录状态下验证票据是否失效，默认登录后6天在做检查
            String lastLoginTime = SharedPreferenceUtil.getInstance().getString(AppConst.LAST_LOGIN_DATE, "", false);
            if (StringUtil.notEmpty(lastLoginTime)) {
                Date lastLoginDate = DateUtil.str2Date(lastLoginTime);
                if (DateUtil.getGapCount(lastLoginDate, new Date(System.currentTimeMillis())) >= 6) {
                    loadValidateTicketExpire();
                } else {
                    requestUserInfo();
                }
            } else {//如果没有值，则不是4.0.0版本，需要登录
                Intent intent = new Intent(Const.UNLOGIN_ACTION);
                intent.putExtra(Const.IS_SHOW_TIP_DIALOG, true);
                AppContext.mAppContext.sendBroadcast(intent);// 发送登录广播
            }
        }
        initFragmentView();

        // android os 4.4及以上场合，可以设置status bar颜色
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            boolean flag = StatusBarControl.setTranslucentStatus(this, true);
//            if (flag) {
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                params.topMargin = Utils.mStatusBarHeight;
//                commonTitleLayout.setLayoutParams(params);
//                mTintManager = new SystemBarTintManager(this);
//                mTintManager.setStatusBarTintEnabled(true);
//                mTintManager.setStatusBarTintResource(R.color.main_color_wh);
//            }
//        }

//        UmengUpdateAgent.update(this);// 友盟渠道版本更新
        UpdateControl.getInstance().update();
    }

    @Override
    public void dealIntent() {
        super.dealIntent();
        String path = BundleNavi.getInstance().getString("path");
        LogUtil.d("JPush", "main value = " + path);
        if (path != null && !path.equals("")) {
            Intent intent = new Intent(this, HtmlActivity.class);
            intent.putExtra("path", path);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        // schema
        Intent intentScheme = getIntent();
        Uri uri =intentScheme.getData();
        if (uri != null) {
            String path2 = uri.getPath();
            if (!TextUtils.isEmpty(path2) && path2 != "") {

                // 芝麻信用
                if (path2.startsWith("/ZMXY/")) {
                    path2 = path2.replace("/ZMXY/", "");
                    if (path2.startsWith("http")) {
                        Intent intent = new Intent(this, HtmlActivity.class);
                        String fragment = uri.getFragment();
                        String query = uri.getQuery();

                        if (!TextUtils.isEmpty(query) && query != "") {
                            path2 = path2 + "?" + query;
                        }
                        if (!TextUtils.isEmpty(fragment) && fragment != "") {
                            path2 = path2 + "#" + fragment;
                        }

                        intent.putExtra("path", path2);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }
        }
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Note that getIntent() still returns the original Intent. You can use setIntent(Intent) to update it to this new Intent.
        setIntent(intent);
        dealIntent();
    }

    /**
     * 初始化Fragment视图
     */
    private void initFragmentView() {
        List<Fragment> mList = new ArrayList<>();
        mList.add(TabHomeFragment.newInstance("home", callBackListener));
        mList.add(TabServerCenterFragment.newInstance("server", callBackListener));
        mList.add(TabCommunityFragment.newInstance("community", callBackListener));
        viewPager.setOffscreenPageLimit(mList.size());
        viewPager.setAdapter(new MainFragmentAdapter(getSupportFragmentManager(), mList));
        radioGroup.getChildAt(0).performClick();
    }

    @Override
    public void initListeners() {
        super.initListeners();
        commonTitleLayout.setOnClickListeners(this);
        userCenterLayout.setOnClickListener(this);
    }

    /**
     * 加载验证票据是否失效
     */
    private void loadValidateTicketExpire() {
        System.out.println("loadValidateTicketExpire()");
        RequestBeanBuilder builder = RequestBeanBuilder.create(true);

        ResponseData data = builder.syncRequest(builder);
        data.path = NetURL.VALIDATE_TICKET;
        data.flag = 1;
        DataLoader.getInstance().loadData(this, data);
    }

    private void requestUserInfo() {
        RequestBeanBuilder builder = RequestBeanBuilder.create(true);

        ResponseData data = builder.syncRequest(builder);
        data.path = NetURL.GET_USER_INFO;
        data.flag = 2;
        DataLoader.getInstance().loadData(this, data);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        try {
            switch (checkedId) {
                case R.id.qu_btn_home:
                    commonTitleLayout.relizeTitleLayout("", false, true);
                    viewPager.setCurrentItem(0, false);
                    break;
                case R.id.qu_btn_servercenter:
                    commonTitleLayout.relizeTitleLayout("", false, true);
                    viewPager.setCurrentItem(1, false);
                    break;
                case R.id.qu_btn_community:
                    commonTitleLayout.relizeTitleLayout("", true, false);
                    viewPager.setCurrentItem(2, false);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (drawerLayout.isDrawerOpen(userCenterLayout)) {
                drawerLayout.closeDrawers();
                return true;
            }
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次 退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                SessionContext.destroy();
                MobclickAgent.onKillProcess(this);// 调用Process.kill或者System.exit之类的方法杀死进程前保存统计数据
                DataLoader.getInstance().clearRequests();
                ActivityTack.getInstanse().exit();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userCenterLayout.unregisterBroadReceiver();
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
//        int width = Utils.mScreenWidth / 2;
//        int value = Math.abs(arg2 - width);
//        float alpha = (float) value / width;
//        commonTitleLayout.findViewById(R.id.title_lay).setAlpha(alpha);
    }

    @Override
    public void onPageSelected(int arg0) {
        try {
            radioGroup.getChildAt(arg0).performClick();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTitleBackgroundAlpha(int scroll) {
        commonTitleLayout.setBackgroundAlpha(scroll);
    }

//    public void setStatusBarBackgroundAlpha(int scroll) {
//        if (mTintManager != null) {
//            int alphaValue;
//            int height = Utils.dip2px(216 - 56) - Utils.mStatusBarHeight;
//            alphaValue = scroll < height ? (int) ((float) scroll / height * 255) : 255;
//            mTintManager.setStatusBarAlpha(alphaValue);
//        }
//    }

    @Override
    public void preExecute(ResponseData request) {
    }

    @Override
    public void notifyMessage(ResponseData request, ResponseData response) throws Exception {
        if (response != null && response.body != null) {
            if (request.flag == 2) {
                System.out.println("json = " + response.body.toString());
                SessionContext.mUser = JSON.parseObject(response.body.toString(), UserInfo.class);
                if (StringUtil.isEmpty(SessionContext.mUser) || StringUtil.isEmpty(SessionContext.mUser.USERBASIC)) {
                    CustomToast.show("获取用户信息失败，请重试", 0);
                    return;
                }
                SharedPreferenceUtil.getInstance().setString(AppConst.USER_PHOTO_URL, SessionContext.mUser.USERBASIC != null ? SessionContext.mUser.USERBASIC.getPhotoUrl() : "", false);
                SharedPreferenceUtil.getInstance().setString(AppConst.USER_INFO, response.body.toString(), true);
                AppContext.mAppContext.sendBroadcast(new Intent(Const.UPDATE_USERINFO));
            }
        }
    }

    @Override
    public void notifyError(ResponseData request, ResponseData response, Exception e) {
        CustomToast.show("登录超时，请重新登录！", 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userinfo_lay:
                drawerLayout.openDrawer(userCenterLayout);
                break;
            case R.id.code_lay:
                if (PRJApplication.getPermissionsChecker(this).lacksPermissions(PermissionsDef.CAMERA)) {
                    PermissionsActivity.startActivityForResult(this, PermissionsDef.PERMISSION_REQ_CODE, PermissionsDef.CAMERA);
                    return;
                }
                Intent intent = new Intent(MainFragmentActivity.this, CaptureActivity.class);
                startActivity(intent);
                break;
            case R.id.title_lay:
                if (popupListener != null) {
                    popupListener.onShowPopUp();
                }
                break;
            default:
                break;
        }
    }

    public interface OnPopUpShowListener {
        void onShowPopUp();
    }

    private static OnPopUpShowListener popupListener = null;

    public static void setOnPopUpShowListener(OnPopUpShowListener listener) {
        popupListener = listener;
    }

    private OnCallBackListener callBackListener = new OnCallBackListener() {
        @Override
        public void onScroll(int scrollvalue) {
//            setStatusBarBackgroundAlpha(scrollvalue);
            setTitleBackgroundAlpha(scrollvalue);
        }

        @Override
        public void onTitleChanged(String title) {
            commonTitleLayout.setTitle(title);
        }
    };

    public interface OnCallBackListener {
        void onTitleChanged(String title);

        void onScroll(int scrollvalue);
    }
}
