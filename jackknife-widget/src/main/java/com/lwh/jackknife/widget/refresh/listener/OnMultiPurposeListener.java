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

package com.lwh.jackknife.widget.refresh.listener;

import com.lwh.jackknife.widget.refresh.api.RefreshFooter;
import com.lwh.jackknife.widget.refresh.api.RefreshHeader;

/**
 * 多功能监听器
 */
public interface OnMultiPurposeListener extends OnRefreshLoadMoreListener, OnStateChangedListener {
    /**
     * 手指拖动下拉（会连续多次调用，添加isDragging并取代之前的onPulling、onReleasing）
     *
     * @param header        头部
     * @param isDragging    true 手指正在拖动 false 回弹动画
     * @param percent       下拉的百分比 值 = offset/footerHeight (0 - percent - (footerHeight+maxDragHeight) / footerHeight )
     * @param offset        下拉的像素偏移量  0 - offset - (footerHeight+maxDragHeight)
     * @param headerHeight  高度 HeaderHeight or FooterHeight
     * @param maxDragHeight 最大拖动高度
     */
    void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight);

    //    void onHeaderPulling(RefreshHeader header, float percent, int offset, int headerHeight, int maxDragHeight);
//    void onHeaderReleasing(RefreshHeader header, float percent, int offset, int headerHeight, int maxDragHeight);
    void onHeaderReleased(RefreshHeader header, int headerHeight, int maxDragHeight);

    void onHeaderStartAnimator(RefreshHeader header, int headerHeight, int maxDragHeight);

    void onHeaderFinish(RefreshHeader header, boolean success);

    /**
     * 手指拖动上拉（会连续多次调用，添加isDragging并取代之前的onPulling、onReleasing）
     *
     * @param footer        尾部
     * @param isDragging    true 手指正在拖动 false 回弹动画
     * @param percent       下拉的百分比 值 = offset/footerHeight (0 - percent - (footerHeight+maxDragHeight) / footerHeight )
     * @param offset        下拉的像素偏移量  0 - offset - (footerHeight+maxDragHeight)
     * @param footerHeight  高度 HeaderHeight or FooterHeight
     * @param maxDragHeight 最大拖动高度
     */
    void onFooterMoving(RefreshFooter footer, boolean isDragging, float percent, int offset, int footerHeight, int maxDragHeight);

    //    void onFooterPulling(RefreshFooter footer, float percent, int offset, int footerHeight, int maxDragHeight);
//    void onFooterReleasing(RefreshFooter footer, float percent, int offset, int footerHeight, int maxDragHeight);
    void onFooterReleased(RefreshFooter footer, int footerHeight, int maxDragHeight);

    void onFooterStartAnimator(RefreshFooter footer, int footerHeight, int maxDragHeight);

    void onFooterFinish(RefreshFooter footer, boolean success);
}
