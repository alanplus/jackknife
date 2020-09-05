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
import android.util.Log;

import com.lwh.jackknife.av.webrtc.voip.parameters.RoomParameters;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

/**
 * 封装了呼叫之后和接听之前的过程，使用具体的通话模式来代理具体的功能，如果模式改变，同一功能的具体行为也随之改变。
 */
public class VoIPCall extends VoIPBase {

    private static String TAG = "VoIPCall";

    private static VoIPCall mInstance;

    private static int mStatus;
    private VoIPSession mSession;
    private long mStartedTimeMillis;

    private VoIPCall(IVoIPMode mode, boolean autoConnect) {
        super(mode, autoConnect);
        setWebRtcBase(this);
        mStatus = CREATED;
    }

    private VoIPCall(IVoIPMode mode) {
        super(mode);
        setWebRtcBase(this);
        mStatus = CREATED;
    }

    public static boolean isSessionStatus() {
        return mStatus == SESSION;
    }

    /**
     * 获取单例对象。
     *
     * @return
     */
    public static synchronized VoIPCall getInstance() {
        //如果为空，则会报空指针，自行保证在stop前调用
        return mInstance;
    }

    /**
     * 初次创建需要指定是视频通话还是音频通话模式。
     *
     * @param mode
     * @return
     */
    public static IVoIPCall create(IVoIPMode mode) {
        if (mInstance == null) {
            synchronized (VoIPCall.class) {
                if (mInstance == null) mInstance = new VoIPCall(mode);
            }
        }
        return mInstance;
    }

    public static IVoIPCall create(IVoIPMode mode, boolean autoConnect) {
        if (mInstance == null) {
            synchronized (VoIPCall.class) {
                if (mInstance == null) mInstance = new VoIPCall(mode, autoConnect);
            }
        }
        return mInstance;
    }

    public static int currentStatus() {
        return mStatus;
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
        return mMode.allowCall();
    }

    @Override
    public void playAudioEffect() {
        mMode.playAudioEffect();
    }

    @Override
    public void releaseAudioEffect() {
        mMode.releaseAudioEffect();
    }

    @Override
    public void sendSessionDescription(boolean isInitiator, SessionDescription sdp) {
        mMode.sendSessionDescription(isInitiator, sdp);
    }

    @Override
    public void sendCandidate(IceCandidate candidate) {
        mMode.sendCandidate(candidate);
    }

    /**
     * 结束通话，每次通话后必须调用stop()方法，且stop应在closeSession之后。
     */
    @Override
    public void stop() {
        super.stop();
        mInstance = null;
        mStatus = DESTROYED;
    }

    @Override
    public void sendHangUp() {
        mMode.sendHangUp();
    }

    /**
     * 呼叫或接听网络电话。
     *
     * @param isInitiator true为呼叫方，false为接听方
     * @param parameters  房间相关信息
     */
    @Override
    public void call(boolean isInitiator, RoomParameters parameters) {
        mMode.call(isInitiator, parameters);
        mStartedTimeMillis = System.currentTimeMillis();
        mStatus = PENDING;
    }

    public VoIPSession createSession() {
        return createSession(null);
    }

    @Override
    public VoIPSession createSession(VoIPSession.OnSessionTimeUpdateListener listener) {
        return mSession = mMode.createSession(listener);
    }

    @Override
    public void closeSession() {
        if (mStatus == SESSION) {
            mSession.onSessionClosed();
            mMode.closeSession();
            mStatus = HANG_UP;
            mSession.stopTimer();
            onSessionClosed();
        }
    }

    @Override
    public void reminderMayNotCreateSession(long waitedTimeMillis) {
        if (mStatus == PENDING) {
            mMode.reminderMayNotCreateSession(waitedTimeMillis);
        }
    }

    @Override
    public void sendEndMsg(String msg) {
        // not support
    }

    @Override
    public void onCallingEvent(int level) {
        if (mStatus == PENDING) {
            mMode.onCallingEvent(level);
        }
    }

    @Override
    public void tickRtcSpentTime(String formattedTime) {
        //not support
    }

    @Override
    public void onSessionCreated(long startedTimeMillis) {
        mStartedTimeMillis = startedTimeMillis;
        mStatus = SESSION;
    }

    @Override
    public void onSessionForeground() {
        //not support
    }

    @Override
    public void onSessionBackground() {
        //not support
    }

    @Override
    public void onSessionClosed() {
        mSession = null;
    }

    public long getStartedTimeMillis() {
        return mStartedTimeMillis;
    }

    public VoIPSession getSession() {
        return mSession;
    }
}
