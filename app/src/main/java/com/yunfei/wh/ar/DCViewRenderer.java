package com.yunfei.wh.ar;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class DCViewRenderer implements GLSurfaceView.Renderer {

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        DCEasyARControlJNI.nativeInitGL();
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
        DCEasyARControlJNI.nativeResizeGL(w, h);
    }

    public void onDrawFrame(GL10 gl) {
        DCEasyARControlJNI.nativeRender();
    }

}
