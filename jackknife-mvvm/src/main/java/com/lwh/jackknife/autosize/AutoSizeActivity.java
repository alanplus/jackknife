/*
 * Copyright (C) 2020 The JackKnife Open Source Project
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

package com.lwh.jackknife.autosize;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.lwh.jackknife.autosize.widget.AutoFrameLayout;
import com.lwh.jackknife.autosize.widget.AutoLinearLayout;
import com.lwh.jackknife.autosize.widget.AutoRelativeLayout;

public class AutoSizeActivity extends AppCompatActivity {

    private static final String LAYOUT_LINEAR_LAYOUT = "LinearLayout";
    private static final String LAYOUT_FRAME_LAYOUT = "FrameLayout";
    private static final String LAYOUT_RELATIVE_LAYOUT = "RelativeLayout";

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = null;
        if (name.equals(LAYOUT_FRAME_LAYOUT)) {
            view = new AutoFrameLayout(context, attrs);
        }
        if (name.equals(LAYOUT_LINEAR_LAYOUT)) {
            view = new AutoLinearLayout(context, attrs);
        }
        if (name.equals(LAYOUT_RELATIVE_LAYOUT)) {
            view = new AutoRelativeLayout(context, attrs);
        }
        if (view != null) return view;
        return super.onCreateView(name, context, attrs);
    }
}
