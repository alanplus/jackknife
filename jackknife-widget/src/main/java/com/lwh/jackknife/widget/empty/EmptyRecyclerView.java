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

package com.lwh.jackknife.widget.empty;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lwh.jackknife.widget.R;

public class EmptyRecyclerView extends RecyclerView implements IEmptyView {

    private View mEmptyView;
    private boolean mEmpty;
    private OnEmptyChangeListener mOnEmptyChangeListener;

    public EmptyRecyclerView(Context context) {
        this(context, null);
    }

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        if (mEmptyView == null) {
            mEmptyView = createDefaultEmptyView(context);
        }
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EmptyView);
        mEmpty = a.getBoolean(R.styleable.EmptyView_ev_empty, true);
        int layoutId = a.getResourceId(R.styleable.EmptyView_ev_layout, View.NO_ID);
        if (layoutId != View.NO_ID) {
            mEmptyView = LayoutInflater.from(context).inflate(layoutId, null);
        }
        a.recycle();
    }

    private View createDefaultEmptyView(Context context) {
        TextView textView = new TextView(context);
        textView.setText("暂无数据");
        return textView;
    }

    public void setEmpty(boolean empty) {
        this.mEmpty = empty;
        invalidate();
        onEmptyChanged(empty);
    }

    public boolean isEmpty() {
        return mEmpty;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mEmpty) {
            measureChild(mEmptyView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mEmpty) {
            drawChild(canvas, mEmptyView, mEmptyView.getDrawingTime());
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mEmpty) {
            int measuredWidth = mEmptyView.getMeasuredWidth();
            int measuredHeight = mEmptyView.getMeasuredHeight();
            int left = l + (r - l - measuredWidth) / 2;
            int top = t + (t - b - measuredHeight) / 2;
            int right = left + measuredWidth;
            int bottom = top + measuredHeight;
            mEmptyView.layout(left, top, right, bottom);
        }
    }

    @Override
    public void setListener(OnEmptyChangeListener listener) {
        this.mOnEmptyChangeListener = listener;
    }

    @Override
    public void onEmptyChanged(boolean isEmpty) {
        if (mOnEmptyChangeListener != null) {
            mOnEmptyChangeListener.onEmptyChanged(isEmpty);
        }
    }
}
