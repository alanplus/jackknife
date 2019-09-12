package com.lwh.jackknife.av.live;

public class JKPusher {

    public native void init(int width, int height, String url);

    public void init(String url) {
        init(500, 400, url);
    }

    public native void start(byte[] yuvImage);

    public native void stop();

    public native void close();
}
