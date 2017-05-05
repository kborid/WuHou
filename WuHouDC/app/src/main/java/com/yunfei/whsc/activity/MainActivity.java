package com.yunfei.whsc.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.prj.sdk.util.Utils;
import com.yunfei.whsc.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout gstj_lay, yyph_lay, zzdb_lay, bjcx_lay, bszn_lay, phsj_lay;
    private RelativeLayout dddzw_lay, phxx_lay, title_lay;
    private float serverHeight, titleHeight;
    private ImageView iv_yyph, iv_bszn, iv_zzdb, iv_bjcx, iv_qr, iv_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.initScreenSize(this);
        findViews();
        setClickListeners();
    }

    private void findViews() {
        title_lay = (RelativeLayout) findViewById(R.id.title_lay);

        gstj_lay = (LinearLayout) findViewById(R.id.gstj_lay);
        yyph_lay = (LinearLayout) findViewById(R.id.yyph_lay);
        zzdb_lay = (LinearLayout) findViewById(R.id.zzdb_lay);
        bjcx_lay = (LinearLayout) findViewById(R.id.bjcx_lay);
        bszn_lay = (LinearLayout) findViewById(R.id.bszn_lay);
        phsj_lay = (LinearLayout) findViewById(R.id.phsj_lay);
        dddzw_lay = (RelativeLayout) findViewById(R.id.dddzw_lay);
        phxx_lay = (RelativeLayout) findViewById(R.id.phxx_lay);

        iv_yyph = (ImageView) findViewById(R.id.iv_yyph);
        iv_bszn = (ImageView) findViewById(R.id.iv_bszn);
        iv_zzdb = (ImageView) findViewById(R.id.iv_zzdb);
        iv_bjcx = (ImageView) findViewById(R.id.iv_bjcx);
        iv_qr = (ImageView) findViewById(R.id.iv_qr);

        iv_logo = (ImageView) findViewById(R.id.iv_logo);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        System.out.println("onWindowFocuschanged");
        titleHeight = title_lay.getMeasuredHeight();
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        titleLp.height = (int) (titleHeight / 2);
        titleLp.width = (int) (titleHeight / 2);
        iv_qr.setLayoutParams(titleLp);
        iv_logo.setLayoutParams(titleLp);

        serverHeight = yyph_lay.getMeasuredHeight();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.height = (int) (serverHeight / 2);
        lp.width = (int) (serverHeight / 2);
        iv_yyph.setLayoutParams(lp);
        iv_bszn.setLayoutParams(lp);
        iv_zzdb.setLayoutParams(lp);
        iv_bjcx.setLayoutParams(lp);
        super.onWindowFocusChanged(hasFocus);
    }

    private void setClickListeners() {
        gstj_lay.setOnClickListener(this);
        yyph_lay.setOnClickListener(this);
        zzdb_lay.setOnClickListener(this);
        bjcx_lay.setOnClickListener(this);
        bszn_lay.setOnClickListener(this);
        phsj_lay.setOnClickListener(this);
        dddzw_lay.setOnClickListener(this);
        phxx_lay.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    public void onClick(View view) {
        Intent intent = null;
        ;
        switch (view.getId()) {
            case R.id.gstj_lay:
                intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("path", "http://118.122.125.48/whinfo/pages/businessinfomobile.aspx");
                intent.putExtra("title", "工商数据统计");
                break;
            case R.id.yyph_lay:
                intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("path", /*"http://cdwh.org/wh_portal/yyph/service/YYPH.depart.do"*/"http://cdwh.org/wh_portal/public/yyph/h5/rule/pages/orderRule.jsp");
                intent.putExtra("title", "预约排号");
                break;
            case R.id.zzdb_lay:
                intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("path", "http://zjpt.cdwh.gov.cn/whzjjgpt/");
                intent.putExtra("title", "证照代办");
                break;
            case R.id.bjcx_lay:
                intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("path", "http://cdwh.org/wh_portal/bjcx/service/Wap.index.do");
                intent.putExtra("title", "办件查询");
                break;
            case R.id.bszn_lay:
                intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("path", "http://cdwh.org/wh_portal/people/service/People.h5index.do");
                intent.putExtra("title", "办事指南");
                break;
            case R.id.dddzw_lay:
                intent = new Intent(MainActivity.this, VideoPlayActivity.class);
                intent.putExtra("title", "3D政务大厅");
                break;
            case R.id.phxx_lay:
                intent = new Intent(MainActivity.this, OrderScreenActivity.class);
                intent.putExtra("title", "排号信息");
                break;
            case R.id.phsj_lay:
                intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("path", "http://118.122.125.48/whinfo/pages/infomobile.aspx");
                intent.putExtra("title", "排号数据发布");
                break;
            default:
                break;
        }

        if (null != intent) {
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }

    private int changeTextSize(int size) {
        Resources r = getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                size, r.getDisplayMetrics());
    }
}
