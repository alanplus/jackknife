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

package com.lwh.jackknife.av.webrtc.mode;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.lwh.jackknife.av.R;
import com.lwh.jackknife.av.media.SimpleAudioPlayer;
import com.lwh.jackknife.av.webrtc.OnVoiceCallEvents;
import com.lwh.jackknife.av.webrtc.WebRtcBase;
import com.lwh.jackknife.av.webrtc.WebRtcSession;
import com.lwh.jackknife.av.webrtc.interfaces.IWebRtcSession;
import com.lwh.jackknife.av.webrtc.util.RtcTimer;

import org.webrtc.AudioTrack;

import java.util.LinkedList;

/**
 * 语音通话模式。
 */
public abstract class JKVoiceMode implements IWebRtcMode, OnVoiceCallEvents {

    private static final String TAG = "JKVoiceMode";
    private Context mContext;
    /**
     * 用来循环播放呼叫声音。
     */
    private SimpleAudioPlayer mPlayer;

    /**
     * 每一个阶段开始的时间，如WebRtcCall和WebRtcSession的值不是同一概念。
     */
    private long mStartedTimeMillis;

    private WebRtcBase mBase;

    /**
     * 每隔0.5秒刷新一次通话时间，比每隔1秒刷新更精准，但又不损耗太多性能，0.5秒是较优的做法，一般用于音乐播放器
     * 播放进度和播放时间的刷新。
     */
    private RtcTimer mTimer;

    public JKVoiceMode(Context context) {
        this.mContext = context;
    }

    @Override
    public IWebRtcSession createSession() {
        IWebRtcSession session = new WebRtcSession(this);
        long time = System.currentTimeMillis();
        session.setStartedTimeMillis(time);
        session.onSessionCreated(time); //这行代码代码很重要，会影响到生命周期状态
        session.onSessionForeground();
        return session;
    }

    @Override
    public void closeSession() {
        // nothing to do
    }

    @Override
    public void changeTo(IWebRtcMode mode) {
        throw new UnsupportedOperationException("模式本身");
    }

    @Override
    public String getCurrentModeName() {
        return "语音通话";
    }

    @Override
    public boolean canChange() {
        return false;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void setWebRtcBase(WebRtcBase base) {
        this.mBase = base;
    }

    @Override
    public boolean allowCall() {
        return true;
    }

    @Override
    public void playAudioEffect() {
        //创建循环播放呼叫声音的播放器
        mPlayer = new SimpleAudioPlayer(mContext);
        //播放呼叫声音
        mPlayer.playByRawId(R.raw.call, true);
    }

    @Override
    public void releaseAudioEffect() {
        if (mPlayer != null) {
            mPlayer.exit();
        }
    }

    @Override
    public void reminderMayNotCreateSession(long waitedTimeMillis) {
        if (waitedTimeMillis >= 10 * 1000) {    //10秒
            onCallingEvent(CallingEventLevel.LEVEL_OBJECT_PHONE_NOT_AROUND);
        }
        if (waitedTimeMillis >= 60 * 1000) {    //60秒
            onCallingEvent(CallingEventLevel.LEVEL_TIME_OUT);
        }
    }

    @Override
    public void startTimer(Handler handler) {
        setStartedTimeMillis(System.currentTimeMillis());
        mTimer = new RtcTimer(handler);
        mTimer.startTimer();
    }

    @Override
    public void stopTimer() {
        if (mTimer != null) {
            mTimer.stopTimer();
        }
    }

    @Override
    public void onCallingEvent(int level) {
        switch (level) {
            case CallingEventLevel.LEVEL_OBJECT_PHONE_NOT_AROUND:
                Toast.makeText(mContext, "对方手机可能不在身边", Toast.LENGTH_SHORT).show();
                break;
            case CallingEventLevel.LEVEL_TIME_OUT:
                onCallHangUp();
                break;
        }
    }

    @Override
    public long getStartedTimeMillis() {
        return mStartedTimeMillis;
    }

    @Override
    public void setStartedTimeMillis(long timeMillis) {
        this.mStartedTimeMillis = timeMillis;
    }

    @Override
    public void tickRtcSpentTime(String formattedTime) {
        Log.i(TAG, "已通话时间 " + formattedTime);
    }

    @Override
    public void onSessionCreated(long startedTimeMillis) {
        mStartedTimeMillis = startedTimeMillis;
    }

    @Override
    public void onSessionClosed() {
        sendEndMsg(getCurrentModeName() + " " + RtcTimer.formatTime(System.currentTimeMillis()
                - mStartedTimeMillis));
    }

    @Override
    public void onSessionForeground() {
        //nothing to do
    }

    @Override
    public void onSessionBackground() {
        //nothing to do
    }

    @Override
    public boolean onToggleMic() {
        LinkedList<AudioTrack> audioTracks = mBase.getAudioTracks();
        int numsOfTracks = 0;
        for (AudioTrack audioTrack : audioTracks) {
            if (audioTrack.setEnabled(!audioTrack.enabled())) {
                numsOfTracks++;
            }
        }
        return numsOfTracks != 0 && numsOfTracks == audioTracks.size();
    }

    @Override
    public void onCallHangUp() {
        sendEndMsg(getCurrentModeName() + " " + RtcTimer.formatTime(System.currentTimeMillis()
                - mStartedTimeMillis));
    }
}
