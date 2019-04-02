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
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder>
        implements DateView.OnDateClickListener {

    private final int MONTH_IN_YEAR = 12;
    private final int INVALID = -1;
    private final Context mContext;
    private DatePickerController mController;
    private final Calendar mCalendar;
    private final SelectedDays<CalendarDay> mSelectedDays;
    private final String mStartMonth;
    private final String mEndMonth;
    private final int startYear;
    private final int startMonth;
    private final int endYear;
    private final int endMonth;
    private boolean mCurrentDaySelected;

    public DateAdapter(Context context, long startTimeMillis, long endTimeMillis) {
        this(context, new SimpleDateFormat("yyyyMM").format(new Date(startTimeMillis)),
                new SimpleDateFormat("yyyyMM").format(new Date(endTimeMillis)));
    }

    public DateAdapter(Context context, String startMonth, String endMonth) {
        this.mStartMonth = startMonth;
        this.mEndMonth = endMonth;
        this.startYear = Integer.valueOf(mStartMonth.substring(0, 4));
        this.startMonth = Integer.valueOf(mStartMonth.substring(4));
        this.endYear = Integer.valueOf(mEndMonth.substring(0, 4));
        this.endMonth = Integer.valueOf(mEndMonth.substring(4));
        mCalendar = Calendar.getInstance();
        mSelectedDays = new SelectedDays<>();
        mContext = context;
        init();
    }

    public void setDatePickerController(DatePickerController controller) {
        this.mController = controller;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int position) {
        DateView dateView = new DateView(mContext, mStartMonth, mEndMonth);
        return new ViewHolder(dateView, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DateView dateView = holder.dateView;
        dateView.setCurrentDayTextColor(0xff999999);
        dateView.setMonthTextColor(0xff999999);
        dateView.setDateTextColor(0xff999999);
        dateView.setDateNumColor(0xff999999);
        dateView.setPreviousDateColor(0xff999999);
        dateView.setSelectedDaysColor(0xffe75f49);
        dateView.setMonthTitleTextColor(0xff000000);
        dateView.setDrawRect(false);
        if (position == 0) {
            dateView.setNeedMonthDayLabels(true);
        }
        HashMap<String, Integer> drawingParams = new HashMap<>();
        int month;
        int year;
        month = (startMonth + (position % MONTH_IN_YEAR)) % MONTH_IN_YEAR;
        year = position / MONTH_IN_YEAR + mCalendar.get(Calendar.YEAR) + ((startMonth +
                (position % MONTH_IN_YEAR)) / MONTH_IN_YEAR);

        int selectedFirstDay = INVALID;
        int selectedLastDay = INVALID;
        int selectedFirstMonth = INVALID;
        int selectedLastMonth = INVALID;
        int selectedFirstYear = INVALID;
        int selectedLastYear = INVALID;

        if (mSelectedDays.getFirst() != null) {
            selectedFirstDay = mSelectedDays.getFirst().day;
            selectedFirstMonth = mSelectedDays.getFirst().month;
            selectedFirstYear = mSelectedDays.getFirst().year;
        }

        if (mSelectedDays.getLast() != null) {
            selectedLastDay = mSelectedDays.getLast().day;
            selectedLastMonth = mSelectedDays.getLast().month;
            selectedLastYear = mSelectedDays.getLast().year;
        }

        dateView.reuse();

        drawingParams.put(DateView.VIEW_PARAMS_SELECTED_BEGIN_YEAR, selectedFirstYear);
        drawingParams.put(DateView.VIEW_PARAMS_SELECTED_LAST_YEAR, selectedLastYear);
        drawingParams.put(DateView.VIEW_PARAMS_SELECTED_BEGIN_MONTH, selectedFirstMonth);
        drawingParams.put(DateView.VIEW_PARAMS_SELECTED_LAST_MONTH, selectedLastMonth);
        drawingParams.put(DateView.VIEW_PARAMS_SELECTED_BEGIN_DAY, selectedFirstDay);
        drawingParams.put(DateView.VIEW_PARAMS_SELECTED_LAST_DAY, selectedLastDay);
        drawingParams.put(DateView.VIEW_PARAMS_YEAR, year);
        drawingParams.put(DateView.VIEW_PARAMS_MONTH, month);
        drawingParams.put(DateView.VIEW_PARAMS_WEEK_START, mCalendar.getFirstDayOfWeek());
        dateView.setMonthParams(drawingParams);
        dateView.invalidate();
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return (endYear - startYear) * 12 + (endMonth - startMonth);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        DateView dateView;

        public ViewHolder(View itemView, DateView.OnDateClickListener listener) {
            super(itemView);
            dateView = (DateView) itemView;
            dateView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
            dateView.setClickable(true);
            dateView.setOnDateClickListener(listener);
        }
    }

    public void setCurrentDaySelected(boolean isCurrentDaySelected) {
        this.mCurrentDaySelected = isCurrentDaySelected;
    }

    protected void init() {
        if (mCurrentDaySelected) {
            onDateTapped(new CalendarDay(System.currentTimeMillis()));
        }
    }

    @Override
    public void onDateClick(DateView dateView, CalendarDay calendarDay) {
        if (calendarDay != null) {
            onDateTapped(calendarDay);
        }
    }

    protected void onDateTapped(CalendarDay calendarDay) {
        mController.onDateSelected(calendarDay.year, calendarDay.month, calendarDay.day);
        setSelectedDay(calendarDay);
    }

    public void setSelectedDay(CalendarDay calendarDay) {
        if (mSelectedDays.getFirst() != null && mSelectedDays.getLast() == null) {
            mSelectedDays.setLast(calendarDay);
            if (mController != null) {
                if (mSelectedDays.getFirst().month < calendarDay.month) {
                    for (int i = 0; i < mSelectedDays.getFirst().month - calendarDay.month - 1; ++i)
                        mController.onDateSelected(mSelectedDays.getFirst().year, mSelectedDays
                                .getFirst().month + i, mSelectedDays.getFirst().day);
                }
                mController.onDateRangeSelected(mSelectedDays);
            }
        } else if (mSelectedDays.getLast() != null) {
            mSelectedDays.setFirst(calendarDay);
            mSelectedDays.setLast(null);
        } else
            mSelectedDays.setFirst(calendarDay);

        notifyDataSetChanged();
    }

    public SelectedDays<CalendarDay> getSelectedDays() {
        return mSelectedDays;
    }
}