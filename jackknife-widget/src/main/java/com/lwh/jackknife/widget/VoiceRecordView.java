package com.lwh.jackknife.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class VoiceRecordView extends AppCompatImageView {

    public VoiceRecordView(Context context) {
        this(context, null);
    }

    public VoiceRecordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceRecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageDrawable(getResources().getDrawable(R.drawable.jknf_voice_record));
    }


    /**
     * 开始播放
     */
    public void startPlay() {
        AnimationDrawable animationDrawable = (AnimationDrawable) getDrawable();
        if (animationDrawable != null)
            animationDrawable.start();
    }

    /**
     * 结束播放
     */
    public void stopPlay() {
        AnimationDrawable animationDrawable = (AnimationDrawable) getDrawable();
        if (animationDrawable != null) {
            animationDrawable.stop();
            animationDrawable.selectDrawable(0);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stopPlay();
        super.onDetachedFromWindow();
    }
}
