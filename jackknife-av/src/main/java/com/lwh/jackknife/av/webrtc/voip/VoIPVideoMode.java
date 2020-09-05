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
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.lwh.jackknife.av.R;
import com.lwh.jackknife.av.media.SimpleAudioPlayer;
import com.lwh.jackknife.av.webrtc.OnVideoCallEvents;
import com.lwh.jackknife.av.webrtc.voip.parameters.CaptureParameters;
import com.lwh.jackknife.av.webrtc.util.RtcTimer;

import org.webrtc.AudioTrack;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.util.LinkedList;

/**
 * 视频通话模式。
 */
public abstract class VoIPVideoMode implements IVoIPMode, OnVideoCallEvents {

    private static final String TAG = "VideoMode";

    private Context mContext;

    /**
     * 用来循环播放呼叫声音。
     */
    private SimpleAudioPlayer mPlayer;
    private long mStartedTimeMillis;
    private CameraVideoCapturer mCapture;
    private CaptureParameters mCaptureParameters;
    private VoIPBase mBase;
    private Handler mHandler;

    public VoIPVideoMode(Context context, CaptureParameters parameters) {
        this.mContext = context;
        this.mCaptureParameters = parameters;
    }

    public void setCapture(CameraVideoCapturer capture) {
        this.mCapture = capture;
    }

    @Override
    public VoIPSession createSession(VoIPSession.OnSessionTimeUpdateListener listener) {
        VoIPSession session = new VoIPSession(this);
        long time = System.currentTimeMillis();
        session.startTimer(mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case RtcTimer.REFRESH_PROGRESS_EVENT:
                        String formattedTime = RtcTimer.formatTime(
                                System.currentTimeMillis() - time);
                        tickRtcSpentTime(formattedTime);
                        if (session.isAlive() && listener != null) {
                            listener.updateTime(formattedTime);
                        }
                        break;
                }
            }
        });
        session.onSessionCreated(time); //这行代码代码很重要，会影响到生命周期状态，要先进到会话状态，再设置会话开始时间
        session.onSessionForeground();
        return session;
    }

    @Override
    public void closeSession() {
        if (mHandler != null) {
            mHandler.removeMessages(RtcTimer.REFRESH_PROGRESS_EVENT);
        }
    }

    @Override
    public void changeTo(IVoIPMode mode) {
        throw new UnsupportedOperationException("模式本身");
    }

    @Override
    public String getCurrentModeName() {
        return "视频通话";
    }

    @Override
    public boolean canChange() {
        return true;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void setWebRtcBase(VoIPBase base) {
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
    public void sendSessionDescription(boolean isInitiator, SessionDescription sdp) {
        //发送SDP给房间服务器
    }

    @Override
    public void sendCandidate(IceCandidate candidate) {
        //发送ICE令牌给房间服务器
    }

    @Override
    public void sendHangUp() {
        //发送挂断请求给房间服务器
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
    public void sendEndMsg(String msg) {
    }

    @Override
    public void onCallingEvent(int level) {
        if (level == CallingEventLevel.LEVEL_OBJECT_PHONE_NOT_AROUND) {
            Toast.makeText(mContext, "对方手机可能不在身边", Toast.LENGTH_SHORT).show();
        } else if (level == CallingEventLevel.LEVEL_TIME_OUT) {
            onCallHangUp();
        }
    }

    @Override
    public void tickRtcSpentTime(String formattedTime) {
        Log.i(TAG, "视频通话 " + formattedTime);
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
        if (mCapture != null) {
            mCapture.startCapture(mCaptureParameters.getVideoWidth(),
                    mCaptureParameters.getVideoHeight(),
                    mCaptureParameters.getVideoFps());
        }
    }

    @Override
    public void onSessionBackground() {
        if (mCapture != null) {
            try {
                mCapture.stopCapture();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCallHangUp() {
        sendEndMsg(getCurrentModeName() + " " + RtcTimer.formatTime(System.currentTimeMillis()
                - mStartedTimeMillis));
    }

    @Override
    public void onCameraSwitch() {
        if (mCapture != null) {
            mCapture.switchCamera(null);
        }
    }

    @Override
    public void onCaptureFormatChange(int width, int height, int framerate) {
        if (mCapture != null) {
            mCapture.changeCaptureFormat(width, height, framerate);
        }
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
}
