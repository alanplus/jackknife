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

public class FFmpegApi {

    private static final String TAG = "FFmpegApi";

    /*
     * 获取视频信息步骤：
     * 1.open(String url)
     * 2.getXXX()
     * 3.close
     **/
    public static boolean openVideo(String url) {
        int ret = open(url);
        return ret >= 0;
    }

    //ms
    public static long getDuration() {
        return getVideoDuration();
    }

    public static int getWidth() {
        return getVideoWidth();
    }

    public static int getHeight() {
        return getVideoHeight();
    }

    public static double getRotation() {
        return getVideoRotation();
    }

    public static native double getVideoRotation();

    public static native int open(String url);

    private static native long getVideoDuration();

    public static native int getVideoWidth();

    public static native int getVideoHeight();

    public static native String getVideoCodecName();

    public static native void close();

}
