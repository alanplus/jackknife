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

package com.lwh.jackknife.widget.pull.state;

import android.view.View;

import com.lwh.jackknife.widget.pull.Callback;

public interface PullState {

    int STATE_NORMAL = 0;
    int STATE_HEADER_CHANGE = 1;
    int STATE_HEADER_START_ANIMATION = 2;
    int STATE_FOOTER_CHANGE = 3;
    int STATE_FOOTER_START_ANIMATION = 4;
    int STATE_DONE = 5;
    int TARGET_HEADER_VIEW = 10;
    int TARGET_FOOTER_VIEW = 20;

    int getFlag();

    void onEnter(int target, View targetView, Callback callback);
}
