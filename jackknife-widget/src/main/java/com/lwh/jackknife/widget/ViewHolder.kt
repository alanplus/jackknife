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
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup

class ViewHolder<VIEW : View>(val context: Context, val convertView: View, itemViewIds: IntArray, val viewParent: ViewGroup, var position: Int) {

    val itemViews: SparseArray<VIEW>
    var layoutId: Int = 0
        protected set

    init {
        this.itemViews = SparseArray()
        for (id in itemViewIds) {
            findViewById(id)
        }
        this.convertView.tag = this
    }

    fun findViewById(id: Int): VIEW {
        var view: View? = itemViews.get(id)
        if (view == null) {
            view = convertView.findViewById(id)
            itemViews.put(id, view as VIEW)
        }
        return view as VIEW
    }
}
