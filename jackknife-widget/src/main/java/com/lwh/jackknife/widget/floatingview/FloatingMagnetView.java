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

package com.lwh.jackknife.widget.floatingview;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.lwh.jackknife.widget.R;
import com.lwh.jackknife.widget.util.DensityUtils;
import com.lwh.jackknife.widget.util.GlobalContext;
import com.lwh.jackknife.widget.util.ScreenUtils;
import com.lwh.jackknife.widget.util.StatusBarUtils;

public class FloatingMagnetView extends FrameLayout {

    public static final int MARGIN_EDGE = (int) DensityUtils.dp2px(5, GlobalContext.get());

    private float mOriginalRawX;
    private float mOriginalRawY;
    private float mOriginalX;
    private float mOriginalY;
    protected MagnetViewListener mMagnetViewListener;
    private static final int TOUCH_TIME_THRESHOLD = 150;
    private long mLastTouchDownTime;
    protected MoveAnimator mMoveAnimator;
    protected int mScreenWidth;
    private int mScreenHeight;
    private int mStatusBarHeight;
    private boolean mNearestLeft;
    private SectorView mSectorView;
    private Animation mSectorInAnimation;
    private Paint mMsgRipplePaint;

    public FloatingMagnetView(Context context) {
        this(context, null);
    }

    public FloatingMagnetView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingMagnetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mMsgRipplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mMsgRipplePaint.setStyle(Paint.Style.FILL);
        this.mMsgRipplePaint.setColor(Color.RED);
        mMoveAnimator = new MoveAnimator();
        mStatusBarHeight = StatusBarUtils.getStatusBarHeight(context);
        setClickable(true);
        updateSize();
    }

    public void setMagnetViewListener(MagnetViewListener magnetViewListener) {
        this.mMagnetViewListener = magnetViewListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                changeOriginalTouchParams(event);
                updateSize();
                mMoveAnimator.stop();
                if (mMagnetViewListener != null) {
                    mMagnetViewListener.onDown(this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                updateViewPosition(event);
                break;
            case MotionEvent.ACTION_UP:
                moveToEdge();
                if (isInSectionRegion(event.getRawX(), event.getRawY())) {
                    FloatingWindow.get().remove();
                    if (mMagnetViewListener != null) {
                        mMagnetViewListener.onRemove(this);
                    }
                }
                if (mMagnetViewListener != null) {
                    if (isOnClickEvent()) {
                        if (mSectorView != null) {
                            mSectorView.setVisibility(GONE);
                        }
                        mMagnetViewListener.onClick(this);
                    } else {
                        mMagnetViewListener.onUp(this);
                    }
                }
                break;
        }
        return true;
    }

    protected boolean isOnClickEvent() {
        return System.currentTimeMillis() - mLastTouchDownTime < TOUCH_TIME_THRESHOLD;
    }

    private void updateViewPosition(MotionEvent event) {
        setX(mOriginalX + event.getRawX() - mOriginalRawX);
        // 限制不可超出屏幕高度
        float desY = mOriginalY + event.getRawY() - mOriginalRawY;
        if (desY < mStatusBarHeight) {
            desY = mStatusBarHeight;
        }
        if (desY > mScreenHeight - getHeight()) {
            desY = mScreenHeight - getHeight();
        }
        setY(desY);
        updateViewPosition(mOriginalX + event.getRawX() - mOriginalRawX, desY);
    }

    /**
     * 判断坐标点是否在屏幕右下角的扇形区域内。
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isInSectionRegion(float x, float y) {
        int screenWidth = ScreenUtils.getScreenWidth(getContext());
        int screenHeight = ScreenUtils.getScreenHeight(getContext());
        float dx = screenWidth - x;
        float dy = screenHeight - y;
        double r = Math.sqrt(dx * dx + dy * dy);
        if (mSectorView != null) {
            return r <= mSectorView.getWidth();
        }
        return false;
    }

    public void updateViewPosition(float x, float y) {
        setX(x);
        setY(y);
    }

    private void changeOriginalTouchParams(MotionEvent event) {
        mOriginalX = getX();
        mOriginalY = getY();
        mOriginalRawX = event.getRawX();
        mOriginalRawY = event.getRawY();
        mLastTouchDownTime = System.currentTimeMillis();
    }

    protected void updateSize() {
        mScreenWidth = (ScreenUtils.getScreenWidth(getContext()) - this.getWidth());
        mScreenHeight = ScreenUtils.getScreenHeight(getContext());
    }

    public void moveToEdge() {
        moveToEdge(isNearestLeft());
    }

    public void moveToEdge(boolean isLeft) {
        float moveDistance = isLeft ? MARGIN_EDGE : mScreenWidth - MARGIN_EDGE;
        mMoveAnimator.start(moveDistance, getY());
    }

    private boolean isNearestLeft() {
        int middle = mScreenWidth / 2;
        mNearestLeft = getX() < middle;
        return mNearestLeft;
    }

    protected class MoveAnimator implements Runnable {

        private Handler handler = new Handler(Looper.getMainLooper());
        private float destinationX;
        private float destinationY;
        private long startingTime;

        void start(float x, float y) {
            this.destinationX = x;
            this.destinationY = y;
            startingTime = System.currentTimeMillis();
            handler.post(this);
        }

        @Override
        public void run() {
            if (getRootView() == null || getRootView().getParent() == null) {
                return;
            }
            float progress = Math.min(1, (System.currentTimeMillis() - startingTime) / 400f);
            float deltaX = (destinationX - getX()) * progress;
            float deltaY = (destinationY - getY()) * progress;
            move(deltaX, deltaY);
            if (progress < 1) {
                handler.post(this);
            }
        }

        private void stop() {
            handler.removeCallbacks(this);
        }
    }

    private void move(float deltaX, float deltaY) {
        setX(getX() + deltaX);
        setY(getY() + deltaY);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateSize();
        moveToEdge(mNearestLeft);
    }

    public void addSectionView(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        ViewGroup contentView = decorView.findViewById(android.R.id.content);
        if (contentView.findViewById(R.id.section_view) != null) {
            contentView.removeView(contentView.findViewById(R.id.section_view));
        }
        mSectorView = new SectorView(activity);
        mSectorView.setId(R.id.section_view);
        int dp160 = (int) DensityUtils.dp2px(160, activity);
        LayoutParams params = new LayoutParams(dp160, dp160);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        mSectorView.setLayoutParams(params);
        contentView.addView(mSectorView);
        mSectorInAnimation = AnimationUtils.loadAnimation(activity, R.anim.jknf_sector_in);
        mSectorInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animation.cancel();
                mSectorView.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mSectorView.startAnimation(mSectorInAnimation);
    }

    public void removeSectionView(Activity activity) {
        final View decorView = activity.getWindow().getDecorView();
        final ViewGroup contentView = decorView.findViewById(android.R.id.content);
        final View sectionView = contentView.findViewById(R.id.section_view);
        if (mSectorInAnimation != null) {
            mSectorInAnimation.cancel();
        }
        sectionView.clearAnimation();
        contentView.removeView(sectionView);
    }
}
