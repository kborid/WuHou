package com.yunfei.wh.control;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.prj.sdk.util.LogUtil;
import com.prj.sdk.util.SharedPreferenceUtil;
import com.yunfei.wh.common.AppConst;

/**
 * 高德定位
 */
public class AMapLocationControl {
    private static final String TAG = "AMapLocationControl";

    private AMapLocationClient mAmapLocationClient = null;
    private static AMapLocationControl instance = null;

    public static AMapLocationControl getInstance() {
        if (instance == null) {
            synchronized (AMapLocationControl.class) {
                if (instance == null) {
                    instance = new AMapLocationControl();
                }
            }
        }
        return instance;
    }

    private void initLocation(Context context, boolean isOnce) {
        if (mAmapLocationClient == null) {
            mAmapLocationClient = new AMapLocationClient(context);
        }
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        if (isOnce) {
            option.setOnceLocation(true);
        } else {
            option.setInterval(2000);
        }
        option.setNeedAddress(true);
        option.setMockEnable(true);
        mAmapLocationClient.setLocationOption(option);
    }

    public synchronized void startLocationOnce(Context context, AMapLocationListener listener) {
        initLocation(context, true);
        mAmapLocationClient.setLocationListener(listener);
        mAmapLocationClient.startLocation();
    }

    public synchronized void startLocationOnce(Context context) {
        initLocation(context, true);
        mAmapLocationClient.setLocationListener(listener);
        mAmapLocationClient.startLocation();
    }

    public synchronized void startLocationAlways(Context context, AMapLocationListener listener) {
        initLocation(context, false);
        mAmapLocationClient.setLocationListener(listener);
        mAmapLocationClient.startLocation();
    }

    public void stopLocation() {
        if (mAmapLocationClient != null) {
            mAmapLocationClient.stopLocation();
            mAmapLocationClient.onDestroy();
            mAmapLocationClient = null;
        }
    }

    public boolean isStart() {
        boolean isStarted = false;
        if (mAmapLocationClient != null) {
            isStarted = mAmapLocationClient.isStarted();
        }
        return isStarted;
    }

    private AMapLocationListener listener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (null != aMapLocation) {
                if (aMapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    try {
                        SharedPreferenceUtil.getInstance().setFloat(AppConst.LOCATION_LON, (float) aMapLocation.getLongitude());
                        SharedPreferenceUtil.getInstance().setFloat(AppConst.LOCATION_LAT, (float)aMapLocation.getLatitude());
                        LogUtil.d(TAG, "lon = " + aMapLocation.getLongitude());
                        LogUtil.d(TAG, "lat = " + aMapLocation.getLatitude());
                        SharedPreferenceUtil.getInstance().setString(AppConst.SITEID, String.valueOf(aMapLocation.getAdCode()), false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    LogUtil.e(TAG, "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };
}
