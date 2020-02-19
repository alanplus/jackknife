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

package com.lwh.jackknife.widget.refresh.footer;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.lwh.jackknife.widget.R;
import com.lwh.jackknife.widget.refresh.api.RefreshFooter;
import com.lwh.jackknife.widget.refresh.api.RefreshLayout;
import com.lwh.jackknife.widget.refresh.constant.SpinnerStyle;
import com.lwh.jackknife.widget.refresh.internal.InternalAbstract;
import com.lwh.jackknife.widget.refresh.util.SmartUtil;

/**
 * 球脉冲底部加载组件
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class BallPulseFooter extends InternalAbstract implements RefreshFooter {

    //<editor-fold desc="属性变量">

    protected boolean mManualNormalColor;
    protected boolean mManualAnimationColor;

    protected Paint mPaint;

    protected int mNormalColor = 0xffeeeeee;
    protected int mAnimatingColor = 0xffe75946;

    protected float mCircleSpacing;

    protected long mStartTime = 0;
    protected boolean mIsStarted = false;
    protected TimeInterpolator mInterpolator = new AccelerateDecelerateInterpolator();
    //</editor-fold>

    //<editor-fold desc="构造方法">
    public BallPulseFooter(Context context) {
        this(context, null);
    }

    public BallPulseFooter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);

        final View thisView = this;
        thisView.setMinimumHeight(SmartUtil.dp2px(60));

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BallPulseFooter);

        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        mSpinnerStyle = SpinnerStyle.Translate;
        mSpinnerStyle = SpinnerStyle.values[ta.getInt(R.styleable.BallPulseFooter_srlClassicsSpinnerStyle, mSpinnerStyle.ordinal)];

        if (ta.hasValue(R.styleable.BallPulseFooter_srlNormalColor)) {
            setNormalColor(ta.getColor(R.styleable.BallPulseFooter_srlNormalColor, 0));
        }
        if (ta.hasValue(R.styleable.BallPulseFooter_srlAnimatingColor)) {
            setAnimatingColor(ta.getColor(R.styleable.BallPulseFooter_srlAnimatingColor, 0));
        }

        ta.recycle();

        mCircleSpacing = SmartUtil.dp2px(4);

    }

    //</editor-fold>

    @Override
    protected void dispatchDraw(Canvas canvas) {
        final View thisView = this;
        final int width = thisView.getWidth();
        final int height = thisView.getHeight();
        float radius = (Math.min(width, height) - mCircleSpacing * 2) / 6;
        float x = width / 2f - (radius * 2 + mCircleSpacing);
        float y = height / 2f;

        final long now = System.currentTimeMillis();

        for (int i = 0; i < 3; i++) {

            long time = now - mStartTime - 120 * (i + 1);
            float percent = time > 0 ? ((time % 750) / 750f) : 0;
            percent = mInterpolator.getInterpolation(percent);

            canvas.save();

            float translateX = x + (radius * 2) * i + mCircleSpacing * i;
            canvas.translate(translateX, y);

            if (percent < 0.5) {
                float scale = 1 - percent * 2 * 0.7f;
                canvas.scale(scale, scale);
            } else {
                float scale = percent * 2 * 0.7f - 0.4f;
                canvas.scale(scale, scale);
            }

            canvas.drawCircle(0, 0, radius, mPaint);
            canvas.restore();
        }

        super.dispatchDraw(canvas);

        if (mIsStarted) {
            thisView.invalidate();
        }
    }


    //<editor-fold desc="刷新方法 - RefreshFooter">
    @Override
    public void onStartAnimator(@NonNull RefreshLayout layout, int height, int maxDragHeight) {
        if (mIsStarted) return;

        final View thisView = this;
        thisView.invalidate();
        mIsStarted = true;
        mStartTime = System.currentTimeMillis();
        mPaint.setColor(mAnimatingColor);
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        mIsStarted = false;
        mStartTime = 0;
        mPaint.setColor(mNormalColor);
        return 0;
    }

    @Override
    @Deprecated
    public void setPrimaryColors(@ColorInt int... colors) {
        if (!mManualAnimationColor && colors.length > 1) {
            setAnimatingColor(colors[0]);
            mManualAnimationColor = false;
        }
        if (!mManualNormalColor) {
            if (colors.length > 1) {
                setNormalColor(colors[1]);
            } else if (colors.length > 0) {
                setNormalColor(ColorUtils.compositeColors(0x99ffffff, colors[0]));
            }
            mManualNormalColor = false;
        }
    }

    //</editor-fold>

    //<editor-fold desc="开放接口 - API">
    public BallPulseFooter setSpinnerStyle(SpinnerStyle mSpinnerStyle) {
        this.mSpinnerStyle = mSpinnerStyle;
        return this;
    }

    public BallPulseFooter setNormalColor(@ColorInt int color) {
        mNormalColor = color;
        mManualNormalColor = true;
        if (!mIsStarted) {
            mPaint.setColor(color);
        }
        return this;
    }

    public BallPulseFooter setAnimatingColor(@ColorInt int color) {
        mAnimatingColor = color;
        mManualAnimationColor = true;
        if (mIsStarted) {
            mPaint.setColor(color);
        }
        return this;
    }
    //</editor-fold>
}