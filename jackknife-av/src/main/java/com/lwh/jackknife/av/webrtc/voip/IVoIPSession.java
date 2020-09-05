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

/**
 * 抽象接通中的WebRTC会话。
 */
public interface IVoIPSession {

    /**
     * 刷新通话时间。
     *
     * @param formattedTime
     */
    void tickRtcSpentTime(String formattedTime);

    /**
     * 会话创建回调。
     */
    void onSessionCreated(long startedTimeMillis);

    /**
     * 会话关闭回调。
     */
    void onSessionClosed();

    /**
     * 会话前台运行，在Activity的onResume()方法中调用。
     */
    void onSessionForeground();

    /**
     * 会话后台运行，在Activity的onPause()方法中调用。
     */
    void onSessionBackground();

    /**
     * 发送总通话时间给房间服务器。
     *
     * @param msg
     */
    void sendEndMsg(String msg);
}
