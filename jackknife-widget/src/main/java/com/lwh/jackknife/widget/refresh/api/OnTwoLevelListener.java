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

package com.lwh.jackknife.widget.refresh.api;

import android.support.annotation.NonNull;

/**
 * 二级刷新监听器
 */
public interface OnTwoLevelListener {
    /**
     * 二级刷新触发
     *
     * @param refreshLayout 刷新布局
     * @return true 将会展开二楼状态 false 关闭刷新
     */
    boolean onTwoLevel(@NonNull RefreshLayout refreshLayout);
}