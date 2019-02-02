package com.lwh.jackknife.ffmpeg;

public class AudioUtils {

    public static final int AUDIO_QUALITY_SQ = 1;
    public static final int AUDIO_QUALITY_HQ = 2;
    public static final int AUDIO_QUALITY_STANDARD = 3;

    public native static void reduceAudioQuality(int audioQuality, String sqPath, String outputPath);
    public native static void extractAudioTrack(String audioFilePath, String outputFolder);
}
