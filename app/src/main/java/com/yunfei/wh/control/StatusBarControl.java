package com.yunfei.wh.control;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yunfei.wh.R;

/**
 * @author kborid
 * @date 2016/10/19 0019
 */
public class StatusBarControl {
    /**
     * 安卓5.0以上版本设置状态栏颜色配合如下两条属性使用
     * android:clipToPadding="true"
     * android:fitsSystemWindows="true"
     *
     * @param activity
     */
    public static void setLopStatBar(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(color));
//            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 状态栏半透明 4.4 以上有效配合如下两条属性使用
     * android:clipToPadding="true"
     * android:fitsSystemWindows="true"
     *
     * @param activity
     */
    public static void initSystemBar(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(activity, true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
        // 使用颜色资源
        tintManager.setStatusBarTintResource(R.color.main_color_wh);
    }

    @TargetApi(19)

    public static boolean setTranslucentStatus(Activity activity, boolean on) {

        Window win = activity.getWindow();

        WindowManager.LayoutParams winParams = win.getAttributes();

        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

        if (on) {

            winParams.flags |= bits;

        } else {

            winParams.flags &= ~bits;

        }
        win.setAttributes(winParams);
        return on;
    }
}
