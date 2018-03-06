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
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.lwh.jackknife.R;

public class VerifyCodeEditTextGroup extends AutoEditTextGroup<VerifyCodeEditText> {

    private int mLength;

    public VerifyCodeEditTextGroup(Context context) {
        this(context, null);
    }

    public VerifyCodeEditTextGroup(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.verifyCodeEditTextGroupStyle);
    }

    public VerifyCodeEditTextGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VerifyCodeEditTextGroup,
                defStyleAttr, 0);
        int type = a.getColor(R.styleable.VerifyCodeEditTextGroup_verifycodeedittextgroup_type, 0);
        switch (type) {
            case 0:
                mLength = 4;
                break;
            case 1:
                mLength = 6;
                break;
        }
    }

    @Override
    protected VerifyCodeEditText createEditText() {
        VerifyCodeEditText section = new VerifyCodeEditText(getContext());
        LayoutParams lp = new LayoutParams(0, LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        section.setLayoutParams(lp);
        section.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 6,
                getResources().getDisplayMetrics()));
        section.setGravity(Gravity.CENTER);
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                getResources().getDisplayMetrics());
        section.setPadding(padding, padding, padding, padding);
        section.setSingleLine();
        section.setFocusableInTouchMode(true);
        applyEditTextTheme(section);
        section.setBackgroundResource(R.drawable.shape_edit_text_border);
        applyEditTextTheme(section);
        return section;
    }

    @Override
    public int getChildCount() {
        return mLength * 2 - 1;
    }

    @Override
    public String getSemicolonText() {
        return "";
    }

    @Override
    public int getMaxLength() {
        return 1;
    }

    @Override
    public void applySemicolonTextViewTheme(TextView textView) {
        textView.setPadding(0, 0, 0, 5);
        textView.getPaint().setFakeBoldText(true);
        textView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        textView.setGravity(Gravity.BOTTOM);
    }

    @Override
    public void applyEditTextTheme(AutoEditText absEditText) {
    }
}
