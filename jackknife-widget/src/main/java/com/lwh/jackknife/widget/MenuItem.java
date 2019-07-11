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

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;

public class MenuItem extends View {

    private Paint mIconPaint;
    private TextPaint mTextPaint;
    private String mTitle;
    private float mTitleTextSize;
    private int mTitleTextColor;
    private float mTitleBottomSpan;
    private Drawable mIcon;
    private Rect mTextBounds;
    private DisplayMetrics mDm;
    private int mGravity;
    public static final int GRAVITY_LEFT_TOP = 0x00;
    public static final int GRAVITY_RIGHT_TOP = 0x01;
    public static final int GRAVITY_LEFT_BOTTOM = 0x02;
    public static final int GRAVITY_RIGHT_BOTTOM = 0x03;
    private static final int INVALID = -1;
    private int mWatermarkColor;
    private float mWatermarkWidth;
    private float mWatermarkHeight;
    private int mOffsetX;
    private int mOffsetY;
    private int mWatermarkNum;
    private Rect mDrawableRect;
    private Resources mResources;
    private Drawable mWatermark;
    private boolean mWatermarkEnabled;
    private RectF mWatermarkBounds;
    private Paint mWatermarkPaint;

    public MenuItem(Context context) {
        this(context, null);
    }

    public MenuItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initRect();
        initPaints();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        mDm = getResources().getDisplayMetrics();
        mResources = context.getResources();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MenuItem);
        mTitle = a.getString(R.styleable.MenuItem_mi_title);
        mIcon = a.getDrawable(R.styleable.MenuItem_mi_icon);
        mTitleTextSize = a.getDimensionPixelSize(R.styleable.MenuItem_mi_titleTextSize,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, mDm));
        mTitleTextColor = a.getColor(R.styleable.MenuItem_mi_titleTextColor, Color.BLACK);
        mTitleBottomSpan = a.getDimension(R.styleable.MenuItem_mi_titleBottomSpan,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, mDm));
        mWatermarkEnabled = a.getBoolean(R.styleable.MenuItem_mi_tagEnabled, false);
        mWatermarkColor = a.getColor(R.styleable.MenuItem_mi_tagColor, Color.RED);
        mWatermarkWidth = a.getDimensionPixelSize(R.styleable.MenuItem_mi_tagWidth, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, mDm));
        mWatermarkHeight = a.getDimensionPixelSize(R.styleable.MenuItem_mi_tagHeight, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, mDm));
        int index = a.getIndex(R.styleable.MenuItem_mi_tagGravity);
        switch (index) {
            case GRAVITY_LEFT_TOP:
                mGravity = Gravity.LEFT | Gravity.TOP;
                break;
            case GRAVITY_RIGHT_TOP:
                mGravity = Gravity.RIGHT | Gravity.TOP;
                break;
            case GRAVITY_LEFT_BOTTOM:
                mGravity = Gravity.LEFT | Gravity.BOTTOM;
                break;
            case GRAVITY_RIGHT_BOTTOM:
                mGravity = Gravity.RIGHT | Gravity.BOTTOM;
                break;
        }
        mOffsetX = a.getDimensionPixelOffset(R.styleable.MenuItem_mi_tagXOffset, 0);
        mOffsetY = a.getDimensionPixelOffset(R.styleable.MenuItem_mi_tagYOffset, 0);
        mWatermarkNum = a.getInt(R.styleable.MenuItem_mi_tagNum, 0);
        a.recycle();
    }

    private void initRect() {
        mTextBounds = new Rect();
    }

    private void initPaints() {
        mIconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIconPaint.setDither(true);
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTitleTextSize);
        mTextPaint.setColor(mTitleTextColor);
        mWatermarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWatermarkPaint.setDither(true);
        mWatermarkPaint.setColor(mWatermarkColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        mDrawableRect = new Rect(0, 0, w, h);
        if (mWatermarkEnabled) {
            initWatermark();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawText(canvas);
        drawIcon(canvas);
        if (mWatermarkEnabled) {
            drawWatermark(canvas);
        }
    }

    private void drawWatermark(Canvas canvas) {
        mWatermarkBounds = calcTagPos();
        mWatermarkPaint.setColor(mWatermarkColor);
        canvas.drawOval(mWatermarkBounds, mWatermarkPaint);
        mWatermarkPaint.setColor(Color.WHITE);
        canvas.drawText(String.valueOf(mWatermarkNum), mWatermarkBounds.left, mWatermarkBounds.top, mWatermarkPaint);
    }
//
//    @Override
//    protected void dispatchDraw(Canvas canvas) {
//        super.dispatchDraw(canvas);
//        if (mWatermarkEnabled) {
//            mWatermark.draw(canvas);
//        }
//    }

    private void initWatermark() {
//        mWatermark = new BitmapDrawable(mResources,
//                BitmapFactory.decodeResource(mResources, R.drawable.small_red_dot));
        mWatermarkBounds = calcTagPos();
//        mWatermark.setBounds(mWatermarkBounds);
    }

    private RectF calcTagPos() {
        Rect rect = new Rect(0, 0, (int) mWatermarkWidth, (int) mWatermarkHeight);
        Gravity.apply(mGravity, rect.width(), rect.height(), mDrawableRect, rect);
        rect.offset(mOffsetX, mOffsetY);
        //通过偏移进行微调，但不超过边界
        if (rect.left < 0) {
            rect.offsetTo(0, rect.top);
        }
        if (rect.right > mDrawableRect.width()) {
            rect.offsetTo(mDrawableRect.width() - rect.width(), rect.top);
        }
        if (rect.top < 0) {
            rect.offsetTo(rect.left, 0);
        }
        if (rect.bottom > mDrawableRect.height()) {
            rect.offsetTo(rect.left, mDrawableRect.height() - rect.height());
        }
        return new RectF(rect);
    }

    private void drawText(Canvas canvas) {
        if (mTitle != null && !mTitle.equals("")) {
            mTextPaint.getTextBounds(mTitle, 0, mTitle.length(), mTextBounds);
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            int textWidth = mTextBounds.width();
            int textHeight = mTextBounds.height();
            float topY = getMeasuredHeight() - mTitleBottomSpan - textHeight;
            float baselineY = topY - fontMetrics.top;   //以文字的顶部为绘制文本的基线
            canvas.drawText(mTitle, (getMeasuredWidth() - textWidth) / 2, baselineY, mTextPaint);
        }
    }

    private void drawIcon(Canvas canvas) {
        if (mIcon != null) {
            int measuredHeight = getMeasuredHeight();
            float iconDrawableWidth = getMeasuredWidth();
            float iconDrawableHeight = measuredHeight - mTextBounds.height() - mTitleBottomSpan;
            int iconWidth = mIcon.getIntrinsicWidth();
            int iconHeight = mIcon.getIntrinsicHeight();
            int left = (int) ((iconDrawableWidth - iconWidth) / 2);
            int top = (int) ((iconDrawableHeight - iconHeight) / 2);
            int right = left + iconWidth;
            int bottom = top + iconHeight;
            mIcon.setBounds(left, top, right, bottom);
            mIcon.draw(canvas);
        }
    }

    public void setTitle(String title) {
        this.mTitle = title;
        invalidate();
    }

    public String getTitle() {
        return mTitle;
    }

    public void setWatermarkNum(int num) {
        this.mWatermarkNum = num;
        setWatermarkEnabled(num > 0 ? true : false);
        invalidate();
    }

    public void setWatermarkEnabled(boolean enabled) {
        this.mWatermarkEnabled = enabled;
        invalidate();
    }

    //    static class SavedState extends BaseSavedState {
//
//        private float watermarkWidth;
//        private float watermarkHeight;
//        private int offsetX;
//        private int offsetY;
//        private int gravity;
//
//        public SavedState(Parcelable superState) {
//            super(superState);
//        }
//
//        private SavedState(Parcel in) {
//            super(in);
//            watermarkWidth = in.readFloat();
//            watermarkHeight = in.readFloat();
//            offsetX = in.readInt();
//            offsetY = in.readInt();
//            gravity = in.readInt();
//        }
//
//        @Override
//        public void writeToParcel(Parcel dest, int flags) {
//            super.writeToParcel(dest, flags);
//            dest.writeFloat(watermarkWidth);
//            dest.writeFloat(watermarkHeight);
//            dest.writeInt(offsetX);
//            dest.writeInt(offsetY);
//            dest.writeInt(gravity);
//        }
//
//        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
//            @Override
//            public SavedState createFromParcel(Parcel in) {
//                return new SavedState(in);
//            }
//
//            @Override
//            public SavedState[] newArray(int size) {
//                return new SavedState[size];
//            }
//        };
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Parcelable state) {
//        super.onRestoreInstanceState(state);
//        SavedState savedState = (SavedState) state;
//        mWatermarkWidth = savedState.watermarkWidth;
//        mWatermarkHeight = savedState.watermarkHeight;
//        mOffsetX = savedState.offsetX;
//        mOffsetY = savedState.offsetY;
//        mGravity = savedState.gravity;
//    }
//
//    @Override
//    protected Parcelable onSaveInstanceState() {
//        Parcelable superState = super.onSaveInstanceState();
//        SavedState savedState = new SavedState(superState);
//        savedState.watermarkWidth = mWatermarkWidth;
//        savedState.watermarkHeight = mWatermarkHeight;
//        savedState.offsetX = mOffsetX;
//        savedState.offsetY = mOffsetY;
//        savedState.gravity = mGravity;
//        return savedState;
//    }
}
