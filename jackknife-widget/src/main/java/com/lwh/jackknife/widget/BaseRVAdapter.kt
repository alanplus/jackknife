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

package com.lwh.jackknife.widget

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections

/**
 * 万能的[RecyclerView]适配器。
 *
 * @author lwh
 * @param <BEAN> 适配的数据类型。
</BEAN> */
abstract class BaseRVAdapter<BEAN>(val context: Context)
        : RecyclerView.Adapter<BaseRVAdapter.ViewHolder>() {

    /**
     * 用来加载条目的布局。
     */
    protected val inflater: LayoutInflater

    /**
     * 数据。
     */
    @Volatile
    var datas: MutableList<BEAN>? = null

    /**
     * 默认的替换条目的策略。
     */
    private var defaultPolicy: ReplacePolicy<BEAN>? = null

    /**
     * 条目点击事件。
     */
    var onItemClickListener: OnItemClickListener? = null

    /**
     * 条目长按事件。
     */
    var onItemLongClickListener: OnItemLongClickListener? = null

    fun itemClick(l: OnItemClickListener) {
        this.onItemClickListener = l
    }

    fun itemLongClick(l: OnItemLongClickListener) {
        this.onItemLongClickListener = l
    }

    /**
     * 条目控件的id。
     *
     * @return 例如R.id.btn_01,R.id.btn_02,btn_03
     */
    protected abstract val itemViewIds: IntArray

    /**
     * 条目布局文件的id。
     *
     * @return 例如item_example.xml
     */
    protected abstract val itemId: Int

    @Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
    @Retention(RetentionPolicy.RUNTIME)
    annotation class ItemViewIds(vararg val value: Int)

    @Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
    @Retention(RetentionPolicy.RUNTIME)
    annotation class ItemId(val value: Int)

    init {
        this.inflater = LayoutInflater.from(context)
        this.datas = ArrayList()
        applyDefaultReplacePolicy()
    }

    constructor(context: Context, datas: MutableList<BEAN>) : this(context) {
        this.datas = datas
        applyDefaultReplacePolicy()
    }

    constructor(context: Context, datas: Array<BEAN>) : this(context) {
        bindDatas(Arrays.asList(*datas))
        applyDefaultReplacePolicy()
    }


    interface DataConverter<T, BEAN> {
        fun convertDatas(datas: T): ArrayList<BEAN>
    }

    interface OnItemClickListener {
        fun onItemClick(parent: ViewGroup, pos: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(parent: ViewGroup, pos: Int): Boolean
    }

    /**
     * 绑定数据到适配器。
     *
     * @param datas 要绑定的数据。
     */
    private fun bindDatas(datas: MutableList<BEAN>) {
        if (datas!!.size > 0) {
            datas!!.clear()
        }
        datas!!.addAll(datas)
        notifyDataSetChanged()
    }

    /**
     * 应用默认的替换策略。
     */
    private fun applyDefaultReplacePolicy() {
        defaultPolicy = DefaultReplacePolicy()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var id = itemId
        var viewIds = itemViewIds
        val adapterClass = javaClass
        val itemId = adapterClass.getAnnotation(ItemId::class.java)
        if (itemId != null) {
            id = itemId!!.value
        }
        val itemViewIds = adapterClass.getAnnotation(ItemViewIds::class.java)
        if (itemViewIds != null) {
            viewIds = itemViewIds!!.value
        }
        val view = inflater.inflate(id, parent, false)
        return ViewHolder(view, viewIds)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getData(position)
        onBindViewHolder(holder, position, data)
        bindListeners(holder)
    }

    /**
     * 在此处理数据的加载等。
     *
     * @param holder View缓存对象。
     * @param position 条目在列表中的位置，从0开始。
     * @param data 给条目加载数据的模型对象。
     */
    abstract fun onBindViewHolder(holder: ViewHolder, position: Int, data: BEAN)

    /**
     * 绑定条目的点击事件和长按事件的监听。
     *
     * @param holder 缓存View的对象。
     */
    private fun bindListeners(holder: ViewHolder) {
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener {
                onItemClickListener!!.onItemClick(holder.itemView as ViewGroup,
                        holder.adapterPosition)
            }
        }
        if (onItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener {
                onItemLongClickListener!!.onItemLongClick(holder.itemView as ViewGroup,
                        holder.adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (datas != null) {
            datas!!.size
        } else -1
    }

    /**
     * 主要用作控件的缓存优化。
     *
     * @param <VIEW> 要缓存的控件。
    </VIEW> */
    class ViewHolder(itemView: View, itemViewIds: IntArray)
        : RecyclerView.ViewHolder(itemView) {

        /**
         * 缓存控件的稀疏数组。
         */
        private val viewCache: SparseArray<View>

        init {
            viewCache = SparseArray()
            if (itemViewIds != null && itemViewIds.size > 0) {
                for (id in itemViewIds) {
                    viewCache.put(id, findViewById(id))
                }
            }
        }

        /**
         * 如果没有缓存，则创建一个以传入id为key的缓存控件；如果有缓存，则获取key等于传入id的缓存控件。
         *
         * @param id 用作缓存key的id。
         * @return 缓存的控件。
         */
        fun findViewById(id: Int): View {
            var view = viewCache.get(id)
            if (view == null) {
                view = itemView.findViewById(id)
                viewCache.put(id, view as View)
            }
            return view as View
        }

        fun setText(textViewId: Int, text: String) {
            val textView = findViewById(textViewId) as TextView
            textView.text = text
        }

        fun setImageResource(imageViewId: Int, resId: Int) {
            val imageView = findViewById(imageViewId) as ImageView
            imageView.setImageResource(resId)
        }
    }

    fun addItem(data: BEAN) {
        datas!!.add(data)
        val position = datas!!.size - 1
        notifyItemInserted(position)
    }

    fun addItem(data: BEAN, index: Int) {
        datas!!.add(index, data)
        notifyItemChanged(index)
    }

    fun addItems(datas: MutableList<BEAN>) {
        val lastSize = itemCount
        val newSize = datas.size
        datas!!.addAll(datas)
        notifyItemRangeInserted(lastSize, newSize)
    }

    fun setItem(position: Int, data: BEAN) {
        datas!![position] = data
        notifyItemChanged(position)
    }

    fun setItems(datas: MutableList<BEAN>) {
        setItems(0, datas)
    }

    @JvmOverloads
    fun setItems(start: Int, datas: MutableList<BEAN>, policy: ReplacePolicy<BEAN>? = defaultPolicy) {
        if (datas.size + start == datas!!.size) {
            for (i in start until itemCount) {
                datas!![i] = datas[start + i]
            }
        }
        if (datas.size + start > datas!!.size) {
            policy!!.replaceIfOutOfRange(this, datas, start, datas)
        }
        if (datas.size + start < datas!!.size) {
            policy!!.replaceIfNotUpToCapacity(this, datas, start, datas)
        }
    }

    /**
     * 用于确定条目的替换策略。
     *
     * @param <BEAN> 数据模型对象。
    </BEAN> */
    interface ReplacePolicy<BEAN> {

        /**
         * 在给定条目数量超出已有条目的处理方式。
         *
         * @param adapter RV的适配器。
         * @param dstDatas 目标数据。
         * @param start 要替换的开始索引。
         * @param srcDatas 原始数据。
         */
        fun replaceIfOutOfRange(adapter: RecyclerView.Adapter<*>, dstDatas: MutableList<BEAN>, start: Int,
                                srcDatas: MutableList<BEAN>)

        /**
         * 在给定条目数量不足以覆盖所有已有条目的处理方式。
         *
         * @param adapter
         * @param dstDatas
         * @param start
         * @param srcDatas
         */
        fun replaceIfNotUpToCapacity(adapter: RecyclerView.Adapter<*>, dstDatas: MutableList<BEAN>,
                                     start: Int, srcDatas: MutableList<BEAN>)
    }

    /**
     * 用于自定义数据替换的策略。
     *
     * @param policy 替换数据的策略。
     */
    fun setReplacePolicy(policy: ReplacePolicy<BEAN>) {
        this.defaultPolicy = policy
    }

    /**
     * 默认的替换策略，超出将忽略超出的条目，不足将只覆盖数据，不会影响到超出范围的。
     */
    private inner class DefaultReplacePolicy : ReplacePolicy<BEAN> {

        override fun replaceIfOutOfRange(adapter: RecyclerView.Adapter<*>, dstDatas: MutableList<BEAN>,
                                         start: Int, srcDatas: MutableList<BEAN>) {
            val leftSize = dstDatas.size - start
            for (i in start until leftSize) {
                dstDatas[i] = srcDatas[i]
            }
            adapter.notifyItemRangeChanged(start, leftSize)
        }

        override fun replaceIfNotUpToCapacity(adapter: RecyclerView.Adapter<*>,
                                              dstDatas: MutableList<BEAN>, start: Int, srcDatas: MutableList<BEAN>) {
            val srcSize = srcDatas.size
            for (i in start until start + srcSize) {
                dstDatas!![i] = srcDatas[i]
            }
            adapter.notifyItemRangeChanged(start, srcSize)
        }
    }

    /**
     * 移除数据。
     *
     * @param position 要移除数据的下标。
     */
    fun removeItem(position: Int) {
        datas!!.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * 移除数据。
     *
     * @throws ArrayIndexOutOfBoundsException
     * @param start 从哪条记录开始？
     * @param count 移除数据的条数。
     */
    fun removeItem(start: Int, count: Int) {
        val end = start + count
        for (i in start until end) {
            datas!!.removeAt(i)
        }
        notifyItemRangeRemoved(start, count)
    }

    /**
     * 清空所有的条目。
     */
    fun clear() {
        val dataSize = datas!!.size
        datas!!.clear()
        notifyItemRangeRemoved(0, dataSize)
    }

    /**
     * 获取指定位置的Bean数据。
     *
     * @param position 要获取的Bean数据的位置。
     * @return Bean数据。
     */
    fun getData(position: Int): BEAN {
        return datas!![position]
    }

    /**
     * 置顶指定条目。
     *
     * @param position 需要置顶的条目的位置。
     */
    fun stickItem(position: Int) {
        val data = datas!![position]
        removeItem(position)
        addItem(data)
    }

    /**
     * 将条目的顺序倒过来。
     */
    fun reverseItems() {
        Collections.reverse(datas!!)
        notifyDataSetChanged()
    }
}