package com.yunfei.wh.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.util.LogUtil;
import com.yunfei.wh.R;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.control.BundleNavi;
import com.yunfei.wh.net.bean.AppCategoryListBean;
import com.yunfei.wh.net.bean.AppListBean;
import com.yunfei.wh.ui.adapter.ServiceColumnAdapter;
import com.yunfei.wh.ui.adapter.ServiceListAdapter;
import com.yunfei.wh.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kborid
 * @date 2016/5/24
 */

public class AllServiceActivity extends BaseActivity implements DataCallback, OnItemClickListener {

    private TextView tv_search;
    private ListView listViewLeft, listViewRight;
    private ServiceColumnAdapter mServiceColumnAdapter;
    private ServiceListAdapter mServiceListAdapter;
    private List<AppCategoryListBean> mCatalogBean = new ArrayList<>();
    private List<AppListBean> mAppBean = new ArrayList<>();
    private int index = 0;
    private int type = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_all_service);
        initViews();
        dealIntent();
        initParams();
        initListeners();
    }

    @Override
    public void initViews() {
        super.initViews();
        tv_search = (TextView) findViewById(R.id.tv_search);
        listViewLeft = (ListView) findViewById(R.id.listViewLeft);
        listViewRight = (ListView) findViewById(R.id.listViewRight);
    }

    @Override
    public void dealIntent() {
        super.dealIntent();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getString("FLAG") != null) {
            String flag = bundle.getString("FLAG");
            if ("service".equals(flag)) {
                type = SearchActivity.SERVICE_SEARCH;
            } else if ("community".equals(flag)) {
                type = SearchActivity.COMMUNITY_SEARCH;
            }
        }
    }

    @Override
    public void initParams() {
        super.initParams();
        tv_center_title.setText(R.string.commu_server);
        mServiceColumnAdapter = new ServiceColumnAdapter(this, mCatalogBean);
        mServiceListAdapter = new ServiceListAdapter(this, mAppBean);
        listViewLeft.setAdapter(mServiceColumnAdapter);
        listViewRight.setAdapter(mServiceListAdapter);

        switch (type) {
            case SearchActivity.SERVICE_SEARCH:
                if (SessionContext.getAllInvestmentServiceList() != null && SessionContext.getAllInvestmentServiceList().size() > 0) {
                    mCatalogBean.clear();
                    mCatalogBean.addAll(SessionContext.getAllInvestmentServiceList());
                }
                break;
            case SearchActivity.COMMUNITY_SEARCH:
                if (SessionContext.getAllCommuCagetoryList() != null && SessionContext.getAllCommuCagetoryList().size() > 0) {
                    mCatalogBean.clear();
                    mCatalogBean.addAll(SessionContext.getAllCommuCagetoryList());
                }
                break;
            default:
                break;
        }

        mServiceColumnAdapter.notifyDataSetChanged();
        if (mCatalogBean.get(0).applist != null) {
            mAppBean.clear();
            mAppBean.addAll(mCatalogBean.get(0).applist);
            mServiceListAdapter.notifyDataSetChanged();
        }

        for (int i = 0; i < mCatalogBean.size(); i++) {
            LogUtil.d("DW", "name = " + mCatalogBean.get(i).catalogname);
            for (int j = 0; j < mCatalogBean.get(i).applist.size(); j++) {
                LogUtil.d("DW", "   name = " + mCatalogBean.get(i).applist.get(j).appname);
            }
        }
    }

    @Override
    public void initListeners() {
        super.initListeners();
        tv_search.setOnClickListener(this);
        listViewLeft.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        index = BundleNavi.getInstance().getInt("index");
        mServiceColumnAdapter.recordDefCheckedItem(index);
        mServiceColumnAdapter.notifyDataSetChanged();
        mAppBean.clear();
        mAppBean.addAll(mCatalogBean.get(index).applist);
        mServiceListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BundleNavi.getInstance().putInt("index", index);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_search:
                intent = new Intent(this, SearchActivity.class);
                intent.putExtra("type", type);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    @Override
    public void preExecute(ResponseData request) {
    }

    @Override
    public void notifyMessage(ResponseData request, ResponseData response) throws Exception {
    }

    @Override
    public void notifyError(ResponseData request, ResponseData response, Exception e) {
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        index = arg2;
        try {
            Object adapter = arg0.getAdapter();
            if (adapter instanceof ServiceColumnAdapter) {
                ServiceColumnAdapter data = (ServiceColumnAdapter) adapter;
                data.recordDefCheckedItem(arg2);
                mServiceColumnAdapter.notifyDataSetChanged();
                mAppBean.clear();
                mAppBean.addAll(mCatalogBean.get(arg2).applist);
                mServiceListAdapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
