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

package com.lwh.jackknife.widget.refresh.internal;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 * 箭头图像
 */
public class ArrowDrawable extends PaintDrawable {

    private int mWidth = 0;
    private int mHeight = 0;
    private Path mPath = new Path();

    @Override
    public void draw(@NonNull Canvas canvas) {
        final Drawable drawable = ArrowDrawable.this;
        final Rect bounds = drawable.getBounds();
        final int width = bounds.width();
        final int height = bounds.height();
        if (mWidth != width || mHeight != height) {
            int lineWidth = width * 30 / 225;
            mPath.reset();

            float vector1 = (lineWidth * 0.70710678118654752440084436210485f);//Math.sin(Math.PI/4));
            float vector2 = (lineWidth / 0.70710678118654752440084436210485f);//Math.sin(Math.PI/4));
            mPath.moveTo(width / 2f, height);
            mPath.lineTo(0, height / 2f);
            mPath.lineTo(vector1, height / 2f - vector1);
            mPath.lineTo(width / 2f - lineWidth / 2f, height - vector2 - lineWidth / 2f);
            mPath.lineTo(width / 2f - lineWidth / 2f, 0);
            mPath.lineTo(width / 2f + lineWidth / 2f, 0);
            mPath.lineTo(width / 2f + lineWidth / 2f, height - vector2 - lineWidth / 2f);
            mPath.lineTo(width - vector1, height / 2f - vector1);
            mPath.lineTo(width, height / 2f);
            mPath.close();

            mWidth = width;
            mHeight = height;
        }
        canvas.drawPath(mPath, mPaint);
    }
}
