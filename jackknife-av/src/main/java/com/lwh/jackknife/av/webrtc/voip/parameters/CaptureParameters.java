/*
 * Copyright (C) 2020 The JackKnife Open Source Project
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

package com.lwh.jackknife.av.webrtc.voip.parameters;

public class CaptureParameters {

    private int videoWidth;
    private int videoHeight;
    private int videoFps;

    public CaptureParameters(int videoWidth, int videoHeight, int videoFps) {
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        this.videoFps = videoFps;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    public int getVideoFps() {
        return videoFps;
    }

    public void setVideoFps(int videoFps) {
        this.videoFps = videoFps;
    }
}
