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

package com.lwh.jackknife.widget.recyclerview.adapter;

import android.view.View;

/**
 * Created by Mr.Jude on 2015/8/18.
 */
public interface EventDelegate {
    void addData(int length);
    void clear();

    void stopLoadMore();
    void pauseLoadMore();
    void resumeLoadMore();

    void setMore(View view, RecyclerArrayAdapter.OnMoreListener listener);
    void setNoMore(View view, RecyclerArrayAdapter.OnNoMoreListener listener);
    void setErrorMore(View view, RecyclerArrayAdapter.OnErrorListener listener);
    void setMore(int res, RecyclerArrayAdapter.OnMoreListener listener);
    void setNoMore(int res, RecyclerArrayAdapter.OnNoMoreListener listener);
    void setErrorMore(int res, RecyclerArrayAdapter.OnErrorListener listener);
}
