package com.lwh.jackknife.av.codec;

public class Encoder {

    static {
        System.loadLibrary("jknfav");
    }
    public native int init(int width,int height, String path);
    public native int encode(byte[] yuvimage);
    public native int flush();
    public native int close();
}
