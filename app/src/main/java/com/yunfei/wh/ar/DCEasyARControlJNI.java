package com.yunfei.wh.ar;

import java.util.ArrayList;

/**
 * @author kborid
 * @date 2016/7/20
 */
public class DCEasyARControlJNI {

    public static native void nativeInitGL();

    public static native void nativeResizeGL(int w, int h);

    public static native void nativeRender();

    public static native boolean nativeInit(ArrayList<DCImageVideoInfo> list);

    public static native void addImageSource(DCImageVideoInfo info);

    public static native void nativeDestory();

    public static native void nativeRotationChange(boolean portrait);
}
