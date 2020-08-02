package com.lwh.jackknife.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import androidx.annotation.RequiresApi;

import static android.animation.ValueAnimator.INFINITE;
import static android.animation.ValueAnimator.RESTART;

@RequiresApi(Build.VERSION_CODES.KITKAT)
public class RotateCoverView extends CircleTextImageView {

    private ObjectAnimator rotateAnimator;
    private boolean firstRotate = true;

    public RotateCoverView(Context context) {
        super(context);
    }

    public RotateCoverView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateCoverView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void start(boolean smart) {
        if (smart) {
            if (firstRotate) {
                firstRotate = false;
                start();
            } else {
                resume();
            }
        } else {
            start();
        }
    }

    public void start() {
        rotateAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f);
        rotateAnimator.setInterpolator(new LinearInterpolator());
        rotateAnimator.setDuration(20000);
        rotateAnimator.setRepeatMode(RESTART);
        rotateAnimator.setRepeatCount(INFINITE);
        setBorderColorResource(R.color.black);
        setBorderWidth(5);
        setImageResource(R.drawable.jknf_doramusic_logo);
        rotateAnimator.setupStartValues();
        rotateAnimator.start();
    }

    public void stop() {
        if (!firstRotate) {
            rotateAnimator.cancel();
        }
    }

    public void pause() {
        if (!firstRotate) {
            rotateAnimator.pause();
        }
    }

    public void resume() {
        if (!firstRotate) {
            rotateAnimator.resume();
        }
    }
}
