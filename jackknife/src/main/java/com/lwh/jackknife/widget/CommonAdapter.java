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

package com.lwh.jackknife.widget;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class CommonAdapter<BEAN> extends android.widget.BaseAdapter {

	private LayoutInflater mInflater;
	private static final String METHOD_INFLATE = "inflate";
	protected List<BEAN> mDatas;
	private Context mContext;

	public CommonAdapter(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mDatas = new ArrayList<>();
		mContext = context;
	}

	public CommonAdapter(Context context, List<BEAN> datas) {
		this(context);
		addItems(datas);
	}

	@Override
	public int getCount() {
		if (mDatas != null) {
			return mDatas.size();
		} else {
			return -1;
		}
	}

	@Override
	public Object getItem(int position) {
		if (position >= 0 && position < mDatas.size()) {
			return mDatas.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void addItem(BEAN data) {
		mDatas.add(data);
		notifyDataSetChanged();
	}

	public void addItems(List<BEAN> datas) {
		mDatas.addAll(datas);
		notifyDataSetChanged();
	}

	public void replaceItems(List<BEAN> datas) {
		mDatas = datas;
		notifyDataSetInvalidated();
	}
	
	public void removeItem(BEAN data){
		mDatas.remove(data);
		notifyDataSetChanged();
	}

	public void removeItem(int position) {
		mDatas.remove(position);
		notifyDataSetChanged();
	}

	public void clear() {
		mDatas.clear();
		notifyDataSetChanged();
	}

	protected abstract int getItemLayoutId();
	protected abstract int[] getItemViewIds();

	protected View inflateView()
			throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		int layoutId = getItemLayoutId();
		Class<?> inflaterClass = LayoutInflater.class;
		Method inflateMethod = inflaterClass.getMethod(METHOD_INFLATE, int.class, ViewGroup.class);
		return (View) inflateMethod.invoke(mInflater, layoutId, null);
	}

	protected abstract <VIEW extends View> void onBindViewHolder(int position, ViewHolder<VIEW> holder);

	public List<BEAN> getDatas() {
		return mDatas;
	}

	@Override
	public final View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			try {
				int[] itemViewIds = getItemViewIds();
				holder = new ViewHolder(mContext, inflateView(), itemViewIds, parent, position);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			holder = (ViewHolder) convertView.getTag();
			holder.setPosition(position);
		}
		onBindViewHolder(position, holder);
		return holder.getConvertView();
	}
}
