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
import android.os.Handler;
import android.util.Log;

import com.lwh.jackknife.av.webrtc.voip.parameters.RoomParameters;
import com.lwh.jackknife.av.webrtc.util.RtcTimer;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

/**
 * 音视频通话中的会话封装，使用具体的通话模式来代理具体的功能，如果模式改变，同一功能的具体行为也随之改变。
 */
public class VoIPSession extends VoIPBase {

    private static String TAG = "VoIPSession";
    /**
     * 每隔0.5秒刷新一次通话时间，比每隔1秒刷新更精准，但又不损耗太多性能，0.5秒是较优的做法，一般用于音乐播放器
     * 播放进度和播放时间的刷新。
     */
    private RtcTimer mTimer;
    private long mStartTimedTimeMillis;
    private boolean mAlive;

    public long getStartedTimeMillis() {
        return mStartTimedTimeMillis;
    }

    public VoIPSession(IVoIPMode mode) {
        super(mode, false);
        setWebRtcBase(this);
    }

    public boolean isAlive() {
        return mAlive;
    }

    @Override
    public void onSessionCreated(long startedTimeMillis) {
        mAlive = true;
    }

    @Override
    public void tickRtcSpentTime(String formattedTime) {
        mMode.tickRtcSpentTime(formattedTime);
    }

    @Override
    public void onSessionClosed() {
        mAlive = false;
    }

    @Override
    public void sendHangUp() {
        mMode.sendHangUp();
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
    public void changeTo(IVoIPMode mode) {
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
    public void setWebRtcBase(VoIPBase base) {
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
    public VoIPSession createSession(OnSessionTimeUpdateListener listener) {
        Log.w(TAG, "已处于通话中");
        return this;
    }

    @Override
    public void call(boolean isInitiator, RoomParameters parameters) {
        // not support
    }

    @Override
    public void closeSession() {
        // not support
    }

    /**
     * 开始计时，计时器每隔0.5秒会发送一次REFRESH_PROGRESS_EVENT给handler。
     */
    void startTimer(Handler handler) {
        mStartTimedTimeMillis = System.currentTimeMillis();
        mTimer = new RtcTimer(handler);
        mTimer.startTimer();
    }

    @Override
    public void reminderMayNotCreateSession(long waitedTimeMillis) {
        // not support
    }

    @Override
    public void sendEndMsg(String msg) {
        mMode.sendEndMsg(msg);
    }

    /**
     * 停止计时，调用stop()方法前务必先停止计时器，否则会报空指针。
     */
    void stopTimer() {
        if (mTimer != null) {
            mTimer.stopTimer();
        }
    }

    public interface OnSessionTimeUpdateListener {
        void updateTime(String formattedTime);
    }

    @Override
    public void onCallingEvent(int level) {
        // not support
    }
}
