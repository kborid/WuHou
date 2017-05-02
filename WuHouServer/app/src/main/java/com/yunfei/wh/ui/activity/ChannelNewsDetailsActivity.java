package com.yunfei.wh.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.net.image.ImageLoader;
import com.prj.sdk.util.DisplayUtil;
import com.prj.sdk.util.LogUtil;
import com.prj.sdk.util.StringUtil;
import com.prj.sdk.util.Utils;
import com.prj.sdk.widget.CustomToast;
import com.yunfei.wh.R;
import com.yunfei.wh.broatcast.UnLoginBroadcastReceiver;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.control.UnescapeControl;
import com.yunfei.wh.net.RequestBeanBuilder;
import com.yunfei.wh.net.bean.ContentListBean;
import com.yunfei.wh.net.bean.NewsCommentInfoBean;
import com.yunfei.wh.tools.HtmlParse;
import com.yunfei.wh.ui.adapter.DiscoveryCommentsAdapter;
import com.yunfei.wh.ui.adapter.DiscoveryPictureAdapter;
import com.yunfei.wh.ui.base.BaseActivity;
import com.yunfei.wh.ui.custom.CircleImageView;
import com.yunfei.wh.ui.custom.MyListViewWidget;
import com.yunfei.wh.ui.custom.ProgressWheel;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kborid
 * @date 2016/6/1
 */
public class ChannelNewsDetailsActivity extends BaseActivity implements DataCallback {

    private static final int MANUAL_REFRESH = 0x00;
    private static final int AUTO_REFRESH = 0x01;
    private static final int ERROR = 0x02;

    private int mCurrentPageNo = 1;
    private static final int PAGESIZE = 10;
    private int showCount = 0;

    private SwipeRefreshLayout swipe_refresh_lay;
    private CircleImageView iv_photo;
    private WebView wv_content;
    private GridView mGridView;
    private TextView tv_support, tv_title, tv_time, tv_nocomment, tv_comment_lable;
    private EditText et_comment;
    private Button btn_public;

    private ContentListBean bean;
    private ArrayList<String> imageUrls = new ArrayList<>();
    private MyListViewWidget listview;
    private DiscoveryCommentsAdapter adapter;
    private boolean isFlag = false;
    private List<NewsCommentInfoBean> commentList = new ArrayList<>();

    private View footer;
    private ProgressWheel progress;
    private TextView tv_footer;
    private int footerHeight = 0;
    private boolean isPullUp = false;
    private boolean isNoMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_channel_new_detail_act);
        initViews();
        initParams();
        initListeners();
    }

    @Override
    public void initViews() {
        super.initViews();
        swipe_refresh_lay = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_lay);
        iv_photo = (CircleImageView) findViewById(R.id.iv_photo);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_support = (TextView) findViewById(R.id.tv_support);
        wv_content = (WebView) findViewById(R.id.wv_content);
        wv_content.setWebViewClient(new MyWebViewClient());
        WebSettings webSettings = wv_content.getSettings();
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setLoadWithOverviewMode(true);
        mGridView = (GridView) findViewById(R.id.gridView);

        tv_comment_lable = (TextView) findViewById(R.id.tv_comment_lable);
        tv_comment_lable.setVisibility(View.GONE);
        tv_nocomment = (TextView) findViewById(R.id.tv_nocomment);

        listview = (MyListViewWidget) findViewById(R.id.listview);
        adapter = new DiscoveryCommentsAdapter(this, commentList);
        listview.setAdapter(adapter);
        footer = LayoutInflater.from(this).inflate(R.layout.common_footer_layout, null);
        progress = (ProgressWheel) footer.findViewById(R.id.progress);
        tv_footer = (TextView) footer.findViewById(R.id.tv_summary);

        et_comment = (EditText) findViewById(R.id.ed_comment);
        btn_public = (Button) findViewById(R.id.btn_public);
        btn_public.setEnabled(false);
    }

    @Override
    public void initParams() {
        super.initParams();
        tv_center_title.setText(R.string.dis_content_title);
        swipe_refresh_lay.setColorSchemeResources(R.color.main_color_wh);
        swipe_refresh_lay.setDistanceToTriggerSync(200);
        swipe_refresh_lay.setSize(SwipeRefreshLayout.DEFAULT);

        footer.measure(0, 0);
        footerHeight = footer.getMeasuredHeight();
        footer.setPadding(0, -footerHeight, 0, 0);
        tv_footer.setText(R.string.footer_click);
        listview.addFooterView(footer, null, true);
    }

    @Override
    public void initListeners() {
        super.initListeners();
        swipe_refresh_lay.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPageNo = 1;
                isNoMore = false;
                requestCommentsByNewsID(mCurrentPageNo, showCount);
            }
        });

        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNoMore) {
                    isPullUp = true;
                    progress.setVisibility(View.VISIBLE);
                    progress.spin();
                    tv_footer.setText(R.string.footer_loading);
                    int count = commentList.size();
                    if (count > PAGESIZE) {
                        mCurrentPageNo = count / PAGESIZE + 1;
                    }
                    requestCommentsByNewsID(++mCurrentPageNo, PAGESIZE);
                }
            }
        });
        tv_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SessionContext.isLogin()) {
                    if (isFlag) {
                        requestToUnSupportNew();
                    } else {
                        requestToSupportNew();
                    }
                } else {
                    sendBroadcast(new Intent(UnLoginBroadcastReceiver.ACTION_NAME));
                }
            }
        });

        et_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_comment.getText().length() > 0) {
                    btn_public.setEnabled(true);
                } else {
                    btn_public.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        et_comment.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                LogUtil.d("dw", "KeyEvent = " + actionId);
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        btn_public.performClick();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        btn_public.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SessionContext.isLogin()) {
                    String contentStr = et_comment.getText().toString();
                    et_comment.setText(null);
                    Utils.closeSoftInputMode(ChannelNewsDetailsActivity.this);
                    requestPublistComment(contentStr);
                } else {
                    sendBroadcast(new Intent(UnLoginBroadcastReceiver.ACTION_NAME));
                }
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChannelNewsDetailsActivity.this, ImageScaleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("INDEX", position);
                bundle.putStringArrayList("URL", imageUrls);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (commentList == null || commentList.size() == 0) {
            footer.setPadding(0, -footerHeight, 0, 0);
            myHandler.sendEmptyMessageDelayed(AUTO_REFRESH, 500);
        }
    }

    private void requestCommentsByNewsID(int pageNo, int pageSize) {
        RequestBeanBuilder b = RequestBeanBuilder.create(false);
        b.addBody("news_id", bean.newsContentsID);
        b.addBody("pageNo", String.valueOf(pageNo));
        b.addBody("pageSize", String.valueOf(pageSize));
        ResponseData d = b.syncRequest(b);
        d.path = NetURL.DIS_COMMENTS_ID;
        d.flag = 0;
        requestID = DataLoader.getInstance().loadData(this, d);
    }

    private void requestSupportStatus() {
        LogUtil.d("dw", "requestSupportStatus()");

        RequestBeanBuilder b = RequestBeanBuilder.create(false);
        b.addBody("news_id", bean.newsContentsID);
        b.addBody("userid", SessionContext.mUser.USERBASIC.id);
        ResponseData d = b.syncRequest(b);
        d.path = NetURL.DIS_SUPPORT_STATUS;
        d.flag = 1;
        requestID = DataLoader.getInstance().loadData(this, d);
    }

    private void requestToSupportNew() {
        LogUtil.d("dw", "requestToSupportNew()");

        RequestBeanBuilder b = RequestBeanBuilder.create(true);
        b.addBody("news_id", bean.newsContentsID);
        ResponseData d = b.syncRequest(b);
        d.path = NetURL.DIS_SUPPORT;
        d.flag = 2;
        requestID = DataLoader.getInstance().loadData(this, d);
    }

    private void requestToUnSupportNew() {
        LogUtil.d("dw", "requestToUnSupportNew()");
        RequestBeanBuilder b = RequestBeanBuilder.create(true);
        b.addBody("news_id", bean.newsContentsID);
        ResponseData d = b.syncRequest(b);
        d.path = NetURL.DIS_CANCEL_SUPPORT;
        d.flag = 3;
        requestID = DataLoader.getInstance().loadData(this, d);
    }

    private void requestPublistComment(String contentStr) {
        LogUtil.d("dw", "requestPublistComment()");
        RequestBeanBuilder b = RequestBeanBuilder.create(true);
        b.addBody("news_id", bean.newsContentsID);
        b.addBody("content", contentStr);
        ResponseData d = b.syncRequest(b);
        d.path = NetURL.DIS_PUBLISH;
        d.flag = 4;

        if (!isProgressShowing()) {
            showProgressDialog("", false);
        }
        requestID = DataLoader.getInstance().loadData(this, d);
    }

    @Override
    public void dealIntent() {
        super.dealIntent();
        LogUtil.d("dw", "dealIntent()");
        if (getIntent().getExtras() != null && getIntent().getExtras().getSerializable("item") != null) {
            bean = (ContentListBean) getIntent().getExtras().getSerializable("item");
            tv_title.setText(bean.title);
            tv_time.setText(bean.updateDateApp);

            if (!StringUtil.isEmpty(bean.imgPath)) {
                String url = NetURL.getApi() + bean.imgPath;
                if (url.length() > 0) {
                    ImageLoader.getInstance().loadBitmap(new ImageLoader.ImageCallback() {
                        @Override
                        public void imageCallback(Bitmap bm, String url, String imageTag) {
                            if (bm != null) {
                                iv_photo.setImageBitmap(bm);
                            }
                        }
                    }, url);
                }
            }
            // 富文本解析
            String contentStr = UnescapeControl.unescape(bean.contents);
            ArrayList<String> imgList = HtmlParse.getImgList(contentStr);
            contentStr = HtmlParse.replaceUrlImage(contentStr);
            wv_content.loadData(contentStr, "text/html; charset=UTF-8", null);

            imageUrls.clear();
            imageUrls.addAll(imgList);
            int size = imageUrls.size();
            if (size == 2 || size == 4) {
                mGridView.setNumColumns(2);
                mGridView.setLayoutParams(new LinearLayout.LayoutParams(DisplayUtil.dip2px(150), LinearLayout.LayoutParams.WRAP_CONTENT));
            } else if (size == 1) {
                mGridView.setNumColumns(1);
                mGridView.setLayoutParams(new LinearLayout.LayoutParams(DisplayUtil.dip2px(150), LinearLayout.LayoutParams.WRAP_CONTENT));
            } else {
                mGridView.setNumColumns(3);
                mGridView.setLayoutParams(new LinearLayout.LayoutParams(DisplayUtil.dip2px(230), LinearLayout.LayoutParams.WRAP_CONTENT));
            }
            DiscoveryPictureAdapter picAdapter = new DiscoveryPictureAdapter(this, imageUrls);
            mGridView.setAdapter(picAdapter);
        }
    }

    @Override
    public void preExecute(ResponseData request) {
    }

    @Override
    public void notifyMessage(ResponseData request, ResponseData response) throws Exception {
        if (request.flag == 0) {// 评论列表
            if (isProgressShowing()) {
                removeProgressDialog();
            }
            if (progress.isSpinning()) {
                progress.stopSpinning();
            }
            footer.setPadding(0, 0, 0, 0);
            progress.setVisibility(View.GONE);
            JSONObject mJson = JSON.parseObject(response.body.toString());
            LogUtil.d("dw", mJson.toString());
            JSONObject mmJson = mJson.getJSONObject("page");
            int totalCount = mmJson.getInteger("totalCount");
            JSONArray mJsonArr = mmJson.getJSONArray("result");
            List<NewsCommentInfoBean> temp = JSON.parseArray(mJsonArr.toString(), NewsCommentInfoBean.class);

            if (!isPullUp) {
                commentList.clear();
                adapter.notifyDataSetChanged();
            } else {
                isPullUp = false;
            }
            if (temp.size() < PAGESIZE) {
                tv_footer.setText(R.string.footer_nomore);
                isNoMore = true;
            } else {
                tv_footer.setText(R.string.footer_click);
            }

            commentList.addAll(temp);
            showCount = commentList.size();
            refreshCommentLayout(totalCount);
            myHandler.sendEmptyMessage(MANUAL_REFRESH);
        } else if (request.flag == 1) {// 赞状态
            JSONObject mJson = JSON.parseObject(response.body.toString());
            JSONObject mmJson = mJson.getJSONObject("dataMap");
            String msgStr = mmJson.getString("msg");
            if (msgStr != null && !msgStr.equals("[]")) {
                refreshSupportStatus(true);
            }
        } else if (request.flag == 2) {// 赞
            JSONObject mJson = JSON.parseObject(response.body.toString());
            JSONObject mmJson = mJson.getJSONObject("dataMap");
            String msgStr = mmJson.getString("msg");
            LogUtil.d("dw", msgStr + "," + request.flag);
            CustomToast.show(msgStr, Toast.LENGTH_LONG);
            boolean ret = mmJson.getBoolean("status");
            if (ret) {
                refreshSupportStatus(true);
            }
        } else if (request.flag == 3) {// 取消赞
            JSONObject mJson = JSON.parseObject(response.body.toString());
            JSONObject mmJson = mJson.getJSONObject("dataMap");
            String msgStr = mmJson.getString("msg");
            LogUtil.d("dw", msgStr + "," + request.flag);
            CustomToast.show(msgStr, Toast.LENGTH_LONG);
            boolean ret = mmJson.getBoolean("status");
            if (ret) {
                refreshSupportStatus(false);
            }
        } else if (request.flag == 4) {// 发表评论
            JSONObject mJson = JSON.parseObject(response.body.toString());
            if (mJson.containsKey("dataMap")) {
                JSONObject mmJson = mJson.getJSONObject("dataMap");
                if (mmJson.containsKey("msg")) {
                    String msgStr = mmJson.getString("msg");
                    LogUtil.d("dw", msgStr + "," + request.flag);
                    CustomToast.show(msgStr, Toast.LENGTH_LONG);
                    boolean ret = mmJson.getBoolean("status");
                    if (ret) {
                        requestCommentsByNewsID(1, commentList.size() + 1);
                    }
                }
            }
        }
    }

    private void refreshCommentLayout(int size) {
        if (size > 0) {
            tv_comment_lable.setText(String.format(getString(R.string.dis_comment_lable), size));
            tv_nocomment.setVisibility(View.GONE);
            tv_comment_lable.setVisibility(View.VISIBLE);
        } else {
            TextView tv = new TextView(this);
            tv.setText(R.string.dis_no_comment);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(50, 50, 50, 50);
            listview.setEmptyView(tv);
            tv_comment_lable.setVisibility(View.GONE);
            tv_nocomment.setVisibility(View.VISIBLE);
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

    private void refreshSupportStatus(boolean isFlag) {
        this.isFlag = isFlag;
        int drawableId = R.drawable.support_btn_red_bg;
        if (isFlag) {
            drawableId = R.drawable.support_btn_gray_bg;
        }
        tv_support.setBackgroundResource(drawableId);
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return true;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MANUAL_REFRESH:
                    adapter.notifyDataSetChanged();
                    swipe_refresh_lay.setRefreshing(false);
                    break;
                case AUTO_REFRESH:
                    swipe_refresh_lay.setRefreshing(true);
                    requestCommentsByNewsID(mCurrentPageNo, PAGESIZE);
                    if (SessionContext.isLogin()) {
                        requestSupportStatus();
                    }
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
