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

public interface MagnetViewListener {

    void onDown(FloatingMagnetView view);   //悬浮磁石控件被按下

    void onUp(FloatingMagnetView view); //手指从悬浮磁石控件松开

    void onRemove(FloatingMagnetView view); //拖拽悬浮磁石控件到删除区域

    void onClick(FloatingMagnetView view);  //触发点击事件
}
