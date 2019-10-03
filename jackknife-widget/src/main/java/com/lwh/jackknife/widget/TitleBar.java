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
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TitleBar extends FrameLayout {

    private TextView mTitleView;
    private TextView mBackView;
    private TextView mMenuView;
    private String mTitle;
    private String mBack;
    private String mMenu;
    private Drawable mBackIcon;
    private Drawable mMenuIcon;
    private Drawable mMenuBg;
    private boolean mNoBack;

    public TitleBar(@NonNull Context context) {
        this(context, null);
    }

    public TitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TitleBar, defStyleAttr, 0);
        int dp6 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics());
        int dp8 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        int dp10 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        mTitle = a.getString(R.styleable.TitleBar_tb_title);
        mBack = a.getString(R.styleable.TitleBar_tb_backName);
        mMenu = a.getString(R.styleable.TitleBar_tb_menuName);
        mNoBack = a.getBoolean(R.styleable.TitleBar_tb_noBack, false);
        if (a.hasValue(R.styleable.TitleBar_tb_backIcon)) {
            mBackIcon = a.getDrawable(R.styleable.TitleBar_tb_backIcon);
        }
        if (a.hasValue(R.styleable.TitleBar_tb_menuIcon)) {
            mMenuIcon = a.getDrawable(R.styleable.TitleBar_tb_menuIcon);
        }
        if (a.hasValue(R.styleable.TitleBar_tb_menuBg)) {
            mMenuBg = a.getDrawable(R.styleable.TitleBar_tb_menuBg);
        }
        a.recycle();
        if (mBackIcon == null) {
            mBackIcon = getContext().getResources().getDrawable(R.drawable.jknf_title_bar_back);
        }
        View.inflate(getContext(), R.layout.jknf_title_bar, this);
        mTitleView = findViewById(R.id.tv_titlebar_title);
        mBackView = findViewById(R.id.tv_titlebar_back);
        mMenuView = findViewById(R.id.tv_titlebar_menu);
        mBackView.setVisibility(mNoBack ? GONE : VISIBLE);
        mTitleView.setText(mTitle);
        mBackView.setText(mBack);
        mMenuView.setText(mMenu);
        if (mMenuIcon != null) {
            mMenuView.setCompoundDrawablesWithIntrinsicBounds(mMenuIcon, null, null, null);
            mMenuView.setCompoundDrawablePadding(10);
        }
        if (mMenuBg != null) {
            mMenuView.setBackgroundDrawable(mMenuBg);
            mMenuView.setPadding(dp8, dp6, dp8, dp6);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mMenuView.getLayoutParams();
            params.rightMargin = dp10;
            mMenuView.setLayoutParams(params);
        }
        int measuredHeight = getMeasuredHeight();
        int iconSize = measuredHeight - dp10;
        mBackIcon.setBounds(0, 0, iconSize, iconSize);
        mBackView.setCompoundDrawablesWithIntrinsicBounds(mBackIcon, null, null, null);
        mBackView.setCompoundDrawablePadding(10);
    }

    public void setTitle(String title) {
        mTitleView.setText(title);
    }

    public void setBack(String back) {
        mBackView.setText(back);
    }

    public void setMenu(String menu) {
        mMenuView.setText(menu);
    }

    public void setOnBackListener(OnClickListener listener) {
        mBackView.setOnClickListener(listener);
    }

    public void setOnMenuListener(OnClickListener listener) {
        mMenuView.setOnClickListener(listener);
    }

    public TextView getBackView() {
        return mBackView;
    }

    public TextView getTitleView() {
        return mTitleView;
    }

    public TextView getMenuView() {
        return mMenuView;
    }

    public void showMenu(boolean show) {
        mMenuView.setVisibility(show ? VISIBLE : GONE);
    }
}
