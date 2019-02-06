package com.lwh.jackknife.ffmpeg;

import android.view.Surface;

/**
 * 视频播放的控制器。
 */
public class JKPlayer {

    public native void render(String inputPath, Surface surface);

    static {
        System.loadLibrary("avutil-55");
        System.loadLibrary("swresample-2");
        System.loadLibrary("avcodec-57");
        System.loadLibrary("avformat-57");
        System.loadLibrary("swscale-4");
        System.loadLibrary("postproc-54");
        System.loadLibrary("avfilter-6");
        System.loadLibrary("avdevice-57");
        System.loadLibrary("yuv");
        System.loadLibrary("jknfmpeg");
    }
}
