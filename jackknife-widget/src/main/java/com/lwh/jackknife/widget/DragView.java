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

package com.lwh.jackknife.widget;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

public class DragView implements View.OnTouchListener {
    private Builder mBuilder;

    private int mStatusBarHeight, mScreenWidth, mScreenHeight;

    private int mStartX, mStartY, mLastX, mLastY;
    private boolean mTouchResult = false;

    private DragView(Builder builder) {
        mBuilder = builder;
        initDragView();
    }

    public View getDragView() {
        return mBuilder.view;
    }

    public Activity getActivity() {
        return mBuilder.activity;
    }

    public boolean getNeedNearEdge() {
        return mBuilder.needNearEdge;
    }

    public void setNeedNearEdge(boolean needNearEdge) {
        mBuilder.needNearEdge = needNearEdge;
        if (mBuilder.needNearEdge) {
            moveNearEdge();
        }
    }

    private void initDragView() {
        if (null == getActivity()) {
            throw new NullPointerException("the activity is null");
        }
        if (null == mBuilder.view) {
            throw new NullPointerException("the dragView is null");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mBuilder.activity.isDestroyed()) {
            return;
        }
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        if (null != windowManager) {
            DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
            mScreenWidth = displayMetrics.widthPixels;
            mScreenHeight = displayMetrics.heightPixels;
        }
        Rect frame = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        mStatusBarHeight = frame.top;
        if (mStatusBarHeight <= 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                mStatusBarHeight = getActivity().getResources().getDimensionPixelSize(x);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        int left = mBuilder.needNearEdge ? 0 : mBuilder.defaultLeft;
        FrameLayout.LayoutParams layoutParams = createLayoutParams(left, mStatusBarHeight + mBuilder.defaultTop, 0, 0);
        FrameLayout rootLayout = (FrameLayout) getActivity().getWindow().getDecorView();
        rootLayout.addView(getDragView(), layoutParams);
        getDragView().setOnTouchListener(this);
    }

    private static DragView createDragView(Builder builder) {
        if (null == builder) {
            throw new NullPointerException("the param builder is null when execute method createDragView");
        }
        if (null == builder.activity) {
            throw new NullPointerException("the activity is null");
        }
        if (null == builder.view) {
            throw new NullPointerException("the view is null");
        }
        DragView dragView = new DragView(builder);
        return dragView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchResult = false;
                mStartX = mLastX = (int) event.getRawX();
                mStartY = mLastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int left, top, right, bottom;
                int dx = (int) event.getRawX() - mLastX;
                int dy = (int) event.getRawY() - mLastY;
                left = v.getLeft() + dx;
                if (left < 0) {
                    left = 0;
                }
                right = left + v.getWidth();
                if (right > mScreenWidth) {
                    right = mScreenWidth;
                    left = right - v.getWidth();
                }
                top = v.getTop() + dy;
                if (top < mStatusBarHeight + 2) {
                    top = mStatusBarHeight + 2;
                }
                bottom = top + v.getHeight();
                if (bottom > mScreenHeight) {
                    bottom = mScreenHeight;
                    top = bottom - v.getHeight();
                }
                Log.e("ltrb", left+","+top+","+right+","+bottom);
                v.layout(left, top, right, bottom);
                mLastX = (int) event.getRawX();
                mLastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                v.setLayoutParams(createLayoutParams(v.getLeft(), v.getTop(), 0, 0));
                float endX = event.getRawX();
                float endY = event.getRawY();
                if (Math.abs(endX - mStartX) > 5 || Math.abs(endY - mStartY) > 5) {
                    mTouchResult = true;
                }
                if (mTouchResult && mBuilder.needNearEdge) {
                    moveNearEdge();
                }
                break;
        }
        return mTouchResult;
    }

    private void moveNearEdge() {
        int left = getDragView().getLeft();
        int lastX;

        if (left + getDragView().getWidth() / 2 <= mScreenWidth / 2) {
            lastX = 0;
        } else {
            lastX = mScreenWidth - getDragView().getWidth();
        }
        ValueAnimator valueAnimator = ValueAnimator.ofInt(left, lastX);
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatCount(0);
        valueAnimator.setInterpolator(new BounceInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int left = (int) animation.getAnimatedValue();
                getDragView().setLayoutParams(createLayoutParams(left, getDragView().getTop(), 0, 0));
            }
        });
        valueAnimator.start();
    }

    private FrameLayout.LayoutParams createLayoutParams(int left, int top, int right, int bottom) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(mBuilder.width, mBuilder.height);
        layoutParams.setMargins(left, top, right, bottom);
        return layoutParams;
    }

    public static class Builder {
        private Activity activity;
        private int width = FrameLayout.LayoutParams.WRAP_CONTENT;
        private int height = FrameLayout.LayoutParams.WRAP_CONTENT;
        private int defaultTop = 0;
        private int defaultLeft = 0;
        private boolean needNearEdge = false;
        private View view;

        public Builder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder setSize(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder setDefaultTop(int top) {
            this.defaultTop = top;
            return this;
        }

        public Builder setDefaultLeft(int left) {
            this.defaultLeft = left;
            return this;
        }

        public Builder setNeedNearEdge(boolean needNearEdge) {
            this.needNearEdge = needNearEdge;
            return this;
        }

        public Builder setView(View view) {
            this.view = view;
            return this;
        }

        public DragView build() {
            return createDragView(this);
        }
    }
}
