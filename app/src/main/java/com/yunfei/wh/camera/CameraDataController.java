package com.yunfei.wh.camera;

import android.content.Intent;

import com.prj.sdk.app.AppContext;
import com.prj.sdk.util.LogUtil;

/**
 * @auth kborid
 * @date 2018/1/13 0013.
 */

public class CameraDataController {
    private static final String TAG = "CameraDataController";
    public static final int CAPTURE_CARD = 0; //拍摄身份证
    public static final int CAPTURE_SELF = 1; //自拍
    private ICustomCameraResultListener listener = null;

    private static CameraDataController instance;

    public static CameraDataController getInstance() {
        if (null == instance) {
            instance = new CameraDataController();
        }
        return instance;
    }

    public void startCameraActivity() {
        startCameraActivity(CAPTURE_CARD);
    }

    public void startCameraActivity(int type) {
        Intent intent = new Intent(AppContext.mAppContext, CustomCameraActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("cameraType", type);
        AppContext.mAppContext.startActivity(intent);
    }

    public void setCameraDataResultListener(ICustomCameraResultListener listener) {
        LogUtil.i(TAG, "setCameraDataResultListener()");
        this.listener = listener;
    }

    public void notifyCameraDataResultListener(byte[] ret) {
        LogUtil.i(TAG, "notifyCameraDataResult() ret = " + ret);
        if (null != listener) {
            listener.notifySuccess(ret);
        }
    }
}
