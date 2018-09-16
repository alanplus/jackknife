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

package com.lwh.jackknife.widget.popupdialog;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class DialogView extends AbstractDialogView {

    private int mViewResId = View.NO_ID;
    private View mContentView;
    private boolean mNeedShadowView;
    private int mShadowColor = 0xFFFFFFFF;
    private final FrameLayout.LayoutParams mShadowLayoutParams = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
    );
    private FrameLayout shadowViewOutsideDismiss;

    public DialogView(int layoutResId) {
        this.mViewResId = layoutResId;
    }

    public DialogView(View view) {
        this.mContentView = view;
    }

    public DialogView(int layoutResId, int shadowColor) {
        this(layoutResId);
        initShadow(shadowColor);
    }

    public DialogView(View view, int shadowColor) {
        this(view);
        initShadow(shadowColor);
    }

    protected void initShadow(int shadowColor) {
        this.mNeedShadowView = true;
        this.mShadowColor = shadowColor;
    }

    public void setNeedShadowView(boolean needShadowView) {
        this.mNeedShadowView = needShadowView;
    }

    public void setShadowColor(int color) {
        this.mShadowColor = color;
    }

    public boolean isNeedShadowView() {
        return mNeedShadowView;
    }

    public int getShadowColor() {
        return mShadowColor;
    }

    public void setContentView(View contentView) {
        this.mContentView = contentView;
    }

    public View getContentView() {
        return mContentView;
    }

    @Override
    protected void addContent(LayoutInflater inflater, ViewGroup parent, ViewGroup viewRoot) {
        if (mViewResId != View.NO_ID) {
            mContentView = inflater.inflate(mViewResId, parent, false); //inflate layout
        }
        mContentView.setFocusable(true);
        mContentView.setFocusableInTouchMode(true);
        if (mNeedShadowView) {
            FrameLayout shadowView = new FrameLayout(viewRoot.getContext());
            shadowView.setBackgroundColor(mShadowColor);
            setShadowViewOutsideCanDismiss(shadowView, true);
            viewRoot.addView(shadowView, mShadowLayoutParams);
            shadowView.addView(mContentView, mLayoutParams);
        } else {
            viewRoot.addView(mContentView);
        }
    }

    protected void setShadowViewOutsideCanDismiss(View shadeView, boolean canDismiss) {
        if (canDismiss) {
            shadeView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mOnCancelListener.onCancel();
                    return false;
                }
            });
        }
    }
}
