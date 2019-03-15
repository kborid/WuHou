package com.yunfei.wh.ui.JSBridge.functions;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.prj.sdk.util.LogUtil;
import com.yunfei.wh.camera.CameraDataController;
import com.yunfei.wh.camera.ICustomCameraResultListener;
import com.yunfei.wh.common.pay.alipay.Base64;
import com.yunfei.wh.ui.JSBridge.WVJBWebViewClient;
import com.yunfei.wh.ui.JSBridge.WVJBWebViewClient.WVJBResponseCallback;

import java.util.Arrays;

public class getSelfPic implements WVJBWebViewClient.WVJBHandler {
    private Context mContext;

    public getSelfPic(Context context) {
        mContext = context;
    }

    @Override
    public void request(Object data, final WVJBResponseCallback callback) {
        try {
            if (callback != null) {
                CameraDataController.getInstance().setCameraDataResultListener(new ICustomCameraResultListener() {
                    @Override
                    public void notifySuccess(byte[] ret) {
                        CameraDataController.getInstance().setCameraDataResultListener(null);
                        if (ret != null && ret.length > 0) {
                            LogUtil.i("getSelfPic", "capture success");
                            LogUtil.i("getSelfPic", Arrays.toString(ret));
                            JSONObject mJson = new JSONObject();
                            mJson.put("data", Base64.encode(ret));
                            callback.callback(mJson.toString());
                        } else {
                            LogUtil.i("getSelfPic", "capture error");
                        }
                    }
                });
                CameraDataController.getInstance().startCameraActivity(CameraDataController.CAPTURE_SELF);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
