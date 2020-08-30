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

package com.lwh.jackknife.av.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

public class SimpleAudioPlayer {

    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener;
    private Context mContext;

    public SimpleAudioPlayer(Context context) {
        this.mContext = context;
        this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.mOnAudioFocusChangeListener = new SimpleAudioFocusChangeListener();
    }

    /**
     * 请求音频焦点，Android系统会自动管理这些音频的播放与暂停。
     */
    private boolean requestFocus() {
        int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    /**
     * 调用这个方法之前不能调用其他play方法，调用后必须exit才可以调用其他play方法。
     *
     * @param rawId
     */
    public void playByRawId(int rawId) {
        playByRawId(rawId, false);
    }

    public void playByRawId(int rawId, boolean loop) {
        mMediaPlayer = MediaPlayer.create(mContext, rawId);
        if (loop) {
            mMediaPlayer.setLooping(true);
        }
        if (requestFocus()) mMediaPlayer.start();
    }

    /**
     * 用来临时播放在线歌曲。
     */
    public void playByUrl(String url) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
        if (requestFocus()) {
            try {
                mMediaPlayer.stop();
                //防止第二次调用报java.lang.IllegalStateException
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(url);
                mMediaPlayer.prepare();
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
            } catch (IllegalArgumentException | IOException e) {
                mMediaPlayer.release();
            }
        }
    }

    public void replay() {
        if (requestFocus()) {
            if (mMediaPlayer != null) {
                mMediaPlayer.start();
            }
        }
    }

    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    public void exit() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private class SimpleAudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener {

        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    pause();
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    replay();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    pause();
                    break;
            }
        }
    }
}
