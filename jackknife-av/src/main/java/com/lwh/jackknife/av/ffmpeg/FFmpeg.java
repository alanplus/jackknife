/*
 * Copyright (C) 2019 The JackKnife Open Source Project
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

package com.lwh.jackknife.av.ffmpeg;

import android.util.Log;

import java.util.List;

public enum FFmpeg {

    instance;

    public static FFmpeg getInstance() {
        return instance;
    }

    private volatile boolean mIsRunning = false;

    boolean isRunning() {
        return mIsRunning;
    }

    FFmpeg() {
//        FFmpegExecutors.executeWork(mProgressRunnable);
    }

    private Callback mCallback;

    public void run(List<String> list, final Callback callback) {
        String[] commands = new String[list.size()];
        list.toArray(commands);
        run(commands, callback);
    }

    private void run(final String[] cmd, final Callback callback) {
        assert cmd != null;
        if (mIsRunning) {
            throw new IllegalStateException("FFmpeg IsRunning");
        }
        mCallback = callback;
        FFmpegExecutors.executeWork(new Runnable() {
            @Override
            public void run() {
                mIsRunning = true;
                int ret = 1;
                try {
                    ret = FFmpegJni.execute(cmd);
                    done(callback, ret != 1);
                } catch (Exception e) {
                    done(callback, ret != 1);
                }
                mIsRunning = false;
            }
        });
    }

    private void done(final Callback callback, final boolean success) {
        if (callback != null) {
            FFmpegExecutors.executeMain(new Runnable() {
                @Override
                public void run() {
                    if (success) {
                        callback.onSuccess();
                    } else {
                        callback.onError();
                    }
                }
            });
        }
    }

    private final Runnable mProgressRunnable = new Runnable() {
        @Override
        public void run() {
            for (; ; ) {
                if (isRunning()) {
                    if (mCallback != null) {
                        String log = FFmpegJni.getLog();
                        Log.d("FFmpeg", log);
                        mCallback.printLog(log);
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
