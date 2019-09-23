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
     * 混音。
     *
     * @param srcAudioPath  原音
     * @param audioPathList 目标音
     * @param outputPath    输出目录
     */
    public static void mixAudio(String srcAudioPath, List<String> audioPathList, String outputPath, Callback callback) {
        ArrayList<String> commandList = new ArrayList<>();
        commandList.add("ffmpeg");
        commandList.add("-i");
        commandList.add(srcAudioPath);
        for (String audioPath : audioPathList) {
            commandList.add("-i");
            commandList.add(audioPath);
        }
        commandList.add("-filter_complex");
        commandList.add("amix=inputs=" + (audioPathList.size() + 1) + ":duration=first:dropout_transition=1");
        commandList.add("-f");
        commandList.add("mp3");
        commandList.add("-ac");//声道数
        commandList.add("1");
        commandList.add("-ar"); //采样率
        commandList.add("24k");
        commandList.add("-ab");//比特率
        commandList.add("32k");
        commandList.add("-y");
        commandList.add(outputPath);
        FFmpeg.getInstance().run(commandList, callback);
    }
}
