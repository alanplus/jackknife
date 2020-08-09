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

package com.lwh.jackknife.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class JKAdapter<T> extends RecyclerView.Adapter<JKViewHolder<T>> {

    protected List<T> mInfos;
    protected OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public JKAdapter(List<T> infos) {
        super();
        this.mInfos = infos;
    }

    /**
     * 遍历所有 {@link JKViewHolder}, 释放他们需要释放的资源
     *
     * @param recyclerView {@link RecyclerView}
     */
    public static void releaseAllHolder(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return;
        }
        for (int i = recyclerView.getChildCount() - 1; i >= 0; i--) {
            final View view = recyclerView.getChildAt(i);
            RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
            if (viewHolder instanceof JKViewHolder) {
                ((JKViewHolder) viewHolder).onRelease();
            }
        }
    }

    /**
     * 创建 {@link JKViewHolder}
     *
     * @param parent   父容器
     * @param viewType 布局类型
     * @return {@link JKViewHolder}
     */
    @Override
    public JKViewHolder<T> onCreateViewHolder(ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(viewType), parent, false);
        JKViewHolder<T> mHolder = getHolder(view, viewType);
        //设置Item点击事件
        mHolder.setOnItemClickListener(new JKViewHolder.OnViewClickListener() {
            @Override
            public void onViewClick(View view, int position) {
                if (mOnItemClickListener != null && mInfos.size() > 0) {
                    //noinspection unchecked
                    mOnItemClickListener.onItemClick(view, viewType, mInfos.get(position), position);
                }
            }
        });
        return mHolder;
    }

    /**
     * 绑定数据
     *
     * @param holder   {@link JKViewHolder}
     * @param position 在 RecyclerView 中的位置
     */
    @Override
    public void onBindViewHolder(JKViewHolder<T> holder, int position) {
        holder.setData(mInfos.get(position), position);
    }

    /**
     * 返回数据总个数
     *
     * @return 数据总个数
     */
    @Override
    public int getItemCount() {
        return mInfos.size();
    }

    /**
     * 返回数据集合
     *
     * @return 数据集合
     */
    public List<T> getInfos() {
        return mInfos;
    }

    /**
     * 获得 RecyclerView 中某个 position 上的 item 数据
     *
     * @param position 在 RecyclerView 中的位置
     * @return 数据
     */
    public T getItem(int position) {
        return mInfos == null ? null : mInfos.get(position);
    }

    /**
     * 让子类实现用以提供 {@link JKViewHolder}
     *
     * @param v        用于展示的 {@link View}
     * @param viewType 布局类型
     * @return {@link JKViewHolder}
     */
    @NonNull
    public abstract JKViewHolder<T> getHolder(@NonNull View v, int viewType);

    /**
     * 提供用于 item 布局的 {@code layoutId}
     *
     * @param viewType 布局类型
     * @return 布局 id
     */
    public abstract int getLayoutId(int viewType);

    /**
     * 设置 item 点击事件
     *
     * @param listener
     */
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     * item 点击事件
     *
     * @param <T>
     */
    public interface OnRecyclerViewItemClickListener<T> {

        /**
         * item 被点击
         *
         * @param view     被点击的 {@link View}
         * @param viewType 布局类型
         * @param data     数据
         * @param position 在 RecyclerView 中的位置
         */
        void onItemClick(@NonNull View view, int viewType, @NonNull T data, int position);
    }
}