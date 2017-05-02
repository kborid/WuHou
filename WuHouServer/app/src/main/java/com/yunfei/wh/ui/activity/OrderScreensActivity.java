package com.yunfei.wh.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.util.LogUtil;
import com.prj.sdk.util.StringUtil;
import com.yunfei.wh.R;
import com.yunfei.wh.control.BundleNavi;
import com.yunfei.wh.net.RequestBeanBuilder;
import com.yunfei.wh.net.bean.MainBannerBean;
import com.yunfei.wh.ui.adapter.OrderScreensAdapter;
import com.yunfei.wh.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kborid
 * @date 2016/9/26
 */
public class OrderScreensActivity extends BaseActivity implements DataCallback {

    private static final String TAG = "OrderScreensActivity";
    private static final int MSGCODE_AUTO = 0x00;
    private static final int MSGCODE_MANUAL = 0x01;
    private static final int MSGCODE_ERROR = 0x02;

    private SwipeRefreshLayout swipe_refresh_lay;
    private ListView listView;
    private OrderScreensAdapter adapter;
    private List<MainBannerBean> list = new ArrayList<>();
    private String url;
    private String paramKey1 = "", paramValue1 = "", paramKey2 = "", paramValue2 = "";
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSGCODE_AUTO:
                    swipe_refresh_lay.setRefreshing(true);
                    requestOrderScreensList();
                    break;
                case MSGCODE_MANUAL:
                    swipe_refresh_lay.setRefreshing(false);
                    adapter.notifyDataSetChanged();
                    break;
                case MSGCODE_ERROR:
                    swipe_refresh_lay.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_orderscreens_act);
        initViews();
        initParams();
        initListeners();
    }

    @Override
    public void initViews() {
        super.initViews();
        swipe_refresh_lay = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_lay);
        listView = (ListView) findViewById(R.id.listview);
    }

    @Override
    public void initParams() {
        super.initParams();
        tv_center_title.setText(R.string.order_screen);
        swipe_refresh_lay.setColorSchemeResources(R.color.main_color_wh);
        swipe_refresh_lay.setDistanceToTriggerSync(400);
        swipe_refresh_lay.setSize(SwipeRefreshLayout.DEFAULT);
        adapter = new OrderScreensAdapter(this, list);
        listView.setAdapter(adapter);
    }

    @Override
    public void initListeners() {
        super.initListeners();

        swipe_refresh_lay.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestOrderScreensList();
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    swipe_refresh_lay.setEnabled(true);
                } else {
                    swipe_refresh_lay.setEnabled(false);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(OrderScreensActivity.this, HtmlActivity.class);
                intent.putExtra("path", list.get(position).linkurls);
                intent.putExtra("id", list.get(position).id);
                intent.putExtra("title", list.get(position).bnname);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        url = BundleNavi.getInstance().getString("path");
        LogUtil.d(TAG, "url = " + url);
        if (StringUtil.notEmpty(url)) {
            String[] str = url.split("[?]");
            url = str[0];
            String[] params = str[1].split("&");
            if (StringUtil.notEmpty(params[0]) && StringUtil.notEmpty(params[1])) {
                String[] param1 = params[0].split("=");
                String[] param2 = params[1].split("=");
                paramKey1 = param1[0];
                paramValue1 = param1[1];
                paramKey2 = param2[0];
                paramValue2 = param2[1];
                LogUtil.d(TAG, "[" + paramKey1 + ", " + paramValue1 + ", " + paramKey2 + ", " + paramValue2 + "]");
            }
        }
        if (list == null || list.size() == 0) {
            myHandler.sendEmptyMessageDelayed(MSGCODE_AUTO, 300);
        }
    }

    private void requestOrderScreensList() {
        RequestBeanBuilder b = RequestBeanBuilder.create(false);
        b.addBody(paramKey1, paramValue1);
        b.addBody(paramKey2, paramValue2);
        ResponseData d = b.syncRequest(b);
        d.path = url;
        d.flag = 1;
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
        if (request.flag == 1) {
            JSONObject mJson = JSON.parseObject(response.body.toString());
            String json = mJson.getString("datalist");
            List<MainBannerBean> tmp = JSONArray.parseArray(json, MainBannerBean.class);
            if (tmp != null && tmp.size() > 0) {
                list.clear();
                list.addAll(tmp);
                adapter.notifyDataSetChanged();
            }
        }
        myHandler.sendEmptyMessage(MSGCODE_MANUAL);
    }

    @Override
    public void notifyError(ResponseData request, ResponseData response, Exception e) {
        myHandler.sendEmptyMessage(MSGCODE_ERROR);
    }
}
