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

import com.lwh.jackknife.av.webrtc.voip.parameters.RoomParameters;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

/**
 * 抽象呼叫和接听等业务。
 */
public interface IVoIPCall {

    /**
     * 检测呼叫和接听的环境，如网络是否畅通。
     *
     * @return
     */
    boolean allowCall();

    /**
     * 循环播放呼叫的声音。
     */
    void playAudioEffect();

    /**
     * 停止播放呼叫的声音。
     */
    void releaseAudioEffect();

    /**
     * 向房间服务器发送SDP。
     *
     * @param isInitiator
     * @param sdp
     */
    void sendSessionDescription(boolean isInitiator, SessionDescription sdp);

    /**
     * 向房间服务器发送Candidate。
     */
    void sendCandidate(IceCandidate candidate);

    /**
     * 向房间服务器发送停止呼叫的请求。
     */
    void sendHangUp();

    /**
     * 呼叫或接听，调起界面。
     *
     * @param isInitiator true为呼叫方，false为接听方
     * @param parameters  房间相关信息
     */
    void call(boolean isInitiator, RoomParameters parameters);

    /**
     * 创建会话。
     *
     * @param listener
     * @return
     */
    VoIPSession createSession(VoIPSession.OnSessionTimeUpdateListener listener);

    /**
     * 关闭会话。
     */
    void closeSession();

    /**
     * 未接通电话，已经等待的时间。
     *
     * @param waitedTimeMillis
     */
    void reminderMayNotCreateSession(long waitedTimeMillis);

    /**
     * 未接通电话一定时间的回调事件。
     *
     * @param level
     */
    void onCallingEvent(int level);

    interface CallingEventLevel {

        /**
         * 对方手机可能不在身边。
         */
        int LEVEL_OBJECT_PHONE_NOT_AROUND = 0;

        /**
         * 超时挂断。
         */
        int LEVEL_TIME_OUT = 1;
    }
}
