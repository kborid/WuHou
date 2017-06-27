package com.yunfei.wh.ui.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.util.LogUtil;
import com.prj.sdk.util.Utils;
import com.prj.sdk.widget.CustomToast;
import com.yunfei.wh.R;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.net.RequestBeanBuilder;
import com.yunfei.wh.net.bean.ContentListBean;
import com.yunfei.wh.net.bean.DisChannelContentListBean;
import com.yunfei.wh.net.bean.DiscoveryChannelBean;
import com.yunfei.wh.ui.adapter.ChannelListAdapter;
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

    private static final int DELAY_TIME = 100;
    private static final int MANUAL_REFRESH = 0x00;
    private static final int AUTO_REFRESH = 0x01;
    private static final int ERROR = 0x02;
    private static final int DISMISS_POP = 0x03;

    private static int mCurrentChannelIndex = 0;
    private static String mCurrentChannelID = "0";
    private int mCurrentPageNo = 1;
    private static final int PAGESIZE = 10;

    private PopupWindow popWindow = null;
    private ListView popListView;
    private SwipeRefreshLayout swipe_refresh_lay;
    private ListView listview;
    private RelativeLayout title_lay;
    private TextView tv_center_title;
    private ImageView iv_title;
    private View footer;
    private ProgressWheel progress;
    private TextView tv_footer;
    private int footerHeight = 0;
    private boolean isPullUp = false;
    private boolean isNoMore = false;
    private boolean isRequesting = false;

    private List<DiscoveryChannelBean> channelList = new ArrayList<>();
    private ChannelListAdapter channelAdapter = null;
    private DiscoveryListAdapter discoveryAdapter = null;
    private List<ContentListBean> contentList = new ArrayList<>();

    private Animation openAm = null;
    private Animation closeAm = null;
    private boolean isOpen = false;
    private boolean isOpening = false;

    private Animation.AnimationListener animListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            isOpening = true;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            isOpening = false;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

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
        View popRootView = LayoutInflater.from(this).inflate(R.layout.discovery_channel_listview, null);
        popListView = (ListView) popRootView.findViewById(R.id.listview);
        channelList.addAll(SessionContext.getChannelList());
        channelAdapter = new ChannelListAdapter(this, channelList, mCurrentChannelIndex);
        popListView.setAdapter(channelAdapter);

        if (null == popWindow) {
            int popupWidth = getResources().getDisplayMetrics().widthPixels / 2;
            popWindow = new PopupWindow(popRootView, popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        }
        popWindow.setAnimationStyle(R.style.AnimTools);
        popWindow.setBackgroundDrawable(new ColorDrawable(0));
        title_lay = (RelativeLayout) findViewById(R.id.center_title_lay);
        tv_center_title = (TextView) findViewById(R.id.tv_center_title);
        iv_title = (ImageView) findViewById(R.id.iv_title);
        swipe_refresh_lay = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_lay);
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
        if (channelList.size() != 0) {
            channelList.clear();
        }
        if (SessionContext.getChannelList().size() > 0) {
            channelList.addAll(SessionContext.getChannelList());
            tv_center_title.setText(channelList.get(mCurrentChannelIndex).name);
        }
        swipe_refresh_lay.setColorSchemeResources(R.color.main_color_wh);
        swipe_refresh_lay.setDistanceToTriggerSync(200);
        swipe_refresh_lay.setSize(SwipeRefreshLayout.DEFAULT);

        openAm = AnimationUtils.loadAnimation(this, R.anim.route_open);
        openAm.setFillAfter(true);
        openAm.setAnimationListener(animListener);
        closeAm = AnimationUtils.loadAnimation(this, R.anim.route_close);
        closeAm.setFillAfter(true);
        closeAm.setAnimationListener(animListener);

        footer.measure(0, 0);
        footerHeight = footer.getMeasuredHeight();
        footer.setPadding(0, -footerHeight, 0, 0);
        tv_footer.setText(R.string.footer_pullup);
        listview.addFooterView(footer, null, false);
    }

    public void startAnimation() {
        if (isOpening) {
            return;
        }
        if (!isOpen) {
            isOpen = true;
            iv_title.startAnimation(openAm);
        }
    }

    public void closeAnimation() {
        if (isOpen) {
            isOpen = false;
            iv_title.startAnimation(closeAm);
        }
    }

    @Override
    public void initListeners() {
        super.initListeners();

        title_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimation();
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.alpha = 0.8f;
                getWindow().setAttributes(params);
                popWindow.showAtLocation(title_lay, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, Utils.mStatusBarHeight + Utils.dip2px(56) + Utils.dip2px(5));
//                popWindow.showAsDropDown(rootView, (Utils.mScreenWidth - POPUPWIDTH) / 2, 0);
            }
        });

        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                closeAnimation();
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.alpha = 1.0f;
                getWindow().setAttributes(params);
            }
        });

        popListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentChannelIndex = (int) parent.getAdapter().getItemId(position);
                tv_center_title.setText(channelList.get(mCurrentChannelIndex).name);
                channelAdapter.setCurrentChannelIndex(mCurrentChannelIndex);
                channelAdapter.notifyDataSetChanged();
                mCurrentChannelID = channelList.get(mCurrentChannelIndex).id;
                mCurrentPageNo = 1;
                isNoMore = false;
                requestNewsByID(mCurrentChannelID, String.valueOf(mCurrentPageNo), String.valueOf(channelList.get(mCurrentChannelIndex).getShowCount()));
                swipe_refresh_lay.setRefreshing(true);
                myHandler.sendEmptyMessageDelayed(DISMISS_POP, DELAY_TIME);
            }
        });

        swipe_refresh_lay.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPageNo = 1;
                isNoMore = false;
                requestNewsByID(mCurrentChannelID, String.valueOf(mCurrentPageNo), String.valueOf(contentList.size()));
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
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
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
                            requestNewsByID(mCurrentChannelID, String.valueOf(++mCurrentPageNo));
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (contentList == null || contentList.size() == 0) {
            footer.setPadding(0, -footerHeight, 0, 0);
            myHandler.sendEmptyMessageDelayed(AUTO_REFRESH, 500);
        }
    }

    private void requestNewsByID(String channelID, String pageNo, String pageSize) {
        RequestBeanBuilder b = RequestBeanBuilder.create(false);
        b.addBody("channel_id", channelID);// 0表示全部频道
        b.addBody("pageNo", pageNo);
        b.addBody("pageSize", pageSize);

        ResponseData d = b.syncRequest(b);
        d.path = NetURL.DIS_CHANNELS_ID;
        d.flag = 0;
        requestID = DataLoader.getInstance().loadData(this, d);
    }

    private void requestNewsByID(String channelID, String pageNo) {
        requestNewsByID(channelID, pageNo, String.valueOf(PAGESIZE));
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
            channelList.get(mCurrentChannelIndex).setShowCount(contentList.size());
            myHandler.sendEmptyMessage(MANUAL_REFRESH);
        }
    }

    @Override
    public void notifyError(ResponseData request, ResponseData response, Exception e) {
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

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DISMISS_POP:
                    popWindow.dismiss();
                    break;
                case MANUAL_REFRESH:
                    channelAdapter.notifyDataSetChanged();
                    discoveryAdapter.notifyDataSetChanged();
                    swipe_refresh_lay.setRefreshing(false);
                    break;
                case AUTO_REFRESH:
                    swipe_refresh_lay.setRefreshing(true);
                    requestNewsByID(mCurrentChannelID, String.valueOf(mCurrentPageNo));
                    break;
                case ERROR:
                    swipe_refresh_lay.setRefreshing(false);
                    CustomToast.show(msg.obj.toString(), Toast.LENGTH_LONG);
                default:
                    break;
            }
        }
    };
}
