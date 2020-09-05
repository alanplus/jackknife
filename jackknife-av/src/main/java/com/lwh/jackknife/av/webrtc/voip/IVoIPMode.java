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

package com.lwh.jackknife.av.webrtc.voip;

import android.content.Context;

/**
 * 如语音通话模式、视频通话模式。
 */
public interface IVoIPMode extends IVoIPCall, IVoIPSession {

    /**
     * 刚被创建状态。
     */
    int CREATED = 1;

    /**
     * 等待接听状态。
     */
    int PENDING = 2;

    /**
     * 通话中状态。
     */
    int SESSION = 3;

    /**
     * 挂断状态。
     */
    int HANG_UP = 4;

    /**
     * 销毁状态。
     */
    int DESTROYED = 5;

    /**
     * 切换音视频通话模式。
     *
     * @param mode
     */
    void changeTo(IVoIPMode mode);

    /**
     * 获取当前模式名称。
     *
     * @return
     */
    String getCurrentModeName();

    /**
     * 是否可以改变状态。
     *
     * @return
     */
    boolean canChange();

    /**
     * 获取上下文。
     *
     * @return
     */
    Context getContext();

    /**
     * 设置WebRtc业务层基类。
     *
     * @param base
     */
    void setWebRtcBase(VoIPBase base);
}
