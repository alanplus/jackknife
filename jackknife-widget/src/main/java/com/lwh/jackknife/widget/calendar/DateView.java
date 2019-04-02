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

package com.lwh.jackknife.widget.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.lwh.jackknife.widget.R;

import java.security.InvalidParameterException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

class DateView extends View {

    public static final String VIEW_PARAMS_HEIGHT = "height";
    public static final String VIEW_PARAMS_MONTH = "month";
    public static final String VIEW_PARAMS_YEAR = "year";
    public static final String VIEW_PARAMS_SELECTED_BEGIN_DAY = "selected_begin_day";
    public static final String VIEW_PARAMS_SELECTED_LAST_DAY = "selected_last_day";
    public static final String VIEW_PARAMS_SELECTED_BEGIN_MONTH = "selected_begin_month";
    public static final String VIEW_PARAMS_SELECTED_LAST_MONTH = "selected_last_month";
    public static final String VIEW_PARAMS_SELECTED_BEGIN_YEAR = "selected_begin_year";
    public static final String VIEW_PARAMS_SELECTED_LAST_YEAR = "selected_last_year";
    public static final String VIEW_PARAMS_WEEK_START = "week_start";

    private static final int SELECTED_CIRCLE_ALPHA = 128;
    protected static int DEFAULT_HEIGHT = 32;
    protected static final int DEFAULT_NUM_ROWS = 6;
    protected static int DAY_SELECTED_CIRCLE_SIZE;
    protected static int DAY_SEPARATOR_WIDTH = 1;
    protected static int MINI_DAY_NUMBER_TEXT_SIZE;
    protected static int MIN_HEIGHT = 10;
    protected static int MONTH_DAY_LABEL_TEXT_SIZE;
    protected static int MONTH_HEADER_SIZE;
    protected static int MONTH_LABEL_TEXT_SIZE;

    protected int mPadding = 0;

    private String mDayOfWeekTypeface;
    private String mMonthTitleTypeface;

    protected Paint mMonthDayLabelPaint;
    protected Paint mMonthNumPaint;
    protected Paint mMonthTitleTextPaint;
    protected Paint mMonthTitlePaint;
    protected Paint mMonthTitleBgPaint;
    protected Paint mSelectedCirclePaint;
    protected int mCurrentDayTextColor;
    protected int mMonthTextColor;
    protected int mDateTextColor;
    protected int mDateNumColor;
    protected int mMonthTitleTextColor;
    protected int mMonthTitleBgColor = 0xfff2f2f2;
    protected int mPreviousDateColor;
    protected int mSelectedDaysColor;

    private final StringBuilder mStringBuilder;

    protected boolean mHasToday = false;
    protected boolean mIsPrev = false;
    private final int INVALID = -1;
    protected int mSelectedBeginDay = INVALID;
    protected int mSelectedLastDay = INVALID;
    protected int mSelectedBeginMonth = INVALID;
    protected int mSelectedLastMonth = INVALID;
    protected int mSelectedBeginYear = INVALID;
    protected int mSelectedLastYear = INVALID;
    protected int mToday = INVALID;

    protected int mWeekStart = 1;
    protected int mNumDays = 7;
    protected int mNumCells = mNumDays;
    private int mDayOfWeekStart = 0;
    protected int mMonth;
    protected Boolean mDrawRect;
    protected int mRowHeight = DEFAULT_HEIGHT;
    protected int mWidth;
    protected int mYear;
    final Time today;

    private final Calendar mCalendar;
    private final Calendar mDayLabelCalendar;
    private final Boolean isPrevDayEnabled;

    private int mNumRows = DEFAULT_NUM_ROWS;

    private DateFormatSymbols mDateFormatSymbols = new DateFormatSymbols();

    private OnDateClickListener mOnDateClickListener;

    private int mFadeColor = 0xffa6c6fd;

    private boolean mNeedMonthDayLabels;

    public DateView(Context context, String startMonth, String endMonth) {
        super(context);

        Resources resources = context.getResources();
        mDayLabelCalendar = Calendar.getInstance();
        mCalendar = Calendar.getInstance();
        today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        mDayOfWeekTypeface = resources.getString(R.string.sans_serif);
        mMonthTitleTypeface = resources.getString(R.string.sans_serif);

        mStringBuilder = new StringBuilder(50);

        MINI_DAY_NUMBER_TEXT_SIZE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, context.getResources().getDisplayMetrics());
        MONTH_LABEL_TEXT_SIZE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, context.getResources().getDisplayMetrics());
        MONTH_DAY_LABEL_TEXT_SIZE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, context.getResources().getDisplayMetrics());
        MONTH_HEADER_SIZE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
        DAY_SELECTED_CIRCLE_SIZE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources().getDisplayMetrics());

        mRowHeight = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 270, context.getResources().getDisplayMetrics()) - MONTH_HEADER_SIZE) / 6;

        isPrevDayEnabled = true;

        initView();

    }

    public void setDrawRect(Boolean isDrawRect) {
        this.mDrawRect = isDrawRect;
    }

    public void setDateTextColor(int color) {
        this.mDateTextColor = color;
    }

    public void setDateNumColor(int color) {
        this.mDateNumColor = color;
    }

    public void setPreviousDateColor(int color) {
        this.mPreviousDateColor = color;
    }

    public void setSelectedDaysColor(int color) {
        this.mSelectedDaysColor = color;
    }

    public void setMonthTitleTextColor(int color) {
        this.mMonthTitleTextColor = color;
    }

    public void setCurrentDayTextColor(int color) {
        this.mCurrentDayTextColor = color;
    }

    public void setMonthTextColor(int color) {
        this.mMonthTextColor = color;
    }

    private int calculateNumRows() {
        int offset = findDayOffset();
        int dividend = (offset + mNumCells) / mNumDays;
        int remainder = (offset + mNumCells) % mNumDays;
        return (dividend + (remainder > 0 ? 1 : 0));
    }

    public void setNeedMonthDayLabels(boolean isNeedMonthDayLabels) {
        this.mNeedMonthDayLabels = isNeedMonthDayLabels;
    }

    private void drawMonthDayLabels(Canvas canvas) {
        if (mNeedMonthDayLabels) {
            int dayWidthHalf = (mWidth - mPadding * 2) / (mNumDays * 2);
            int y = (MONTH_HEADER_SIZE - MONTH_DAY_LABEL_TEXT_SIZE) / 2 + (MONTH_LABEL_TEXT_SIZE / 3);

            for (int i = 0; i < mNumDays; i++) {
                int calendarDay = (i + mWeekStart) % mNumDays;
                int x = (2 * i + 1) * dayWidthHalf + mPadding;
                mDayLabelCalendar.set(Calendar.DAY_OF_WEEK, calendarDay);
                if (i == 0 || i == 6) {
                    mMonthDayLabelPaint.setColor(0xfff4250a);
                } else {
                    mMonthDayLabelPaint.setColor(mDateTextColor);
                }
                canvas.drawText(mDateFormatSymbols.getShortWeekdays()[mDayLabelCalendar
                        .get(Calendar.DAY_OF_WEEK)].toUpperCase(Locale.getDefault())
                        .replace("周", ""), x, y, mMonthDayLabelPaint);
            }
        }
    }

    private void drawMonthTitle(Canvas canvas) {
        canvas.drawRect(new Rect(0, 0, mWidth, MONTH_HEADER_SIZE), mMonthTitleBgPaint);
        int x = (mWidth + 2 * mPadding) / 2;
        int y = MONTH_HEADER_SIZE / 2;
        StringBuilder sb = new StringBuilder(getMonthAndYearString().toLowerCase());
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        canvas.drawText(sb.toString(), x, y, mMonthTitlePaint);
    }

    private int findDayOffset() {
        return (mDayOfWeekStart < mWeekStart ? (mDayOfWeekStart + mNumDays) : mDayOfWeekStart)
                - mWeekStart;
    }

    private String getMonthAndYearString() {
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_NO_MONTH_DAY;
        mStringBuilder.setLength(0);
        long millis = mCalendar.getTimeInMillis();
        return DateUtils.formatDateRange(getContext(), millis, millis, flags);
    }

    private void onDateClick(CalendarDay calendarDay) {
        if (mOnDateClickListener != null && (isPrevDayEnabled || !((calendarDay.month == today.month)
                && (calendarDay.year == today.year) && calendarDay.day < today.monthDay))) {
            mOnDateClickListener.onDateClick(this, calendarDay);
        }
    }

    private boolean sameDay(int monthDay, Time time) {
        return (mYear == time.year) && (mMonth == time.month) && (monthDay == time.monthDay);
    }

    private boolean prevDay(int monthDay, Time time) {
        return ((mYear < time.year)) || (mYear == time.year && mMonth < time.month) || (mMonth == time.month && monthDay < time.monthDay);
    }

    protected void drawMonthNums(Canvas canvas) {
        int y = (mRowHeight + MINI_DAY_NUMBER_TEXT_SIZE) / 2 - DAY_SEPARATOR_WIDTH + MONTH_HEADER_SIZE;
        int paddingDay = (mWidth - 2 * mPadding) / (2 * mNumDays);
        int dayOffset = findDayOffset();
        int day = 1;
        while (day <= mNumCells) {
            int x = paddingDay * (1 + dayOffset * 2) + mPadding;
            if ((mMonth == mSelectedBeginMonth && mSelectedBeginDay == day && mSelectedBeginYear == mYear) || (mMonth == mSelectedLastMonth && mSelectedLastDay == day && mSelectedLastYear == mYear)) {
                mSelectedCirclePaint.setColor(0xff4f8ffc);
                if (mDrawRect) {
                    RectF rectF = new RectF(x - DAY_SELECTED_CIRCLE_SIZE, (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) - DAY_SELECTED_CIRCLE_SIZE, x + DAY_SELECTED_CIRCLE_SIZE, (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) + DAY_SELECTED_CIRCLE_SIZE);
                    canvas.drawRoundRect(rectF, rectF.centerX(), rectF.centerY(), mSelectedCirclePaint);
                } else {
                    canvas.drawCircle(x, y - MINI_DAY_NUMBER_TEXT_SIZE / 3, DAY_SELECTED_CIRCLE_SIZE, mSelectedCirclePaint);
                }
            }
            if (mHasToday && (mToday == day)) {
                mMonthNumPaint.setColor(mCurrentDayTextColor);
                mMonthNumPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            } else {
                mMonthNumPaint.setColor(mDateNumColor);
                mMonthNumPaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }

            if ((mMonth == mSelectedBeginMonth && mSelectedBeginDay == day && mSelectedBeginYear == mYear) || (mMonth == mSelectedLastMonth && mSelectedLastDay == day && mSelectedLastYear == mYear))
                mMonthNumPaint.setColor(mMonthTitleTextColor);

            if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1 && mSelectedBeginYear == mSelectedLastYear &&
                    mSelectedBeginMonth == mSelectedLastMonth &&
                    mSelectedBeginDay == mSelectedLastDay &&
                    day == mSelectedBeginDay &&
                    mMonth == mSelectedBeginMonth &&
                    mYear == mSelectedBeginYear))
                mMonthNumPaint.setColor(mSelectedDaysColor);

            if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1 && mSelectedBeginYear == mSelectedLastYear && mSelectedBeginYear == mYear) &&
                    (((mMonth == mSelectedBeginMonth && mSelectedLastMonth == mSelectedBeginMonth) && ((mSelectedBeginDay < mSelectedLastDay && day > mSelectedBeginDay && day < mSelectedLastDay) || (mSelectedBeginDay > mSelectedLastDay && day < mSelectedBeginDay && day > mSelectedLastDay))) ||
                            ((mSelectedBeginMonth < mSelectedLastMonth && mMonth == mSelectedBeginMonth && day > mSelectedBeginDay) || (mSelectedBeginMonth < mSelectedLastMonth && mMonth == mSelectedLastMonth && day < mSelectedLastDay)) ||
                            ((mSelectedBeginMonth > mSelectedLastMonth && mMonth == mSelectedBeginMonth && day < mSelectedBeginDay) || (mSelectedBeginMonth > mSelectedLastMonth && mMonth == mSelectedLastMonth && day > mSelectedLastDay)))) {
                mMonthNumPaint.setColor(Color.WHITE);
            }
            if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1 && mSelectedBeginYear != mSelectedLastYear && ((mSelectedBeginYear == mYear &&
                    mMonth == mSelectedBeginMonth) || (mSelectedLastYear == mYear && mMonth == mSelectedLastMonth)) &&
                    (((mSelectedBeginMonth < mSelectedLastMonth && mMonth == mSelectedBeginMonth && day < mSelectedBeginDay) || (mSelectedBeginMonth < mSelectedLastMonth && mMonth == mSelectedLastMonth && day > mSelectedLastDay)) ||
                            ((mSelectedBeginMonth > mSelectedLastMonth && mMonth == mSelectedBeginMonth && day > mSelectedBeginDay) || (mSelectedBeginMonth > mSelectedLastMonth && mMonth == mSelectedLastMonth && day < mSelectedLastDay))))) {
                mMonthNumPaint.setColor(mSelectedDaysColor);
            }

            if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1 && mSelectedBeginYear == mSelectedLastYear && mYear == mSelectedBeginYear) &&
                    ((mMonth > mSelectedBeginMonth && mMonth < mSelectedLastMonth && mSelectedBeginMonth < mSelectedLastMonth) ||
                            (mMonth < mSelectedBeginMonth && mMonth > mSelectedLastMonth && mSelectedBeginMonth > mSelectedLastMonth))) {
                mMonthNumPaint.setColor(mSelectedDaysColor);
            }

            if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1 && mSelectedBeginYear != mSelectedLastYear) &&
                    ((mSelectedBeginYear < mSelectedLastYear && ((mMonth > mSelectedBeginMonth && mYear == mSelectedBeginYear) || (mMonth < mSelectedLastMonth && mYear == mSelectedLastYear))) ||
                            (mSelectedBeginYear > mSelectedLastYear && ((mMonth < mSelectedBeginMonth && mYear == mSelectedBeginYear) || (mMonth > mSelectedLastMonth && mYear == mSelectedLastYear))))) {
                mMonthNumPaint.setColor(mSelectedDaysColor);
            }

            if (!isPrevDayEnabled && prevDay(day, today) && today.month == mMonth && today.year == mYear) {
                mMonthNumPaint.setColor(mPreviousDateColor);
                mMonthNumPaint.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
            }

            //相同年的情况
            if ((mYear == mSelectedBeginYear && mYear == mSelectedLastYear) && (mMonth > mSelectedBeginMonth && mMonth < mSelectedLastMonth) ||
                    (mMonth < mSelectedBeginMonth && mMonth > mSelectedLastMonth
                            && mSelectedLastMonth != -1)) {
                //这种情况将覆盖之前的所有情况
                mMonthNumPaint.setColor(Color.WHITE);
            }
            canvas.drawText(String.format("%d", day), x, y, mMonthNumPaint);

            dayOffset++;
            if (dayOffset == mNumDays) {
                dayOffset = 0;
                y += mRowHeight;
            }
            day++;
        }
    }

    private void drawTextEdge(Canvas canvas) {
        int y = (mRowHeight + MINI_DAY_NUMBER_TEXT_SIZE) / 2 - DAY_SEPARATOR_WIDTH + MONTH_HEADER_SIZE;
        int paddingDay = (mWidth - 2 * mPadding) / (2 * mNumDays);
        int dayOffset = findDayOffset();
        int day = 1;
        List<PointF> points = new ArrayList<>();
        PointF firstPoint = null;
        PointF lastPoint = null;
        PointF startPoint;
        PointF endPoint;
        boolean needFirstPoint = false;
        boolean needLastPoint = false;
        while (day <= mNumCells) {
            int x = paddingDay * (1 + dayOffset * 2) + mPadding;
            if (day == 1) {
                firstPoint = new PointF(x, y);
            }
            if (day == mNumCells) {
                lastPoint = new PointF(x, y);
            }
            if (mMonth == mSelectedBeginMonth
                    && mSelectedBeginDay == day
                    && mSelectedBeginYear == mYear) {
                startPoint = new PointF(x, y);
                points.add(startPoint);
                if (mSelectedLastMonth - mSelectedBeginMonth >= 1) {
                    needLastPoint = true;
                }
                if (mSelectedLastMonth != -1 && mSelectedBeginMonth - mSelectedLastMonth >= 1) {
                    needFirstPoint = true;
                }
            }
            if (mMonth == mSelectedLastMonth
                    && mSelectedLastDay == day
                    && mSelectedLastYear == mYear) {
                if (mSelectedLastMonth != -1 && mSelectedBeginMonth - mSelectedLastMonth >= 1) {
                    needLastPoint = true;
                }
                endPoint = new PointF(x, y);
                if (firstPoint != null) {
                    if (mSelectedLastMonth - mSelectedBeginMonth >= 1) {
                        points.add(firstPoint);
                    }
                }
                points.add(endPoint);
            }

            dayOffset++;
            if (dayOffset == mNumDays) {
                dayOffset = 0;
                y += mRowHeight;
            }
            day++;
        }
        if (needLastPoint && lastPoint != null) {
            points.add(1, lastPoint);
        }
        if (needFirstPoint && firstPoint != null) {
            points.add(0, firstPoint);
        }
        if ((mYear == mSelectedBeginYear && mYear == mSelectedLastYear) && (mMonth > mSelectedBeginMonth && mMonth < mSelectedLastMonth) ||
                (mMonth < mSelectedBeginMonth && mMonth > mSelectedLastMonth
                && mSelectedLastMonth != -1)) {
            points.add(firstPoint);
            points.add(lastPoint);
        }
        if (points.size() > 1 && points.size() % 2 == 0) {
            for (int i = 0; i < points.size(); i+=2) {
                drawTextEdgeInternal(points.get(i), points.get(i+1), canvas);
            }
        }
    }

    private void drawTextEdgeInternal(PointF startPoint, PointF endPoint, Canvas canvas) {
        mSelectedCirclePaint.setStrokeWidth(40.0f);
        mSelectedCirclePaint.setColor(mFadeColor);
        if (endPoint.y > startPoint.y) {
            int row = ((int) Math.abs(endPoint.y - startPoint.y) / 55 + 1);
            if (row > 2) {
                canvas.drawLine(startPoint.x, startPoint.y - 9, 660.0f, startPoint.y - 9, mSelectedCirclePaint);
                canvas.drawLine(40, endPoint.y - 9, endPoint.x, endPoint.y - 9, mSelectedCirclePaint);
                for (int i = 1; i < row - 1; i++) {
                    canvas.drawLine(40, startPoint.y - 9 + 55 * i, 660.0f,
                            startPoint.y - 9 + 55 * i, mSelectedCirclePaint);
                }
            } else if (row == 2) {
                canvas.drawLine(startPoint.x, startPoint.y - 9, 660.0f, startPoint.y - 9, mSelectedCirclePaint);
                canvas.drawLine(40, endPoint.y - 9, endPoint.x, endPoint.y - 9, mSelectedCirclePaint);
            }
        } else {
            canvas.drawLine(startPoint.x, startPoint.y - 9, endPoint.x, endPoint.y - 9, mSelectedCirclePaint);
        }
    }

    public CalendarDay getDayFromLocation(float x, float y) {
        int padding = mPadding;
        if ((x < padding) || (x > mWidth - mPadding)) {
            return null;
        }

        int yDay = (int) (y - MONTH_HEADER_SIZE) / mRowHeight;
        int day = 1 + ((int) ((x - padding) * mNumDays / (mWidth - padding - mPadding)) - findDayOffset()) + yDay * mNumDays;

        if (mMonth > 11 || mMonth < 0 || getDaysOfMonth(mYear, mMonth+1) < day || day < 1)
            return null;

        return new CalendarDay(mYear, mMonth, day);
    }

    protected void initView() {
        mMonthTitlePaint = new Paint();
        mMonthTitlePaint.setFakeBoldText(true);
        mMonthTitlePaint.setAntiAlias(true);
        mMonthTitlePaint.setTextSize(MONTH_LABEL_TEXT_SIZE);
        mMonthTitlePaint.setTypeface(Typeface.create(mMonthTitleTypeface, Typeface.BOLD));
        mMonthTitlePaint.setColor(mMonthTextColor);
        mMonthTitlePaint.setTextAlign(Align.CENTER);
        mMonthTitlePaint.setStyle(Style.FILL);

        mMonthTitleBgPaint = new Paint();
        mMonthTitleBgPaint.setAntiAlias(true);
        mMonthTitleBgPaint.setStyle(Style.FILL);
        mMonthTitleBgPaint.setColor(mMonthTitleBgColor);

        mMonthTitleTextPaint = new Paint();
        mMonthTitleTextPaint.setFakeBoldText(true);
        mMonthTitleTextPaint.setAntiAlias(true);
        mMonthTitleTextPaint.setColor(mMonthTitleTextColor);
        mMonthTitleTextPaint.setTextAlign(Align.CENTER);
        mMonthTitleTextPaint.setStyle(Style.FILL);

        mSelectedCirclePaint = new Paint();
        mSelectedCirclePaint.setFakeBoldText(true);
        mSelectedCirclePaint.setAntiAlias(true);
        mSelectedCirclePaint.setColor(mSelectedDaysColor);
        mSelectedCirclePaint.setTextAlign(Align.CENTER);
        mSelectedCirclePaint.setStyle(Style.FILL);
        mSelectedCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        mSelectedCirclePaint.setAlpha(SELECTED_CIRCLE_ALPHA);

        mMonthDayLabelPaint = new Paint();
        mMonthDayLabelPaint.setAntiAlias(true);
        mMonthDayLabelPaint.setTextSize(MONTH_DAY_LABEL_TEXT_SIZE);
        mMonthDayLabelPaint.setColor(mDateTextColor);
        mMonthDayLabelPaint.setTypeface(Typeface.create(mDayOfWeekTypeface, Typeface.NORMAL));
        mMonthDayLabelPaint.setStyle(Style.FILL);
        mMonthDayLabelPaint.setTextAlign(Align.CENTER);
        mMonthDayLabelPaint.setFakeBoldText(true);

        mMonthNumPaint = new Paint();
        mMonthNumPaint.setAntiAlias(true);
        mMonthNumPaint.setTextSize(MINI_DAY_NUMBER_TEXT_SIZE);
        mMonthNumPaint.setStyle(Style.FILL);
        mMonthNumPaint.setTextAlign(Align.CENTER);
        mMonthNumPaint.setFakeBoldText(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawMonthDayLabels(canvas);
        drawMonthTitle(canvas);
        drawTextEdge(canvas);
        drawMonthNums(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mRowHeight * mNumRows + MONTH_HEADER_SIZE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            CalendarDay calendarDay = getDayFromLocation(event.getX(), event.getY());
            if (calendarDay != null) {
                onDateClick(calendarDay);
            }
        }
        return true;
    }

    public void reuse() {
        mNumRows = DEFAULT_NUM_ROWS;
        requestLayout();
    }

    public void setMonthParams(HashMap<String, Integer> params) {
        if (!params.containsKey(VIEW_PARAMS_MONTH) && !params.containsKey(VIEW_PARAMS_YEAR)) {
            throw new InvalidParameterException("You must specify month and year for this view");
        }
        setTag(params);

        if (params.containsKey(VIEW_PARAMS_HEIGHT)) {
            mRowHeight = params.get(VIEW_PARAMS_HEIGHT);
            if (mRowHeight < MIN_HEIGHT) {
                mRowHeight = MIN_HEIGHT;
            }
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_DAY)) {
            mSelectedBeginDay = params.get(VIEW_PARAMS_SELECTED_BEGIN_DAY);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_DAY)) {
            mSelectedLastDay = params.get(VIEW_PARAMS_SELECTED_LAST_DAY);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_MONTH)) {
            mSelectedBeginMonth = params.get(VIEW_PARAMS_SELECTED_BEGIN_MONTH);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_MONTH)) {
            mSelectedLastMonth = params.get(VIEW_PARAMS_SELECTED_LAST_MONTH);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_YEAR)) {
            mSelectedBeginYear = params.get(VIEW_PARAMS_SELECTED_BEGIN_YEAR);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_YEAR)) {
            mSelectedLastYear = params.get(VIEW_PARAMS_SELECTED_LAST_YEAR);
        }

        mMonth = params.get(VIEW_PARAMS_MONTH);
        mYear = params.get(VIEW_PARAMS_YEAR);

        mHasToday = false;
        mToday = -1;

        mCalendar.set(Calendar.MONTH, mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        mDayOfWeekStart = mCalendar.get(Calendar.DAY_OF_WEEK);

        if (params.containsKey(VIEW_PARAMS_WEEK_START)) {
            mWeekStart = params.get(VIEW_PARAMS_WEEK_START);
        } else {
            mWeekStart = mCalendar.getFirstDayOfWeek();
        }

        mNumCells = getDaysOfMonth(mYear, mMonth + 1);
        for (int i = 0; i < mNumCells; i++) {
            final int day = i + 1;
            if (sameDay(day, today)) {
                mHasToday = true;
                mToday = day;
            }

            mIsPrev = prevDay(day, today);
        }

        mNumRows = calculateNumRows();
    }

    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }

    private int getDaysOfMonth(int year, int month) {
        int result;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                result = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                result = 30;
                break;
            default:
                if (isLeapYear(year)) {
                    result = 29;
                }else{
                    result = 28;
                }
                break;
        }
        return result;
    }

    public void setOnDateClickListener(OnDateClickListener l) {
        mOnDateClickListener = l;
    }

    public interface OnDateClickListener {
        void onDateClick(DateView dateView, CalendarDay calendarDay);
    }
}