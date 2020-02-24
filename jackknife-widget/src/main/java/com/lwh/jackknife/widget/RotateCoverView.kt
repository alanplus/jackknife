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

package com.lwh.jackknife.av.widget

import android.animation.ObjectAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.RESTART
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import com.lwh.jackknife.widget.CircleTextImageView
import com.lwh.jackknife.widget.R

class RotateCoverView @JvmOverloads constructor(internal var context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : CircleTextImageView(context, attrs, defStyleAttr) {

    val ratoteAnimator: ObjectAnimator

    init {
        ratoteAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f)
        ratoteAnimator.setInterpolator(LinearInterpolator())
        ratoteAnimator.setDuration(20000)
        ratoteAnimator.repeatMode = RESTART
        ratoteAnimator.repeatCount = INFINITE
        setBorderColorResource(R.color.black)
        setBorderWidth(5)
        setImageResource(R.drawable.jknf_doramusic_logo)
    }

    fun start() {
        ratoteAnimator.setupStartValues()
        ratoteAnimator.start()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun pause() {
        ratoteAnimator.pause()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun resume() {
        ratoteAnimator.resume()
    }
}
