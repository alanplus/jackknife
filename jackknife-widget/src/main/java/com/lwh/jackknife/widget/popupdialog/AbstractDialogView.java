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
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lwh.jackknife.widget.R;

public abstract class AbstractDialogView {

    protected static final int INVALID = -1;
    protected static final int INVALID_COLOR = 0;
    protected OnCancelListener mOnCancelListener;
    protected View.OnKeyListener mOnBackListener;
    protected FrameLayout.LayoutParams mLayoutParams = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.BOTTOM
    );

    public interface OnCancelListener {
        void onCancel();
    }

    public FrameLayout.LayoutParams getLayoutParams() {
        return mLayoutParams;
    }

    public View getView(LayoutInflater inflater, ViewGroup parent) {
        View dialogView = inflater.inflate(R.layout.jknf_dialog_view, parent, false);
        FrameLayout dialogViewRoot = (FrameLayout) dialogView.findViewById(R.id
                .jknf_dialog_view_content);
        addContent(inflater, parent, dialogViewRoot);
        return dialogView;
    }

    protected abstract void addContent(LayoutInflater inflater, ViewGroup parent, ViewGroup viewRoot);

    public void setOnCancelListener(OnCancelListener listener) {
        this.mOnCancelListener = listener;
    }

    protected void setOnBackListener(View.OnKeyListener listener) {
        this.mOnBackListener = listener;
    }
}
