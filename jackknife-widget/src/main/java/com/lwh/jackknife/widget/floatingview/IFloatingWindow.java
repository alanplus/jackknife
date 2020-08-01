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

package com.lwh.jackknife.widget.floatingview;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;

public interface IFloatingWindow {

    FloatingWindow remove();

    FloatingWindow add(@DrawableRes int drawableResId);

    FloatingWindow attach(Activity activity);

    FloatingWindow attach(FrameLayout container);

    FloatingWindow detach(Activity activity);

    FloatingWindow detach(FrameLayout container);

    FloatingMagnetView getView();

    FloatingWindow icon(@DrawableRes int drawableResId);

    FloatingWindow customView(FloatingMagnetView magnetView);

    FloatingWindow customView(@LayoutRes int resource);

    FloatingWindow layoutParams(ViewGroup.LayoutParams params);

    FloatingWindow listener(MagnetViewListener listener);
}
