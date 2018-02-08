/*
 * Copyright (C) 2018 The JackKnife Open Source Project
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
import android.text.InputType;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MacEditTextGroup extends AutoEditTextGroup {

    public MacEditTextGroup(Context context) {
        this(context, null);
    }

    public MacEditTextGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MacEditTextGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
    }

    @Override
    protected AutoEditText createEditText() {
        AutoEditText section = new MacEditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        section.setLayoutParams(lp);
        section.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 6, getResources().getDisplayMetrics()));
        section.setGravity(Gravity.CENTER);
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        section.setPadding(padding, padding, padding, padding);
        section.setSingleLine();
        section.setFocusableInTouchMode(true);
        section.addInputFilter(InputType.TYPE_CLASS_TEXT);
        applyEditTextTheme(section);
        return section;
    }

    @Override
    public int getChildCount() {
        return 6;
    }

    @Override
    public String getSemicolonText() {
        return ":";
    }

    @Override
    public int getMaxLength() {
        return 2;
    }

    @Override
    public void applySemicolonTextViewTheme(TextView semicolonTextView) {
        semicolonTextView.setGravity(Gravity.CENTER);
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        semicolonTextView.setPadding(padding, padding, padding, padding);
    }

    @Override
    public void applyEditTextTheme(AutoEditText absEditText) {
    }
}
