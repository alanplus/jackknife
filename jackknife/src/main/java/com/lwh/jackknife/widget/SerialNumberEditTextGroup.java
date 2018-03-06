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
import android.graphics.Color;
import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.text.method.ReplacementTransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lwh.jackknife.R;

public class SerialNumberEditTextGroup extends AutoEditTextGroup<SerialNumberEditText> {

    public SerialNumberEditTextGroup(Context context) {
        super(context);
    }

    public SerialNumberEditTextGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SerialNumberEditTextGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initAttrs(Context context, AttributeSet attributeSet, int i) {
    }

    @Override
    protected SerialNumberEditText createEditText() {
        final SerialNumberEditText section = new SerialNumberEditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        lp.setMargins(10, 10, 10, 10);
        section.setLayoutParams(lp);
        section.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 4, getResources()
                .getDisplayMetrics()));
        section.setGravity(Gravity.CENTER);
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1 ,
                getResources().getDisplayMetrics());
        section.setPadding(padding, padding, padding, padding);
        section.setSingleLine();
        section.setTextColor(Color.GRAY);
        section.setKeyListener(new NumberKeyListener() {
            protected char[] getAcceptedChars() {
                return section.getInputFilterAcceptedChars();
            }

            public int getInputType() {
                return InputType.TYPE_MASK_CLASS;

            }
        });
        section.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.shape_serial_number_edit_text_border));
        section.setFocusableInTouchMode(true);
        section.setTransformationMethod(new ReplacementTransformationMethod() {
            @Override
            protected char[] getOriginal() {
                char[] lowerLetters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
                        'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
                return lowerLetters;
            }

            @Override
            protected char[] getReplacement() {
                char[] upperLetters = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
                        'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
                return upperLetters;
            }
        });
        applyEditTextTheme(section);
        return section;
    }

    @Override
    public int getChildCount() {
        return 23;
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
    public void applySemicolonTextViewTheme(TextView semicolonTextView) {
    }

    @Override
    public void applyEditTextTheme(AutoEditText autoEditText) {
    }
}
