package com.yunfei.wh.circle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.util.LogUtil;
import com.prj.sdk.widget.CustomToast;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yunfei.wh.R;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.net.RequestBeanBuilder;
import com.yunfei.wh.net.bean.ContentListBean;
import com.yunfei.wh.net.bean.DisChannelContentListBean;
import com.yunfei.wh.ui.activity.ChannelNewsDetailsActivity;
import com.yunfei.wh.ui.adapter.DiscoveryListAdapter;
import com.yunfei.wh.ui.base.BaseActivity;
import com.yunfei.wh.ui.custom.ProgressWheel;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kborid
 * @date 2016/8/30
 */
public class DiscoveryActivity extends BaseActivity implements DataCallback {
    private static final String TAG = "DiscoveryActivity";

    private int mCurrentPageNo = 1;
    private static final int PAGESIZE = 50;

    private SmartRefreshLayout swipe_refresh_lay;
    private ListView listview;
    private View footer;
    private ProgressWheel progress;
    private TextView tv_footer;
    private int footerHeight = 0;
    private boolean isPullUp = false;
    private boolean isNoMore = false;
    private boolean isRequesting = false;

    private DiscoveryListAdapter discoveryAdapter = null;
    private List<ContentListBean> contentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_discovery_activity);
        initViews();
        initParams();
        initListeners();
    }

    @Override
    public void initViews() {
        super.initViews();
        swipe_refresh_lay = (SmartRefreshLayout) findViewById(R.id.swipe_refresh_lay);
        listview = (ListView) findViewById(R.id.listview);
        discoveryAdapter = new DiscoveryListAdapter(this, contentList);
        listview.setAdapter(discoveryAdapter);
        footer = LayoutInflater.from(this).inflate(R.layout.common_footer_layout, null);
        progress = (ProgressWheel) footer.findViewById(R.id.progress);
        tv_footer = (TextView) footer.findViewById(R.id.tv_summary);
    }


    @Override
    public void initParams() {
        super.initParams();
        tv_center_title.setText("Discovery");
        swipe_refresh_lay.setEnableRefresh(true);
//        swipe_refresh_lay.setRefreshHeader(new BezierCircleHeader(this));
//        swipe_refresh_lay.setRefreshHeader(new DeliveryHeader(this));
//        swipe_refresh_lay.setRefreshHeader(new DropboxHeader(this));
//        swipe_refresh_lay.setRefreshHeader(new FunGameBattleCityHeader(this));
//        swipe_refresh_lay.setRefreshHeader(new FunGameHitBlockHeader(this));
//        swipe_refresh_lay.setRefreshHeader(new FlyRefreshHeader(this));
        swipe_refresh_lay.setRefreshHeader(new MaterialHeader(this));
        footer.measure(0, 0);
        footerHeight = footer.getMeasuredHeight();
        footer.setPadding(0, -footerHeight, 0, 0);
        tv_footer.setText(R.string.footer_pullup);
        listview.addFooterView(footer, null, false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipe_refresh_lay.autoRefresh();
            }
        }, 200);
    }

    @Override
    public void initListeners() {
        super.initListeners();

        swipe_refresh_lay.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                mCurrentPageNo = 1;
                isNoMore = false;
                requestNewsByID(String.valueOf(mCurrentPageNo));
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DiscoveryActivity.this, ChannelNewsDetailsActivity.class);
                intent.putExtra("item", (ContentListBean) parent.getAdapter().getItem(position));
                startActivity(intent);
            }
        });

        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (view.getLastVisiblePosition() >= view.getCount() - PAGESIZE / 2) {
                    if (!isNoMore && !isRequesting) {
                        isRequesting = true;
                        isPullUp = true;
                        progress.setVisibility(View.VISIBLE);
                        progress.spin();
                        tv_footer.setText(R.string.footer_loading);
                        int count = contentList.size();
                        if (count > PAGESIZE) {
                            mCurrentPageNo = count / PAGESIZE + 1;
                        }
                        requestNewsByID(String.valueOf(++mCurrentPageNo));
                    }
                }

                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    Glide.with(DiscoveryActivity.this).resumeRequests();
                } else {
                    Glide.with(DiscoveryActivity.this).pauseRequests();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    private void requestNewsByID(String pageNo) {
        RequestBeanBuilder b = RequestBeanBuilder.create(false);
        b.addBody("channel_id", "0");// 0表示全部频道
        b.addBody("pageNo", pageNo);
        b.addBody("pageSize", String.valueOf(PAGESIZE));

        ResponseData d = b.syncRequest(b);
        d.path = NetURL.DIS_CHANNELS_ID;
        d.flag = 0;
        requestID = DataLoader.getInstance().loadData(this, d);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void preExecute(ResponseData request) {
    }

    @Override
    public void notifyMessage(ResponseData request, ResponseData response) throws Exception {
        if (request.flag == 0) {
            if (progress.isSpinning()) {
                progress.stopSpinning();
            }
            footer.setPadding(0, 0, 0, 0);
            progress.setVisibility(View.GONE);
            JSONObject mJson = JSON.parseObject(response.body.toString());
            LogUtil.d(TAG, mJson.toString());
            JSONObject mmJson = mJson.getJSONObject("dataMap");

            DisChannelContentListBean contentListBean = new DisChannelContentListBean();
            if (mmJson.containsKey("data")) {
                JSONArray arrJson = mmJson.getJSONArray("data");
                contentListBean.contentList = JSON.parseArray(arrJson.toString(), ContentListBean.class);
            }

            if (!isPullUp) {
                contentList.clear();
            } else {
                isPullUp = false;
                isRequesting = false;
            }

            if (contentListBean.contentList.size() < PAGESIZE) {
                tv_footer.setText(R.string.footer_nomore);
                isNoMore = true;
            } else {
                tv_footer.setText(R.string.footer_pullup);
            }
            contentList.addAll(contentListBean.contentList);
            discoveryAdapter.notifyDataSetChanged();
            swipe_refresh_lay.finishRefresh();
        }
    }

    @Override
    public void notifyError(ResponseData request, ResponseData response, Exception e) {
        String message;
        if (e instanceof ConnectException) {
            message = getString(R.string.dialog_tip_net_error);
        } else {
            message = response != null && response.data != null ? response.data.toString() : getString(R.string.dialog_tip_null_error);
        }
        CustomToast.show(message, Toast.LENGTH_LONG);
        swipe_refresh_lay.finishRefresh();
    }
}
