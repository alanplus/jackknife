/*
 * Copyright (C) 2017 The JackKnife Open Source Project
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

package com.lwh.jackknife.graphics;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

public class RoundDrawable extends Drawable {

    private Bitmap mBitmap;
    private Paint mPaint;
    private RectF mRectF;

    public RoundDrawable(Bitmap bitmap) {
        this.mBitmap = bitmap;
        this.mPaint = new Paint();
        this.mRectF = new RectF();
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        this.mPaint.setDither(true);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setShader(shader);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        mRectF.set(left, top, right, bottom);
    }

    @Override
    public int getIntrinsicWidth() {
        return mBitmap.getWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mBitmap.getHeight();
    }

    @Override
    public void draw(Canvas canvas) {
        int width = getIntrinsicWidth();
        int height = getIntrinsicHeight();
        int min = Math.min(width, height);
        canvas.drawCircle(width/2, height/2, min/2, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        this.mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter filter) {
        this.mPaint.setColorFilter(filter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
