package com.yunfei.wh.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.net.image.ImageLoader;
import com.prj.sdk.util.StringUtil;
import com.prj.sdk.widget.CustomToast;
import com.yunfei.wh.R;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.net.RequestBeanBuilder;
import com.yunfei.wh.net.bean.SchoolDetailInfoBean;
import com.yunfei.wh.ui.base.BaseActivity;
import com.yunfei.wh.ui.custom.CommonBannerLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kborid
 * @date 2016/12/28 0028
 */
public class SchoolMapDetailActivity extends BaseActivity implements DataCallback {

    private SchoolDetailInfoBean bean;
    private String title, id;
    private CommonBannerLayout banner;
    private List<String> picList = new ArrayList<>();
    private TextView tv_school_name, tv_school_phone, tv_school_add, tv_summary, tv_school_nature, tv_school_web;

    private ImageView iv_phone, iv_navi;
    private LinearLayout area_lay;
    private ImageView iv_area_icon;
    private TextView tv_area_title;
    private LinearLayout child_lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_schooldetail_layout);

        initViews();
        initParams();
        initListeners();
    }

    @Override
    public void initViews() {
        super.initViews();
        banner = (CommonBannerLayout) findViewById(R.id.banner);
        tv_school_name = (TextView) findViewById(R.id.tv_school_name);
        tv_school_phone = (TextView) findViewById(R.id.tv_school_phone);
        tv_school_add = (TextView) findViewById(R.id.tv_school_add);
        tv_summary = (TextView) findViewById(R.id.tv_summary);

        iv_phone = (ImageView) findViewById(R.id.iv_phone);
        iv_navi = (ImageView) findViewById(R.id.iv_navi);

        tv_school_nature = (TextView) findViewById(R.id.tv_school_nature);
        tv_school_web = (TextView) findViewById(R.id.tv_school_web);
        tv_school_web.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        area_lay = (LinearLayout) findViewById(R.id.area_lay);
        iv_area_icon = (ImageView) findViewById(R.id.iv_area_icon);
        tv_area_title = (TextView) findViewById(R.id.tv_area_title);
        child_lay = (LinearLayout) findViewById(R.id.child_lay);
    }

    @Override
    public void dealIntent() {
        super.dealIntent();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            title = bundle.getString("name");
            id = bundle.getString("id");
        }
    }

    @Override
    public void initParams() {
        super.initParams();
        tv_center_title.setText(title);
        requestSchoolDetail();
    }

    @Override
    public void initListeners() {
        super.initListeners();
        iv_phone.setOnClickListener(this);
        iv_navi.setOnClickListener(this);
        tv_school_web.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent mIntent = null;
        switch (v.getId()) {
            case R.id.iv_phone:
                if (StringUtil.notEmpty(tv_school_phone.getText().toString())) {
                    Uri dialUri = Uri.parse("tel:" + tv_school_phone.getText().toString());
                    mIntent = new Intent(Intent.ACTION_DIAL, dialUri);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                break;
            case R.id.tv_school_web:
                if (StringUtil.notEmpty(tv_school_web.getText().toString())) {
                    mIntent = new Intent(this, HtmlActivity.class);
                    mIntent.putExtra("path", tv_school_web.getText().toString());
                }
                break;
            case R.id.iv_navi:
                if (bean != null) {
                    try {
                        Uri mUri = Uri.parse("geo:" + bean.latitude + "," + bean.longitude + "?q=" + bean.name);
                        Intent intent = new Intent(Intent.ACTION_VIEW, mUri);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        CustomToast.show("您的手机中未安装任何地图导航工具", Toast.LENGTH_SHORT);
                    }
                }
                break;
            default:
                break;
        }
        if (mIntent != null) {
            startActivity(mIntent);
        }
    }

    private void requestSchoolDetail() {
        RequestBeanBuilder b = RequestBeanBuilder.create(false);
        b.addBody("id", id);

        ResponseData d = b.syncRequest(b);
        d.flag = 0;
        d.path = NetURL.SCHOOL_MAP_DETAIL;

        if (!isProgressShowing()) {
            showProgressDialog(false);
        }

        requestID = DataLoader.getInstance().loadData(this, d);
    }

    private void refreshSchoolInfo(SchoolDetailInfoBean bean) {

        picList.clear();
        if (bean.piclist != null && bean.piclist.size() > 0) {
            picList.addAll(bean.piclist);
            for (int i = 0; i < picList.size(); i++) {
            }
        } else {
            picList.add(NetURL.API_LINK + bean.imgurl);
        }
        banner.setImageResource(picList);
        tv_school_name.setText(bean.name);
        tv_school_phone.setText(bean.tel);
        tv_school_add.setText(bean.addr);
        tv_summary.setText(bean.introduct);
        tv_school_nature.setText(bean.nature);
        tv_school_web.setText(bean.website);
        if (bean.attachment != null && bean.attachment.size() > 0) {
            for (int i = 0; i < bean.attachment.size(); i++) {
                if (bean.attachment.get(i).detail != null && bean.attachment.get(i).detail.size() > 0) {
                    area_lay.setVisibility(View.VISIBLE);
                    getImage(iv_area_icon, NetURL.API_LINK + bean.attachment.get(i).iconUrl, 160, 90);
                    tv_area_title.setText(bean.attachment.get(i).title);
                    child_lay.removeAllViews();
                    for (int j = 0; j < bean.attachment.get(i).detail.size(); j++) {
                        View v = LayoutInflater.from(this).inflate(R.layout.child_item_layout, null);
                        TextView tv_child_title = (TextView) v.findViewById(R.id.tv_child_title);
                        TextView tv_child_content = (TextView) v.findViewById(R.id.tv_child_content);
                        if (StringUtil.notEmpty(bean.attachment.get(i).detail.get(j).content)) {
                            tv_child_title.setText(bean.attachment.get(i).detail.get(j).title);
                            tv_child_content.setText(bean.attachment.get(i).detail.get(j).content);
                            child_lay.addView(v);
                        }
                    }
                } else {
                    area_lay.setVisibility(View.GONE);
                }
            }
        }
    }

    private void getImage(final ImageView imageView, String url, int width, int height) {
        if (StringUtil.isEmpty(url)) {
            return;
        }
        ImageLoader.getInstance().loadBitmap(new ImageLoader.ImageCallback() {
            @Override
            public void imageCallback(Bitmap bm, String url, String imageTag) {
                if (null != bm) {
                    imageView.setImageBitmap(bm);
                }
            }
        }, url, url, width, height, -1);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            if (isProgressShowing()) {
                removeProgressDialog();
            }
            JSONObject json = JSON.parseObject(response.body.toString());
            if (json.containsKey("data")) {
                String jsonStr = json.getString("data");
                bean = JSON.parseObject(jsonStr, SchoolDetailInfoBean.class);
                refreshSchoolInfo(bean);
            }
        }
    }

    @Override
    public void notifyError(ResponseData request, ResponseData response, Exception e) {

    }
}
