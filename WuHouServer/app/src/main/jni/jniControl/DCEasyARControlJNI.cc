#include "./DCEasyARControlJNI.hpp"
#include "../arcontroller.hpp"
#include <android/log.h>

EasyAR::samples::ARController ar;

JNIEXPORT jboolean JNICALL JNIFUNCTION_NATIVE(nativeInit(JNIEnv * env, jclass, jobject
                                                      list)) {
    bool status = ar.initCamera();
    status &= ar.start();
    if (list == NULL) {
        return (jboolean) status;
    }
    jclass cls_list = env->GetObjectClass(list);
    jmethodID get_list = env->GetMethodID(cls_list, "get", "(I)Ljava/lang/Object;");
    jmethodID size_list = env->GetMethodID(cls_list, "size", "()I");
    jint len = env->CallIntMethod(list, size_list);
    __android_log_print(ANDROID_LOG_INFO, "EasyAr", "list len = %d", len);
    for (jint i = 0; i < len; i++) {
        jobject obj_info = env->CallObjectMethod(list, get_list, i);
        jclass cls_info = env->GetObjectClass(obj_info);
        jfieldID ID_image = env->GetFieldID(cls_info, "image", "Ljava/lang/String;");
        jfieldID ID_name = env->GetFieldID(cls_info, "name", "Ljava/lang/String;");
        jfieldID ID_isJson = env->GetFieldID(cls_info, "isJson", "Z");
        jfieldID ID_type = env->GetFieldID(cls_info, "type", "I");
        jfieldID ID_path = env->GetFieldID(cls_info, "path", "Ljava/lang/String;");
        jstring image = (jstring) env->GetObjectField(obj_info, ID_image);
        jstring name = (jstring) env->GetObjectField(obj_info, ID_name);
        jint type = env->GetIntField(obj_info, ID_type);
        jboolean isJson = env->GetBooleanField(obj_info, ID_isJson);
        jstring path = (jstring) env->GetObjectField(obj_info, ID_path);

        const char *c_image = env->GetStringUTFChars(image, NULL);
        std::string str_image = c_image;
        env->ReleaseStringUTFChars(image, c_image);
        const char *c_name = env->GetStringUTFChars(name, NULL);
        std::string str_name = c_name;
        env->ReleaseStringUTFChars(name, c_name);
        const char *c_path = env->GetStringUTFChars(path, NULL);
        std::string str_path = c_path;
        env->ReleaseStringUTFChars(path, c_path);
        ar.addImageSource(str_image, str_name, isJson, type, str_path);
    }

    return (jboolean) status;
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(addImageSource(JNIEnv * env, jclass, jobject
                                                  jobj)) {
    if (jobj != NULL) {
        return;
    }
    jclass clazz = env->GetObjectClass(jobj);
    jfieldID ID_image = env->GetFieldID(clazz, "image", "Ljava/lang/String;");
    jfieldID ID_name = env->GetFieldID(clazz, "name", "Ljava/lang/String;");
    jfieldID ID_isJson = env->GetFieldID(clazz, "isJson", "Z");
    jfieldID ID_type = env->GetFieldID(clazz, "type", "I");
    jfieldID ID_path = env->GetFieldID(clazz, "path", "Ljava/lang/String;");
    jstring image = (jstring) env->GetObjectField(jobj, ID_image);
    jstring name = (jstring) env->GetObjectField(jobj, ID_name);
    jint type = env->GetIntField(jobj, ID_type);
    jboolean isJson = env->GetBooleanField(jobj, ID_isJson);
    jstring path = (jstring) env->GetObjectField(jobj, ID_path);

    const char *c_image = env->GetStringUTFChars(image, NULL);
    std::string str_image = c_image;
    env->ReleaseStringUTFChars(image, c_image);
    const char *c_name = env->GetStringUTFChars(name, NULL);
    std::string str_name = c_name;
    env->ReleaseStringUTFChars(name, c_name);
    const char *c_path = env->GetStringUTFChars(path, NULL);
    std::string str_path = c_path;
    env->ReleaseStringUTFChars(path, c_path);
    ar.addImageSource(str_image, str_name, isJson, type, str_path);
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeDestory(JNIEnv * , jclass)) {
    ar.clear();
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeInitGL(JNIEnv * , jclass)) {
    ar.initGL();
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeResizeGL(JNIEnv * , jclass, jint
                                                  w, jint
                                                  h)) {
    ar.resizeGL(w, h);
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeRender(JNIEnv * , jclass)) {
    ar.render();
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeRotationChange(JNIEnv * , jclass, jboolean
                                                  portrait)) {
    ar.setPortrait(portrait);
}

