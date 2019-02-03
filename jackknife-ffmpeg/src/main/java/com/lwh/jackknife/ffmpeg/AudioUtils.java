package com.lwh.jackknife.ffmpeg;

public class AudioUtils {

    /**
     * 码率达到320kbps。
     */
    public static final int AUDIO_QUALITY_SQ = 1;

    /**
     * 码率达到192kbps。
     */
    public static final int AUDIO_QUALITY_HQ = 2;

    /**
     * 标准码率128kbps。
     */
    public static final int AUDIO_QUALITY_STANDARD = 3;

    /**
     * 降低音频品质。
     *
     * @param audioQuality 音频品质，无损音质{@link #AUDIO_QUALITY_SQ}、高品质{@link #AUDIO_QUALITY_HQ}
     *                     和标准音质{@link #AUDIO_QUALITY_STANDARD}
     * @param sqPath 无损音质文件路径
     * @param outputPath 输出文件的路径
     */
    public native static void reduceAudioQuality(int audioQuality, String sqPath, String outputPath);

    /**
     * 抽取音频文件的音轨，用于翻唱歌曲。
     *
     * @param audioFilePath 待处理的音频文件
     * @param outputFolder 存放抽离出来的wav文件的文件夹
     */
    public native static void extractAudioTrack(String audioFilePath, String outputFolder);
}
