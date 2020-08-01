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

package com.lwh.jackknife.widget.refresh.header.flyrefresh;

import android.content.Context;
import android.util.AttributeSet;

import com.lwh.jackknife.widget.refresh.header.internal.pathview.PathsView;
import com.lwh.jackknife.widget.refresh.util.SmartUtils;

/**
 * 纸飞机视图
 */
public class FlyView extends PathsView {

    public FlyView(Context context) {
        this(context, null);
    }

    public FlyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.parserColors(0xffffffff);
        if (!mPathsDrawable.parserPaths("M2.01,21L23,12 2.01,3 2,10l15,2 -15,2z")) {
            mPathsDrawable.declareOriginal(2, 3, 20, 18);
        }
        int side = SmartUtils.dp2px(25);
        mPathsDrawable.setBounds(0, 0, side, side);
    }
}
