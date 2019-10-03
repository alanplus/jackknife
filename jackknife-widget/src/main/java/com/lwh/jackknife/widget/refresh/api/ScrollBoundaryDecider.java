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

import android.view.View;

/**
 * 滚动边界
 */
public interface ScrollBoundaryDecider {
    /**
     * 根据内容视图状态判断是否可以开始下拉刷新
     *
     * @param content 内容视图
     * @return true 将会触发下拉刷新
     */
    boolean canRefresh(View content);

    /**
     * 根据内容视图状态判断是否可以开始上拉加载
     *
     * @param content 内容视图
     * @return true 将会触发加载更多
     */
    boolean canLoadMore(View content);
}
