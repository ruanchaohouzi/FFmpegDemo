### 一、FFmpeg库编译和JNI编译
## 1、FFmpeg库编译
（1）可以自行网上找编译好的FFmpeg库，当然也就可以自己编译ffmpeg库，自己编译大约需要30分钟，坑比较多。

（2）编译好的FFmpeg库包含so文件和include文件，详见工程目录src/main/cpp 目录和 jniLibs目录。

## 2、JNI编写和编译

（1）命令行执行FFmpeg库的jni,详见src/main/cpp/native-lib.c文件
```
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
```

（2）需要依赖源文件的ffmpeg.c文件的main函数，并且依赖一些源文件，所以需要拷贝源代码的一些文件到工程中，详见工程目录src/main/cpp/
的一些源文件

（3）需要修改ffmpeg.c文件的main函数名为jxRun，或者其他名称，需要在头文件中加入jxRun。

（4）JNI编译，主要是编写CMakeList.txt文件，详见工程的CMakeList.txt文件。

（5）如果需要采取FFmpeg的api编写C代码调取ffmpeg接口当然也是可以的，只要在jni文件中编写即可。