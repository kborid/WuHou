package com.yunfei.whsc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.yunfei.whsc.R;
import com.yunfei.whsc.custom.CommonLoadingWidget;
import com.yunfei.whsc.net.MainBannerBean;
import com.yunfei.whsc.net.RequestBeanBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kborid
 * @date 2017/1/17 0017
 */
public class OrderScreenActivity extends AppCompatActivity implements DataCallback {
    private static final String TAG = "OrderScreensActivity";
    private String mTitle;
    private TextView tv_center_title, tv_left_title_back;
    private GridView gridview;
    private CommonLoadingWidget common_loading;
    private MyGridViewAdapter adapter;
    private List<MainBannerBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_orderscreen_layout);
        dealIntent();
        findViews();
        setClickListeners();

        requestOrderScreensList();
//        for (int i = 0; i < 7; i++) {
//            MainBannerBean bean = new MainBannerBean();
//            bean.bnname = "TEST " + i;
//            list.add(bean);
//        }
    }

    private void dealIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mTitle = bundle.getString("title");
        }
    }

    private void findViews() {
        tv_center_title = (TextView) findViewById(R.id.tv_center_title);
        tv_center_title.setText(mTitle);
        tv_left_title_back = (TextView) findViewById(R.id.tv_left_title_back);
        gridview = (GridView) findViewById(R.id.gridview);
        adapter = new MyGridViewAdapter(this, list);
        gridview.setAdapter(adapter);
        common_loading = (CommonLoadingWidget) findViewById(R.id.loading);
    }

    private void setClickListeners() {
        tv_left_title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(OrderScreenActivity.this, WebViewActivity.class);
                intent.putExtra("path", list.get(i).linkurls);
                intent.putExtra("title", list.get(i).bnname);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void requestOrderScreensList() {
        RequestBeanBuilder b = RequestBeanBuilder.create(false);
        b.addBody("calltype", "1");
        b.addBody("channel", "6");
        ResponseData d = b.syncRequest(b);
        d.path = "http://cdwh.org/wh_portal/service/CW1006";
        d.flag = 1;
        if (!common_loading.isShown()) {
            common_loading.startLoading();
        }
        DataLoader.getInstance().loadData(this, d);
    }

    @Override
    protected void onPause() {
        super.onPause();
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
            if (common_loading.isShown()) {
                common_loading.closeLoading();
            }
            JSONObject mJson = JSON.parseObject(response.body.toString());
            if (mJson.containsKey("datalist")) {
                String json = mJson.getString("datalist");
                List<MainBannerBean> tmp = JSON.parseArray(json, MainBannerBean.class);
                if (tmp != null && tmp.size() > 0) {
                    list.clear();
                    list.addAll(tmp);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void notifyError(ResponseData request, ResponseData response, Exception e) {

    }

    class MyGridViewAdapter extends BaseAdapter {
        private Context context;
        private List<MainBannerBean> list = new ArrayList<>();

        public MyGridViewAdapter(Context context, List<MainBannerBean> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ViewHolder viewHolder;
            if (null == view) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.gv_orderscreen_item, null);
//                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                lp.width = (Utils.mScreenWidth - Utils.dip2px(50)) / 2;
//                lp.height = (Utils.mScreenHeight - Utils.dip2px(50) - Utils.dip2px(80)/* - Utils.mStatusBarHeight*/) / 4;
//                view.setLayoutParams(lp);
                viewHolder.iv_order_icon = (ImageView) view.findViewById(R.id.iv_order_icon);
                viewHolder.tv_order_count = (TextView) view.findViewById(R.id.tv_order_count);
                viewHolder.tv_order_title = (TextView) view.findViewById(R.id.tv_order_title);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.tv_order_count.setText(String.valueOf(i + 1));
            viewHolder.tv_order_title.setText(list.get(i).bnname);
            switch (i) {
                case 0:
                    viewHolder.iv_order_icon.setImageResource(R.mipmap.iv_order1);
                    break;
                case 1:
                    viewHolder.iv_order_icon.setImageResource(R.mipmap.iv_order2);
                    break;
                case 2:
                    viewHolder.iv_order_icon.setImageResource(R.mipmap.iv_order3);
                    break;
                case 3:
                    viewHolder.iv_order_icon.setImageResource(R.mipmap.iv_order4);
                    break;
                case 4:
                    viewHolder.iv_order_icon.setImageResource(R.mipmap.iv_order5);
                    break;
                case 5:
                    viewHolder.iv_order_icon.setImageResource(R.mipmap.iv_order6);
                    break;
                case 6:
                    viewHolder.iv_order_icon.setImageResource(R.mipmap.iv_order7);
                    break;
                case 7:
                    viewHolder.iv_order_icon.setImageResource(R.mipmap.iv_order8);
                    break;
                default:
                    viewHolder.iv_order_icon.setImageResource(R.mipmap.iv_order1);
                    break;
            }
            return view;
        }

        class ViewHolder {
            TextView tv_order_count;
            ImageView iv_order_icon;
            TextView tv_order_title;
        }
    }
}
