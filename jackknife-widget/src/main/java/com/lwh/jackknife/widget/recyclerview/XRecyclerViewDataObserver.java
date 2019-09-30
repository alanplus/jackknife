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

package com.lwh.jackknife.widget.recyclerview;

import android.support.v7.widget.RecyclerView;

import com.lwh.jackknife.widget.recyclerview.adapter.RecyclerArrayAdapter;

public class XRecyclerViewDataObserver extends RecyclerView.AdapterDataObserver {

    private XRecyclerView recyclerView;
    private RecyclerArrayAdapter adapter;

    public XRecyclerViewDataObserver(XRecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        if (recyclerView.getAdapter() instanceof RecyclerArrayAdapter) {
            adapter = (RecyclerArrayAdapter) recyclerView.getAdapter();
        }
    }

    private boolean isHeaderFooter(int position) {
        return adapter != null && (position < adapter.getHeaderCount() || position >= adapter.getHeaderCount() + adapter.getCount());
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
        super.onItemRangeChanged(positionStart, itemCount);
        if (!isHeaderFooter(positionStart)) {
            update();
        }
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        super.onItemRangeInserted(positionStart, itemCount);
        if (!isHeaderFooter(positionStart)) {
            update();
        }
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        super.onItemRangeRemoved(positionStart, itemCount);
        if (!isHeaderFooter(positionStart)) {
            update();
        }
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        super.onItemRangeMoved(fromPosition, toPosition, itemCount);
        update();//header&footer不会有移动操作
    }

    @Override
    public void onChanged() {
        super.onChanged();
        update();//header&footer不会引起changed
    }


    //自动更改Container的样式
    private void update() {
        int count;
        if (recyclerView.getAdapter() instanceof RecyclerArrayAdapter) {
            RecyclerArrayAdapter adapter = ((RecyclerArrayAdapter) recyclerView.getAdapter());
            // 有Header Footer就不显示Empty,但排除EventFooter。
            count = adapter.getCount()+adapter.getHeaderCount()+adapter.getFooterCount()-(adapter.hasEventFooter()?1:0);
        } else {
            count = recyclerView.getAdapter().getItemCount();
        }
        if (count == 0) {
            recyclerView.showEmpty();
        } else {
            recyclerView.showRecycler();
        }
    }
}