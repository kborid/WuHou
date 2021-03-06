
package com.yunfei.wh.ui.fragment;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import com.prj.sdk.util.SharedPreferenceUtil;
import com.prj.sdk.util.StringUtil;
import com.prj.sdk.util.Utils;
import com.prj.sdk.widget.CustomToast;
import com.yunfei.wh.R;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.control.ServerDispatchControl;
import com.yunfei.wh.net.RequestBeanBuilder;
import com.yunfei.wh.net.bean.AppCategoryListBean;
import com.yunfei.wh.net.bean.AppListBean;
import com.yunfei.wh.net.bean.CommuInfoBean;
import com.yunfei.wh.net.bean.MainBannerBean;
import com.yunfei.wh.ui.activity.MainFragmentActivity;
import com.yunfei.wh.ui.base.BaseFragment;
import com.yunfei.wh.ui.custom.CommonBannerLayout;
import com.yunfei.wh.ui.custom.CommuServerNewLayout;
import com.yunfei.wh.ui.custom.MyScrollView;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kborid
 * @date 2016/5/19
 */
public class TabCommunityFragment extends BaseFragment implements DataCallback, SwipeRefreshLayout.OnRefreshListener {

    private static final int BANNER_TOP = 1;
    private static final int COMMUNITY_SERVICE = 2;
    private static final int MANUAL_COMPLETE = 0x00;
    private static final int ERROR = 0x01;
    private SwipeRefreshLayout swipe_refresh_lay;
    private MyScrollView scrollview;
    private LinearLayout server_lay;
    private LinearLayout tab_lay;
    private ArrayList<AppListBean> tabList = new ArrayList<>();
    private CommonBannerLayout banner;
    private View line_below_banner;
    private int scrollValue = 0;
    private static MainFragmentActivity.OnCallBackListener callBackListener = null;
    private static boolean isFirstLoad = false;
    private Map<Integer, Integer> mTag = new HashMap<>();
    private List<MainBannerBean> topBannerList = new ArrayList<>();
    private PopupWindow popupWindow;
    private int mStreetId = -1;
    private int mSelectedId = -1;
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MANUAL_COMPLETE:
                    swipe_refresh_lay.setRefreshing(false);
                    if (isProgressShowing()) {
                        removeProgressDialog();
                    }
                    break;
                case ERROR:
                    swipe_refresh_lay.setRefreshing(false);
                    if (isProgressShowing()) {
                        removeProgressDialog();
                    }
                    CustomToast.show(msg.obj.toString(), Toast.LENGTH_LONG);
                    break;
                default:
                    break;
            }
        }
    };

    public static Fragment newInstance(String key, MainFragmentActivity.OnCallBackListener callBackListener) {
        isFirstLoad = true;
        TabCommunityFragment.callBackListener = callBackListener;
        Fragment fragment = new TabCommunityFragment();
        Bundle bundle = new Bundle();
        bundle.putString("key", key);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getArguments().getString("key");
        View view = inflater.inflate(R.layout.fragment_tab_community, container, false);
        initViews(view);
        initParams();
        initListeners();
        return view;
    }

    protected void onVisible() {
        super.onVisible();
        MainFragmentActivity.setOnPopUpShowListener(new MainFragmentActivity.OnPopUpShowListener() {
            @Override
            public void onShowPopUp() {
                showPopupWindow();
            }
        });

        if (callBackListener != null) {
            callBackListener.onScroll(scrollValue);
            for (int i = 0; i < SessionContext.getCommunityStreetList().size(); i++) {
                if (SessionContext.getCommunityStreetList().get(i).id == mStreetId) {
                    callBackListener.onTitleChanged(SessionContext.getCommunityStreetList().get(i).catalogname);
                    break;
                }
            }
        }
        banner.startBanner();
        if (isFirstLoad) {
            isFirstLoad = false;
            if (NetworkUtil.isNetworkAvailable()) {
                if (mStreetId == -1 || SessionContext.getCommunityStreetList().size() == 0) {
                    requestCommunityStreetList();
                } else {
                    requestStreetBanner(mStreetId);
                    requestCommunityService(mStreetId);
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
        line_below_banner = view.findViewById(R.id.line_below_banner);
        tab_lay = (LinearLayout) view.findViewById(R.id.icon_lay);
        server_lay = (LinearLayout) view.findViewById(R.id.server_lay);
    }

    @Override
    protected void initParams() {
        super.initParams();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.commu_choose_layout, null);
        ListView listView = (ListView) view.findViewById(R.id.listview);
        MyCommuAdapter adapter = new MyCommuAdapter(getActivity(), SessionContext.getCommunityStreetList());
        listView.setAdapter(adapter);
        if (popupWindow == null) {
            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, Utils.mScreenHeight * 2 / 3, true);
        }
        popupWindow.setAnimationStyle(R.style.AnimTools);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));

        swipe_refresh_lay.setColorSchemeResources(R.color.main_color_wh);
        swipe_refresh_lay.setDistanceToTriggerSync(200);
        int start = Utils.dip2px(56);
        int end = start + Utils.dip2px(20);
        swipe_refresh_lay.setProgressViewOffset(true, start, end);
        swipe_refresh_lay.setSize(SwipeRefreshLayout.DEFAULT);
        loadCommuCache();
        loadServiceLayout();
        mStreetId = SharedPreferenceUtil.getInstance().getInt("streetId", -1);
        if (-1 == mStreetId) {
            for (int i = 0; i < SessionContext.getCommunityStreetList().size(); i++) {
                CommuInfoBean bean = SessionContext.getCommunityStreetList().get(i);
                if (StringUtil.notEmpty(bean.catalogurls)) {
                    mStreetId = bean.id;
                    SharedPreferenceUtil.getInstance().setInt("streetId", mStreetId);
                    break;
                }
            }
        }
        mSelectedId = mStreetId;
    }

    private void showPopupWindow() {
        WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
        params.alpha = 0.8f;
        getActivity().getWindow().setAttributes(params);
        popupWindow.showAtLocation(getView(), Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void parseCommuServer(ResponseData response) {
        if (response != null && response.body != null) {
            JSONObject mJson = JSON.parseObject(response.body.toString());
            JSONArray mJsonArr = mJson.getJSONArray("list_catalog");
            List<AppCategoryListBean> cateList = JSON.parseArray(mJsonArr.toString(), AppCategoryListBean.class);
            int length = cateList.size();
            List<AppListBean> allCommuList = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < cateList.get(i).applist.size(); j++) {
                    LogUtil.d("dw", cateList.get(i).catalogname + ", " + cateList.get(i).applist.get(j).appname + ", " + cateList.get(i).applist.get(j).appurls);
                }
                allCommuList.addAll(cateList.get(i).applist);
            }
            SessionContext.setAllCommuCagetoryList(cateList);
            SessionContext.setAllCommuAppList(allCommuList);
        }
    }

    private void loadCommuCache() {
        try {
            // 获取社区banner
            byte[] commuBanner = DataLoader.getInstance().getCacheData(NetURL.HM_BANNER);
            if (null != commuBanner) {
                String json = new String(commuBanner, "UTF-8");
                ResponseData response = JSON.parseObject(json, ResponseData.class);
                JSONObject mJson = JSON.parseObject(response.body.toString());
                String mmJson = mJson.getString("datalist");
                LogUtil.d("dw", mmJson);
                List<MainBannerBean> temp = JSON.parseArray(mmJson, MainBannerBean.class);
                banner.setImageResource(temp, NetURL.COMM_API_LINK);
                banner.stopBanner();
            }

            // 获取社区所有类型app
            byte[] commuService = DataLoader.getInstance().getCacheData(NetURL.ALL_HM_SERVER);
            if (null != commuService) {
                String json = new String(commuService, "UTF-8");
                ResponseData response = JSON.parseObject(json, ResponseData.class);
                parseCommuServer(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadServiceLayout() {
        tab_lay.removeAllViews();
        server_lay.removeAllViews();

        for (int i = 0; i < SessionContext.getAllCommuCagetoryList().size(); i++) {
            if (SessionContext.getAllCommuCagetoryList().get(i).catalogname.equals("推荐位")) {
                tabList.clear();
                tabList.addAll(SessionContext.getAllCommuCagetoryList().get(i).applist);
                SessionContext.getAllCommuCagetoryList().remove(i);
            }
        }

        if (tabList != null && tabList.size() != 0) {
            tab_lay.setVisibility(View.VISIBLE);
            line_below_banner.setVisibility(View.VISIBLE);

            for (int i = 0; i < tabList.size(); i++) {
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.commu_icon_item, null);
                TextView tv = (TextView) v.findViewById(R.id.tv_icon_name);
                ImageView iv_icon = (ImageView) v.findViewById(R.id.iv_icon);
                tv.setText(tabList.get(i).appname);
                loadImg(NetURL.COMM_API_LINK, tabList.get(i).imgurls, iv_icon);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                lp.weight = 1;
                final int finalI = i;
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ServerDispatchControl.serverEventDispatcher(getActivity(), tabList.get(finalI));
                    }
                });
                tab_lay.addView(v, lp);
            }
        } else {
            tab_lay.setVisibility(View.GONE);
        }

        int length = SessionContext.getAllCommuCagetoryList().size();
        for (int i = 0; i < length; i++) {
            if (SessionContext.getAllCommuCagetoryList().get(i).applist.size() > 0) {
                CommuServerNewLayout serverItem = new CommuServerNewLayout(getActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                lp.topMargin = Utils.dip2px(10);
                lp.leftMargin = Utils.dip2px(10);
                lp.rightMargin = Utils.dip2px(10);
                if (i == length - 1) {
                    lp.bottomMargin = Utils.dip2px(10);
                }

                List<AppCategoryListBean> temp = SessionContext.getAllCommuCagetoryList();
                String title = temp.get(i).catalogname;
                List<AppListBean> applist = new ArrayList<>();
                applist.addAll(temp.get(i).applist);

                if (applist.size() > 7) {
                    applist = applist.subList(0, 7);
                    AppListBean bean = new AppListBean();
                    bean.appname = getString(R.string.commu_more);
                    bean.pid = i;
                    bean.appurls = "AllServiceActivity";
                    applist.add(bean);
                }
                serverItem.notifyLayoutDataChange(title, applist);
                server_lay.addView(serverItem, i, lp);
            }
        }
    }

    @Override
    public void initListeners() {
        super.initListeners();
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
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
                params.alpha = 1.0f;
                getActivity().getWindow().setAttributes(params);

                if (mStreetId != mSelectedId) {
                    if (!isProgressShowing()) {
                        showProgressDialog("", false);
                    }
                    mStreetId = mSelectedId;
                    requestStreetBanner(mStreetId);
                    requestCommunityService(mStreetId);
                    if (callBackListener != null) {
                        for (int i = 0; i < SessionContext.getCommunityStreetList().size(); i++) {
                            if (SessionContext.getCommunityStreetList().get(i).id == mStreetId) {
                                callBackListener.onTitleChanged(SessionContext.getCommunityStreetList().get(i).catalogname);
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    private void requestCommunityStreetList() {
        RequestBeanBuilder b = RequestBeanBuilder.create(false);

        ResponseData d = b.syncRequest(b);
        d.path = NetURL.COMMUNITY_LIST;
        d.flag = 0;

        requestID = DataLoader.getInstance().loadData(this, d);
    }

    /**
     * 加载banner
     */
    private void requestStreetBanner(int streetId) {
        LogUtil.d("dw", "requestStreetBanner()");
        RequestBeanBuilder b = RequestBeanBuilder.create(false);
        b.addBody("getConfForMgr", "YES");
        b.addBody("SID", String.valueOf(streetId));
        b.addBody("channel", "10");

        ResponseData data = b.syncRequest(b);
        data.path = NetURL.HM_BANNER;
        data.flag = BANNER_TOP;
        requestID = DataLoader.getInstance().loadData(this, data);
    }

    private void requestCommunityService(int streetId) {
        RequestBeanBuilder b = RequestBeanBuilder.create(false);
        b.addBody("PID", String.valueOf(streetId));

        ResponseData d = b.syncRequest(b);
        d.path = NetURL.COMMUNITY_SERVICE;
        d.flag = COMMUNITY_SERVICE;

        requestID = DataLoader.getInstance().loadData(this, d);
    }

    @Override
    public void preExecute(ResponseData request) {
    }

    @Override
    public void notifyMessage(ResponseData req, ResponseData res)
            throws Exception {
        if (res != null && res.body != null) {
            if (req.flag == 0) {
                JSONObject mJson = JSON.parseObject(res.body.toString());
                if (mJson.containsKey("list_catalog")) {
                    String mmJson = mJson.getString("list_catalog");
                    List<CommuInfoBean> temp = JSON.parseArray(mmJson, CommuInfoBean.class);
                    SessionContext.setCommunityStreetList(temp);
                    if (mStreetId == -1) {
                        for (int i = 0; i < SessionContext.getCommunityStreetList().size(); i++) {
                            CommuInfoBean bean = SessionContext.getCommunityStreetList().get(i);
                            if (StringUtil.notEmpty(bean.catalogurls)) {
                                mStreetId = bean.id;
                                SharedPreferenceUtil.getInstance().setInt("streetId", mStreetId);
                                break;
                            }
                        }
                    }

                    if (callBackListener != null) {
                        callBackListener.onScroll(scrollValue);
                        for (int i = 0; i < SessionContext.getCommunityStreetList().size(); i++) {
                            if (SessionContext.getCommunityStreetList().get(i).id == mStreetId) {
                                callBackListener.onTitleChanged(SessionContext.getCommunityStreetList().get(i).catalogname);
                                break;
                            }
                        }
                    }

                    requestStreetBanner(mStreetId);
                    requestCommunityService(mStreetId);
                    mSelectedId = mStreetId;
                }
            } else {
                synchronized (MyAsyncTask.class) {
                    new MyAsyncTask().setResponse(res).execute(req.flag);
                }
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
        if (mStreetId == -1 || SessionContext.getCommunityStreetList().size() == 0) {
            requestCommunityStreetList();
        } else {
            requestStreetBanner(mStreetId);
            requestCommunityService(mStreetId);
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
                case BANNER_TOP: {
                    JSONObject mJson = JSON.parseObject(response.body.toString());
                    String mmJson = mJson.getString("datalist");
                    topBannerList = JSON.parseArray(mmJson, MainBannerBean.class);
                    mTag.put(BANNER_TOP, BANNER_TOP);
                    break;
                }
                case COMMUNITY_SERVICE: {
                    parseCommuServer(response);
                    mTag.put(COMMUNITY_SERVICE, COMMUNITY_SERVICE);
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
            if (mTag.size() == 2) {
                mTag.clear();
                banner.setImageResource(topBannerList, NetURL.COMM_API_LINK);
                loadServiceLayout();
                myHandler.sendEmptyMessage(MANUAL_COMPLETE);
            }
        }
    }

    class MyCommuAdapter extends BaseAdapter {
        private Context context;
        private List<CommuInfoBean> list = new ArrayList<>();

        public MyCommuAdapter(Context context, List<CommuInfoBean> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public CommuInfoBean getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.commu_choose_item, null);
                viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
                viewHolder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
                viewHolder.iv_choose = (ImageView) convertView.findViewById(R.id.iv_choose);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            boolean isSelected = list.get(position).id == mStreetId;
            boolean isDeveloping = StringUtil.isEmpty(list.get(position).catalogurls);
            if (isDeveloping) {
                viewHolder.tv_status.setVisibility(View.VISIBLE);
                convertView.setEnabled(false);
                viewHolder.iv_choose.setVisibility(View.GONE);
            } else {
                viewHolder.tv_status.setVisibility(View.GONE);
                convertView.setEnabled(true);
                if (isSelected) {
                    viewHolder.iv_choose.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.iv_choose.setVisibility(View.GONE);
                }
            }

            viewHolder.tv_title.setEnabled(!isDeveloping);
            viewHolder.tv_address.setEnabled(!isDeveloping);
            viewHolder.tv_title.setText(list.get(position).catalogname);
            viewHolder.tv_address.setText(list.get(position).catalogdesc);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectedId = list.get(position).id;
                    SharedPreferenceUtil.getInstance().setInt("streetId", mSelectedId);
                    popupWindow.dismiss();
                }
            });

            return convertView;
        }

        class ViewHolder {
            TextView tv_title;
            TextView tv_address;
            TextView tv_status;
            ImageView iv_choose;
        }
    }
}
