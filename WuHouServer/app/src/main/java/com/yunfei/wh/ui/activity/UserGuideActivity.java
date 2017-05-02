package com.yunfei.wh.ui.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.prj.sdk.util.SharedPreferenceUtil;
import com.prj.sdk.util.Utils;
import com.yunfei.wh.R;
import com.yunfei.wh.common.AppConst;
import com.yunfei.wh.ui.adapter.ViewPagerAdapter;
import com.yunfei.wh.ui.base.BaseActivity;

/**
 * 用户引导页面
 *
 * @author LiaoBo
 */
public class UserGuideActivity extends BaseActivity implements OnPageChangeListener {

    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;
    private int flaggingWidth;        // 互动翻页所需滚动的长度是当前屏幕宽度的1/3
    private GestureDetector gestureDetector;    // 用户滑动
    private int currentItem;        // 当前位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_user_guide);
        initViews();
        initListeners();
        initParams();
    }

    @Override
    public void initViews() {
        super.initViews();
        mViewPager = (ViewPager) findViewById(R.id.guide_view);
    }

    @Override
    public void initListeners() {
        super.initListeners();
        mViewPager.setOnPageChangeListener(this);
    }

    @Override
    public void initParams() {
        super.initParams();
        final int[] resIds = {R.drawable.user_guide_1, R.drawable.user_guide_2, R.drawable.user_guide_3};
        ArrayList<View> list = new ArrayList<>();
        for (int i = 0; i < resIds.length; i++) {
            ImageView view = new ImageView(this);
            view.setBackgroundResource(resIds[i]);
            list.add(view);
        }
        mAdapter = new ViewPagerAdapter(list);
        mViewPager.setAdapter(mAdapter);
        gestureDetector = new GestureDetector(this, new GuideViewTouch());
        flaggingWidth = Utils.mScreenWidth / 3;
    }

    @Override
    public void onClick(View v) {
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            event.setAction(MotionEvent.ACTION_CANCEL);
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 手势监听，最后一页滑动进入首页
     *
     * @author LiaoBo
     */
    private class GuideViewTouch extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (currentItem == 2) {
                if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY() - e2.getY()) && (e1.getX() - e2.getX() <= (-flaggingWidth) || e1.getX() - e2.getX() >= flaggingWidth)) {
                    if (e1.getX() - e2.getX() >= flaggingWidth) {
                        gotoMain();
                        return true;
                    }
                }
            }
            return false;
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        currentItem = arg0;

    }

    /**
     * 跳转到首页
     */
    private void gotoMain() {
        int versionCode = 0;
        try {
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        SharedPreferenceUtil.getInstance().setInt(AppConst.LAST_USE_VERSIONCODE, versionCode);
        Intent intent = new Intent(this, MainFragmentActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != gestureDetector) {
            gestureDetector = null;
        }
    }
}
