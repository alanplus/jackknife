/*
 * Copyright (C) 2019 The JackKnife Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lwh.jackknife.av.util;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;

import com.lwh.jackknife.av.ffmpeg.Callback;
import com.lwh.jackknife.av.ffmpeg.FFmpeg;
import com.lwh.jackknife.av.filter.VideoFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoUtils {


    static {
        System.loadLibrary("jknfav");
    }

    public static final int WATER_MARK_POS_LEFT_UP = 0;
    public static final int WATER_MARK_POS_RIGHT_UP = 1;
    public static final int WATER_MARK_POS_LEFT_BOTTOM = 2;
    public static final int WATER_MARK_POS_RIGHT_BOTTOM = 3;

    /**
     * 扫描媒体文件。
     *
     * @param filePath
     */
    public static void scanMediaFile(Context context, String filePath) {
        if (new File(filePath).exists()) {
            MediaScannerConnection.scanFile(context,
                    new String[]{filePath}, null, null);
        }
    }

    private static void setVideo(List<String> cmdLine) {
        cmdLine.add("-b:v");
        cmdLine.add("600k");
        cmdLine.add("-bufsize");
        cmdLine.add("600k");
        cmdLine.add("-maxrate");
        cmdLine.add("800k");

        cmdLine.add("-c:v");
        cmdLine.add("libx264");
        cmdLine.add("-preset");
        cmdLine.add("fast");
        cmdLine.add("-crf");
        cmdLine.add("28");
        cmdLine.add("-threads");
        cmdLine.add("2");

        cmdLine.add("-y");
        cmdLine.add("-f");
        cmdLine.add("mp4");
    }

    private static void setAudio(List<String> cmdLine) {
        cmdLine.add("-c:a");
        cmdLine.add("aac");
        cmdLine.add("-ar");
        cmdLine.add("44100");
        cmdLine.add("-ab");
        cmdLine.add("48k");
    }

    private static void setSize(List<String> cmdLine) {
        cmdLine.add("scale");
        cmdLine.add("400x600");
    }

    public static void mixVideo(List<String> videoPathList, String outputPath, Callback callback) {
        ArrayList<String> cmdLine = new ArrayList<>();
        cmdLine.add("ffmpeg");
        for (String audioPath : videoPathList) {
            cmdLine.add("-i");
            cmdLine.add(audioPath);
        }
        setAudio(cmdLine);
        setVideo(cmdLine);
        cmdLine.add("-filter_complex");
        if (videoPathList.size() == 2) {
            cmdLine.add("[0:v]pad=iw*2:ih[int];[int][1:v]overlay=W/2:0[vid]");
        } else if (videoPathList.size() == 3) {
            cmdLine.add("[0:v]pad=iw*2:ih*2[a];[a][1:v]overlay=w[b];[b][2:v]overlay=0:h[vid]");
        } else if (videoPathList.size() == 4) {
            cmdLine.add("[0:v]pad=iw*2:ih*2[a];[a][1:v]overlay=w[b];[b][2:v]overlay=0:h[c];[c][3:v]overlay=w:h[vid]");
        }
        cmdLine.add("-map");
        cmdLine.add("[vid]");
        cmdLine.add(outputPath);
        FFmpeg.getInstance().run(cmdLine, callback);
    }

    // <editor-folder desc="水印相关">

    /**
     * 给视频添加背景音乐。
     *
     * @param input_video
     * @param input_music
     * @param output_path
     * @return
     */
    public native static boolean addVideoBGM(String input_video, String input_music, String output_path);

    /**
     * 获取视频的角度。
     *
     * @param path
     * @return
     */
    public static long getVideoDuration(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        String result = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        try {
            return Long.parseLong(result);
        } catch (Exception ignored) {

        }
        return 0;
    }

    /**
     * 获取视频的宽度信息。
     *
     * @param path
     * @return
     */
    public static int getVideoWidth(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        String result = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        try {
            return Integer.parseInt(result);
        } catch (Exception ignored) {

        }
        return 0;
    }

    /**
     * 获取视频的高度信息。
     *
     * @param path
     * @return
     */
    public static int getVideoHeight(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        String result = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        try {
            return Integer.parseInt(result);
        } catch (Exception ignored) {

        }
        return 0;
    }

    /**
     * 给视频加水印。
     *
     * @see #WATER_MARK_POS_LEFT_UP
     * @see #WATER_MARK_POS_RIGHT_UP
     * @see #WATER_MARK_POS_LEFT_BOTTOM
     * @see #WATER_MARK_POS_RIGHT_BOTTOM
     */
    public static void addWatermark(String srcVideoPath, String watermarkImgPath, int pos,
                                    String outputPath, Callback callback) {
        ArrayList<String> cmdLine = new ArrayList<>();
        cmdLine.add("ffmpeg");
        cmdLine.add("-i");
        cmdLine.add(srcVideoPath);
        setAudio(cmdLine);
        setVideo(cmdLine);
        cmdLine.add("-vf");
        String cmd = "movie=";
        cmd += watermarkImgPath;
        cmd += ",scale= 100: 60[watermask]; [in] [watermask] ";
        switch (pos) {
            case 0:
                cmd += "overlay=10:10";
                break;
            case 1:
                cmd += "overlay=main_w-overlay_w-10:10";
                break;
            case 2:
                cmd += "overlay=0:main_h-overlay_h-10";
                break;
            case 3:
                cmd += "overlay=main_w-overlay_w-10:main_h-overlay_h-10";
                break;
        }
        cmd += " [out]";
        cmdLine.add(cmd);
        cmdLine.add(outputPath);
        FFmpeg.getInstance().run(cmdLine, callback);
    }

    /**
     * 去水印。
     *
     * @param x 去除区域位置
     * @param y 去除区域位置
     * @param w 去除区域宽
     * @param h 去除区域高
     */
    public static void clearWatermark(String srcVideoPath, int x, int y, int w, int h, String outputPath, Callback callback) {
        ArrayList<String> cmdLine = new ArrayList<>();
        cmdLine.add("ffmpeg");
        cmdLine.add("-i");
        cmdLine.add(srcVideoPath);
        setAudio(cmdLine);
        setVideo(cmdLine);
        cmdLine.add("-filter_complex");
        String cmd = "delogo=";
        cmd += "x=" + x;
        cmd += ":";
        cmd += "y=" + y;
        cmd += ":";
        cmd += "w=" + w;
        cmd += ":";
        cmd += "h=" + h;
        cmd += ":show=0";
        cmdLine.add(cmd);
        cmdLine.add(outputPath);
        FFmpeg.getInstance().run(cmdLine, callback);
    }

    // </editor-folder>

    /**
     * 从视频文件中提取音频。
     *
     * @param srcVideoPath
     * @param outputPath
     * @param callback
     */
    public static void demuxAudio(String srcVideoPath, String outputPath, Callback callback) {
        ArrayList<String> cmdLine = new ArrayList<>();
        cmdLine.add("ffmpeg");
        cmdLine.add("-i");
        cmdLine.add(srcVideoPath);
        cmdLine.add("-acodec");
        cmdLine.add("copy");
        cmdLine.add("-vn");
        cmdLine.add(outputPath);
        FFmpeg.getInstance().run(cmdLine, callback);
    }

    /**
     * 从视频文件中提取视频。
     *
     * @param srcVideoPath
     * @param outputPath
     * @param callback
     */
    public static void demuxVideo(String srcVideoPath, String outputPath, Callback callback) {
        ArrayList<String> cmdLine = new ArrayList<>();
        cmdLine.add("ffmpeg");
        cmdLine.add("-i");
        cmdLine.add(srcVideoPath);
        cmdLine.add("-vcodec");
        cmdLine.add("copy");
        cmdLine.add("-an");
        cmdLine.add(outputPath);
        FFmpeg.getInstance().run(cmdLine, callback);
    }

    /**
     * 镜像翻转。
     */
    public static void flipVideo(String srcVideoPath, String outputPath, boolean vertical, Callback callback) {
        ArrayList<String> cmdLine = new ArrayList<>();
        cmdLine.add("ffmpeg");
        cmdLine.add("-i");
        cmdLine.add(srcVideoPath);
        setAudio(cmdLine);
        setVideo(cmdLine);
        cmdLine.add("-vf");
        if (vertical) {
            cmdLine.add("vflip");
        } else {
            cmdLine.add("hflip");
        }
        cmdLine.add(outputPath);
        FFmpeg.getInstance().run(cmdLine, callback);
    }

    /**
     * 视频滤镜。
     */
    public static void filterVideo(String srcVideoPath,
                                   VideoFilter filter,
                                   String outputPath,
                                   Callback callback) {
        ArrayList<String> cmdLine = new ArrayList<>();
        cmdLine.add("ffmpeg");
        cmdLine.add("-i");
        cmdLine.add(srcVideoPath);
        setAudio(cmdLine);
        setVideo(cmdLine);
        cmdLine.add("-vf");
        cmdLine.add(filter.getFilter());
        cmdLine.add(outputPath);
        FFmpeg.getInstance().run(cmdLine, callback);
    }
}
