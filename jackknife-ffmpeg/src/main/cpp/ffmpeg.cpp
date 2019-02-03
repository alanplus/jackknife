#include <jni.h>
#include <string>

extern "C"
{
#include "libavformat/avformat.h"
}


extern "C"
JNIEXPORT jstring

JNICALL
Java_com_yj_ffmpegdemo_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    //注册
    av_register_all();


    return env->NewStringUTF(hello.c_str());
}
