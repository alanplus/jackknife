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

import com.lwh.jackknife.av.ffmpeg.Callback;
import com.lwh.jackknife.av.ffmpeg.FFmpeg;

import java.util.ArrayList;
import java.util.List;

public class AudioUtils {

    static {
        System.loadLibrary("jknfav");
    }

    public native static int getBitrate(String input_music);

    /**
     * 转码压缩。
     */
    public static void transcodeAndCompress(String srcAudioPath, String outputPath, int samplingRate, int bitrate, Callback callback) {
        ArrayList<String> cmdLine = new ArrayList<>();
        cmdLine.add("ffmpeg");
        cmdLine.add("-i");
        cmdLine.add(srcAudioPath);
        cmdLine.add("-ar"); //采样率
        cmdLine.add(String.valueOf(samplingRate));
        cmdLine.add("-ab");//比特率
        cmdLine.add(String.valueOf(bitrate));
        cmdLine.add("-y");  //覆盖已有文件
        cmdLine.add(outputPath);
        cmdLine.add(String.valueOf(bitrate));
        FFmpeg.getInstance().run(cmdLine, callback);
    }

    /**
     * 混音。
     *
     * @param srcAudioPath  原音
     * @param audioPathList 目标音
     * @param outputPath    输出目录
     */
    public static void mixAudio(String srcAudioPath, List<String> audioPathList, String outputPath, Callback callback) {
        mixAudio(srcAudioPath, audioPathList, outputPath, 24_000, 32_000, callback);
    }

    public static void mixAudio(String srcAudioPath, List<String> audioPathList, String outputPath, int samplingRate, int bitrate, Callback callback) {
        ArrayList<String> cmdLine = new ArrayList<>();
        cmdLine.add("ffmpeg");
        cmdLine.add("-i");
        cmdLine.add(srcAudioPath);
        for (String audioPath : audioPathList) {
            cmdLine.add("-i");
            cmdLine.add(audioPath);
        }
        cmdLine.add("-filter_complex");
        cmdLine.add("amix=inputs=" + (audioPathList.size() + 1) + ":duration=first:dropout_transition=1");
        cmdLine.add("-f");
        cmdLine.add("mp3");
        cmdLine.add("-ac");//声道数
        cmdLine.add("1");
        cmdLine.add("-ar"); //采样率
        cmdLine.add(String.valueOf(samplingRate));
        cmdLine.add("-ab");//比特率
        cmdLine.add(String.valueOf(bitrate));
        cmdLine.add("-y");  //覆盖已有文件
        cmdLine.add(outputPath);
        FFmpeg.getInstance().run(cmdLine, callback);
    }
}
