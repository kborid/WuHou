package com.yunfei.wh.ui.base;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dc.statistic.StatisticProxy;
import com.prj.sdk.net.image.ImageLoader;
import com.prj.sdk.util.LogUtil;
import com.umeng.analytics.MobclickAgent;
import com.yunfei.wh.R;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.control.UpdateControl;
import com.yunfei.wh.ui.dialog.ProgressDialog;

/**
 * fragment基类，提供公共属性
 *
 * @author LiaoBo
 */
public abstract class BaseFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    protected static String requestID;
    /**
     * Fragment当前状态是否可见
     */
    protected boolean isVisible = false;
    protected boolean isPrepared = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isResumed()) {
            onVisibilityChangedToUser(isVisibleToUser, true);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isPrepared = true;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void onVisible() {
    }

    protected void onInvisible() {
    }

    protected void initViews(View view) {
    }

    public void dealIntent() {
    }

    // 参数设置
    protected void initParams() {
    }

    protected void initListeners() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            onVisibilityChangedToUser(true, false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onVisibilityChangedToUser(false, false);
        }
    }

    /**
     * 当Fragment对用户的可见性发生了改变的时候就会回调此方法
     *
     * @param isVisibleToUser                      true：用户能看见当前Fragment；false：用户看不见当前Fragment
     * @param isHappenedInSetUserVisibleHintMethod true：本次回调发生在setUserVisibleHintMethod方法里；false：发生在onResume或onPause方法里
     */
    public void onVisibilityChangedToUser(boolean isVisibleToUser, boolean isHappenedInSetUserVisibleHintMethod) {
        try {
            if (isVisibleToUser) {
                isVisible = true;
                onVisible();
                MobclickAgent.onPageStart(this.getClass().getName());
                // 平台埋点
                String tt = getClass().getSimpleName();
                String userId = "";
                if (SessionContext.isLogin()) {
                    userId = SessionContext.mUser.USERBASIC.id;
                }
                StatisticProxy.getInstance().onPageViews(getActivity(), "m99", SessionContext.getAreaInfo(1), UpdateControl.getInstance().getCurVersionName(), "", userId, tt, tt, System.currentTimeMillis());

                LogUtil.d(tt, " - display - " + (isHappenedInSetUserVisibleHintMethod ? "setUserVisibleHint" : "onResume"));
            } else {
                isVisible = false;
                onInvisible();
                MobclickAgent.onPageEnd(this.getClass().getName());
                LogUtil.d(getClass().getSimpleName(), " - hidden - " + (isHappenedInSetUserVisibleHintMethod ? "setUserVisibleHint" : "onPause"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void showProgressDialog(String tip, boolean cancelable) {
        showProgressDialog(getActivity(), tip, cancelable, null);
    }

    /**
     * 显示loading对话框
     */
    public final void showProgressDialog(Context cxt, String tip, boolean cancelable, DialogInterface.OnCancelListener mCancel) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(cxt);
        }
//        mProgressDialog.setMessage(tip);
        mProgressDialog.setCanceledOnTouchOutside(false);
        // mProgressDialog.setCancelable(cancelable);
        mProgressDialog.setCancelable(false);
        if (cancelable) {
            mProgressDialog.setOnCancelListener(mCancel);
        }
        mProgressDialog.show();
    }

    public final boolean isProgressShowing() {
        if (mProgressDialog != null) {
            return mProgressDialog.isShowing();
        } else {
            return false;
        }
    }

    /**
     * 销毁loading对话框
     */
    public final void removeProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    public void loadImg(String domain, String url, final ImageView imageView) {
        if (url != null) {
            url = domain + url;
            imageView.setImageResource(R.drawable.ic_logo_placeholder);
            imageView.setTag(R.id.image_url, url);
            ImageLoader.getInstance().loadBitmap(new ImageLoader.ImageCallback() {
                @Override
                public void imageCallback(Bitmap bm, String url,
                                          String imageTag) {
                    if (bm != null) {
                        imageView.setImageBitmap(bm);
                    }
                }
            }, url, url, 480, 320, -1);
        }
    }
}
