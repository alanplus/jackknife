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

import com.lwh.jackknife.av.webrtc.interfaces.IWebRtcCall;
import com.lwh.jackknife.av.webrtc.interfaces.IWebRtcSession;
import com.lwh.jackknife.av.webrtc.mode.IWebRtcMode;
import com.lwh.jackknife.av.webrtc.parameters.RoomParameters;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

/**
 * 封装了呼叫之后和接听之前的过程，使用具体的通话模式来代理具体的功能，如果模式改变，同一功能的具体行为也随之改变。
 */
public class WebRtcCall extends WebRtcBase {

    private static final String TAG = "WebRtcCall";

    private static WebRtcCall mInstance;

    private int mStatus;

    /**
     * 在SESSION生命周期可以获取到这个对象。
     */
    private IWebRtcSession mSession;

    private WebRtcCall(IWebRtcMode mode, boolean autoConnect) {
        super(mode, autoConnect);
        setWebRtcBase(this);
        mStatus = CREATED;
    }

    private WebRtcCall(IWebRtcMode mode) {
        super(mode);
        setWebRtcBase(this);
        mStatus = CREATED;
    }

    /**
     * 获取单例对象。
     *
     * @return
     */
    public static synchronized WebRtcCall getInstance() {
        //如果为空，则会报空指针，自行保证在stop前调用
        return mInstance;
    }

    /**
     * 初次创建需要指定是视频通话还是音频通话模式。
     *
     * @param mode
     * @return
     */
    public static IWebRtcCall create(IWebRtcMode mode) {
        if (mInstance == null) {
            synchronized (WebRtcCall.class) {
                if (mInstance == null) {
                    mInstance = new WebRtcCall(mode);
                }
            }
        }
        return mInstance;
    }

    public static IWebRtcCall create(IWebRtcMode mode, boolean autoConnect) {
        if (mInstance == null) {
            synchronized (WebRtcCall.class) {
                if (mInstance == null) mInstance = new WebRtcCall(mode, autoConnect);
            }
        }
        return mInstance;
    }

    /**
     * 结束通话，每次通话周期完成后必须调用stop()方法。
     */
    @Override
    public void stop() {
        super.stop();
        mInstance = null;
        mStatus = DESTROYED;
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
        mStatus = PENDING;
    }

    @Override
    public IWebRtcSession createSession() {
        return mSession = mMode.createSession();
    }

    @Override
    public void closeSession() {
        mMode.closeSession();
        mStatus = HANG_UP;
        onSessionClosed();
    }

    @Override
    public void reminderMayNotCreateSession(long waitedTimeMillis) {
        mMode.reminderMayNotCreateSession(waitedTimeMillis);
    }

    @Override
    public void sendEndMsg(String msg) {
        mMode.sendEndMsg(msg);
    }

    @Override
    public void startTimer(Handler handler) {
        //not support
    }

    @Override
    public void stopTimer() {
        //not support
    }

    @Override
    public void onCallingEvent(int level) {
        if (mStatus == PENDING) {
            mMode.onCallingEvent(level);
        }
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
        //not support
    }

    @Override
    public void onSessionCreated(long startedTimeMillis) {
        setStartedTimeMillis(startedTimeMillis);
        mStatus = SESSION;
    }

    @Override
    public void onSessionClosed() {
        mSession = null;
    }

    @Override
    public void onSessionForeground() {
        //not support
    }

    @Override
    public void onSessionBackground() {
        //not support
    }

    public int getStatus() {
        return mStatus;
    }

    public IWebRtcSession getSession() {
        return mSession;
    }
}
