#include <jni.h>

#ifndef __INCLUDE_JNI_DCEASYARCONTROLJNI_H__
#define __INCLUDE_JNI_DCEASYARCONTROLJNI_H__

#define JNIFUNCTION_NATIVE(sig) Java_com_yunfei_wh_ar_DCEasyARControlJNI_##sig

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jboolean JNICALL JNIFUNCTION_NATIVE(nativeInit(JNIEnv * , jclass, jobject
                                                      list));

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(addImageSource(JNIEnv * , jclass, jobject
                                                  jobj));

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeDestory(JNIEnv * , jclass));

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeInitGL(JNIEnv * , jclass));

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeResizeGL(JNIEnv * , jclass, jint
                                                  w, jint
                                                  h));

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeRender(JNIEnv * , jclass));

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeRotationChange(JNIEnv * , jclass, jboolean
                                                  portrait));
#ifdef __cplusplus
}
#endif
#endif
