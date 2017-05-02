package com.yunfei.wh.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.prj.sdk.util.GUIDGenerator;
import com.prj.sdk.util.Utils;
import com.prj.sdk.widget.CustomToast;
import com.yunfei.wh.common.AppConst;

import java.io.File;

public class GetPicDialog {
    private Activity mAct;
    private CustomDialog mDialog;
    private File mCameraFile;

    public GetPicDialog(Activity context) {
        this(context, null);
    }

    /**
     * 获取图片
     *
     * @param context
     * @param imgName 图片名称，如果为空默认生成唯一uuid作为名称
     */
    public GetPicDialog(Activity context, String imgName) {
        this.mAct = context;
        if (imgName == null || imgName.length() == 0) {
            mCameraFile = new File(Utils.getFolderDir("pic"), GUIDGenerator.generate() + ".jpg");
        } else {
            mCameraFile = new File(Utils.getFolderDir("pic"), imgName + ".jpg");
        }
        mCameraFile.deleteOnExit();// 虚拟机关闭删除文件
    }

    /**
     * 获取手机相机图片路径uri
     */
    public Uri getPicPathUri() {
        return Uri.fromFile(mCameraFile);
    }

    /**
     * 获取手机相机图片路径
     */
    public File getPicCameraFile() {
        return mCameraFile;
    }

    /**
     * 显示
     */
    public void showDialog() {
        if (mDialog == null) {
            mDialog = new CustomDialog(mAct);
        }
        mDialog.setMessage("请选择图片获取方式");
        mDialog.setPositiveButton("图库", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utils.isSDCardEnable()) {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    i.setType("image/*");
                    mAct.startActivityForResult(i, AppConst.ACTIVITY_GET_IMAGE);
                } else {
                    CustomToast.show("内存卡不可用，请检测内存卡", Toast.LENGTH_LONG);
                }
                dialog.dismiss();
            }
        });
        mDialog.setNegativeButton("相机", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utils.isSDCardEnable()) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, getPicPathUri());
                    intent.putExtra("android.intent.extra.screenOrientation", false);
                    mAct.startActivityForResult(intent, AppConst.ACTIVITY_IMAGE_CAPTURE);
                } else {
                    CustomToast.show("内存卡不可用，请检测内存卡", Toast.LENGTH_LONG);
                }
                dialog.dismiss();
            }
        });

        mDialog.show();
    }
}
