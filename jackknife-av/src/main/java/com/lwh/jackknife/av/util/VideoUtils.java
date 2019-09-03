package com.lwh.jackknife.av.util;

public class VideoUtils {

    static {
        System.loadLibrary("jknfav");
    }

    public native boolean addVideoBgMusic(String input_video, String input_music, String output_path);
}
