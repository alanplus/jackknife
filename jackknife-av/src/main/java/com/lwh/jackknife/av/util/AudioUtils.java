package com.lwh.jackknife.av.util;

public class AudioUtils {

    static {
        System.loadLibrary("jknfav");
    }

    public native int getBitrate(String input_music);
}
