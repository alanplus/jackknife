/*
 * Copyright (C) 2018 The JackKnife Open Source Project
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

package com.lwh.jackknife.widget.pull;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.lwh.jackknife.widget.pull.state.FooterAnimationState;
import com.lwh.jackknife.widget.pull.state.FooterChangeState;
import com.lwh.jackknife.widget.pull.state.HeaderAnimationState;
import com.lwh.jackknife.widget.pull.state.HeaderChangeState;
import com.lwh.jackknife.widget.pull.state.NormalState;
import com.lwh.jackknife.widget.pull.state.PullState;

public abstract class AbsPullView extends RelativeLayout {

    private OnPullListener mOnPullListener;
    private Callback mCallback;
    private float mDownY;
    private float mLastY;
    public float mPullDownY = 0;
    private float mPullUpY = 0;
    private float mHeaderDist = 200;
    private float mFooterDist = 200;

    private boolean mFirstLayout = true;
    private boolean mTouching = false;
    private float mRatio = 2;
    private int mEvents;
    private boolean mCanPullDown = true;
    private boolean mCanPullUp = true;
    private PullState mState = new NormalState();

    private boolean mPullDownEnable = true;
    private boolean mPullUpEnable = true;
    private View mHeaderView;
    private Pullable mPullableView;
    private View mFooterView;

    protected AbsPullView(Context context) {
        super(context);
        initView();
    }

    protected AbsPullView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    protected AbsPullView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        mHeaderView = createHeaderView();
        mPullableView = createPullableView();
        mFooterView = createFooterView();
        addView(mHeaderView);
        addView((View) mPullableView);
        addView(mFooterView);
    }

    protected abstract View createHeaderView();

    protected abstract Pullable createPullableView();

    protected abstract View createFooterView();

    public Pullable getPullableView() {
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v instanceof Pullable) {
                mPullableView = (Pullable) v;
                return mPullableView;
            }
        }
        return mPullableView;
    }

    public void setHeaderView(View headerView) {
        removeView(mHeaderView);
        mHeaderView = headerView;
        addView(mHeaderView);
    }

    public void setFooterView(View footerView) {
        removeView(mFooterView);
        mFooterView = footerView;
        addView(mFooterView);
    }

    private void changeState(PullState state) {
        mState = state;
        if (mCallback != null) {
            if (mHeaderView != null) {
                mState.onEnter(PullState.TARGET_HEADER_VIEW, mHeaderView, mCallback);
            }
            if (mFooterView != null) {
                mState.onEnter(PullState.TARGET_FOOTER_VIEW, mFooterView, mCallback);
            }
        }
    }

    private void releasePull() {
        mCanPullDown = true;
        mCanPullUp = true;
    }

    public void setPullDownEnable(boolean pullDownEnable)
    {
        mPullDownEnable = pullDownEnable;
    }

    public void setPullUpEnable(boolean pullUpEnable)
    {
        mPullUpEnable = pullUpEnable;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getY();
                mLastY = mDownY;
                mEvents = 0;
                releasePull();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                mEvents = -1;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mEvents == 0) {
                    if (mPullDownY > 0
                            || (mPullableView.canPullDown()
                            && mCanPullDown && mPullDownEnable && mState.getFlag() !=
                            PullState.STATE_FOOTER_START_ANIMATION)) {
                        mPullDownY = mPullDownY + (ev.getY() - mLastY) / mRatio;
                        if (mPullDownY < 0) {
                            mPullDownY = 0;
                            mCanPullDown = false;
                            mCanPullUp = true;
                        }
                        if (mPullDownY > getMeasuredHeight()) {
                            mPullDownY = getMeasuredHeight();
                        }
                        if (mState.getFlag() == PullState.STATE_HEADER_START_ANIMATION) {
                            mTouching = true;
                        }
                    } else if (mPullUpY < 0
                            || mPullableView.canPullUp() && mCanPullUp
                            && mPullUpEnable && mState.getFlag() !=
                            PullState.STATE_HEADER_START_ANIMATION) {
                        mPullUpY = mPullUpY + (ev.getY() - mLastY) / mRatio;
                        if (mPullUpY > 0) {
                            mPullUpY = 0;
                            mCanPullDown = true;
                            mCanPullUp = false;
                        }
                        if (mPullUpY < -getMeasuredHeight())
                            mPullUpY = -getMeasuredHeight();
                        if (mState.getFlag() == PullState.STATE_FOOTER_START_ANIMATION) {
                            mTouching = true;
                        }
                    } else {
                        releasePull();
                    }
                } else {
                    mEvents = 0;
                    mLastY = ev.getY();
                    mRatio = (float) (2 + 2 * Math.tan(Math.PI / 2 / getMeasuredHeight()
                            * (mPullDownY + Math.abs(mPullUpY))));
                }
                if (mPullDownY > 0 || mPullUpY < 0)
                    requestLayout();
                if (mPullDownY > 0) {
                    if (mPullDownY <= mHeaderDist
                            && (mState.getFlag() == PullState.STATE_HEADER_CHANGE || mState.getFlag() == PullState.STATE_DONE)) {
                        changeState(new NormalState());
                    }
                    if (mPullDownY >= mHeaderDist && mState.getFlag() == PullState.STATE_NORMAL) {
                        changeState(new HeaderChangeState());
                    }
                } else if (mPullUpY < 0) {
                    if (-mPullUpY <= mFooterDist
                            && (mState.getFlag() == PullState.STATE_FOOTER_CHANGE || mState.getFlag() == PullState.STATE_DONE)) {
                        changeState(new NormalState());
                    }
                    if (-mPullUpY >= mFooterDist && mState.getFlag() == PullState.STATE_NORMAL) {
                        changeState(new FooterChangeState());
                    }

                }
                if ((mPullDownY + Math.abs(mPullUpY)) > 8) {
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mPullDownY > mHeaderDist || -mPullUpY > mFooterDist) {
                    mTouching = false;
                }
                if (mState.getFlag() == PullState.STATE_HEADER_CHANGE) {
                    changeState(new HeaderAnimationState());
                    if (mOnPullListener != null)
                        mOnPullListener.onPullDown();
                } else if (mState.getFlag() == PullState.STATE_FOOTER_CHANGE) {
                    changeState(new FooterAnimationState());
                    if (mOnPullListener != null)
                        mOnPullListener.onPullUp();
                }
            default:
                break;
        }
        super.dispatchTouchEvent(ev);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mFirstLayout) {
            mFirstLayout = false;
            initView();
            mHeaderView.measure(0, 0);
            mHeaderDist = mHeaderView.getMeasuredHeight();
            mFooterView.measure(0, 0);
            mFooterDist = mFooterView.getMeasuredHeight();
        }
        mHeaderView.layout(0,
                (int) (mPullDownY + mPullUpY) - mHeaderView.getMeasuredHeight(),
                mHeaderView.getMeasuredWidth(), (int) (mPullDownY + mPullUpY));
        View pullableView = (View) mPullableView;
        pullableView.layout(0, (int) (mPullDownY + mPullUpY),
                pullableView.getMeasuredWidth(), (int) (mPullDownY + mPullUpY)
                        + pullableView.getMeasuredHeight());
        mFooterView.layout(0,
                (int) (mPullDownY + mPullUpY) + mHeaderView.getMeasuredHeight(),
                mFooterView.getMeasuredWidth(),
                (int) (mPullDownY + mPullUpY) + mHeaderView.getMeasuredHeight()
                        + mFooterView.getMeasuredHeight());
    }

    public void setOnPullListener(OnPullListener l) {
        this.mOnPullListener = l;
    }

    public boolean isTouching() {
        return mTouching;
    }

    public void addCallback(Callback c) {
        this.mCallback = c;
    }
}
