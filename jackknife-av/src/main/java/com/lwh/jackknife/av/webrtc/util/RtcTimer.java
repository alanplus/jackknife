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

package com.lwh.jackknife.av.webrtc.util;

import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 每隔0.5秒刷新一次通话时间。
 */
public class RtcTimer {

    public final static int REFRESH_PROGRESS_EVENT = 0x100;
    private static final int INTERVAL_TIME = 500;
    private Handler[] mHandler;
    private Timer mTimer;
    private TimerTask mTimerTask;

    private int what;
    private boolean mTimerStart = false;

    public RtcTimer(Handler... handler) {
        this.mHandler = handler;
        this.what = REFRESH_PROGRESS_EVENT;
        mTimer = new Timer();
    }

    public static String formatTime(long milliSecs) {
        StringBuffer sb = new StringBuffer();
        long m = milliSecs / (60 * 1000);
        sb.append(m < 10 ? "0" + m : m);
        sb.append(":");
        long s = (milliSecs % (60 * 1000)) / 1000;
        sb.append(s < 10 ? "0" + s : s);
        return sb.toString();
    }

    public void startTimer() {
        if (mHandler == null || mTimerStart) {
            return;
        }
        mTimerTask = new RtcTimerTask();
        mTimer.schedule(mTimerTask, INTERVAL_TIME, INTERVAL_TIME);
        mTimerStart = true;
    }

    public void stopTimer() {
        if (!mTimerStart) {
            return;
        }
        mTimerStart = false;
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    class RtcTimerTask extends TimerTask {

        @Override
        public void run() {
            if (mHandler != null) {
                for (Handler handler : mHandler) {
                    Message msg = handler.obtainMessage(what);
                    msg.sendToTarget();
                }
            }
        }
    }
}