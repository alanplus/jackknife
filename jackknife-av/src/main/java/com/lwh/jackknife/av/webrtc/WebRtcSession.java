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

package com.lwh.jackknife.av.webrtc;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.lwh.jackknife.av.webrtc.interfaces.IWebRtcSession;
import com.lwh.jackknife.av.webrtc.mode.IWebRtcMode;
import com.lwh.jackknife.av.webrtc.parameters.RoomParameters;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

/**
 * 音视频通话中的会话封装，使用具体的通话模式来代理具体的功能，如果模式改变，同一功能的具体行为也随之改变。
 */
public class WebRtcSession extends WebRtcBase {

    private static String TAG = "WebRtcSession";

    public WebRtcSession(IWebRtcMode mode) {
        super(mode, false);
        setWebRtcBase(this);
    }

    @Override
    public long getStartedTimeMillis() {
        return mMode.getStartedTimeMillis();
    }

    @Override
    public void setStartedTimeMillis(long timeMillis) {
        mMode.setStartedTimeMillis(timeMillis);
    }

    @Override
    public void tickRtcSpentTime(String formattedTime) {
        mMode.tickRtcSpentTime(formattedTime);
    }

    @Override
    public void onSessionCreated(long startedTimeMillis) {
        // not support
    }

    @Override
    public void onSessionClosed() {
        // not support
    }

    @Override
    public void onSessionForeground() {
        mMode.onSessionForeground();
    }

    @Override
    public void onSessionBackground() {
        mMode.onSessionBackground();
    }

    @Override
    public void changeTo(IWebRtcMode mode) {
        if (mode.getCurrentModeName().equals(mMode.getCurrentModeName())) {
            return;
        }
        if (canChange()) {
            setMode(mode);
        } else {
            Log.d(TAG, "通话模式不可改变");
        }
    }

    @Override
    public String getCurrentModeName() {
        return mMode.getCurrentModeName();
    }

    @Override
    public boolean canChange() {
        return mMode.canChange();
    }

    @Override
    public Context getContext() {
        return mMode.getContext();
    }

    @Override
    public void setWebRtcBase(WebRtcBase base) {
        mMode.setWebRtcBase(base);
    }

    @Override
    public boolean allowCall() {
        return false;
    }

    @Override
    public void playAudioEffect() {
        mMode.playAudioEffect();
    }

    @Override
    public void releaseAudioEffect() {
        mMode.playAudioEffect();
    }

    @Override
    public void sendSessionDescription(boolean isInitiator, SessionDescription sdp) {
        // not support
    }

    @Override
    public void sendCandidate(IceCandidate candidate) {
        // not support
    }

    @Override
    public void sendHangUp() {
        mMode.sendHangUp();
    }

    @Override
    public void call(boolean isInitiator, RoomParameters parameters) {
        // not support
    }

    @Override
    public IWebRtcSession createSession() {
        Log.w(TAG, "已处于通话中");
        return this;
    }

    @Override
    public void closeSession() {
        // not support
    }

    @Override
    public void reminderMayNotCreateSession(long waitedTimeMillis) {
        // not support
    }

    @Override
    public void sendEndMsg(String msg) {
        mMode.sendEndMsg(msg);
    }

    @Override
    public void startTimer(Handler handler) {
        mMode.startTimer(handler);
    }

    @Override
    public void stopTimer() {
        mMode.stopTimer();
    }

    @Override
    public void onCallingEvent(int level) {
        // not support
    }
}
