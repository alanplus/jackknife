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
import android.text.TextPaint;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.lwh.jackknife.widget.R;

import java.security.InvalidParameterException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

class CalendarView extends View {

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
    protected static final int ROW_NUMS = 6;
    protected static int DAY_SELECTED_CIRCLE_SIZE;
    protected static int DAY_SEPARATOR_WIDTH = 1;
    protected static int MINI_DAY_NUMBER_TEXT_SIZE;
    protected static int MIN_HEIGHT = 10;
    protected static int MONTH_DAY_LABEL_TEXT_SIZE;
    protected static int MONTH_HEADER_SIZE;
    protected static int MONTH_LABEL_TEXT_SIZE;
    private DisplayMetrics mDisplayMetrics;

    protected int mPadding = 0;

    private String mDayOfWeekTypeface;
    private String mMonthHeaderTypeface;

    protected TextPaint mMonthDayLabelPaint;
    protected TextPaint mMonthDatePaint;
    protected Paint mMonthHeaderBgPaint;
    protected TextPaint mMonthHeaderTextPaint;
    protected Paint mSelectedMarkerPaint;
    protected Paint mDateEdgePaint;
    protected int mCurrentDayTextColor;
    protected int mMonthTextColor = 0xff000000;
    protected int mDateTextColor;
    protected int mDateNumColor;
    protected int mMonthTitleTextColor;
    protected int mMonthTitleBgColor = 0xfff2f2f2;
    protected int mPreviousDateColor;
    protected int mSelectedDaysColor;

    private StringBuilder mStringBuilder;

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
    protected int ROW_DAYS = 7;
    protected int mDateNums = ROW_DAYS;
    private int mDayOfWeekStart = 0;
    protected int mMonth;
    protected Boolean mDrawRect;
    protected int mRowHeight;
    protected int mWidth;
    protected int mYear;
    Time mTime;

    private Calendar mCalendar;
    private Calendar mDayLabelCalendar;
    private Boolean isPrevDayEnabled;

    private CalendarDay mStartDay;
    private CalendarDay mEndDay;

    /**
     * 行数。
     */
    private int mRowNums = ROW_NUMS;

    private DateFormatSymbols mDateFormatSymbols;

    private OnDateClickListener mOnDateClickListener;
    private Resources mResources;
    private int mEdgeColor = 0xffa6c6fd;

    private boolean mNeedMonthDayLabels = true;

    public CalendarView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mResources = context.getResources();
        mDayLabelCalendar = Calendar.getInstance();
        mCalendar = Calendar.getInstance();
        mTime = new Time(Time.getCurrentTimezone());
        mTime.setToNow();
        mDayOfWeekTypeface = mResources.getString(R.string.sans_serif);
        mMonthHeaderTypeface = mResources.getString(R.string.sans_serif);

        mStringBuilder = new StringBuilder(50);
        mDisplayMetrics = mResources.getDisplayMetrics();
        MINI_DAY_NUMBER_TEXT_SIZE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, mDisplayMetrics);
        MONTH_LABEL_TEXT_SIZE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, mDisplayMetrics);
        MONTH_DAY_LABEL_TEXT_SIZE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, mDisplayMetrics);
        MONTH_HEADER_SIZE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, mDisplayMetrics);
        DAY_SELECTED_CIRCLE_SIZE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, mDisplayMetrics);
        mRowHeight = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 270, mDisplayMetrics) - MONTH_HEADER_SIZE) / 6;
        isPrevDayEnabled = true;
        initPaints();
    }

    public void setStartDay(CalendarDay startDay) {
        this.mStartDay = startDay;
    }

    public void setEndDay(CalendarDay endDay) {
        this.mEndDay = endDay;
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

    private int calculateRowNums() {
        int offset = calculateDayOffset();
        int dividend = (offset + mDateNums) / ROW_DAYS;
        int remainder = (offset + mDateNums) % ROW_DAYS;
        return (dividend + (remainder > 0 ? 1 : 0));
    }

    public void setNeedMonthDayLabels(boolean isNeedMonthDayLabels) {
        this.mNeedMonthDayLabels = isNeedMonthDayLabels;
    }

    private int calculateDayOffset() {
        return (mDayOfWeekStart < mWeekStart ? (mDayOfWeekStart + ROW_DAYS) : mDayOfWeekStart)
                - mWeekStart;
    }

    private String getMonthAndYearString() {
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_NO_MONTH_DAY;
        mStringBuilder.setLength(0);
        long millis = mCalendar.getTimeInMillis();
        return DateUtils.formatDateRange(getContext(), millis, millis, flags);
    }

    private void onDateClick(CalendarDay calendarDay) {
        if (mOnDateClickListener != null && (isPrevDayEnabled || !((calendarDay.month == mTime.month)
                && (calendarDay.year == mTime.year) && calendarDay.day < mTime.monthDay))) {
            mOnDateClickListener.onDateClick(this, calendarDay);
        }
    }

    private boolean checkSameDay(int monthDay, Time time) {
        return (mYear == time.year) && (mMonth == time.month) && (monthDay == time.monthDay);
    }

    private boolean checkPrevDay(int monthDay, Time time) {
        return ((mYear < time.year)) || (mYear == time.year && mMonth < time.month) ||
                (mMonth == time.month && monthDay < time.monthDay);
    }

    private void drawMonthDayLabels(Canvas canvas) {
        if (mNeedMonthDayLabels) {
            int dayWidthHalf = (mWidth - mPadding * 2) / (ROW_DAYS * 2);
            int y = (MONTH_HEADER_SIZE - MONTH_DAY_LABEL_TEXT_SIZE) / 2 + (MONTH_LABEL_TEXT_SIZE / 3);
            mDateFormatSymbols = new DateFormatSymbols();
            for (int i = 0; i < ROW_DAYS; i++) {
                int calendarDay = (i + mWeekStart) % ROW_DAYS;
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

    private void drawMonthHeader(Canvas canvas) {
        canvas.drawRect(new Rect(0, 0, mWidth, MONTH_HEADER_SIZE), mMonthHeaderBgPaint);
        int x = (mWidth + 2 * mPadding) / 2;
        int y = MONTH_HEADER_SIZE / 2;
        Paint.FontMetrics fontMetrics = mMonthHeaderTextPaint.getFontMetrics();
        //文本绘制基线，水平穿过文字中央
        float baselineY = y + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        StringBuilder sb = new StringBuilder(getMonthAndYearString().toLowerCase());
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        canvas.drawText(new SimpleDateFormat("yyyy年MM月").format(mCalendar.getTime()), x, baselineY, mMonthHeaderTextPaint);
    }

    protected void drawMonthDate(Canvas canvas) {
        int y = (mRowHeight + MINI_DAY_NUMBER_TEXT_SIZE) / 2 - DAY_SEPARATOR_WIDTH + MONTH_HEADER_SIZE;
        //paddingDay，item宽度的一半
        int paddingDay = (mWidth - 2 * mPadding) / (2 * ROW_DAYS);
        int dayOffset = calculateDayOffset();
        int day = 1;
        CalendarDay selectedLastDay = new CalendarDay(mSelectedLastYear, mSelectedLastMonth,
                mSelectedLastDay);
        CalendarDay selectedBeginDay = new CalendarDay(mSelectedBeginYear, mSelectedBeginMonth,
                mSelectedBeginDay);
        while (day <= mDateNums) {
            CalendarDay calendarDay = new CalendarDay(mYear, mMonth, day);
            int x = paddingDay * (1 + dayOffset * 2) + mPadding;
            // 选择的两个点
            if (calendarDay.compareTo(selectedBeginDay) == 0 ||
                    calendarDay.compareTo(selectedLastDay) == 0) {
                mSelectedMarkerPaint.setColor(0xff4f8ffc);
                if (mDrawRect) {
                    RectF rectF = new RectF(x - DAY_SELECTED_CIRCLE_SIZE,
                            (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) - DAY_SELECTED_CIRCLE_SIZE,
                            x + DAY_SELECTED_CIRCLE_SIZE, (y - MINI_DAY_NUMBER_TEXT_SIZE / 3)
                            + DAY_SELECTED_CIRCLE_SIZE);
                    canvas.drawRoundRect(rectF, rectF.centerX(), rectF.centerY(), mSelectedMarkerPaint);
                } else {
                    canvas.drawCircle(x, y - MINI_DAY_NUMBER_TEXT_SIZE / 3,
                            DAY_SELECTED_CIRCLE_SIZE, mSelectedMarkerPaint);
                }
            }
            // 今天
            if (mHasToday && (mToday == day)) {
                mMonthDatePaint.setColor(Color.BLACK);
//                mMonthDatePaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            } else {
                mMonthDatePaint.setColor(mDateNumColor);
                mMonthDatePaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }

            // 起点或终点
            if (calendarDay.compareTo(selectedBeginDay) == 0 ||
                    calendarDay.compareTo(selectedLastDay) == 0) {
                mMonthDatePaint.setColor(mMonthTitleTextColor);
            }
            // 起点和终点重叠
            if (calendarDay.compareTo(selectedBeginDay) == 0 &&
                    calendarDay.compareTo(selectedLastDay) == 0) {
                mMonthDatePaint.setColor(Color.WHITE);
            }
            if ((selectedBeginDay.isValid() && selectedLastDay.isValid()
                    // 年份相同
                    && mSelectedBeginYear == mSelectedLastYear && mSelectedBeginYear == mYear)
                    && (((mMonth == mSelectedBeginMonth && mSelectedLastMonth ==
                    mSelectedBeginMonth)
                    && ((mSelectedBeginDay < mSelectedLastDay && day >
                    mSelectedBeginDay && day < mSelectedLastDay) || (mSelectedBeginDay >
                    mSelectedLastDay && day < mSelectedBeginDay && day > mSelectedLastDay))) ||
                    ((mSelectedBeginMonth < mSelectedLastMonth && mMonth ==
                            mSelectedBeginMonth && day > mSelectedBeginDay)
                            || (mSelectedBeginMonth < mSelectedLastMonth &&
                            mMonth == mSelectedLastMonth && day < mSelectedLastDay)) ||
                    ((mSelectedBeginMonth > mSelectedLastMonth && mMonth ==
                            mSelectedBeginMonth && day < mSelectedBeginDay) ||
                            (mSelectedBeginMonth > mSelectedLastMonth && mMonth ==
                                    mSelectedLastMonth && day > mSelectedLastDay)))) {
                //选择的两个点之间的
                mMonthDatePaint.setColor(Color.WHITE);
            }
            if ((selectedBeginDay.isValid() && selectedLastDay.isValid()
                    && mSelectedBeginYear != mSelectedLastYear
                    && ((mSelectedBeginYear == mYear &&
                    mMonth == mSelectedBeginMonth) || (mSelectedLastYear == mYear
                    && mMonth == mSelectedLastMonth)) &&
                    (((mSelectedBeginMonth < mSelectedLastMonth && mMonth == mSelectedBeginMonth
                            && day < mSelectedBeginDay) || (mSelectedBeginMonth < mSelectedLastMonth
                            && mMonth == mSelectedLastMonth && day > mSelectedLastDay)) ||
                            ((mSelectedBeginMonth > mSelectedLastMonth && mMonth
                                    == mSelectedBeginMonth && day > mSelectedBeginDay) ||
                                    (mSelectedBeginMonth > mSelectedLastMonth && mMonth ==
                                            mSelectedLastMonth && day < mSelectedLastDay))))) {
                //跨年的边缘日期
                mMonthDatePaint.setColor(Color.WHITE);
            }

            if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1 && mSelectedBeginYear
                    == mSelectedLastYear && mYear == mSelectedBeginYear) &&
                    ((mMonth > mSelectedBeginMonth && mMonth < mSelectedLastMonth
                            && mSelectedBeginMonth < mSelectedLastMonth) ||
                            (mMonth < mSelectedBeginMonth && mMonth > mSelectedLastMonth
                                    && mSelectedBeginMonth > mSelectedLastMonth))) {
                //不跨年的中间月
                mMonthDatePaint.setColor(Color.WHITE);
            }

            if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1 && mSelectedBeginYear != mSelectedLastYear) &&
                    ((mSelectedBeginYear < mSelectedLastYear && ((mMonth > mSelectedBeginMonth
                            && mYear == mSelectedBeginYear) || (mMonth < mSelectedLastMonth
                            && mYear == mSelectedLastYear))) ||
                            (mSelectedBeginYear > mSelectedLastYear && ((mMonth < mSelectedBeginMonth
                                    && mYear == mSelectedBeginYear) || (mMonth > mSelectedLastMonth
                                    && mYear == mSelectedLastYear))))) {
                //跨年的中间月
                mMonthDatePaint.setColor(Color.WHITE);
            }

            if (!isPrevDayEnabled && checkPrevDay(day, mTime) && mTime.month == mMonth && mTime.year == mYear) {
                mMonthDatePaint.setColor(mPreviousDateColor);
                mMonthDatePaint.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
            }

            if (dayOffset % ROW_DAYS == 0 || dayOffset % ROW_DAYS == 6) {
                // 设置周六和周日的字体颜色
                mMonthDatePaint.setColor(0xfff4250a);
            }
            if (isDateOutOfRange(calendarDay)) {
                // 屏蔽不可选择的日期
                mMonthDatePaint.setAlpha(128);
            }
            canvas.drawText(String.format("%d", day), x, y, mMonthDatePaint);

            dayOffset++;
            if (dayOffset == ROW_DAYS) {
                dayOffset = 0;
                y += mRowHeight;
            }
            day++;
        }
    }

    public boolean isStartDay(CalendarDay calendarDay) {
        CalendarDay startDay = new CalendarDay(mSelectedBeginYear, mSelectedBeginMonth, mSelectedBeginDay);
        return calendarDay.compareTo(startDay) == 0;
    }

    public boolean isBeforeStartDay(CalendarDay calendarDay) {
        CalendarDay startDay = new CalendarDay(mSelectedBeginYear, mSelectedBeginMonth, mSelectedBeginDay);
        return calendarDay.compareTo(startDay) < 0;
    }

    public boolean isBeforeStartMonth(CalendarDay calendarDay) {
        CalendarDay startDay = new CalendarDay(mSelectedBeginYear, mSelectedBeginMonth, mSelectedBeginDay);
        return calendarDay.compareMonth(startDay) < 0;
    }

    public boolean isAfterStartDay(CalendarDay calendarDay) {
        CalendarDay startDay = new CalendarDay(mSelectedBeginYear, mSelectedBeginMonth, mSelectedBeginDay);
        return calendarDay.compareTo(startDay) > 0;
    }

    public boolean isAfterStartMonth(CalendarDay calendarDay) {
        CalendarDay startDay = new CalendarDay(mSelectedBeginYear, mSelectedBeginMonth, mSelectedBeginDay);
        return calendarDay.compareMonth(startDay) > 0;
    }

    public boolean isEndDay(CalendarDay calendarDay) {
        CalendarDay endDay = new CalendarDay(mSelectedLastYear, mSelectedLastMonth, mSelectedLastDay);
        return calendarDay.compareTo(endDay) == 0;
    }

    public boolean isBeforeEndDay(CalendarDay calendarDay) {
        CalendarDay endDay = new CalendarDay(mSelectedLastYear, mSelectedLastMonth, mSelectedLastDay);
        return calendarDay.compareTo(endDay) < 0;
    }

    public boolean isBeforeEndMonth(CalendarDay calendarDay) {
        CalendarDay endDay = new CalendarDay(mSelectedLastYear, mSelectedLastMonth, mSelectedLastDay);
        return calendarDay.compareMonth(endDay) < 0;
    }

    public boolean isAfterEndDay(CalendarDay calendarDay) {
        CalendarDay endDay = new CalendarDay(mSelectedLastYear, mSelectedLastMonth, mSelectedLastDay);
        return calendarDay.compareTo(endDay) > 0;
    }

    public boolean isAfterEndMonth(CalendarDay calendarDay) {
        CalendarDay endDay = new CalendarDay(mSelectedLastYear, mSelectedLastMonth, mSelectedLastDay);
        return calendarDay.compareMonth(endDay) > 0;
    }

    private void drawDateEdge(Canvas canvas) {
        int y = (mRowHeight + MINI_DAY_NUMBER_TEXT_SIZE) / 2 - DAY_SEPARATOR_WIDTH + MONTH_HEADER_SIZE;
        int paddingDay = (mWidth - 2 * mPadding) / (2 * ROW_DAYS);
        int dayOffset = calculateDayOffset();
        int day = 1;
        List<PointF> points = new ArrayList<>();
        PointF firstPoint = null;
        PointF lastPoint = null;
        PointF startPoint;
        PointF endPoint;
        boolean needFirstPoint = false;
        boolean needLastPoint = false;
        CalendarDay selectedLastDay = new CalendarDay(mSelectedLastYear, mSelectedLastMonth,
                mSelectedLastDay);
        CalendarDay selectedBeginDay = new CalendarDay(mSelectedBeginYear, mSelectedBeginMonth,
                mSelectedBeginDay);
        while (day <= mDateNums) {
            int x = paddingDay * (1 + dayOffset * 2) + mPadding;
            if (day == 1) {
                firstPoint = new PointF(x, y);
            }
            if (day == mDateNums) {
                lastPoint = new PointF(x, y);
            }
            if (isStartDay(new CalendarDay(mYear, mMonth, day))) {
                startPoint = new PointF(x, y);
                points.add(startPoint);
                if (selectedBeginDay.isValid() && selectedLastDay
                        .distanceMonth(selectedBeginDay) >= 1) {
                    needLastPoint = true;
                }
                if (selectedLastDay.isValid() && selectedBeginDay
                        .distanceMonth(selectedLastDay) >= 1) {
                    needFirstPoint = true;
                }
            }
            if (isEndDay(new CalendarDay(mYear, mMonth, day))) {
                if (selectedLastDay.isValid() && selectedBeginDay.distanceMonth(selectedLastDay) >= 1) {
                    needLastPoint = true;
                }
                endPoint = new PointF(x, y);
                if (firstPoint != null) {
                    if (selectedBeginDay.isValid() && selectedLastDay
                            .distanceMonth(selectedBeginDay) >= 1) {
                        points.add(firstPoint);
                    }
                }
                points.add(endPoint);
            }
            dayOffset++;
            if (dayOffset == ROW_DAYS) {
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
        if ((new CalendarDay(mYear, mMonth, day).compareMonth(selectedBeginDay) > 0 &&
                new CalendarDay(mYear, mMonth, day).compareMonth(selectedLastDay) < 0
                && selectedBeginDay.isValid() && selectedBeginDay.compareTo(selectedLastDay) < 0) ) {
            //中间月
            points.add(firstPoint);
            points.add(lastPoint);
        }
        if ((new CalendarDay(mYear, mMonth, day).compareMonth(selectedBeginDay) < 0 &&
                new CalendarDay(mYear, mMonth, day).compareMonth(selectedLastDay) > 0)
                && selectedLastDay.isValid() && selectedBeginDay.compareTo(selectedLastDay) > 0) {
            //中间月
            points.add(firstPoint);
            points.add(lastPoint);
        }
        if (points.size() > 1 && points.size() % 2 == 0) {
            for (int i = 0; i < points.size(); i += 2) {
                drawTextEdgeInternal(points.get(i), points.get(i + 1), canvas);
            }
        }
    }

    private void drawTextEdgeInternal(PointF startPoint, PointF endPoint, Canvas canvas) {
        if (endPoint.y > startPoint.y) {
            int row = ((int) Math.abs(endPoint.y - startPoint.y) / mRowHeight + 1);
            if (row > 2) {
                canvas.drawLine(startPoint.x, startPoint.y - 9, 660.0f, startPoint.y - 9, mDateEdgePaint);
                canvas.drawLine(50, endPoint.y - 9, endPoint.x, endPoint.y - 9, mDateEdgePaint);
                for (int i = 1; i < row - 1; i++) {
                    canvas.drawLine(50, startPoint.y - 9 + mRowHeight * i, 660.0f,
                            startPoint.y - 9 + mRowHeight * i, mDateEdgePaint);
                }
            } else if (row == 2) {
                canvas.drawLine(startPoint.x, startPoint.y - 9, 660.0f, startPoint.y - 9, mDateEdgePaint);
                canvas.drawLine(50, endPoint.y - 9, endPoint.x, endPoint.y - 9, mDateEdgePaint);
            }
        } else {
            canvas.drawLine(startPoint.x, startPoint.y - 9, endPoint.x, endPoint.y - 9, mDateEdgePaint);
        }
    }

    /**
     * 获取点击的位置为哪一天。
     */
    public CalendarDay getDayFromTouchPoint(PointF point) {
        return getDayFromTouchPoint(point.x, point.y);
    }

    /**
     * 获取点击的位置为哪一天。
     */
    public CalendarDay getDayFromTouchPoint(float x, float y) {
        int padding = mPadding;
        if ((x < padding) || (x > mWidth - mPadding)) {
            return null;
        }
        //yIndex，第几行的日期，0为第1行
        int yIndex = (int) (y - MONTH_HEADER_SIZE) / mRowHeight;
        int day = 1 + ((int) ((x - padding) * ROW_DAYS / (mWidth - padding - mPadding))
                - calculateDayOffset()) + yIndex * ROW_DAYS;
        if (mMonth > 11 || mMonth < 0 || getDaysOfMonth(mYear, mMonth + 1) < day || day < 1) {
            return null;    //返回不了天数的情况
        }
        return new CalendarDay(mYear, mMonth, day);
    }

    public boolean isDateOutOfRange(CalendarDay calendarDay) {
        return calendarDay.compareTo(mStartDay) < 0 || calendarDay.compareTo(mEndDay) > 0;
    }

    protected void initPaints() {

        mMonthHeaderBgPaint = new Paint();
        mMonthHeaderBgPaint.setAntiAlias(true);
        mMonthHeaderBgPaint.setStyle(Style.FILL);
        mMonthHeaderBgPaint.setColor(mMonthTitleBgColor);

        mMonthHeaderTextPaint = new TextPaint();
        mMonthHeaderTextPaint.setAntiAlias(true);
        mMonthHeaderTextPaint.setTextSize(MONTH_LABEL_TEXT_SIZE);
        mMonthHeaderTextPaint.setTypeface(Typeface.create(mMonthHeaderTypeface, Typeface.BOLD));
        mMonthHeaderTextPaint.setColor(mMonthTextColor);
        mMonthHeaderTextPaint.setTextAlign(Align.CENTER);
        mMonthHeaderTextPaint.setStyle(Style.FILL);

        mSelectedMarkerPaint = new Paint();
        mSelectedMarkerPaint.setFakeBoldText(true);
        mSelectedMarkerPaint.setAntiAlias(true);
        mSelectedMarkerPaint.setColor(mSelectedDaysColor);
        mSelectedMarkerPaint.setTextAlign(Align.CENTER);
        mSelectedMarkerPaint.setStyle(Style.FILL);
        mSelectedMarkerPaint.setStrokeCap(Paint.Cap.ROUND);
        mSelectedMarkerPaint.setAlpha(SELECTED_CIRCLE_ALPHA);

        mDateEdgePaint = new Paint();
        mDateEdgePaint.setFakeBoldText(true);
        mDateEdgePaint.setAntiAlias(true);
        mDateEdgePaint.setTextAlign(Align.CENTER);
        mDateEdgePaint.setStyle(Style.FILL_AND_STROKE);
        mDateEdgePaint.setStrokeCap(Paint.Cap.ROUND);
        mDateEdgePaint.setAlpha(SELECTED_CIRCLE_ALPHA);

        mDateEdgePaint.setStrokeWidth(MINI_DAY_NUMBER_TEXT_SIZE * 2);
        mDateEdgePaint.setColor(mEdgeColor);

        mMonthDayLabelPaint = new TextPaint();
        mMonthDayLabelPaint.setAntiAlias(true);
        mMonthDayLabelPaint.setTextSize(MONTH_DAY_LABEL_TEXT_SIZE);
        mMonthDayLabelPaint.setColor(mDateTextColor);
        mMonthDayLabelPaint.setTypeface(Typeface.create(mDayOfWeekTypeface, Typeface.NORMAL));
        mMonthDayLabelPaint.setStyle(Style.FILL);
        mMonthDayLabelPaint.setTextAlign(Align.CENTER);
        mMonthDayLabelPaint.setFakeBoldText(true);

        mMonthDatePaint = new TextPaint();
        mMonthDatePaint.setAntiAlias(true);
        mMonthDatePaint.setTextSize(MINI_DAY_NUMBER_TEXT_SIZE);
        mMonthDatePaint.setStyle(Style.FILL);
        mMonthDatePaint.setTextAlign(Align.CENTER);
        mMonthDatePaint.setFakeBoldText(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawMonthDayLabels(canvas);
        drawMonthHeader(canvas);
        drawDateEdge(canvas);
        drawMonthDate(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                mRowHeight * mRowNums + MONTH_HEADER_SIZE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            CalendarDay calendarDay = getDayFromTouchPoint(event.getX(), event.getY());
            if (calendarDay != null && !isDateOutOfRange(calendarDay)) {
                onDateClick(calendarDay);
            }
        }
        return true;
    }

    public void reuse() {
        mRowNums = ROW_NUMS;
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

        mDateNums = getDaysOfMonth(mYear, mMonth + 1);
        for (int i = 0; i < mDateNums; i++) {
            final int day = i + 1;
            if (checkSameDay(day, mTime)) {
                mHasToday = true;
                mToday = day;
            }

            mIsPrev = checkPrevDay(day, mTime);
        }

        mRowNums = calculateRowNums();
    }

    /**
     * 判断传入的年份是否为闰年。
     *
     * @param year 年份，如2019
     * @return 是否为闰年，闰年366天，平年365天
     */
    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }

    /**
     * 传入年份和月份获取该月份的天数。
     *
     * @param year  年份，如2019
     * @param month 月份，跟实际月份一样，比如一月为1，不为0
     * @return 该月份的天数
     */
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
                } else {
                    result = 28;
                }
                break;
        }
        return result;
    }

    public void setOnDateClickListener(OnDateClickListener l) {
        mOnDateClickListener = l;
    }

    /**
     * 日期点击监听器。
     */
    public interface OnDateClickListener {
        void onDateClick(CalendarView calendarView, CalendarDay calendarDay);
    }
}