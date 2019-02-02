package com.lwh.jackknife.ffmpeg;

public class VideoUtils {

    public native static void reduceVideoQuality(int bitrate);
    public native static void adjustVideoSize(String inputPath, int widthPixels, int heightPixels);
}
