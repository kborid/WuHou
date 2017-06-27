package com.yunfei.wh.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.util.StringUtil;
import com.prj.sdk.widget.CustomToast;
import com.yunfei.wh.R;
import com.yunfei.wh.net.RequestBeanBuilder;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.net.bean.UserAddrs;
import com.yunfei.wh.ui.adapter.AddressManageAdapter;
import com.yunfei.wh.ui.base.BaseActivity;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

/**
 * 地址管理
 *
 * @author LiaoBo
 */
public class AddressManageActivity extends BaseActivity implements DataCallback {
    private ListView listView;
    private AddressManageAdapter mAdapter;
    private LinearLayout layoutEmptyView, btn_add;
    private List<UserAddrs> mUserAddrs;
    private int mIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_addressmanage_act);
        initViews();
        initParams();
        initListeners();
    }

    @Override
    public void initViews() {
        super.initViews();
        tv_center_title.setText(R.string.uc_addressmanager);
        tv_right_title.setText(R.string.new_addr);
        tv_right_title.setVisibility(View.GONE);
        listView = (ListView) findViewById(R.id.listView);
        layoutEmptyView = (LinearLayout) findViewById(R.id.layoutEmptyView);
        btn_add = (LinearLayout) findViewById(R.id.btn_add);
    }

    @Override
    public void initParams() {
        super.initParams();
        mUserAddrs = new ArrayList<>();
        mAdapter = new AddressManageAdapter(this, mUserAddrs);
        mAdapter.setOnEditAddressItemChange(new AddressManageAdapter.OnEditAddressItemChange() {
            @Override
            public void onDeleteChange(int index) {
                mIndex = index;
                requestDeleteAddressItem(mUserAddrs.get(index).id);
            }

            @Override
            public void onSetDefault(int index) {
                mIndex = index;
                requestSetDefault(mIndex);
            }
        });
        listView.setAdapter(mAdapter);
    }

    @Override
    public void initListeners() {
        super.initListeners();
        btn_add.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SessionContext.isLogin()) {
            requestAddressInfo();
        } else {
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 加载地址数组
     */
    private void requestAddressInfo() {
        mUserAddrs.clear();
        RequestBeanBuilder builder = RequestBeanBuilder.create(true);
        ResponseData data = builder.syncRequest(builder);
        data.path = NetURL.SELECT_ADDRESS;
        data.flag = 1;
        if (!isProgressShowing()) {
            showProgressDialog(getString(R.string.loading), true);
        }
        requestID = DataLoader.getInstance().loadData(this, data);
    }

    /**
     * 删除
     */
    private void requestDeleteAddressItem(String id) {
        RequestBeanBuilder builder = RequestBeanBuilder.create(true);
        builder.addBody("id", id);
        ResponseData data = builder.syncRequest(builder);
        data.path = NetURL.DELETE_ADDRESS;
        data.flag = 2;
        if (!isProgressShowing()) {
            showProgressDialog(getString(R.string.loading), true);
        }

        requestID = DataLoader.getInstance().loadData(this, data);
    }

    /**
     * 编辑默认item
     *
     * @param index
     */
    private void requestSetDefault(int index) {
        RequestBeanBuilder builder = RequestBeanBuilder.create(true);
        UserAddrs addr = mUserAddrs.get(index);

        builder.addBody("name", addr.name);
        builder.addBody("phone", addr.phone);
        builder.addBody("province", addr.province);
        builder.addBody("city", addr.city);
        builder.addBody("area", addr.area);
        builder.addBody("address", addr.address);
        builder.addBody("default", addr.def ? "false" : "true");//参数为要设置成的值
        builder.addBody("id", addr.id);
        ResponseData data = builder.syncRequest(builder);
        data.path = NetURL.EDIT_ADDRESS;
        data.flag = 3;

        if (!isProgressShowing()) {
            showProgressDialog(getString(R.string.loading), true);
        }

        requestID = DataLoader.getInstance().loadData(this, data);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent mIntent;
        switch (v.getId()) {
            case R.id.btn_add:
                mIntent = new Intent(this, AddressEditActivity.class);
                startActivity(mIntent);
                break;
            case R.id.tv_right_title:
                if (StringUtil.isEmpty(tv_right_title.getText())) {
                    return;
                }
                mIntent = new Intent(this, AddressEditActivity.class);
                startActivity(mIntent);
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
        removeProgressDialog();
        if (request.flag == 1) {
            JSONObject mJson = JSON.parseObject(response.body.toString());
            String json = mJson.getString("userAddrs");
            List<UserAddrs> temp = JSON.parseArray(json, UserAddrs.class);
            if (temp != null && !temp.isEmpty()) {
                for (int i = 0; i < temp.size(); i++) {
                    UserAddrs ud = temp.get(i);
                    ud.allCopyAddress = ud.province + (ud.city.equals(ud.province) ? "" : ud.city) + ud.area + ud.address;
                }
                mUserAddrs.addAll(temp);
                mAdapter.notifyDataSetChanged();
                tv_right_title.setVisibility(View.VISIBLE);
            } else {
                listView.setEmptyView(layoutEmptyView);
            }
        } else if (request.flag == 2) {
            mUserAddrs.remove(mIndex);
            mAdapter.notifyDataSetChanged();
            CustomToast.show("删除成功", 0);
        } else if (request.flag == 3) {
            for (int i = 0; i < mUserAddrs.size(); i++) {
                UserAddrs item = mUserAddrs.get(i);
                item.def = mIndex == i && !mUserAddrs.get(mIndex).def;
            }
            mAdapter.notifyDataSetChanged();
            CustomToast.show("提交成功", 0);
        }
    }

    @Override
    public void notifyError(ResponseData request, ResponseData response, Exception e) {
        removeProgressDialog();
        String message;
        if (e != null && e instanceof ConnectException) {
            message = getString(R.string.dialog_tip_net_error);
        } else {
            message = response != null && response.data != null ? response.data.toString() : getString(R.string.dialog_tip_null_error);
        }
        CustomToast.show(message, Toast.LENGTH_LONG);
    }

}
