package com.yunfei.wh.camera;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.prj.sdk.util.LogUtil;
import com.prj.sdk.util.StringUtil;
import com.prj.sdk.util.Utils;
import com.prj.sdk.widget.CustomToast;
import com.yunfei.wh.R;
import com.yunfei.wh.ui.base.BaseActivity;

import java.io.IOException;
import java.util.Arrays;

/**
 * @auth kborid
 * @date 2018/1/12 0012.
 */

public class CustomCameraActivity extends BaseActivity implements SurfaceHolder.Callback {

    private static final String TAG = "CustomCameraActivity";

    private Camera mCamera;
    private SurfaceView surfaceView;
    private SurfaceHolder mHolder;
    private int mCameraId = 0;
    private byte[] tmp = null;
    private int cameraType = 0;
    private boolean isPreviewing = true;

    //屏幕宽高
    private int mScreenWidth;
    private int mScreenHeight;

    private ImageView iv_camera, iv_cancel, iv_ok;
    private ImageView iv_close, iv_light;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_customcamera_layout);
        initViews();
        initParams();
        initListeners();
    }

    private void initDecorView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void initViews() {
        super.initViews();
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        iv_camera = (ImageView) findViewById(R.id.iv_camera);
        iv_ok = (ImageView) findViewById(R.id.iv_ok);
        iv_cancel = (ImageView) findViewById(R.id.iv_cancel);
        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_light = (ImageView) findViewById(R.id.iv_light);
    }

    @Override
    public void dealIntent() {
        super.dealIntent();
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            cameraType = bundle.getInt("cameraType");
        }
    }

    @Override
    public void initParams() {
        super.initParams();
        if (cameraType == CameraDataController.CAPTURE_SELF) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mCameraId = (mCameraId + 1) % mCamera.getNumberOfCameras();
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mCameraId = 0;
        }
        LogUtil.i(TAG, "cameraType = " + cameraType);
        mHolder = surfaceView.getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void initListeners() {
        super.initListeners();
        iv_camera.setOnClickListener(this);
        iv_ok.setOnClickListener(this);
        iv_cancel.setOnClickListener(this);
        iv_close.setOnClickListener(this);
        iv_light.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_camera:
                if (!isPreviewing) {
                    return;
                }
                isPreviewing = false;
                takeCapture();
                break;
            case R.id.iv_ok:
                LogUtil.i(TAG, "ok btn click");
                finish();
                CameraDataController.getInstance().notifyCameraDataResultListener(tmp);
                break;
            case R.id.iv_cancel:
                isPreviewing = true;
                mCamera.startPreview();
                captureRestartAnim();
                break;
            case R.id.iv_close:
                LogUtil.i(TAG, "close btn click");
                finish();
                CameraDataController.getInstance().notifyCameraDataResultListener(null);
                break;
            case R.id.iv_light:
                if (cameraType == CameraDataController.CAPTURE_SELF || !isPreviewing) {
                    return;
                }
                Camera.Parameters parameters = mCamera.getParameters();
                if (iv_light.isSelected()) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(parameters);
                } else {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);//开启
                    mCamera.setParameters(parameters);
                }
                iv_light.setSelected(!iv_light.isSelected());
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i(TAG, "onResume()");
        initDecorView();
        if (mCamera == null) {
            mCamera = getCamera(mCameraId);
            if (mHolder != null) {
                startPreview(mCamera, mHolder);
            }
        }
        captureRestartAnim();
    }

    @Override
    public void finish() {
        super.finish();
        CameraUtil.getInstance().turnLightOff(mCamera);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.i(TAG, "onPause()");
        releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tmp != null) {
            tmp = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.i(TAG, "surfaceCreated()");
        startPreview(mCamera, holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtil.i(TAG, "surfaceChanged()");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtil.i(TAG, "surfaceDestroyed()");
        releaseCamera();
    }

    private void captureOverAnim() {
        ObjectAnimator alphaCapture = ObjectAnimator.ofFloat(iv_camera, "alpha", 1f, 0f);
        ObjectAnimator alphaCancel = ObjectAnimator.ofFloat(iv_cancel, "alpha", 0f, 1f);
        ObjectAnimator alphaOk = ObjectAnimator.ofFloat(iv_ok, "alpha", 0f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.play(alphaCancel).with(alphaOk).with(alphaCapture);
        set.setDuration(200);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                iv_camera.setVisibility(View.GONE);
                iv_cancel.setVisibility(View.VISIBLE);
                iv_ok.setVisibility(View.VISIBLE);
            }
        });
        set.start();
    }

    private void captureRestartAnim() {
        ObjectAnimator alphaCapture = ObjectAnimator.ofFloat(iv_camera, "alpha", 0f, 1f);
        ObjectAnimator alphaCancel = ObjectAnimator.ofFloat(iv_cancel, "alpha", 1f, 0f);
        ObjectAnimator alphaOk = ObjectAnimator.ofFloat(iv_ok, "alpha", 1f, 0f);
        AnimatorSet set = new AnimatorSet();
        set.play(alphaCancel).with(alphaOk).with(alphaCapture);
        set.setDuration(200);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                iv_camera.setVisibility(View.VISIBLE);
                iv_cancel.setVisibility(View.GONE);
                iv_ok.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        set.start();
    }

    /**
     * 获取Camera实例
     */
    private Camera getCamera(int id) {
        Camera camera = null;
        try {
            camera = Camera.open(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return camera;
    }

    /**
     * 预览相机
     */
    private void startPreview(Camera camera, SurfaceHolder holder) {
        LogUtil.i(TAG, "startPreview()");
        try {
            setupCamera(camera);
            camera.setPreviewDisplay(holder);
            //亲测的一个方法 基本覆盖所有手机 将预览矫正
            CameraUtil.getInstance().setCameraDisplayOrientation(this, mCameraId, camera);
//            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置相机参数
     */
    private void setupCamera(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        //这里第三个参数为最小尺寸 getPropPreviewSize方法会对从最小尺寸开始升序排列 取出所有支持尺寸的最小尺寸
        Camera.Size previewSize = CameraUtil.getInstance().getPropSizeForHeight(parameters.getSupportedPreviewSizes(), 1024);
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        Camera.Size pictureSize = CameraUtil.getInstance().getPropSizeForHeight(parameters.getSupportedPictureSizes(), 1024);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);
        camera.setParameters(parameters);

        /*
         * 设置surfaceView的尺寸 因为camera默认是横屏，所以取得支持尺寸也都是横屏的尺寸
         * 我们在startPreview方法里面把它矫正了过来，但是这里我们设置设置surfaceView的尺寸的时候要注意 previewSize.height<previewSize.width
         * previewSize.width才是surfaceView的高度
         */
        if (cameraType == CameraDataController.CAPTURE_SELF) {
            mScreenWidth = previewSize.height;
            mScreenHeight = previewSize.width;
        } else {
            mScreenWidth = previewSize.width;
            mScreenHeight = previewSize.height;
        }
        surfaceView.setLayoutParams(new RelativeLayout.LayoutParams(mScreenWidth, mScreenHeight));
    }

    /**
     * 释放相机资源
     */
    private void releaseCamera() {
        LogUtil.i(TAG, "releaseCamera()");
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 拍照
     */
    private void takeCapture() {
        LogUtil.i(TAG, "tackCapture()");
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                captureOverAnim();
                LogUtil.i(TAG, "onPictureTaken()");
                if (StringUtil.notEmpty(data)) {
                    LogUtil.i(TAG, "data = " + Arrays.toString(data));
                    //将data 转换为位图 或者你也可以直接保存为文件使用 FileOutputStream
                    //这里我相信大部分都有其他用处，比如加个水印
                    Bitmap captureBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Bitmap saveBitmap = CameraUtil.getInstance().setTakePicktrueOrientation(mCameraId, captureBitmap);
                    int topValue = Utils.dip2px(65);
                    int sideValue = Utils.dip2px(40);
                    int btmValue = Utils.dip2px(145);
                    if (cameraType == CameraDataController.CAPTURE_SELF) {
                        saveBitmap = Bitmap.createScaledBitmap(saveBitmap, mScreenWidth, mScreenHeight, true);
                        saveBitmap = Bitmap.createBitmap(saveBitmap, sideValue, topValue, mScreenWidth - 2 * sideValue, mScreenHeight - topValue - btmValue);
                    } else {
                        saveBitmap = Bitmap.createScaledBitmap(saveBitmap, mScreenHeight, mScreenWidth, true);
                        saveBitmap = Bitmap.createBitmap(saveBitmap, sideValue, topValue, mScreenHeight - 2 * sideValue, mScreenWidth - topValue - btmValue);
                    }
                    tmp = BitmapUtils.getByteFromBitmap(saveBitmap);
                    //保存照片
//                    String img_path = getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath() + File.separator + "tmp.jpg";
//                    BitmapUtils.saveJPGE_After(CustomCameraActivity.this, saveBitmap, img_path, 100);
                    //这里打印宽高 就能看到 CameraUtil.getInstance().getPropPictureSize(parameters.getSupportedPictureSizes(), 200);
                    // 这设置的最小宽度影响返回图片的大小 所以这里一般这是1000左右把我觉得
                    if (!captureBitmap.isRecycled()) {
                        captureBitmap.recycle();
                    }
                    if (!saveBitmap.isRecycled()) {
                        saveBitmap.recycle();
                    }
                } else {
                    tmp = null;
                    CustomToast.show("拍摄失败，请重试", Toast.LENGTH_LONG);
                }
            }
        });
    }
}
