package com.lwh.jackknife.av.codec;

public class Decoder {

    static {
        System.loadLibrary("jknfav");
    }
    public native int decode(String inputurl, String outputurl);
}
