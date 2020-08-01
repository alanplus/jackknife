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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

/**
 * 右下角的扇形。
 */
public class SectorView extends View {

    private Paint mPaint;
    private static final int SECTOR_COLOR = 0xCCFF0000; //屏幕右下角扇形的颜色
    private RectF mSectorBounds = new RectF();

    public SectorView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(1);
        mPaint.setColor(SECTOR_COLOR);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        float right = 2 * measuredWidth;
        float bottom = 2 * measuredHeight;
        mSectorBounds.set(0, 0, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(mSectorBounds, 180, 90, true, mPaint);
    }
}
