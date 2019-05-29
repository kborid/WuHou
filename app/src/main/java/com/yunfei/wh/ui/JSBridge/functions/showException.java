package com.yunfei.wh.ui.JSBridge.functions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.prj.sdk.util.StringUtil;
import com.thunisoft.jsbridge.wvjb.WVJBResponseCallback;
import com.thunisoft.jsbridge.wvjb.handler.WVJBHandler;

/**
 * 3.4 异常处理,显示异常并且会退出整个webView
 *
 * @author LiaoBo
 */
public class showException implements WVJBHandler {
    private Context mContext;

    public showException(Context context) {
        mContext = context;
    }

    @Override
    public void request(Object data, final WVJBResponseCallback callback) {
        try {

            if (StringUtil.isEmpty(data)) {
                return;
            }
            // 解析请求参数
            JSONObject mJson = JSON.parseObject(data.toString());
            String exception = mJson.getString("exception");
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(exception);
            builder.setPositiveButton("关闭", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}