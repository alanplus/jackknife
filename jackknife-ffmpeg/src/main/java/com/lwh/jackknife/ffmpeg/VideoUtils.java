package com.lwh.jackknife.ffmpeg;

public class VideoUtils {

    /**
     * 视频像素数据YUV420P和YUV420SP。
     */
    public static final int PIX_FMT_YUV420 = 0x01;

    /**
     * 视频像素数据YUV422。
     */
    public static final int PIX_FMT_YUV422 = 0x02;

    /**
     * 视频像素数据YUV444。
     */
    public static final int PIX_FMT_YUV444 = 0x03;

    /**
     * 视频像素数据RGB24。
     */
    public static final int PIX_FMT_RGB24 = 0x04;

    public static final int AVMEDIA_TYPE_UNKNOWN = -1;  ///< Usually treated as AVMEDIA_TYPE_DATA
    public static final int AVMEDIA_TYPE_VIDEO = 0;
    public static final int AVMEDIA_TYPE_AUDIO = 1;
    public static final int AVMEDIA_TYPE_DATA = 2;          ///< Opaque data information usually continuous
    /**
     * 字幕。
     */
    public static final int AVMEDIA_TYPE_SUBTITLE = 3;
    public static final int AVMEDIA_TYPE_ATTACHMENT = 4;    ///< Opaque data information usually sparse
    public static final int AVMEDIA_TYPE_NB = 5;

    public native static void reduceVideoQuality(int bitrate);

    public native static void adjustVideoSize(String inputPath, int widthPixels, int heightPixels);
}
