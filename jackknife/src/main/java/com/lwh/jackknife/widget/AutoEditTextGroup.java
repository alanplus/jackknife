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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lwh.jackknife.R;

import java.util.ArrayList;
import java.util.List;

public abstract class AutoEditTextGroup extends LinearLayout implements TextWatcher {

    protected List<AutoEditText> mSections;

    public AutoEditTextGroup(Context context) {
        this(context, null);
    }

    public AutoEditTextGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoEditTextGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    protected void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mSections = new ArrayList<>();
        initAttrs(context, attrs, defStyleAttr);
        initViews();
        initListeners();
    }

    protected void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
    }

    public boolean checkChildrenInputValue(AutoEditText... editTexts) {
        boolean result = true;
        for (int i = 0; i < editTexts.length - 1; i++) {
            if (!editTexts[i].checkInputValue()) {
                result = false;
                break;
            }
        }
        return result;
    }

    public String getText() {
        String result = "";
        for (int i = 0; i < mSections.size(); i++) {
            result += mSections.get(i).getText().toString();
            if (i != mSections.size()-1){
                result += getSemicolonText();
            }
        }
        return result;
    }

    protected abstract AutoEditText createEditText();

    private class OnDelKeyListener implements OnKeyListener{

        private AutoEditText mClearEditText;
        private AutoEditText mRequestEditText;

        public OnDelKeyListener(AutoEditText clear, AutoEditText request){
            this.mClearEditText = clear;
            this.mRequestEditText = request;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_UP &&
                    mRequestEditText.getText().toString().trim().equals("")) {
                if (mRequestEditText.hasFocus()) {
                    mRequestEditText.clearFocus();
                    mClearEditText.requestFocus();
                }
                return true;
            }
            return false;
        }
    }

    public abstract int getChildCount();

    public abstract String getSemicolonText();

    public abstract int getMaxLength();

    public abstract void applySemicolonTextViewTheme(TextView semicolonTextView);

    public abstract void applyEditTextTheme(AutoEditText absEditText);

    private void initListeners() {
        for (int i=0;i<mSections.size();i++) {
            mSections.get(i).addTextChangedListener(this);
            if (i != 0){
                mSections.get(i).setOnKeyListener(new OnDelKeyListener(mSections.get(i-1),mSections.get(i)));
            }
        }
    }

    protected void initViews() {
        int count = getChildCount() * 2 - 1;
        for (int i=0;i<count;i++) {
            if (i%2 == 0) {
                AutoEditText section = createEditText();
                mSections.add(section);
                addView(section);
            } else {
                addView(createSemicolonTextView());
            }
        }
    }

    private View createSemicolonTextView() {
        TextView textView = new TextView(getContext());
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(lp);
        textView.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 6, getResources().getDisplayMetrics()));
        textView.setTextColor(getResources().getColor(R.color.gray));
        textView.setText(getSemicolonText());
        applySemicolonTextViewTheme(textView);
        return textView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        int maxLength = getMaxLength();
        int length = s.toString().length();
        if (maxLength == length) {
            for (int i=0;i<mSections.size()-1;i++){
                if (mSections.get(i).hasFocus()){//hasFocus √ & isFocus ×
                    mSections.get(i).clearFocus();
                    mSections.get(i+1).requestFocus();
                    break;
                }
            }
        }
        if (!checkChildrenInputValue((AutoEditText[]) mSections.toArray())) {
            for (int i=0;i<mSections.size()-1;i++){
                if (mSections.get(i).hasFocus()){//hasFocus √ & isFocus ×
                    if (!mSections.get(i).checkInputValue()) {
                        mSections.get(i).clearFocus();
                        mSections.get(i + 1).requestFocus();
                        break;
                    }
                }
            }
        }
    }
}
