package com.yunfei.wh.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.util.LogUtil;
import com.yunfei.wh.R;
import com.yunfei.wh.common.AppConst;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.net.RequestBeanBuilder;
import com.yunfei.wh.net.bean.MessageBean;
import com.yunfei.wh.ui.adapter.MessageListAdapter;
import com.yunfei.wh.ui.base.BaseActivity;

import java.util.List;

/**
 * @author kborid
 * @date 2016/5/24
 */
public class MyNotifyActivity extends BaseActivity implements DataCallback {

    private static final int MSGCODE_AUTO = 0x00;
    private static final int MSGCODE_MANUAL = 0x01;
    private static final int MSGCODE_ERROR = 0x02;

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSGCODE_AUTO:
                    swipe_refresh_lay.setRefreshing(true);
                    requestMessageList();
                    break;
                case MSGCODE_MANUAL:
                    swipe_refresh_lay.setRefreshing(false);
                    if (SessionContext.getAllMessageList() != null && SessionContext.getAllMessageList().size() > 0) {
                        tv_empty.setVisibility(View.GONE);
                    }
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

    private SwipeRefreshLayout swipe_refresh_lay;
    private ListView listview;
    private TextView tv_empty;
    private MessageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_notifylist_act);
        initViews();
        initParams();
        initListeners();
    }

    @Override
    public void initViews() {
        super.initViews();
        swipe_refresh_lay = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_lay);
        listview = (ListView) findViewById(R.id.listview);
        tv_empty = (TextView) findViewById(R.id.tv_empty);
    }

    @Override
    public void initParams() {
        super.initParams();
        tv_center_title.setText(R.string.uc_message);
        swipe_refresh_lay.setColorSchemeResources(R.color.main_color_wh);
        swipe_refresh_lay.setDistanceToTriggerSync(200);
        swipe_refresh_lay.setSize(SwipeRefreshLayout.DEFAULT);
        adapter = new MessageListAdapter(this, SessionContext.getAllMessageList());
        listview.setAdapter(adapter);
    }

    @Override
    public void initListeners() {
        super.initListeners();
        swipe_refresh_lay.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestMessageList();
            }
        });
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SessionContext.getAllMessageList() == null || SessionContext.getAllMessageList().size() == 0) {
            tv_empty.setVisibility(View.VISIBLE);
            myHandler.sendEmptyMessageDelayed(MSGCODE_AUTO, 300);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void requestMessageList() {
        RequestBeanBuilder b = RequestBeanBuilder.create(SessionContext.isLogin());
        b.addBody("pageIndex", "1");
        b.addBody("pageSize", AppConst.COUNT);

        ResponseData data = b.syncRequest(b);
        data.path = NetURL.NOTIFY_LIST;
        data.flag = 1;

        requestID = DataLoader.getInstance().loadData(this, data);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    public void preExecute(ResponseData request) {
    }

    @Override
    public void notifyMessage(ResponseData request, ResponseData response) throws Exception {
        if (request.flag == 1) {
            JSONArray mmJsonArr = JSON.parseArray(response.body.toString());
            LogUtil.d("dw", mmJsonArr.toString());
            List<MessageBean> temp = JSON.parseArray(mmJsonArr.toString(), MessageBean.class);
            SessionContext.setAllMessageList(temp);
        }
        myHandler.sendEmptyMessage(MSGCODE_MANUAL);
    }

    @Override
    public void notifyError(ResponseData request, ResponseData response, Exception e) {
        myHandler.sendEmptyMessage(MSGCODE_ERROR);
    }
}
