package com.yunfei.wh.ui.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.yunfei.wh.R;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.control.AMapLocationControl;
import com.yunfei.wh.net.RequestBeanBuilder;
import com.yunfei.wh.net.bean.SchoolInfoBean;
import com.yunfei.wh.ui.adapter.PinInfoWindowAdapter;
import com.yunfei.wh.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kborid
 * @date 2016/10/31 0031
 * @modify 2017/02/09 by kborid
 */
public class MapActivity extends BaseActivity implements LocationSource, AMap.OnMarkerClickListener, AMapLocationListener, DataCallback {

    private ImageButton iv_zoom_out;
    private ImageButton iv_zoom_in;
    private ImageButton iv_loc;

    private MapView mapview = null;
    private AMap amap = null;
    private Marker currentMarker;
    private boolean isFirst = true;
    private boolean toMyLoc = false;
    private List<SchoolInfoBean> list = new ArrayList<>();

    private OnLocationChangedListener mListener;
    private AMapLocation aMapLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_map_layout);
        initViews();
        initParams();
        initListeners();
        mapview.onCreate(savedInstanceState);
    }

    @Override
    public void initViews() {
        super.initViews();
        mapview = (MapView) findViewById(R.id.mapview);
        iv_zoom_out = (ImageButton) findViewById(R.id.iv_zoom_out);
        iv_zoom_in = (ImageButton) findViewById(R.id.iv_zoom_in);
        iv_loc = (ImageButton) findViewById(R.id.iv_loc);
    }

    @Override
    public void initParams() {
        tv_center_title.setText("附近学校");

        if (amap == null) {
            amap = mapview.getMap();
            setUpMap();
        }
        requestSchoolData();
        super.initParams();
    }

    private void setUpMap() {
        amap.getUiSettings().setScaleControlsEnabled(true);
        amap.getUiSettings().setZoomControlsEnabled(false);
        amap.setTrafficEnabled(false);// 设置地图是否显示traffic情报
        PinInfoWindowAdapter infoWindowAdapter = new PinInfoWindowAdapter(this);
        amap.setInfoWindowAdapter(infoWindowAdapter);
    }

    @Override
    public void initListeners() {
        super.initListeners();
        iv_zoom_out.setOnClickListener(this);
        iv_zoom_in.setOnClickListener(this);
        iv_loc.setOnClickListener(this);
        amap.setOnMarkerClickListener(this);
        amap.setLocationSource(this);// 设置定位监听
        amap.setMyLocationEnabled(true);
        amap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_zoom_in:
                if (amap.getCameraPosition().zoom < amap.getMaxZoomLevel()) {
                    if (!iv_zoom_out.isEnabled()) {
                        iv_zoom_out.setEnabled(true);
                    }
                    amap.animateCamera(CameraUpdateFactory.zoomIn());
                } else {
                    iv_zoom_in.setEnabled(false);
                }
                break;
            case R.id.iv_zoom_out:
                if (amap.getCameraPosition().zoom > amap.getMinZoomLevel()) {
                    if (!iv_zoom_in.isEnabled()) {
                        iv_zoom_in.setEnabled(true);
                    }
                    amap.animateCamera(CameraUpdateFactory.zoomOut());
                } else {
                    iv_zoom_out.setEnabled(false);
                }
                break;
            case R.id.iv_loc:
                if (aMapLocation != null) {
                    amap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()), amap.getCameraPosition().zoom));
                } else {
                    toMyLoc = true;
                    AMapLocationControl.getInstance().startLocationAlways(this, this);
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapview.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapview.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapview.onDestroy();
        deactivate();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (aMapLocation != null) {
            if (marker.getPosition().latitude == aMapLocation.getLatitude() && marker.getPosition().longitude == aMapLocation.getLongitude()) {
                return true;
            }
        } else {
            isFirst = false;
        }
        if (currentMarker != null) {
            currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.iv_pin)));
        }
        currentMarker = marker;
        currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.iv_selected_pin)));
        amap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentMarker.getPosition(), amap.getCameraPosition().zoom));
        return false;
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (null != aMapLocation) {
            if (aMapLocation.getErrorCode() == 0) {
                this.aMapLocation = aMapLocation;
                if (mListener != null) {
                    mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                }
                if (isFirst || toMyLoc) {
                    isFirst = false;
                    toMyLoc = false;
                    amap.animateCamera(CameraUpdateFactory.changeLatLng(new LatLng(
                            aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    amap.animateCamera(CameraUpdateFactory.zoomTo(15));
                }
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    private void requestSchoolData() {
        RequestBeanBuilder b = RequestBeanBuilder.create(false);

        ResponseData d = b.syncRequest(b);
        d.flag = 0;
        d.path = NetURL.SCHOOL_MAP;

        if (!isProgressShowing()) {
            showProgressDialog(false);
        }
        requestID = DataLoader.getInstance().loadData(this, d);
    }

    @Override
    public void preExecute(ResponseData request) {

    }

    @Override
    public void notifyMessage(ResponseData request, ResponseData response) throws Exception {
        if (request.flag == 0) {
            if (isProgressShowing()) {
                removeProgressDialog();
            }
            JSONObject json = JSON.parseObject(response.body.toString());
            if (json.containsKey("data")) {
                String jsonStr = json.getString("data");
                List<SchoolInfoBean> temp = JSON.parseArray(jsonStr, SchoolInfoBean.class);
                if (temp.size() > 0) {
                    for (int i = 0; i < temp.size(); i++) {
                        SchoolInfoBean bean = temp.get(i);
                        LatLng lonlat = new LatLng(Double.valueOf(bean.latitude), Double.valueOf(bean.longitude));
                        MarkerOptions marker = new MarkerOptions();
                        marker.position(lonlat).title(bean.name).draggable(true).snippet(bean.addr);
                        marker.period(bean.id);
                        marker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.iv_pin)));
                        amap.addMarker(marker);
                    }
                    if (list != null && list.size() > 0) {
                        list.clear();
                    }
                    assert list != null;
                    list.addAll(temp);
                }
            }
        }
    }

    @Override
    public void notifyError(ResponseData request, ResponseData response, Exception e) {

    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        AMapLocationControl.getInstance().startLocationAlways(this, this);
    }

    @Override
    public void deactivate() {
        mListener = null;
        AMapLocationControl.getInstance().stopLocation();
    }
}
