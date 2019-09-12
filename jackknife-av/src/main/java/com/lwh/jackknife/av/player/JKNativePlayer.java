package com.lwh.jackknife.av.player;

import android.view.Surface;

public class JKNativePlayer {

    static {
        System.loadLibrary("jknfav");
    }

    public native static int playAddFilter(Surface surface, String path);
    public native static int play(Surface surface, String path);
}
