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

package com.lwh.jackknife.widget.refresh.impl;

import android.graphics.PointF;
import android.view.View;

import com.lwh.jackknife.widget.refresh.api.ScrollBoundaryDecider;
import com.lwh.jackknife.widget.refresh.util.SmartUtils;

/**
 * 滚动边界
 */
@SuppressWarnings("WeakerAccess")
public class ScrollBoundaryDeciderAdapter implements ScrollBoundaryDecider {

    //<editor-fold desc="Internal">
    public PointF mActionEvent;
    public ScrollBoundaryDecider boundary;
    public boolean mEnableLoadMoreWhenContentNotFull = true;

//    void setScrollBoundaryDecider(ScrollBoundaryDecider boundary){
//        this.boundary = boundary;
//    }

//    void setActionEvent(MotionEvent event) {
//        //event 在没有必要时候会被设置为 null
//        mActionEvent = event;
//    }
//
//    public void setEnableLoadMoreWhenContentNotFull(boolean enable) {
//        mEnableLoadMoreWhenContentNotFull = enable;
//    }
    //</editor-fold>

    //<editor-fold desc="ScrollBoundaryDecider">
    @Override
    public boolean canRefresh(View content) {
        if (boundary != null) {
            return boundary.canRefresh(content);
        }
        //mActionEvent == null 时 canRefresh 不会动态递归搜索
        return SmartUtils.canRefresh(content, mActionEvent);
    }

    @Override
    public boolean canLoadMore(View content) {
        if (boundary != null) {
            return boundary.canLoadMore(content);
        }
//        if (mEnableLoadMoreWhenContentNotFull) {
//            //mActionEvent == null 时 canScrollDown 不会动态递归搜索
//            return !ScrollBoundaryUtil.canScrollDown(content, mActionEvent);
//        }
        //mActionEvent == null 时 canLoadMore 不会动态递归搜索
        return SmartUtils.canLoadMore(content, mActionEvent, mEnableLoadMoreWhenContentNotFull);
    }
    //</editor-fold>
}
