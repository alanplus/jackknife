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

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class DialogView extends AbstractDialogView {

    private int mViewResId = View.NO_ID;
    private View mContentView;

    public DialogView(int layoutResId) {
        this.mViewResId = layoutResId;
    }

    public DialogView(View view) {
        this.mContentView = view;
    }

     DialogView(int layoutResId, int shadowColor) {
        this(layoutResId);
        initShadow(shadowColor);
    }

     DialogView(View view, int shadowColor) {
        this(view);
        initShadow(shadowColor);
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

    @Override
    protected View getContentView() {
        return mContentView;
    }

    @Override
    protected void addContent(LayoutInflater inflater, ViewGroup parent, ViewGroup viewRoot) {
        if (mNeedShadowView) {
            if (mViewResId != View.NO_ID) {
                //add to dialog view root
                mContentView = inflater.inflate(mViewResId, null); //inflate layout
            }
        } else {
            if (mViewResId != View.NO_ID) {
                //add to dialog view root
                mContentView = inflater.inflate(mViewResId, parent, false); //inflate layout
            }
        }
        if (mNeedShadowView) {
            FrameLayout shadowView = new FrameLayout(viewRoot.getContext());
            shadowView.setBackgroundColor(mShadowColor);
//            setShadowViewOutsideCanDismiss(shadowView, true);
            shadowView.addView(mContentView, mLayoutParams);
            shadowView.setFocusable(true);
            shadowView.setFocusableInTouchMode(true);
            viewRoot.addView(shadowView, mShadowLayoutParams);
        } else {
            mContentView.setFocusable(true);
            mContentView.setFocusableInTouchMode(true);
            viewRoot.addView(mContentView);
        }
    }

    @Override
    protected void initShadow(int shadowColor) {
        this.mNeedShadowView = true;
        this.mShadowColor = shadowColor;
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
