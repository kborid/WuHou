package com.yunfei.wh.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.prj.sdk.net.image.ImageLoader;
import com.prj.sdk.util.Utils;
import com.yunfei.wh.R;
import com.yunfei.wh.ui.adapter.ViewPagerAdapter;
import com.yunfei.wh.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/***
 * 图片缩放预览
 */
public class ImageScaleActivity extends BaseActivity implements OnPageChangeListener {

    private List<String> imgUrl;
    private ViewPagerAdapter mVPAdapter;
    private List<View> mView = new ArrayList<>();
    private ViewPager mViewPager;
    private int mSelection;
    private LinearLayout mIndicatorLayout;
    private RelativeLayout rootview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_image_scale);
        initViews();
        initParams();
        initListeners();
    }

    @Override
    public void initViews() {
        super.initViews();
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mIndicatorLayout = (LinearLayout) findViewById(R.id.point_indicator);
        rootview = (RelativeLayout) findViewById(R.id.rootview);
    }

    @Override
    public void dealIntent() {
        super.dealIntent();
        if (getIntent().getExtras() != null && getIntent().getExtras().getStringArrayList("URL") != null) {
            imgUrl = getIntent().getExtras().getStringArrayList("URL");
            mSelection = getIntent().getExtras().getInt("INDEX");
        }
    }

    @Override
    public void initParams() {
        super.initParams();
        findViewById(R.id.title_lay).setBackgroundResource(R.color.transparent);
        mVPAdapter = new ViewPagerAdapter(this, mView);
        mViewPager.setAdapter(mVPAdapter);
        mViewPager.setCurrentItem(mSelection);

        if (imgUrl != null && imgUrl.size() > 0) {
            for (int i = 0; i < imgUrl.size(); i++) {
                String url = imgUrl.get(i);
                final ImageView view = new ImageView(this);
                view.setTag(url);
                mView.add(view);
                view.setImageResource(R.drawable.ic_logo_placeholder);
                ImageLoader.getInstance().loadBitmap(new ImageLoader.ImageCallback() {

                    @Override
                    public void imageCallback(Bitmap bm, String url, String imageTag) {
                        if (bm != null) {
                            view.setImageBitmap(bm);
                            mVPAdapter.notifyDataSetChanged();
                        }
                    }
                }, url, url, 1600, 900, -1);
                mVPAdapter.notifyDataSetChanged();
            }
            initTopIndicator();
            updateTopGalleryItem(mSelection);
            tv_center_title.setText(new StringBuilder().append(mSelection + 1).append("/").append(mView.size()));
        }
    }

    @Override
    public void initListeners() {
        super.initListeners();
        mViewPager.addOnPageChangeListener(this);
        rootview.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewPager.setCurrentItem(mSelection);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rootview:
                finish();
                break;
            default:
                break;
        }
    }

    public void initTopIndicator() {
        if (mView.size() == 0) {
            mIndicatorLayout.setVisibility(View.GONE);
            return;
        } else {
            mIndicatorLayout.setVisibility(View.VISIBLE);
        }

        mIndicatorLayout.removeAllViews();
        for (int i = 0; i < mView.size(); i++) {
            ImageView img = new ImageView(this);
            img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            img.setBackgroundResource(R.drawable.banner_indicator_bg);
            img.setEnabled(false);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Utils.dip2px(7), Utils.dip2px(7));
            if (i > 0) {
                lp.leftMargin = Utils.dip2px(7);
            }
            img.setLayoutParams(lp);
            mIndicatorLayout.addView(img);
        }

        updateTopGalleryItem(0);
    }

    public synchronized void updateTopGalleryItem(int index) {
        for (int i = 0; i < mIndicatorLayout.getChildCount(); i++) {
            if (i == index) {
                mIndicatorLayout.getChildAt(i).setEnabled(true);
            } else {
                mIndicatorLayout.getChildAt(i).setEnabled(false);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {
        updateTopGalleryItem(arg0);
        tv_center_title.setText(new StringBuilder().append(arg0 + 1).append("/").append(mView.size()));
    }
}
