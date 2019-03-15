package com.yunfei.wh.permission;

import android.Manifest;

/**
 * @author kborid
 * @date 2017/9/27.
 */

public class PermissionsDef {

    public static final int PERMISSION_REQ_CODE = 0x999;
    public static final int PERMISSIONS_GRANTED = 0; // 权限授权
    public static final int PERMISSIONS_DENIED = 1; // 权限拒绝

    //App启动所必需的权限
    public static final String[] LAUNCH_REQUIRE_PERMISSIONS = new String[]{
            //存储权限
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,

            //位置权限
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,

            //电话权限
//            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.CALL_PHONE,
    };

    //位置权限
    public static final String[] LOCATION_PERMISSION = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    //存储权限
    public static final String[] STORAGE_PERMISSION = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    //电话权限
    public static final String[] PHONE_PERMISSION = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
    };

    //相机权限
    public static final String[] CAMERA = new String[] {
            Manifest.permission.CAMERA,
    };
}
