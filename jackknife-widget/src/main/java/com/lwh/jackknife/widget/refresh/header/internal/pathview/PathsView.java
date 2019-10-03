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

package com.lwh.jackknife.widget.refresh.header.internal.pathview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 路径视图
 */
public class PathsView extends View {

    protected PathsDrawable mPathsDrawable;

    public PathsView(Context context) {
        this(context, null);
    }

    public PathsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPathsDrawable = new PathsDrawable();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final View thisView = this;
        if (thisView.getTag() instanceof String) {
            parserPaths(thisView.getTag().toString());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final View thisView = this;
        final Drawable drawable = mPathsDrawable;
        super.setMeasuredDimension(
                View.resolveSize(drawable.getBounds().width() + thisView.getPaddingLeft() + thisView.getPaddingRight(), widthMeasureSpec),
                View.resolveSize(drawable.getBounds().height() + thisView.getPaddingTop() + thisView.getPaddingBottom(), heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final View thisView = this;
        super.onLayout(changed, left, top, right, bottom);
        mPathsDrawable.setBounds(thisView.getPaddingLeft(), thisView.getPaddingTop(),
                Math.max((right - left) - thisView.getPaddingRight(), thisView.getPaddingLeft()),
                Math.max((bottom - top) - thisView.getPaddingTop(), thisView.getPaddingTop()));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPathsDrawable.draw(canvas);
    }

    public boolean parserPaths(String... paths) {
        return mPathsDrawable.parserPaths(paths);
    }

    public void parserColors(int... colors) {
        mPathsDrawable.parserColors(colors);
    }


}
