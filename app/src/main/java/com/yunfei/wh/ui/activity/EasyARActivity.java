/**
 * Copyright (c) 2015-2016 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
 * EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
 * and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
 */

package com.yunfei.wh.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.yunfei.wh.R;
import com.yunfei.wh.ar.DCEasyARControlJNI;
import com.yunfei.wh.ar.DCImageVideoInfo;
import com.yunfei.wh.ar.DCOpenGLView;
import com.yunfei.wh.ar.DCViewRenderer;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.ui.base.BaseActivity;

import java.util.ArrayList;

import cn.easyar.engine.EasyAR;


public class EasyARActivity extends BaseActivity {

    private ArrayList<DCImageVideoInfo> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_easyar_layout);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        EasyAR.initialize(this, getString(R.string.EasyARAppKey));
        initViews();
        initParams();
        initListeners();
        DCEasyARControlJNI.nativeRotationChange(getWindowManager().getDefaultDisplay().getRotation() == android.view.Surface.ROTATION_0);
    }

    @Override
    public void initViews() {
        super.initViews();
        DCOpenGLView glView = new DCOpenGLView(this);
        glView.setRenderer(new DCViewRenderer());
        glView.setZOrderMediaOverlay(true);
        ((ViewGroup) findViewById(R.id.preview)).addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void initParams() {
        super.initParams();
        DCImageVideoInfo info1 = new DCImageVideoInfo("wuhouARTarget.jpg", "wuhouARTarget", false, 2, NetURL.getApi() + "img/attached/pic/app/media/wh_AR.MP4");
        DCImageVideoInfo info2 = new DCImageVideoInfo("wuhouARLogo.jpg", "wuhouARLogo", false, 2, NetURL.getApi() + "img/attached/pic/app/media/wh_AR.MP4");
        list.add(info1);
        list.add(info2);
        DCEasyARControlJNI.nativeInit(list);
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        DCEasyARControlJNI.nativeRotationChange(getWindowManager().getDefaultDisplay().getRotation() == android.view.Surface.ROTATION_0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EasyAR.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EasyAR.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DCEasyARControlJNI.nativeDestory();
    }
}
