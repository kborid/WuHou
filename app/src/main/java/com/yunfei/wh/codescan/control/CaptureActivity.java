package com.yunfei.wh.codescan.control;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.prj.sdk.util.LogUtil;
import com.prj.sdk.widget.CustomToast;
import com.yunfei.wh.BuildConfig;
import com.yunfei.wh.R;
import com.yunfei.wh.broatcast.UnLoginBroadcastReceiver;
import com.yunfei.wh.codescan.camera.CameraManager;
import com.yunfei.wh.codescan.view.ViewfinderView;
import com.yunfei.wh.common.SessionContext;
import com.yunfei.wh.ui.activity.EasyARActivity;
import com.yunfei.wh.ui.activity.HtmlActivity;
import com.yunfei.wh.ui.activity.MyQRCodeActivity;
import com.yunfei.wh.ui.base.BaseActivity;
import com.yunfei.wh.ui.dialog.CustomDialog;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * 这个activity打开相机，在后台线程做常规的扫描；它绘制了一个结果view来帮助正确地显示条形码，在扫描的时候显示反馈信息，
 * 然后在扫描成功的时候覆盖扫描结果
 */
public final class CaptureActivity extends BaseActivity implements
        SurfaceHolder.Callback {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    // 相机控制
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private IntentSource source;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, ?> decodeHints;
    private String characterSet;
    // 声音、震动控制
    private BeepManager beepManager;
    private TextView tv_myqr, tv_ar;

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    /**
     * OnCreate中初始化一些辅助类，如InactivityTimer（休眠）、Beep（声音）以及AmbientLight（闪光灯）
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // 保持Activity处于唤醒状态
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.ui_code_scan_act);
        initViews();
        initParams();
        initListeners();
        hasSurface = false;
        beepManager = new BeepManager(this);
    }

    @Override
    public void initViews() {
        super.initViews();
        tv_myqr = (TextView) findViewById(R.id.tv_myqr);
        tv_ar = (TextView) findViewById(R.id.tv_ar);
        if (BuildConfig.FLAVOR.equals("liangjiang")) {
            tv_myqr.setVisibility(View.GONE);
            tv_ar.setVisibility(View.GONE);
        }
    }

    @Override
    public void initListeners() {
        super.initListeners();
        tv_myqr.setOnClickListener(this);
        tv_ar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_myqr:
                if (SessionContext.isLogin()) {
                    Intent intent = new Intent(this, MyQRCodeActivity.class);
                    startActivity(intent);
                } else {
                    sendBroadcast(new Intent(UnLoginBroadcastReceiver.ACTION_NAME));
                }
                break;
            case R.id.tv_ar:
                Intent intent = new Intent(this, EasyARActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // CameraManager必须在这里初始化，而不是在onCreate()中。
        // 这是必须的，因为当我们第一次进入时需要显示帮助页，我们并不想打开Camera,测量屏幕大小
        // 当扫描框的尺寸不正确时会出现bug
        cameraManager = new CameraManager(getApplication());

        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        viewfinderView.setCameraManager(cameraManager);

        handler = null;

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            // activity在paused时但不会stopped,因此surface仍旧存在；
            // surfaceCreated()不会调用，因此在这里初始化camera
            initCamera(surfaceHolder);
        } else {
            // 重置callback，等待surfaceCreated()来初始化camera
            surfaceHolder.addCallback(this);
        }

        beepManager.updatePrefs();

        source = IntentSource.NONE;
        decodeFormats = null;
        characterSet = null;
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        beepManager.close();
        cameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    /**
     * 扫描成功，处理反馈信息
     *
     * @param rawResult
     * @param barcode
     * @param scaleFactor
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        boolean fromLiveScan = barcode != null;
        //这里处理解码完成后的结果，此处将参数回传到Activity处理
        if (fromLiveScan) {
            beepManager.playBeepSoundAndVibrate();
//            Intent intent = getIntent();
//            intent.putExtra("codedContent", rawResult.getText());
//            setResult(RESULT_OK, intent);
            if (rawResult != null) {
                String ret = rawResult.getText();
                LogUtil.d(TAG, "decord result = " + ret);
                Intent intent;
                if (ret.startsWith("http")) {
                    if (ret.contains("weixin.qq.com/r/")) {
                        try {
                            intent = new Intent(Intent.ACTION_MAIN);
                            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                            intent.addCategory(Intent.CATEGORY_LAUNCHER);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setComponent(cmp);
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            CustomToast.show(getString(R.string.not_install_wx), 0);
                        }
                    } else {
                        intent = new Intent(this, HtmlActivity.class);
                        intent.putExtra("path", ret);
                        startActivity(intent);
                    }
                    finish();
                } else {
                    CustomDialog dialog = new CustomDialog(this);
                    dialog.setTitle(R.string.ret_scan);
                    dialog.setMessage(ret);
                    dialog.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.sendEmptyMessage(R.id.restart_preview);
                        }
                    });
                    dialog.show();
                }
            }
        }

    }

    /**
     * 初始化Camera
     *
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            return;
        }
        try {
            // 打开Camera硬件设备
            cameraManager.openDriver(surfaceHolder);
            // 创建一个handler来打开预览，并抛出一个运行时异常
            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats,
                        decodeHints, characterSet, cameraManager);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            displayFrameworkBugMessageAndExit();
        }
    }

    /**
     * 显示底层错误信息并退出应用
     */
    private void displayFrameworkBugMessageAndExit() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(getString(R.string.app_name));
//        builder.setMessage(getString(R.string.msg_camera_framework_bug));
//        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
//        builder.setOnCancelListener(new FinishListener(this));
//        builder.show();
    }
}
