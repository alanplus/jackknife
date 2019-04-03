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

import java.util.Calendar;
import java.util.HashMap;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder>
        implements CalendarView.OnDateClickListener {

    private final int MONTH_IN_YEAR = 12;
    private final int INVALID = -1;
    private final Context mContext;
    private final CalendarDay mStartDay;
    private final CalendarDay mEndDay;
    private DatePickerController mController;
    private final SelectedDays<CalendarDay> mSelectedDays;
    private boolean mCurrentDaySelected;

    public DateAdapter(Context context, CalendarDay startDay, CalendarDay endDay) {
        this.mContext = context;
        this.mStartDay = startDay;
        this.mEndDay = endDay;
        this.mSelectedDays = new SelectedDays<>();
        init();
    }

    public void setDatePickerController(DatePickerController controller) {
        this.mController = controller;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int position) {
        CalendarView calendarView = new CalendarView(mContext);
        return new ViewHolder(calendarView, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CalendarView calendarView = holder.calendarView;
        calendarView.setCurrentDayTextColor(0xff999999);
        calendarView.setMonthTextColor(0xff999999);
        calendarView.setDateTextColor(0xff999999);
        calendarView.setDateNumColor(0xff000000);
        calendarView.setPreviousDateColor(0xff999999);
        calendarView.setSelectedDaysColor(0xffe75f49);
        calendarView.setMonthTitleTextColor(0xffffffff);
        calendarView.setDrawRect(false);
        calendarView.setNeedMonthDayLabels(true);
        calendarView.setStartDay(mStartDay);
        calendarView.setEndDay(mEndDay);
        HashMap<String, Integer> drawingParams = new HashMap<>();
        int month;
        int year;
        month = (mStartDay.month + (position % MONTH_IN_YEAR)) % MONTH_IN_YEAR;
        year = position / MONTH_IN_YEAR + mStartDay.year + ((mStartDay.month +
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

        calendarView.reuse();

        drawingParams.put(CalendarView.VIEW_PARAMS_SELECTED_BEGIN_YEAR, selectedFirstYear);
        drawingParams.put(CalendarView.VIEW_PARAMS_SELECTED_LAST_YEAR, selectedLastYear);
        drawingParams.put(CalendarView.VIEW_PARAMS_SELECTED_BEGIN_MONTH, selectedFirstMonth);
        drawingParams.put(CalendarView.VIEW_PARAMS_SELECTED_LAST_MONTH, selectedLastMonth);
        drawingParams.put(CalendarView.VIEW_PARAMS_SELECTED_BEGIN_DAY, selectedFirstDay);
        drawingParams.put(CalendarView.VIEW_PARAMS_SELECTED_LAST_DAY, selectedLastDay);
        drawingParams.put(CalendarView.VIEW_PARAMS_YEAR, year);
        drawingParams.put(CalendarView.VIEW_PARAMS_MONTH, month);
        drawingParams.put(CalendarView.VIEW_PARAMS_WEEK_START, Calendar.getInstance().getFirstDayOfWeek());
        calendarView.setMonthParams(drawingParams);
        calendarView.invalidate();
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return (mEndDay.year - mStartDay.year) * 12 + (mEndDay.month - mStartDay.month) + 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CalendarView calendarView;

        public ViewHolder(View itemView, CalendarView.OnDateClickListener listener) {
            super(itemView);
            calendarView = (CalendarView) itemView;
            calendarView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
            calendarView.setClickable(true);
            calendarView.setOnDateClickListener(listener);
        }
    }

    public void setCurrentDaySelected(boolean isCurrentDaySelected) {
        this.mCurrentDaySelected = isCurrentDaySelected;
    }

    protected void init() {
        if (mCurrentDaySelected) {
            onDateSelected(new CalendarDay(System.currentTimeMillis()));
        }
    }

    @Override
    public void onDateClick(CalendarView calendarView, CalendarDay calendarDay) {
        if (calendarDay != null) {
            onDateSelected(calendarDay);
        }
    }

    protected void onDateSelected(CalendarDay calendarDay) {
        if (mController != null) {
            mController.onDateSelected(calendarDay.year, calendarDay.month, calendarDay.day);
            setSelectedDay(calendarDay);
        }
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