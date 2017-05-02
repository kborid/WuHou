package com.yunfei.wh.ui.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.prj.sdk.util.ActivityTack;

/**
 * FragmentActivity 基类提供公共属性
 *
 * @author LiaoBo
 */
public abstract class BaseFragmentActivity extends AppCompatActivity implements OnCheckedChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTack.getInstanse().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityTack.getInstanse().removeActivity(this);
    }

    public void onResume() {
        super.onResume();
        // MobclickAgent.onResume(this); //统计时长
    }

    public void onPause() {
        super.onPause();
        // removeProgressDialog();// pause时关闭加载框
        // MobclickAgent.onPause(this);
    }

    public void initViews() {
    }

    public void dealIntent() {
    }

    // 参数设置
    public void initParams() {
    }

    // 监听设置
    public void initListeners() {
    }

}
