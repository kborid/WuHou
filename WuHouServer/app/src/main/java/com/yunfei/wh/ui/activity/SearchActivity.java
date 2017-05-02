package com.yunfei.wh.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.prj.sdk.util.DisplayUtil;
import com.prj.sdk.util.StringUtil;
import com.umeng.analytics.MobclickAgent;
import com.yunfei.wh.R;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.net.bean.AppListBean;
import com.yunfei.wh.ui.adapter.ColumnAdapter;
import com.yunfei.wh.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchActivity extends BaseActivity implements TextWatcher, OnItemClickListener {

    public static final int SERVICE_SEARCH = 0;
    public static final int COMMUNITY_SEARCH = 1;
    public static final int GLOBAL_SEARCH = 2;

    private AutoCompleteTextView mAuto_text;
    private TextView mTextView, emptyView;
    private ListView mListView;
    private ColumnAdapter mAdapter;
    private ArrayList<AppListBean> mBean = new ArrayList<>();
    private ArrayList<AppListBean> mAllApp = new ArrayList<>();
    private ListView listHistory;
    private View footView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_search);
        initViews();
        dealIntent();
        initParams();
        initListeners();
    }

    @Override
    public void initViews() {
        mAuto_text = (AutoCompleteTextView) findViewById(R.id.auto_text);
        mTextView = (TextView) findViewById(R.id.tv_title_right);
        mListView = (ListView) findViewById(R.id.listView);
        listHistory = (ListView) findViewById(R.id.listHistory);
        mAuto_text.requestFocus();
    }

    @Override
    public void dealIntent() {
        super.dealIntent();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mAllApp.clear();
            int type = bundle.getInt("type");
            switch (type) {
                case GLOBAL_SEARCH:
                    mAllApp.addAll(SessionContext.getAllHomeAppList());
                    mAllApp.addAll(SessionContext.getAllInvestmentAppList());
                    mAllApp.addAll(SessionContext.getAllCommuAppList());
                    break;
                case SERVICE_SEARCH:
                    mAllApp.addAll(SessionContext.getAllInvestmentAppList());
                    break;
                case COMMUNITY_SEARCH:
                    mAllApp.addAll(SessionContext.getAllCommuAppList());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void initParams() {
        super.initParams();
        mAdapter = new ColumnAdapter(this, mBean);
        mAdapter.isSearchHistory(true);
        mListView.setAdapter(mAdapter);
        emptyView = new TextView(this);
        emptyView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        emptyView.setText(R.string.src_not_find);
        emptyView.setTextSize(12);
        emptyView.setTextColor(R.color.darkGray);
        emptyView.setPadding(5, DisplayUtil.dip2px(30), 5, 5);
        emptyView.setGravity(Gravity.CENTER_HORIZONTAL);
        emptyView.setVisibility(View.GONE);
        mListView.setEmptyView(emptyView);
        showHistory();
    }

    @Override
    public void initListeners() {
        mTextView.setOnClickListener(this);
        mAuto_text.addTextChangedListener(this);
        listHistory.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title_right:
                this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        String name = s.toString().trim();
        mBean.clear();
        if (name.length() > 0) {
            hideListView();
            for (AppListBean bean : mAllApp) {
                if (bean.appname.contains(name)) {
                    mBean.add(bean);
                }
            }
            mAdapter.notifyDataSetChanged();
        } else {
            showHistory();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 获取历史
     */
    public void showHistory() {
        listHistory.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
        ((ViewGroup) mListView.getParent()).removeView(emptyView);
        if (footView == null) {
            footView = getLayoutInflater().inflate(R.layout.view_search_footview, null, false);
            listHistory.addFooterView(footView);
        }

        // 获取搜索记录文件内容
        SharedPreferences sp = getSharedPreferences("search_history", 0);
        String history = sp.getString("history", null);
        if (history == null) {
            hideListView();
            return;
        }
        // 用逗号分割内容返回数组
        String[] history_arr = history.split(",");
        if (history_arr.length > 0) {
            ArrayAdapter<String> arr_adapter = new ArrayAdapter<String>(this, R.layout.lv_searc_history_item, history_arr);
            // 设置适配器
            listHistory.setAdapter(arr_adapter);
        } else {
            hideListView();
        }
    }

    /**
     * 隐藏搜索历史listview
     */
    private void hideListView() {
        listHistory.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
        ((ViewGroup) mListView.getParent()).removeView(emptyView);//先移除，后添加
        ((ViewGroup) mListView.getParent()).addView(emptyView);
    }

    /**
     * 清除搜索记录
     *
     * @param v
     */
    public void cleanHistory(View v) {
        SharedPreferences sp = getSharedPreferences("search_history", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
        hideListView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String keyword = mAuto_text.getText().toString().trim();
        if (StringUtil.notEmpty(keyword)) {
            // 添加友盟自定义事件
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("keyword", keyword);
            MobclickAgent.onEvent(this, "Search", map);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        try {
            String temp = (String) arg0.getAdapter().getItem(arg2);
            mAuto_text.setText(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
