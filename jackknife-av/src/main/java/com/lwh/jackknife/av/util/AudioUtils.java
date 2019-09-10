package com.lwh.jackknife.av.util;

public class AudioUtils {

    static {
        System.loadLibrary("jknfav");
    }

    public native static int getBitrate(String input_music);
}
