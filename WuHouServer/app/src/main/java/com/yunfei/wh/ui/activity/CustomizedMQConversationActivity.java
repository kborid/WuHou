package com.yunfei.wh.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.meiqia.meiqiasdk.activity.MQConversationActivity;
import com.meiqia.meiqiasdk.model.Agent;

/**
 * 集成自 MQConversationActivity，可以动态改变其中的一些方法实现
 */
public class CustomizedMQConversationActivity extends MQConversationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 这里可以动态添加一些 View 到布局中
    }

    @Override
    protected void onLoadDataComplete(MQConversationActivity mqConversationActivity, Agent agent) {
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
