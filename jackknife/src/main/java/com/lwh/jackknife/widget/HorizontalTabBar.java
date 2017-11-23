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

package com.lwh.jackknife.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lwh.jackknife.R;

import java.util.ArrayList;
import java.util.Locale;

public class HorizontalTabBar extends HorizontalScrollView {

    private float mUnderlineHeight;

    private int mUnderlineColor;

    private float mIndicatorHeight;

    private int mIndicatorColor;

    private float mDividerWidth;

    private int mDividerColor;

    private float mDividerPaddingTop;

    private float mDividerPaddingBottom;

    private int mTabPaddingLeft;

    private int mTabPaddingRight;

    private float mTabTextSize;

    private int mTabTextColor;

    private int mSelectedTabTextColor;

    private int mTabColor;

    private int mTabCount;

    private Adapter mAdapter;

    private int mPosition;

    private float mPositionOffset;

    private float mLastScrollX;

    private boolean mTextAllCaps;

    private boolean mAverage;

    private DisplayMetrics mMetrics;

    private RectF mUnderlineRect;

    private RectF mIndicatorRect;

    private Paint mIndicatorPaint;

    private Paint mUnderlinePaint;

    private Paint mDividerPaint;

    private final int DEFAULT_UNDERLINE_COLOR = 0xFFFFFFFF;

    private final int DEFAULT_INDICATOR_COLOR = 0xFFFFA500;

    private final int DEFAULT_DIVIDER_COLOR = 0xFF2B2B2B;

    private final int DEFAULT_TAB_COLOR = 0x00000000;

    private final int DEFAULT_TAB_TEXT_COLOR = 0xFF2B2B2B;

    private final int DEFAULT_SELECTED_TAB_TEXT_COLOR = 0xFFFFA500;

    private LinearLayout mTabContainer;

    private LinearLayout.LayoutParams mTabContainerLayoutParams =
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

    private LinearLayout.LayoutParams mDefaultTabLayoutParams =
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT);

    private LinearLayout.LayoutParams mAverageTabLayoutParams =
            new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.MATCH_PARENT, 1);

    private OnTabClickListener mOnTabClickListener;

    private ViewPagerObserver mViewPagerObserver = new ViewPagerObserver() {
        @Override
        public void onPageScrolled(int position, float positionOffset) {
            if (position != mPosition || positionOffset != mPositionOffset) {
                mPosition = position;
                mPositionOffset = positionOffset;
                scrollToChild(position, (int) (positionOffset * mTabContainer.getChildAt(position)
                        .getWidth()));
                invalidateView();
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (position != mPosition) {
                mPosition = position;
                invalidateView();
            }
        }
    };

    private Context mContext;

    private Locale mLocale;

    private final String STATE_INSTANCE = "state_instance";

    private final String STATE_CURRENT = "state_current";

    public HorizontalTabBar(Context context) {
        this(context, null);
    }

    public HorizontalTabBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.horizontalTabBarStyle);
    }

    public HorizontalTabBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        this.mMetrics = getResources().getDisplayMetrics();
        this.mLocale = getResources().getConfiguration().locale;
        initAttrs(context, attrs, defStyleAttr);
        initRects();
        initPaints();
        initTabContainer();
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        mAverageTabLayoutParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        mDefaultTabLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        mUnderlineHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, mMetrics);
        mUnderlineColor = DEFAULT_UNDERLINE_COLOR;
        mIndicatorHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, mMetrics);
        mIndicatorColor = DEFAULT_INDICATOR_COLOR;
        mDividerWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, mMetrics);
        mDividerColor = DEFAULT_DIVIDER_COLOR;
        mDividerPaddingTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, mMetrics);
        mDividerPaddingBottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                mMetrics);
        mTabPaddingLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, mMetrics);
        mTabPaddingRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                mMetrics);
        mTabColor = DEFAULT_TAB_COLOR;
        mTabTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, mMetrics);
        mTabTextColor = DEFAULT_TAB_TEXT_COLOR;
        mSelectedTabTextColor = DEFAULT_SELECTED_TAB_TEXT_COLOR;
        mAverage = true;
        mTextAllCaps = true;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HorizontalTabBar,
                defStyleAttr, 0);
        mUnderlineHeight = a.getDimension(
                R.styleable.HorizontalTabBar_horizontaltabbar_underlineHeight, mUnderlineHeight);
        mUnderlineColor = a.getColor(R.styleable.HorizontalTabBar_horizontaltabbar_underlineColor,
                mUnderlineColor);
        mIndicatorHeight = a.getDimension(
                R.styleable.HorizontalTabBar_horizontaltabbar_indicatorHeight, mIndicatorHeight);
        mIndicatorColor = a.getColor(R.styleable.HorizontalTabBar_horizontaltabbar_indicatorColor,
                mIndicatorColor);
        mDividerWidth = a.getDimension(R.styleable.HorizontalTabBar_horizontaltabbar_dividerWidth,
                mDividerWidth);
        mDividerColor = a.getColor(R.styleable.HorizontalTabBar_horizontaltabbar_dividerColor,
                mDividerColor);
        mDividerPaddingTop = a.getDimension(
                R.styleable.HorizontalTabBar_horizontaltabbar_dividerPaddingTop,
                mDividerPaddingTop);
        mDividerPaddingBottom = a.getDimension(
                R.styleable.HorizontalTabBar_horizontaltabbar_dividerPaddingBottom,
                mDividerPaddingBottom);
        mTabPaddingLeft = a.getDimensionPixelOffset(
                R.styleable.HorizontalTabBar_horizontaltabbar_tabPaddingLeft, mTabPaddingLeft);
        mTabPaddingRight = a.getDimensionPixelOffset(
                R.styleable.HorizontalTabBar_horizontaltabbar_tabPaddingRight, mTabPaddingRight);
        mTabColor = a.getColor(R.styleable.HorizontalTabBar_horizontaltabbar_tabColor,
                mTabColor);
        mTabTextSize = a.getDimension(R.styleable.HorizontalTabBar_horizontaltabbar_tabTextSize,
                mTabTextSize);
        mTabTextColor = a.getColor(R.styleable.HorizontalTabBar_horizontaltabbar_tabColor,
                mTabTextColor);
        mSelectedTabTextColor = a.getColor(
                R.styleable.HorizontalTabBar_horizontaltabbar_tabSelectedTextColor,
                mSelectedTabTextColor);
        mAverage = a.getBoolean(R.styleable.HorizontalTabBar_horizontaltabbar_isAverage, mAverage);
        mTextAllCaps = a.getBoolean(R.styleable.HorizontalTabBar_horizontaltabbar_textAllCaps,
                mTextAllCaps);
        a.recycle();
    }

    private void initRects() {
        mUnderlineRect = new RectF();
        mIndicatorRect = new RectF();
    }


    private void initPaints() {
        mUnderlinePaint = new Paint();
        mUnderlinePaint.setAntiAlias(true);
        mUnderlinePaint.setDither(true);
        mUnderlinePaint.setColor(mUnderlineColor);
        mUnderlinePaint.setStyle(Paint.Style.FILL);
        mIndicatorPaint = new Paint();
        mIndicatorPaint.setAntiAlias(true);
        mIndicatorPaint.setDither(true);
        mIndicatorPaint.setColor(mIndicatorColor);
        mIndicatorPaint.setStyle(Paint.Style.FILL);
        mDividerPaint = new Paint();
        mDividerPaint.setAntiAlias(true);
        mDividerPaint.setDither(true);
        mDividerPaint.setColor(mDividerColor);
        mDividerPaint.setStrokeWidth(mDividerWidth);
    }

    private void initTabContainer() {
        mTabContainer = new LinearLayout(mContext);
        mTabContainer.setLayoutParams(mTabContainerLayoutParams);
        mTabContainer.setOrientation(LinearLayout.HORIZONTAL);
        addView(mTabContainer);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getMeasuredHeight();
        float underlineLeft = 0;
        float underlineTop = height - mUnderlineHeight;
        float underlineRight = mTabContainer.getWidth();
        float underlineBottom = height;
        mUnderlineRect.set(underlineLeft, underlineTop, underlineRight, underlineBottom);
        View currentTab = mTabContainer.getChildAt(mPosition);
        float indicatorLeft = currentTab.getLeft();
        float indicatorTop = height - mIndicatorHeight;
        float indicatorRight = currentTab.getRight();
        float indicatorBottom = height;
        if (mPositionOffset > 0.0f && mPosition < mTabCount - 1) {
            View nextTab = mTabContainer.getChildAt(mPosition + 1);
            float nextTabLeft = nextTab.getLeft();
            float nextTabRight = nextTab.getRight();
            indicatorLeft = (mPositionOffset * nextTabLeft + (1.0f - mPositionOffset)
                    * indicatorLeft);
            indicatorRight = (mPositionOffset * nextTabRight + (1.0f - mPositionOffset)
                    * indicatorRight);
        }
        mIndicatorRect.set(indicatorLeft, indicatorTop, indicatorRight, indicatorBottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode() || mTabCount == 0) {
            return;
        }
        drawUnderline(canvas);
        drawIndicator(canvas);
        drawDivider(canvas);
    }

    private void drawUnderline(Canvas canvas) {
        canvas.drawRect(mUnderlineRect, mUnderlinePaint);
    }

    private void drawIndicator(Canvas canvas) {
        canvas.drawRect(mIndicatorRect, mIndicatorPaint);
    }

    private void drawDivider(Canvas canvas) {
        int height = getHeight();
        for (int i=0;i<mTabCount-1;i++) {
            View tab = mTabContainer.getChildAt(i);
            canvas.drawLine(tab.getRight(), mDividerPaddingTop, tab.getRight(),
                    height - mDividerPaddingBottom, mDividerPaint);
        }
    }

    public void setOnTabClickListener(OnTabClickListener l) {
        this.mOnTabClickListener = l;
    }

    public void registerViewPagerObserver(ViewPagerObserver observer) {
        this.mViewPagerObserver = observer;
    }

    public void unregisterViewPagerObserver() {
        this.mViewPagerObserver = null;
    }

    private void addTab(final int position, final View tab) {
        tab.setFocusable(true);
        tab.setPadding(mTabPaddingLeft, 0, mTabPaddingRight, 0);
        tab.setBackgroundColor(mTabColor);
        mTabContainer.addView(tab, position, mAverage ?
                mAverageTabLayoutParams : mDefaultTabLayoutParams);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosition = position;
                if (tab instanceof TextView) {
                    updateTextTab();
                }
                if (mOnTabClickListener != null) {
                    mOnTabClickListener.onClick(v, position);
                }
            }
        });
    }

    public void addTextTab(final int position, String title) {
        TextView tab = new TextView(mContext);
        tab.setText(title);
        tab.setTextSize(mTabTextSize);
        tab.setTextColor(mTabTextColor);
        tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();
        addTab(position, tab);
    }

    public void addIconTab(final int position, int resId) {
        ImageView tab = new ImageView(mContext);
        tab.setImageResource(resId);
        addTab(position, tab);
    }

    public void addIconTab(final int position, Drawable drawable) {
        ImageView tab = new ImageView(mContext);
        tab.setImageDrawable(drawable);
        addTab(position, tab);
    }

    public void addIconTab(final int position, Bitmap bitmap) {
        ImageView tab = new ImageView(mContext);
        tab.setImageBitmap(bitmap);
        addTab(position, tab);
    }

    public void addIconTab(final int position, Uri uri) {
        ImageView tab = new ImageView(mContext);
        tab.setImageURI(uri);
        addTab(position, tab);
    }

    private void updateTextTab() {
        if (mTabCount > 0) {
            for (int i = 0; i < mTabCount; i++) {
                View tab = mTabContainer.getChildAt(i);
                if (tab instanceof TextView) {
                    TextView textTab = (TextView) tab;
                    textTab.setTextSize(mTabTextSize);
                    textTab.setTextColor(mTabTextColor);
                    if (mTextAllCaps) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            textTab.setAllCaps(true);
                        } else {
                            String title = textTab.getText().toString();
                            textTab.setText(title.toUpperCase(mLocale));
                        }
                    }
                    if (i == mPosition) {
                        textTab.setTextColor(mSelectedTabTextColor);
                    }
                }
            }
            invalidateView();
        }
    }

    public static class Adapter extends DataSetObservable {

        private ArrayList<String> mTabTitles;

        private final DataSetObservable mDataSetObservable = new DataSetObservable();

        public Adapter(String[] titles) {
            this.mTabTitles = new ArrayList<>();
            for (String title : titles) {
                mTabTitles.add(title);
            }
        }

        public Adapter(ArrayList<String> titles) {
            this.mTabTitles = titles;
        }

        public void registerDataSetObserver(DataSetObserver observer) {
            mDataSetObservable.registerObserver(observer);
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            mDataSetObservable.unregisterObserver(observer);
        }

        public ArrayList<String> getTitles() {
            return mTabTitles;
        }

        public void addTitle(String title) {
            this.mTabTitles.add(title);
            notifyDataSetChanged();
        }

        public void addTitle(ArrayList<String> titles) {
            this.mTabTitles.addAll(titles);
            notifyDataSetChanged();
        }

        public void addTitle(String[] titles) {
            for (String title : titles) {
                this.mTabTitles.add(title);
            }
            notifyDataSetChanged();
        }

        public void removeTitle(int position) {
            this.mTabTitles.remove(position);
            notifyDataSetChanged();
        }

        public void replaceTitle(int position, String title) {
            this.mTabTitles.set(position, title);
            notifyDataSetChanged();
        }

        public void clearTitle() {
            this.mTabTitles.clear();
            notifyDataSetInvalidated();
        }

        public int getCount() {
            return mTabTitles.size();
        }

        public String getTitleAt(int position) {
            return mTabTitles.get(position);
        }

        /**
         * Notifies the attached observers that the underlying data has been changed
         * and any View reflecting the data set should refresh itself.
         */
        public void notifyDataSetChanged() {
            mDataSetObservable.notifyChanged();
        }

        /**
         * Notifies the attached observers that the underlying data is no longer valid
         * or available. Once invoked this adapter is no longer valid and should
         * not report further data set changes.
         */
        public void notifyDataSetInvalidated() {
            mDataSetObservable.notifyInvalidated();
        }
    }

    public Adapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(Adapter adapter) {
        this.mAdapter = adapter;
    }

    public float getUnderlineHeight() {
        return mUnderlineHeight;
    }

    public void setUnderlineHeight(float height) {
        if (height != mUnderlineHeight) {
            this.mUnderlineHeight = height;
            invalidateView();
        }
    }

    public int getUnderlineColor() {
        return mUnderlineColor;
    }

    public void setUnderlineColor(int color) {
        if (color != mUnderlineColor) {
            this.mUnderlineColor = color;
            invalidateView();
        }
    }

    public float getIndicatorHeight() {
        return mIndicatorHeight;
    }

    public void setIndicatorHeight(float height) {
        if (height != mIndicatorHeight) {
            this.mIndicatorHeight = height;
            invalidateView();
        }
    }

    public int getIndicatorColor() {
        return mIndicatorColor;
    }

    public void setIndicatorColor(int color) {
        if (color != mIndicatorColor) {
            this.mIndicatorColor = color;
            invalidateView();
        }
    }

    public float getDividerWidth() {
        return mDividerWidth;
    }

    public void setDividerWidth(float width) {
        if (width != mDividerWidth) {
            this.mDividerWidth = width;
        }
    }

    public int getDividerColor() {
        return mDividerColor;
    }

    public void setDividerColor(int color) {
        if (color != mDividerColor) {
            this.mDividerColor = color;
            invalidateView();
        }
    }

    public float getDividerPaddingTop() {
        return mDividerPaddingTop;
    }

    public void setDividerPaddingTop(float padding) {
        if (padding != mDividerPaddingTop) {
            this.mDividerPaddingTop = padding;
            invalidateView();
        }
    }

    public float getDividerPaddingBottom() {
        return mDividerPaddingBottom;
    }

    public void setDividerPaddingBottom(float padding) {
        if (padding != mDividerPaddingBottom) {
            this.mDividerPaddingBottom = padding;
            invalidateView();
        }
    }

    public int getTabPaddingLeft() {
        return mTabPaddingLeft;
    }

    public void setTabPaddingLeft(int padding) {
        if (padding != mTabPaddingLeft) {
            this.mTabPaddingLeft = padding;
            invalidateView();
        }
    }

    public int getTabPaddingRight() {
        return mTabPaddingRight;
    }

    public void setTabPaddingRight(int padding) {
        if (padding != mTabPaddingRight) {
            this.mTabPaddingRight = padding;
            invalidateView();
        }
    }

    public float getTabTextSize() {
        return mTabTextSize;
    }

    public void setTabTextSize(float size) {
        if (size != mTabTextSize) {
            this.mTabTextSize = size;
            updateTextTab();
        }
    }

    public int getTabTextColor() {
        return mTabTextColor;
    }

    public void setTabTextColor(int color) {
        if (color != mTabTextColor) {
            this.mTabTextColor = color;
            updateTextTab();
        }
    }

    public int getSelectedTabTextColor() {
        return mSelectedTabTextColor;
    }

    public void setSelectedTabTextColor(int color) {
        if (color != mSelectedTabTextColor) {
            this.mSelectedTabTextColor = color;
            updateTextTab();
        }
    }

    public int getTabColor() {
        return mTabColor;
    }

    public void setTabColor(int color) {
        if (color != mTabColor) {
            this.mTabColor = color;
            invalidateView();
        }
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        if (position != mPosition) {
            this.mPosition = position;
            invalidateView();
        }
    }

    public float getPositionOffset() {
        return mPositionOffset;
    }

    public void setPositionOffset(float offset) {
        if (offset != mPositionOffset) {
            this.mPositionOffset = offset;
            invalidateView();
        }
    }

    public boolean isTextAllCaps() {
        return mTextAllCaps;
    }

    public void setTextAllCaps(boolean textAllCaps) {
        if (textAllCaps != mTextAllCaps) {
            this.mTextAllCaps = textAllCaps;
            updateTextTab();
        }
    }

    public boolean isAverage() {
        return mAverage;
    }

    public void setAverage(boolean average) {
        if (average != mAverage) {
            this.mAverage = average;
            invalidateView();
        }
    }

    public int getTabCount() {
        return mTabCount;
    }

    public ViewPagerObserver getViewPagerObserver() {
        return mViewPagerObserver;
    }

    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    public void scrollToChild(int position, float offset) {
        if (mTabCount == 0) {
            return;
        }
        int width = getWidth();
        View tab = mTabContainer.getChildAt(position);
        int tabWidth = tab.getWidth();
        int tabLeft = tab.getLeft();
        int scrollOffset = (width-tabWidth)/2;
        float scrollX = tabLeft + offset;
        if (position > 0 || offset > 0) {
            scrollX -= scrollOffset;
        }
        if (scrollX != mLastScrollX) {
            mLastScrollX = scrollX;
            if (mViewPagerObserver != null) {
                mViewPagerObserver.onPageScrolled(position, offset);
                mViewPagerObserver.onPageSelected(position);
            }
            smoothScrollTo((int) scrollX, 0);
        }
    }

    public interface OnTabClickListener {
        void onClick(View view, int position);
    }

    public interface ViewPagerObserver {
        void onPageScrolled(int position, float positionOffset);
        void onPageSelected(int position);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_INSTANCE, super.onSaveInstanceState());
        bundle.putInt(STATE_CURRENT, mPosition);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof Bundle) {
            Bundle bundle = (Bundle) parcelable;
            mPosition = bundle.getInt(STATE_CURRENT);
            super.onRestoreInstanceState(bundle.getParcelable(STATE_INSTANCE));
        } else {
            super.onRestoreInstanceState(parcelable);
        }
    }
}
