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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.util.LogUtil;
import com.prj.sdk.util.NetworkUtil;
import com.prj.sdk.util.Utils;
import com.prj.sdk.widget.CustomToast;
import com.umeng.analytics.MobclickAgent;
import com.yunfei.wh.R;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.control.ServerDispatchControl;
import com.yunfei.wh.net.RequestBeanBuilder;
import com.yunfei.wh.net.bean.AppCategoryListBean;
import com.yunfei.wh.net.bean.AppListBean;
import com.yunfei.wh.net.bean.MainBannerBean;
import com.yunfei.wh.ui.activity.HtmlActivity;
import com.yunfei.wh.ui.activity.MainFragmentActivity;
import com.yunfei.wh.ui.base.BaseFragment;
import com.yunfei.wh.ui.custom.CommonBannerLayout;
import com.yunfei.wh.ui.custom.GovServerLayout;
import com.yunfei.wh.ui.custom.MyScrollView;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tab2 办事大厅
 *
 * @author duanwei
 */
public class TabServerCenterFragment extends BaseFragment implements DataCallback, SwipeRefreshLayout.OnRefreshListener {

    public static final int BANNER_TOP = 1;
    public static final int BANNER_CENTER_BC = 2;
    public static final int SERVICE_REQUEST = 3;
    public static final String SERVERDATA_TYPE = "2";

    private static final int MANUAL_COMPLETE = 0x00;
    private static final int ERROR = 0x01;
    private SwipeRefreshLayout swipe_refresh_lay;
    private MyScrollView scrollview;
    private int scrollValue = 0;
    private CommonBannerLayout banner;
    private LinearLayout center_lay;
    private View line_view;
    private TextView tv_title, tv_summary;
    private LinearLayout tab_lay;
    private LinearLayout server_lay;
    private static MainFragmentActivity.OnScrollChangeListener scrollChangeListener = null;
    private static boolean isFirstLoad = false;
    private Map<Integer, Integer> mTag = new HashMap<>();
    private ArrayList<AppListBean> tabList = new ArrayList<>();
    private List<MainBannerBean> topBannerList = new ArrayList<>();
    private List<MainBannerBean> centerBannerList = new ArrayList<>();

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
        View view = inflater.inflate(R.layout.fragment_tab_servercenter, container, false);
        initViews(view);
        initParams();
        initListeners();
        return view;
    }

    protected void onVisible() {
        super.onVisible();
        MainFragmentActivity.setOnPopUpShowListener(null);

        if (scrollChangeListener != null) {
            scrollChangeListener.onScroll(scrollValue);
        }
        banner.startBanner();
        if (isFirstLoad) {
            isFirstLoad = false;
            if (NetworkUtil.isNetworkAvailable()) {
                requestBannerData(BANNER_TOP);
                requestBannerData(BANNER_CENTER_BC);
                requestGovServerData();
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

    public static Fragment newInstance(String key, MainFragmentActivity.OnScrollChangeListener scrollChangeListener) {
        TabServerCenterFragment.scrollChangeListener = scrollChangeListener;
        Fragment fragment = new TabServerCenterFragment();
        Bundle bundle = new Bundle();
        bundle.putString("key", key);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initViews(View view) {
        super.initViews(view);
        swipe_refresh_lay = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_lay);
        scrollview = (MyScrollView) view.findViewById(R.id.scrollview);
        banner = (CommonBannerLayout) view.findViewById(R.id.banner);
        line_view = view.findViewById(R.id.line_view);
        center_lay = (LinearLayout) view.findViewById(R.id.center_lay);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_summary = (TextView) view.findViewById(R.id.tv_summary);
        tab_lay = (LinearLayout) view.findViewById(R.id.tab_lay);
        server_lay = (LinearLayout) view.findViewById(R.id.server_lay);
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
        loadGovServiceCache();
        loadServiceLayout();
    }

    private void loadServiceLayout() {

        for (int i = 0; i < SessionContext.getAllInvestmentServiceList().size(); i++) {
            if (SessionContext.getAllInvestmentServiceList().get(i).catalogname.equals("常用")
                    || SessionContext.getAllInvestmentServiceList().get(i).catalogname.equals("置顶应用")) {
                tabList.clear();
                tabList.addAll(SessionContext.getAllInvestmentServiceList().get(i).applist);
                SessionContext.getAllInvestmentServiceList().remove(i);
            }
        }
        tab_lay.removeAllViews();
        if (tabList != null && tabList.size() != 0) {
            LinearLayout.LayoutParams tablp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            tablp.topMargin = Utils.dip2px(10);
            tab_lay.setLayoutParams(tablp);

            for (int i = 0; i < 2; i++) {
                LinearLayout rowLay = new LinearLayout(getActivity());
                rowLay.setMotionEventSplittingEnabled(false);
                rowLay.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams lplay = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                lplay.setLayoutDirection(LinearLayout.HORIZONTAL);
                if (i == 0) {
                    lplay.topMargin = Utils.dip2px(5);
                } else {
                    lplay.bottomMargin = Utils.dip2px(5);
                }
                rowLay.setLayoutParams(lplay);
                rowLay.setPadding(Utils.dip2px(8), Utils.dip2px(0), Utils.dip2px(8), Utils.dip2px(0));
                rowLay.removeAllViews();
                for (int j = 0; j < 2; j++) {
                    final int index = i * 2 + j;
                    if (index < tabList.size()) {
                        View v = LayoutInflater.from(getActivity()).inflate(R.layout.home_server_item, null);
                        TextView tv = (TextView) v.findViewById(R.id.tv_name);
                        ImageView iv_icon = (ImageView) v.findViewById(R.id.iv_icon);
                        tv.setText(tabList.get(index).appname);
                        loadImg(NetURL.API_LINK, tabList.get(index).imgurls, iv_icon);
                        LinearLayout.LayoutParams itemlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        itemlp.weight = 1;
                        itemlp.setMargins(Utils.dip2px(2), Utils.dip2px(2), Utils.dip2px(2), Utils.dip2px(2));
                        v.setBackgroundResource(getHotServiceResource(index));
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ServerDispatchControl.serverEventDispatcher(getActivity(), tabList.get(index));
                            }
                        });
                        rowLay.addView(v, itemlp);
                    } else {
                        View empty = new View(getActivity());
                        LinearLayout.LayoutParams emptyLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        emptyLp.weight = 1;
                        empty.setLayoutParams(emptyLp);
                        rowLay.addView(empty);
                    }
                }
                tab_lay.addView(rowLay);
            }
        }


        server_lay.removeAllViews();
        int length = SessionContext.getAllInvestmentServiceList().size();
        for (int i = 0; i < length; i++) {
            GovServerLayout serverItem = new GovServerLayout(getActivity());
            serverItem.setMotionEventSplittingEnabled(false);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.topMargin = Utils.dip2px(10);
            if (i == length - 1) {
                lp.bottomMargin = Utils.dip2px(10);
            }

            List<AppCategoryListBean> temp = SessionContext.getAllInvestmentServiceList();
            String title = temp.get(i).catalogname;
            List<AppListBean> applist = new ArrayList<>();
            applist.addAll(temp.get(i).applist);

            if (applist.size() > 5) {
                applist = applist.subList(0, 5);
                AppListBean bean = new AppListBean();
                bean.appname = getString(R.string.commu_more);
                bean.pid = i;
                bean.appurls = "AllServiceActivity";
                applist.add(bean);
            }
            serverItem.updateServerDataChange(title, applist);
            server_lay.addView(serverItem, lp);
        }
    }

    private int getHotServiceResource(int index) {
        int res;
        switch (index) {
            case 0:
                res = R.drawable.ic_service_hot1_bg;
                break;
            case 1:
                res = R.drawable.ic_service_hot2_bg;
                break;
            case 2:
                res = R.drawable.ic_service_hot3_bg;
                break;
            case 3:
                res = R.drawable.ic_service_hot4_bg;
                break;
            default:
                res = R.drawable.ic_service_hot1_bg;
                break;
        }

        return res;
    }

    @Override
    public void initListeners() {
        super.initListeners();
        swipe_refresh_lay.setOnRefreshListener(this);
        scrollview.setScrollViewListener(new MyScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(MyScrollView scrollView, int x, int y, int oldx, int oldy) {
                scrollValue = y;
                if (scrollChangeListener != null) {
                    scrollChangeListener.onScroll(scrollValue);
                }
            }
        });
    }

    private void loadGovServiceCache() {
        try {
            byte[] topBanner = DataLoader.getInstance().getCacheData(NetURL.CACHE_URL[2]);
            if (topBanner != null) {
                String json = new String(topBanner, "UTF-8");
                ResponseData response = JSON.parseObject(json, ResponseData.class);
                JSONObject mJson = JSON.parseObject(response.body.toString());
                String jsonStr = mJson.getString("datalist");
                List<MainBannerBean> temp = JSON.parseArray(jsonStr, MainBannerBean.class);
                banner.setImageResource(temp, NetURL.API_LINK);
            }

            byte[] centerBanner = DataLoader.getInstance().getCacheData(NetURL.CACHE_URL[3]);
            if (centerBanner != null) {
                String json = new String(centerBanner, "UTF-8");
                ResponseData response = JSON.parseObject(json, ResponseData.class);
                JSONObject mJson = JSON.parseObject(response.body.toString());
                String jsonStr = mJson.getString("datalist");
                List<MainBannerBean> temp = JSON.parseArray(jsonStr, MainBannerBean.class);
                updateCenterBanner(temp);
            }

            byte[] allApp = DataLoader.getInstance().getCacheData(NetURL.CACHE_URL[6]);
            if (allApp != null) {
                String json = new String(allApp, "UTF-8");
                ResponseData response = JSON.parseObject(json, ResponseData.class);
                parseCenterService(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCenterBanner(final List<MainBannerBean> list) {
        if (list != null && list.size() > 0) {
            line_view.setVisibility(View.VISIBLE);
            center_lay.setVisibility(View.VISIBLE);
            tv_title.setText(list.get(0).bnname);
            tv_summary.setText(list.get(0).bndesc);
            center_lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String url = NetURL.SERVER_CENTER;
                    // 添加友盟自定义事件
                    HashMap<String, String> map = new HashMap<>();
                    map.put("url", url);
                    MobclickAgent.onEvent(getActivity(), "ServiceDidTapped", map);

                    Intent intent = new Intent(getActivity(), HtmlActivity.class);
                    intent.putExtra("title", list.get(0).bnname);
                    intent.putExtra("path", url);
                    intent.putExtra("id", list.get(0).id);
                    startActivity(intent);
                }
            });
        }
    }

    private void parseCenterService(ResponseData response) {
        if (response != null && response.body != null) {
            JSONObject mJson = JSON.parseObject(response.body.toString());
            if (mJson.containsKey("list_catalog3")) {
                JSONArray mJsonArr3 = mJson.getJSONArray("list_catalog3");
                List<AppCategoryListBean> cateList = JSON.parseArray(mJsonArr3.toString(), AppCategoryListBean.class);
                int length = cateList.size();
                List<AppListBean> allServiceList = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    for (int j = 0; j < cateList.get(i).applist.size(); j++) {
                        LogUtil.d("dw", cateList.get(i).catalogname + ", " + cateList.get(i).applist.get(j).appname + ", " + cateList.get(i).applist.get(j).appurls);
                    }
                    allServiceList.addAll(cateList.get(i).applist);
                }
                SessionContext.setAllInvestmentServiceList(cateList);
                SessionContext.setAllInvestmentAppList(allServiceList);
            }
        }
    }

    private void requestBannerData(int channel) {
        RequestBeanBuilder b = RequestBeanBuilder.create(false);
        b.addBody("getConfForMgr", "YES");
        b.addBody("calltype", "1");
        b.addBody("channel", String.valueOf(channel));

        ResponseData d = b.syncRequest(b);
        d.path = NetURL.WH_BANNER;
        d.flag = channel;
        d.key = d.path + channel;
        requestID = DataLoader.getInstance().loadData(this, d);
    }

    private void requestGovServerData() {
        RequestBeanBuilder b = RequestBeanBuilder.create(false);
        b.addBody("type", SERVERDATA_TYPE);
        ResponseData d = b.syncRequest(b);
        d.path = NetURL.SERVICE_DATA;
        d.flag = SERVICE_REQUEST;
        d.key = d.path + SERVERDATA_TYPE;
        requestID = DataLoader.getInstance().loadData(this, d);
    }

    @Override
    public void onRefresh() {
        requestBannerData(BANNER_TOP);
        requestBannerData(BANNER_CENTER_BC);
        requestGovServerData();
    }

    @Override
    public void preExecute(ResponseData request) {
    }

    @Override
    public void notifyMessage(ResponseData req, final ResponseData res)
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

    class MyAsyncTask extends AsyncTask<Integer, Void, Void> {

        private ResponseData response = null;

        public MyAsyncTask setResponse(ResponseData response) {
            this.response = response;
            return this;
        }

        @Override
        protected Void doInBackground(Integer... params) {
            switch (params[0]) {
                case BANNER_TOP: {
                    JSONObject mJson = JSON.parseObject(response.body.toString());
                    String jsonStr = mJson.getString("datalist");
                    topBannerList = JSON.parseArray(jsonStr, MainBannerBean.class);
                    mTag.put(BANNER_TOP, BANNER_TOP);
                    break;
                }
                case BANNER_CENTER_BC: {
                    JSONObject mJson = JSON.parseObject(response.body.toString());
                    String json = mJson.getString("datalist");
                    centerBannerList = JSON.parseArray(json, MainBannerBean.class);
                    mTag.put(BANNER_CENTER_BC, BANNER_CENTER_BC);
                    break;
                }
                case SERVICE_REQUEST: {
                    parseCenterService(response);
                    mTag.put(SERVICE_REQUEST, SERVICE_REQUEST);
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
            if (mTag.size() == 3) {
                mTag.clear();
                banner.setImageResource(topBannerList, NetURL.API_LINK);
                updateCenterBanner(centerBannerList);
                loadServiceLayout();
                myHandler.sendEmptyMessage(MANUAL_COMPLETE);
            }
        }
    }
}