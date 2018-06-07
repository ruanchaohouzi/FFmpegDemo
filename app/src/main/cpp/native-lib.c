
#include "ffmpeg.h"
#include <jni.h>

JNIEXPORT jint JNICALL
Java_com_ruanchao_ffmpegdemo_utils_FFmpegUtil_ffmpegRun(JNIEnv *env, jobject instance,
                                                        jobjectArray cmd) {
    int argc = (*env)->GetArrayLength(env,cmd);
    char *argv[argc];
    int i;
    for (i = 0; i < argc; i++) {
        jstring js = (jstring) (*env)->GetObjectArrayElement(env,cmd, i);
        argv[i] = (char *) (*env)->GetStringUTFChars(env,js, 0);
    }
    return jxRun(argc,argv);

}

JNIEXPORT jstring JNICALL
Java_com_ruanchao_ffmpegdemo_utils_FFmpegUtil_getFFmpegConfig(JNIEnv *env, jobject instance) {

    char info[10000] = {0};
    sprintf(info, "%s\n", avcodec_configuration());
    return (*env)->NewStringUTF(env,info);
}