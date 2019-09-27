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

package com.lwh.jackknife.av.ffmpeg;

import java.util.ArrayList;
import java.util.List;

public class FFmpegCmd {

    private String mInputPath;
    private String mOutputPath;
    private final String FFMPEG = "ffmpeg";
    private List<String> mCmdLine;
    private static FFmpegCmd sInstance;

    private FFmpegCmd(String inputPath, String outputPath) {
        this.mInputPath = inputPath;
        this.mOutputPath = outputPath;
        this.mCmdLine = new ArrayList<>();
        this.mCmdLine.add(FFMPEG);
    }

    public static FFmpegCmd create(String inputPath, String outputPath) {
        if (sInstance == null) {
            synchronized(FFmpegCmd.class) {
                if (sInstance == null) {
                    sInstance = new FFmpegCmd(inputPath, outputPath);
                }
            }
        }
        return sInstance;
    }

    public void start() {
        start(null);
    }

    public void start(final Callback callback) {
        FFmpegExecutors.get().executeWork(new Runnable() {

            @Override
            public void run() {
                List<String> input = new ArrayList<>();
                input.add("-i");
                input.add(mInputPath);
                mCmdLine.addAll(0, input);
                mCmdLine.add(mOutputPath);
                FFmpeg.getInstance().run(mCmdLine, callback);
            }
        });
    }

    public FFmpegCmd map(String map) {
        mCmdLine.add("-map");
        mCmdLine.add(map);
        return this;
    }

    public FFmpegCmd bV(int bv) {
        mCmdLine.add("-b:v");
        mCmdLine.add(String.valueOf(bv));
        return this;
    }

    public FFmpegCmd bA(int ba) {
        mCmdLine.add("-b:a");
        mCmdLine.add(String.valueOf(ba));
        return this;
    }

    public FFmpegCmd cA(String cA) {
        mCmdLine.add("-c:a");
        mCmdLine.add(cA);
        return this;
    }

    public FFmpegCmd cV(String cV) {
        mCmdLine.add("-c:v");
        mCmdLine.add(cV);
        return this;
    }

    public FFmpegCmd f(String format) {
        mCmdLine.add("-f");
        mCmdLine.add(format);
        return this;
    }

    public FFmpegCmd r(int fps) {
        mCmdLine.add("-r");
        mCmdLine.add(String.valueOf(fps));
        return this;
    }

    /**
     * Show license.
     *
     * @return
     */
    public FFmpegCmd L() {
        mCmdLine.add("-L");
        return this;
    }

    /**
     * Show help. An optional parameter may be specified to print help about a specific item. If no argument is specified,
     * only basic (non advanced) tool options are shown.
     *
     * @return
     */
    public FFmpegCmd h() {
        mCmdLine.add("-h");
        return this;
    }

    public FFmpegCmd v() {
        mCmdLine.add("-v");
        return this;
    }

    public FFmpegCmd bufsize(int bufsize) {
        mCmdLine.add("-bufsize");
        mCmdLine.add(String.valueOf(bufsize));
        return this;
    }

    public FFmpegCmd y() {
        mCmdLine.add("-y");
        return this;
    }

    public FFmpegCmd n() {
        mCmdLine.add("-n");
        return this;
    }

    public FFmpegCmd ss(String startTime) {
        mCmdLine.add("-ss");
        mCmdLine.add(startTime);
        return this;
    }

    public FFmpegCmd t(int duration) {
        mCmdLine.add("-t");
        mCmdLine.add(String.valueOf(duration));
        return this;
    }

    public FFmpegCmd to(int position) {
        mCmdLine.add("-to");
        mCmdLine.add(String.valueOf(position));
        return this;
    }
}}