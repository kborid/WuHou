package com.yunfei.wh.ui.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.util.DateUtil;
import com.prj.sdk.util.LogUtil;
import com.prj.sdk.util.NetworkUtil;
import com.prj.sdk.util.SharedPreferenceUtil;
import com.prj.sdk.util.StringUtil;
import com.prj.sdk.util.Utils;
import com.prj.sdk.widget.CustomToast;
import com.umeng.analytics.MobclickAgent;
import com.yunfei.wh.BuildConfig;
import com.yunfei.wh.R;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.control.ServerDispatchControl;
import com.yunfei.wh.control.UpdateControl;
import com.yunfei.wh.control.WeatherInfoControl;
import com.yunfei.wh.net.RequestBeanBuilder;
import com.yunfei.wh.net.bean.AppCategoryListBean;
import com.yunfei.wh.net.bean.AppListBean;
import com.yunfei.wh.net.bean.DiscoveryChannelBean;
import com.yunfei.wh.net.bean.MainBannerBean;
import com.yunfei.wh.net.bean.WeatherForHomeBean;
import com.yunfei.wh.circle.DiscoveryActivity;
import com.yunfei.wh.ui.activity.HtmlActivity;
import com.yunfei.wh.ui.activity.MainFragmentActivity;
import com.yunfei.wh.ui.adapter.HomeImageAdapter;
import com.yunfei.wh.ui.base.BaseFragment;
import com.yunfei.wh.ui.custom.CommonBannerLayout;
import com.yunfei.wh.ui.custom.MyListViewWidget;
import com.yunfei.wh.ui.custom.MyScrollView;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tab1 首页
 *
 * @author duanwei
 */
public class TabHomeFragment extends BaseFragment implements DataCallback, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    public static final int BANNER_HOME_TOP = 10;
    public static final int SERVICE_REQUEST = 1;
    private static final int WEATHER_DATA = 2;
    public static final int BANNER_SERVER = 3;
    private static final int DISCOVERY_CHANNEL = 4;
    public static final String SERVERDATA_TYPE = "1";

    private static final int COLUME = 4;

    private static final int MANUAL_COMPLETE = 0x00;
    private static final int ERROR = 0x01;
    private static final int UPDATECOMPLETE = 0x03;
    private SwipeRefreshLayout swipe_refresh_lay;
    private MyScrollView scrollview;
    private int scrollValue = 0;
    private CommonBannerLayout banner;
    private List<AppCategoryListBean> listCategory = null;
    private List<AppListBean> appList = new ArrayList<>();
    private LinearLayout install_lay;
    private TextView tv_install;
    private ImageView iv_ignore;
    private LinearLayout server_lay;
    private MyListViewWidget listview;
    private List<MainBannerBean> homeImageList = new ArrayList<>();
    private HomeImageAdapter adapter;
    private RelativeLayout weather_lay;
    private RelativeLayout wh_air_lay;
    private LinearLayout lj_air_lay;
    private TextView tv_air, tv_air_unit, tv_pm;
    private RelativeLayout limit_lay;
    private TextView tv_limit1, tv_limit2;
    private TextView tv_temp, tv_weather;
    private ImageView iv_weather;
    private WeatherForHomeBean weatherbean = null;
    private static MainFragmentActivity.OnCallBackListener callBackListener = null;
    private static boolean isFirstLoad = false;
    private Map<Integer, Integer> mTag = new HashMap<>();
    private List<MainBannerBean> topBannerList = new ArrayList<>();

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MANUAL_COMPLETE:
                    swipe_refresh_lay.setRefreshing(false);
                    break;
                case ERROR:
                    swipe_refresh_lay.setRefreshing(false);
                    CustomToast.show(msg.obj.toString(), Toast.LENGTH_LONG);
                    break;
                case UPDATECOMPLETE:
                    if (UpdateControl.getInstance().isCanInstall()) {
                        if (!SharedPreferenceUtil.getInstance().getBoolean("IgnoreInstall", false)) {
                            install_lay.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        isFirstLoad = true;
        getArguments().getString("key");
        View view = inflater.inflate(R.layout.fragment_tab_home, container, false);
        initViews(view);
        initParams();
        initListeners();
        return view;
    }

    public static Fragment newInstance(String key, MainFragmentActivity.OnCallBackListener callBackListener) {
        TabHomeFragment.callBackListener = callBackListener;
        Fragment fragment = new TabHomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("key", key);
        fragment.setArguments(bundle);
        return fragment;
    }

    protected void onVisible() {
        super.onVisible();
        MainFragmentActivity.setOnPopUpShowListener(null);

        if (callBackListener != null) {
            callBackListener.onScroll(scrollValue);
        }

        if (UpdateControl.getInstance().isCanInstall()) {
            install_lay.setVisibility(View.VISIBLE);
        } else {
            install_lay.setVisibility(View.GONE);
            UpdateControl.getInstance().downloadAPKFile();
        }
        banner.startBanner();
        if (isFirstLoad) {
            isFirstLoad = false;
            if (NetworkUtil.isNetworkAvailable()) {
                requestBannerData(BANNER_HOME_TOP);
                requestBannerData(BANNER_SERVER);
                requestWeatherInfo();
                requestServiceData();
                if (SessionContext.getChannelList().size() == 0) {
                    requestDiscoveryChannel();
                }
            }
        }
    }

    protected void onInvisible() {
        super.onInvisible();
        banner.stopBanner();
        if (swipe_refresh_lay.isRefreshing()) {
            swipe_refresh_lay.setRefreshing(false);
        }
    }

    @Override
    protected void initViews(View view) {
        super.initViews(view);
        swipe_refresh_lay = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_lay);
        scrollview = (MyScrollView) view.findViewById(R.id.scrollview);
        banner = (CommonBannerLayout) view.findViewById(R.id.banner);
        install_lay = (LinearLayout) view.findViewById(R.id.install_lay);
        iv_ignore = (ImageView) view.findViewById(R.id.iv_ignore);
        tv_install = (TextView) view.findViewById(R.id.tv_install);
        server_lay = (LinearLayout) view.findViewById(R.id.server_lay);
        listview = (MyListViewWidget) view.findViewById(R.id.listview);
        adapter = new HomeImageAdapter(getActivity(), homeImageList);
        listview.setAdapter(adapter);
        weather_lay = (RelativeLayout) view.findViewById(R.id.weather_lay);
        wh_air_lay = (RelativeLayout) view.findViewById(R.id.wh_air_lay);
        lj_air_lay = (LinearLayout)view.findViewById(R.id.lj_air_lay);
        tv_air = (TextView) view.findViewById(R.id.tv_air);
        tv_air_unit = (TextView) view.findViewById(R.id.tv_air_unit);
        tv_limit1 = (TextView) view.findViewById(R.id.tv_limit1);
        limit_lay = (RelativeLayout) view.findViewById(R.id.limit_lay);
        iv_weather = (ImageView) view.findViewById(R.id.iv_weather);
        tv_limit2 = (TextView) view.findViewById(R.id.tv_limit2);
        tv_temp = (TextView) view.findViewById(R.id.tv_temp);
        tv_weather = (TextView) view.findViewById(R.id.tv_weather);
        tv_pm = (TextView) view.findViewById(R.id.tv_pm);
    }

    @Override
    protected void initParams() {
        super.initParams();
        swipe_refresh_lay.setColorSchemeResources(R.color.main_color_wh);
        swipe_refresh_lay.setDistanceToTriggerSync(200);
        int start = Utils.dip2px(56);
        int end = start + Utils.dip2px(20);
        swipe_refresh_lay.setProgressViewOffset(true, start, end);
        swipe_refresh_lay.setSize(SwipeRefreshLayout.DEFAULT);

        listCategory = SessionContext.getAllCategoryList();
        loadHomeServiceCache();
        refreshServiceLayout();
    }

    @Override
    public void initListeners() {
        super.initListeners();
        weather_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), DiscoveryActivity.class));
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String url = homeImageList.get(position).linkurls;
                // 添加友盟自定义事件
                HashMap<String, String> map = new HashMap<>();
                map.put("url", url);
                MobclickAgent.onEvent(getActivity(), "ServiceDidTapped", map);

                Intent intent = new Intent(getActivity(), HtmlActivity.class);
                intent.putExtra("title", homeImageList.get(position).bnname);
                intent.putExtra("path", url);
                intent.putExtra("id", String.valueOf(position));
                startActivity(intent);
            }
        });

        UpdateControl.getInstance().setDownloadlistener(new UpdateControl.DownloadListener() {
            @Override
            public void onComplete() {
                myHandler.sendEmptyMessage(UPDATECOMPLETE);
            }

            @Override
            public void onProgress(int progress) {
            }

            @Override
            public void onFail() {
                UpdateControl.getInstance().downloadAPKFile();
            }
        });

        swipe_refresh_lay.setOnRefreshListener(this);
        scrollview.setScrollViewListener(new MyScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(MyScrollView scrollView, int x, int y, int oldx, int oldy) {
                scrollValue = y;
                if (callBackListener != null) {
                    callBackListener.onScroll(scrollValue);
                }
            }
        });
        install_lay.setOnClickListener(this);
        tv_install.setOnClickListener(this);
        iv_ignore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_install:
                UpdateControl.getInstance().installApk();
                break;
            case R.id.iv_ignore:
                SharedPreferenceUtil.getInstance().setBoolean("IgnoreInstall", true);
                install_lay.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void refreshServiceLayout() {
        if (listCategory != null && listCategory.size() != 0) {
            server_lay.setVisibility(View.VISIBLE);
            if (appList != null) {
                appList.clear();
            }
            for (int i = 0; i < listCategory.size(); i++) {
                appList.addAll(listCategory.get(i).applist);
            }
            if (appList != null && appList.size() != 0) {
                server_lay.removeAllViews();
                int length = appList.size();
                int row = 2;
                for (int i = 0; i < row; i++) {
                    LinearLayout rowLay = new LinearLayout(getActivity());
                    rowLay.setMotionEventSplittingEnabled(false);
                    rowLay.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams lplay = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                    lplay.setLayoutDirection(LinearLayout.HORIZONTAL);
                    lplay.topMargin = Utils.dip2px(8);
                    rowLay.setLayoutParams(lplay);
                    rowLay.removeAllViews();
                    for (int j = 0; j < COLUME; j++) {
                        final int index = i * COLUME + j;
                        if (index < length) {
                            View v = LayoutInflater.from(getActivity()).inflate(R.layout.home_server_item, null);
                            TextView tv = (TextView) v.findViewById(R.id.tv_name);
                            ImageView iv_icon = (ImageView) v.findViewById(R.id.iv_icon);
                            tv.setText(appList.get(index).appname);
                            loadImg(NetURL.API_LINK, appList.get(index).imgurls, iv_icon);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            lp.weight = 1;
                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ServerDispatchControl.serverEventDispatcher(getActivity(), appList.get(index));
                                }
                            });
                            rowLay.addView(v, lp);
                        } else {
                            View empty = new View(getActivity());
                            LinearLayout.LayoutParams emptyLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            emptyLp.weight = 1;
                            empty.setLayoutParams(emptyLp);
                            rowLay.addView(empty);
                        }
                    }
                    server_lay.addView(rowLay);
                }
            }
        } else {
            server_lay.setVisibility(View.GONE);
        }
    }

    private void setWeatherInfo(WeatherForHomeBean bean) {
        weather_lay.setVisibility(View.VISIBLE);
        String limitStr = bean.limitnumber;
        if (StringUtil.notEmpty(limitStr)) {
            limit_lay.setVisibility(View.VISIBLE);
            String[] tmp = limitStr.split("\\|");
            tv_limit1.setText(tmp[0]);
            tv_limit2.setText(tmp[1]);
        } else {
            limit_lay.setVisibility(View.INVISIBLE);
        }
        tv_temp.setText(bean.temperature2 + "°C" + " ~ " + bean.temperature1 + "°C");
//        String week = DateUtil.dateToWeek(DateUtil.str2Date(
//                bean.savedate_weather, "yyyy-MM-dd"));
        int weatherRes;
        if (DateUtil.getDayOrNight()) {
            weatherRes = WeatherInfoControl
                    .getWeatherResForNight(bean.status2);
        } else {
            weatherRes = WeatherInfoControl
                    .getWeatherResForDay(bean.status1);
        }
        iv_weather.setImageResource(weatherRes);
        tv_weather.setText(bean.status1);

        if (BuildConfig.FLAVOR.equals("liangjiang")) {
            lj_air_lay.setVisibility(View.VISIBLE);
            tv_air.setText(bean.pmdata);
            tv_air_unit.setText(bean.pmdesc);
            limit_lay.setVisibility(View.INVISIBLE);
        } else {
            wh_air_lay.setVisibility(View.VISIBLE);
            tv_pm.setText(bean.pmdata + bean.pmdesc);
            try {
                int n = Integer.parseInt(bean.pmdata);
                if (n < 100) {// 优
                    tv_pm.setBackgroundResource(R.drawable.pm2_5_1bg);
                } else if (n > 100 && n < 200) {// 良
                    tv_pm.setBackgroundResource(R.drawable.pm2_5_2bg);
                } else {
                    tv_pm.setBackgroundResource(R.drawable.pm2_5_3bg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            limit_lay.setVisibility(View.VISIBLE);
        }
    }

    private void parseCenterService(ResponseData response) {
        if (response != null && response.body != null) {
            JSONObject mJson = JSON.parseObject(response.body.toString());
            if (mJson.containsKey("list_catalog2")) {
                JSONArray mJsonArr2 = mJson.getJSONArray("list_catalog2");
                List<AppCategoryListBean> cateList = JSON.parseArray(mJsonArr2.toString(), AppCategoryListBean.class);
                listCategory = cateList;
                int length = listCategory.size();
                List<AppListBean> allHomeAppList = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    for (int j = 0; j < listCategory.get(i).applist.size(); j++) {
                        LogUtil.d("dw", listCategory.get(i).catalogname + ", " + listCategory.get(i).applist.get(j).appname + ", " + cateList.get(i).applist.get(j).appurls);
                    }
                    allHomeAppList.addAll(listCategory.get(i).applist);
                }
                SessionContext.setAllCategoryList(listCategory);
                SessionContext.setAllHomeAppList(allHomeAppList);
            }
        }
    }

    private void requestBannerData(int channelID) {
        RequestBeanBuilder b = RequestBeanBuilder.create(false);
        b.addBody("getConfForMgr", "YES");
        b.addBody("calltype", "1");
        b.addBody("channel", String.valueOf(channelID));

        ResponseData d = b.syncRequest(b);
        d.path = NetURL.WH_BANNER;
        d.flag = channelID;
        d.key = d.path + channelID;
        requestID = DataLoader.getInstance().loadData(this, d);
    }

    private void loadHomeServiceCache() {
        try {
            byte[] topBanner = DataLoader.getInstance().getCacheData(NetURL.CACHE_URL[0]);
            if (topBanner != null) {
                String json = new String(topBanner, "UTF-8");
                ResponseData response = JSON.parseObject(json, ResponseData.class);
                JSONObject mJson = JSON.parseObject(response.body.toString());
                String jsonStr = mJson.getString("datalist");
                List<MainBannerBean> temp = JSON.parseArray(jsonStr, MainBannerBean.class);
                banner.setImageResource(temp, NetURL.API_LINK);
            }

//            byte[] centerBanner = DataLoader.getInstance().getCacheData(NetURL.CACHE_URL[1]);
//            if (centerBanner != null) {
//                String json = new String(centerBanner, "UTF-8");
//                ResponseData response = JSON.parseObject(json, ResponseData.class);
//                parseCenterBanner(response);
//            }

            byte[] allApp = DataLoader.getInstance().getCacheData(NetURL.CACHE_URL[5]);
            if (allApp != null) {
                String json = new String(allApp, "UTF-8");
                ResponseData response = JSON.parseObject(json, ResponseData.class);
                parseCenterService(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestServiceData() {
        RequestBeanBuilder b = RequestBeanBuilder.create(false);
        b.addBody("type", SERVERDATA_TYPE);
        ResponseData d = b.syncRequest(b);
        d.path = NetURL.SERVICE_DATA;
        d.flag = SERVICE_REQUEST;
        d.key = d.path + SERVERDATA_TYPE;
        requestID = DataLoader.getInstance().loadData(this, d);
    }

    private void requestWeatherInfo() {
        RequestBeanBuilder b = RequestBeanBuilder.create(false);
        b.addBody("cityCode", getString(R.string.siteId));
        b.addBody("cityId", getString(R.string.cityId));
//        b.addBody("nowdate", DateUtil.getCurrentDate());
        ResponseData d = b.syncRequest(b);
        d.path = NetURL.WEATHER_SERVER;
        d.flag = WEATHER_DATA;
        requestID = DataLoader.getInstance().loadData(this, d);
    }

    private void requestDiscoveryChannel() {
        LogUtil.d("dw", "requestDiscoveryChannel()");
        RequestBeanBuilder b = RequestBeanBuilder.create(false);
        ResponseData d = b.syncRequest(b);
        d.path = NetURL.DIS_CHANNEL;
        d.flag = DISCOVERY_CHANNEL;
        requestID = DataLoader.getInstance().loadData(this, d);
    }

    @Override
    public void preExecute(ResponseData request) {
    }

    @Override
    public void notifyMessage(ResponseData req, ResponseData res)
            throws Exception {
        if (res != null && res.body != null) {
            synchronized (MyAsyncTask.class) {
                new MyAsyncTask().setResponse(res).execute(req.flag);
            }
        }
    }

    @Override
    public void notifyError(ResponseData request, ResponseData response,
                            Exception e) {
        String message;
        if (e != null && e instanceof ConnectException) {
            message = getString(R.string.dialog_tip_net_error);
        } else {
            message = response != null && response.data != null ? response.data.toString() : getString(R.string.dialog_tip_null_error);
        }
        if (!myHandler.hasMessages(ERROR)) {
            Message msg = new Message();
            msg.what = ERROR;
            msg.obj = message;
            myHandler.sendMessage(msg);
        }
    }

    @Override
    public void onRefresh() {
        requestBannerData(BANNER_HOME_TOP);
        requestBannerData(BANNER_SERVER);
        requestWeatherInfo();
        requestServiceData();
        if (SessionContext.getChannelList().size() == 0) {
            requestDiscoveryChannel();
        }
    }

    private class MyAsyncTask extends AsyncTask<Integer, Void, Void> {
        private ResponseData response = null;

        public MyAsyncTask setResponse(ResponseData response) {
            this.response = response;
            return this;
        }

        @Override
        protected Void doInBackground(Integer... params) {
            switch (params[0]) {
                case BANNER_HOME_TOP: {
                    JSONObject mJson = JSON.parseObject(response.body.toString());
                    String jsonStr = mJson.getString("datalist");
                    List<MainBannerBean> temp = JSON.parseArray(jsonStr, MainBannerBean.class);
                    if (temp != null && temp.size() > 0) {
                        topBannerList.clear();
                        topBannerList.addAll(temp);
                    }
                    mTag.put(BANNER_HOME_TOP, BANNER_HOME_TOP);
                    break;
                }
                case BANNER_SERVER: {
                    JSONObject mJson = JSON.parseObject(response.body.toString());
                    String json = mJson.getString("datalist");
                    List<MainBannerBean> temp = JSON.parseArray(json, MainBannerBean.class);
                    if (temp != null && temp.size() > 0) {
                        homeImageList.clear();
                        homeImageList.addAll(temp);
                    }
                    mTag.put(BANNER_SERVER, BANNER_SERVER);
                    break;
                }
                case WEATHER_DATA: {
                    String res = response.body.toString();
                    weatherbean = JSON.parseObject(res, WeatherForHomeBean.class);
                    mTag.put(WEATHER_DATA, WEATHER_DATA);
                    break;
                }
                case SERVICE_REQUEST: {
                    parseCenterService(response);
                    mTag.put(SERVICE_REQUEST, SERVICE_REQUEST);
                    break;
                }
                case DISCOVERY_CHANNEL: {
                    LogUtil.d("dw", "parse discovery channel data");
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
                    break;
                }
                default:
                    break;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mTag.size() == 4) {
                mTag.clear();
                banner.setImageResource(topBannerList, NetURL.API_LINK);
                if (weatherbean != null) {
                    setWeatherInfo(weatherbean);
                }
                if (homeImageList.size() != 0) {
                    listview.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
                refreshServiceLayout();
                myHandler.sendEmptyMessage(MANUAL_COMPLETE);
            }
        }
    }
}
