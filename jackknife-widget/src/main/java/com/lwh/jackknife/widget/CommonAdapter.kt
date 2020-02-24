/*
 * Copyright (C) 2017 The JackKnife Open Source Project
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import java.lang.reflect.InvocationTargetException
import java.util.*
import kotlin.collections.ArrayList

abstract class CommonAdapter<BEAN>(context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater

    @JvmField
    var datas: MutableList<BEAN>?
    private val context: Context
    override fun getCount(): Int {
        return if (datas != null) {
            datas!!.size
        } else {
            -1
        }
    }

    fun getChildAt(position: Int): BEAN? {
        return if (position >= 0 && position < datas!!.size) {
            datas!![position]
        } else {
            null
        }
    }

    override fun getItem(position: Int): BEAN? {
        return getChildAt(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun addItem(data: BEAN) {
        datas!!.add(data)
        notifyDataSetChanged()
    }

    open fun addItems(datas: MutableList<BEAN>?) {
        this.datas!!.addAll(datas!!)
        notifyDataSetChanged()
    }

    fun replaceItems(datas: MutableList<BEAN>?) {
        this.datas = datas
        notifyDataSetInvalidated()
    }

    fun removeItem(data: BEAN) {
        datas!!.remove(data)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        datas!!.removeAt(position)
        notifyDataSetChanged()
    }

    fun clear() {
        datas!!.clear()
        notifyDataSetChanged()
    }

    protected abstract val itemLayoutId: Int
    protected abstract val itemViewIds: IntArray

    open fun initDatas(): MutableList<BEAN> {
        return ArrayList()
    }

    @Throws(NoSuchMethodException::class, IllegalArgumentException::class, IllegalAccessException::class, InvocationTargetException::class)
    protected fun inflateView(): View {
        val layoutId = itemLayoutId
        val inflaterClass: Class<*> = LayoutInflater::class.java
        val inflateMethod = inflaterClass.getMethod(METHOD_INFLATE, Int::class.javaPrimitiveType, ViewGroup::class.java)
        return inflateMethod.invoke(inflater, layoutId, null) as View
    }

    /**
     * To determine how each item is displayed?
     *
     * @param position The position of the entity class in the collection.
     * @param data     The entity class.
     * @param holder   The view cache.
     * @param <VIEW>   Subtype of view.
    </VIEW> */
    protected abstract fun <VIEW : View?> onBindViewHolder(position: Int, data: BEAN?, holder: ViewHolder<VIEW>?)

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        var holder: ViewHolder<*>? = null
        if (convertView == null) {
            try {
                val itemViewIds = itemViewIds
                holder = ViewHolder<View>(context, inflateView(), itemViewIds, parent, position)
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
        } else {
            holder = convertView.tag as ViewHolder<*>
            holder.setPosition(position)
        }
        val bean = getChildAt(position)
        onBindViewHolder(position, bean, holder)
        return holder!!.convertView
    }

    companion object {
        private const val METHOD_INFLATE = "inflate"
    }

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        datas = ArrayList()
        this.context = context
        val datas = initDatas()
        datas?.let { addItems(it) }
    }
}