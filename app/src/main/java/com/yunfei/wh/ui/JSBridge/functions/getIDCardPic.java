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

/**
 * 获取用户ID,用户未登录时调用此接口框架会要求用户去登录
 *
 * @author LiaoBo
 */
public class getIDCardPic implements WVJBWebViewClient.WVJBHandler {
    private Context mContext;

    public getIDCardPic(Context context) {
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
                            LogUtil.i("getIDCardPic", "capture success");
                            LogUtil.i("getIDCardPic", Arrays.toString(ret));
                            JSONObject mJson = new JSONObject();
                            mJson.put("data", Base64.encode(ret));
                            callback.callback(mJson.toString());
                        } else {
                            LogUtil.i("getIDCardPic", "capture error");
                        }
                    }
                });
                CameraDataController.getInstance().startCameraActivity(CameraDataController.CAPTURE_CARD);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
