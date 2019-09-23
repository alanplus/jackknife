#ifndef FFMPEGCMD_FFMPEG_API_H
#define FFMPEGCMD_FFMPEG_API_H

#include <jni.h>
#include <libavformat/avformat.h>

JNIEXPORT jint JNICALL
Java_com_lwh_jackknife_av_ffmpeg_FFmpegApi_open(JNIEnv *env, jclass type, jstring url_);

JNIEXPORT jdouble JNICALL
Java_com_lwh_jackknife_av_ffmpeg_FFmpegApi_getVideoRotation(JNIEnv *env, jclass type);

JNIEXPORT jlong JNICALL
Java_com_lwh_jackknife_av_ffmpeg_FFmpegApi_getDuration(JNIEnv *env, jclass type);

JNIEXPORT jint JNICALL
Java_com_lwh_jackknife_av_ffmpeg_FFmpegApi_getVideoWidth(JNIEnv *env, jclass type);

JNIEXPORT jint JNICALL
Java_com_lwh_jackknife_av_ffmpeg_FFmpegApi_getVideoHeight(JNIEnv *env, jclass type);

JNIEXPORT jstring JNICALL
Java_com_lwh_jackknife_av_ffmpeg_FFmpegApi_getVideoCodecName(JNIEnv *env, jclass type);

JNIEXPORT void JNICALL
Java_com_lwh_jackknife_av_ffmpeg_FFmpegApi_close(JNIEnv *env, jclass type);

#endif //FFMPEGCMD_FFMPEG_API_H
