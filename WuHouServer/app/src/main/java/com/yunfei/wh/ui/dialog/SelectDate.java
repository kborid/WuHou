package com.yunfei.wh.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.prj.sdk.util.Utils;
import com.yunfei.wh.R;

import java.util.Calendar;

public class SelectDate extends LinearLayout {

    private Button button;
    private LinearLayout mainLayout;
    private PopupWindow popupWindow;
    DatePicker datePicker;
    private Calendar calendar;
    private LinearLayout dialogLayout;
    private String result = "";
    private View mView;
    private Context mContext;

    public SelectDate(Context context, View view) {
        super(context);
        mContext = context;
        mView = view;
        InitializeComponent();
    }

    private void InitializeComponent() {
        this.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        this.setOrientation(LinearLayout.VERTICAL);
        mainLayout = new LinearLayout(mContext);
        LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        mainLayout.setLayoutParams(lp);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        popupWindow = new PopupWindow(mContext);
        // 设置SelectPicPopupWindow弹出窗体的宽
        popupWindow.setWidth(LayoutParams.FILL_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        popupWindow.setHeight(Utils.mScreenHeight / 3);
        // 设置SelectPicPopupWindow弹出窗体可点击
        popupWindow.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        // popupWindow.setAnimationStyle(R.style.AnimBottom);

        button = new Button(mContext);
        LayoutParams btn_params = new LayoutParams(LayoutParams.MATCH_PARENT, Utils.dip2px(50));
        btn_params.leftMargin = 5;
        btn_params.rightMargin = 5;
        btn_params.topMargin = 10;
        btn_params.gravity = Gravity.BOTTOM;
        button.setLayoutParams(btn_params);
        button.setTextSize(20);
        button.setText(R.string.ok);

        dialogLayout = new LinearLayout(mContext);
        LayoutParams params = new LayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        dialogLayout.setLayoutParams(params);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setBackgroundColor(Color.argb(60, 255, 255, 255));
        params.gravity = Gravity.CENTER_HORIZONTAL;

        calendar = Calendar.getInstance();

        datePicker = new DatePicker(mContext);
        LayoutParams params2 = new LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        params2.gravity = Gravity.CENTER_HORIZONTAL;
        params2.topMargin = 10;
        datePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        datePicker.setLayoutParams(params2);
        dialogLayout.addView(datePicker);
        dialogLayout.addView(button);
        popupWindow.setContentView(dialogLayout);
    }

    public void SetModel() {

        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker arg0, int year, int month, int day) {
                if (month + 1 < 10)
                    result = year + "-" + "0" + (month + 1) + "-" + day + "";
                else
                    result = year + "-" + (month + 1) + "-" + day + "";
            }
        });

        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (result.length() <= 0 || "".equals(result))
                    if (calendar.get(Calendar.MONDAY) + 1 < 10)
                        result = calendar.get(Calendar.YEAR) + "-" + "0" + (calendar.get(Calendar.MONDAY) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
                    else
                        result = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONDAY) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
//				UIViewMgr.Instance().AddFinishedResult(new InvokeResult(paras, result));
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(mView, Gravity.BOTTOM, 0, 0);
    }
}
