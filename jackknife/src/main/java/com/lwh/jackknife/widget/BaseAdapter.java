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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {

	private List<T> mBeans = null;
	private LayoutInflater mInflater = null;
	private static final String METHOD_INFLATE = "inflate";
	private View mConvertView;
	private ViewHolder<? extends View> mViewHolder;

	public BaseAdapter(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public BaseAdapter(Context context, List<T> beans) {
		this(context);
		addItems(beans);
	}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ItemViewId {
		int[]value();
	}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ItemLayoutId {
		int value();
	}

	@Override
	public int getCount() {
		if (mBeans != null) {
			return mBeans.size();
		} else {
			return Integer.MIN_VALUE;
		}
	}

	@Override
	public Object getItem(int position) {
		if (position >= 0 && position < mBeans.size()) {
			return mBeans.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void addItem(T data) {
		mBeans.add(data);
		notifyDataSetChanged();
	}

	public void addItem(int position, T bean) {
		mBeans.add(position, bean);
		notifyDataSetChanged();
	}

	public void addItems(List<T> beans) {
		mBeans.addAll(beans);
		notifyDataSetChanged();
	}

	public void addItems(int start, List<T> beans) {
		mBeans.addAll(start, beans);
		notifyDataSetChanged();
	}

	public void replaceItem(int position, T bean) {
		mBeans.set(position, bean);
		notifyDataSetChanged();
	}

	public void replaceItems(int start, List<T> beans) {
		for (T bean : beans) {
			mBeans.set(start, bean);
			start++;
		}
	}

	public void replace(List<T> beans) {
		mBeans = beans;
		notifyDataSetInvalidated();
	}
	
	public void removeItem(T bean){
		mBeans.remove(bean);
		notifyDataSetChanged();
	}

	public void removeItem(int position) {
		mBeans.remove(position);
		notifyDataSetChanged();
	}

	public void removeItems(int start, int end) {
		for (int i = start; i <= end; i++) {
			mBeans.remove(i);
		}
		notifyDataSetChanged();
	}

	public void clear() {
		mBeans.clear();
		notifyDataSetChanged();
	}

	private View inflateView()
			throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Class<?> adapterClass = getClass();
		ItemLayoutId itemLayoutId = adapterClass.getAnnotation(ItemLayoutId.class);
		int layoutId = itemLayoutId.value();
		Class<?> inflaterClass = LayoutInflater.class;
		Method inflateMethod = inflaterClass.getMethod(METHOD_INFLATE, int.class, ViewGroup.class);
		return (View) inflateMethod.invoke(mInflater, layoutId, null);
	}

	private int[] getItemViewIds() {
		Class<?> adapterClass = getClass();
		ItemViewId itemViewId = adapterClass.getAnnotation(ItemViewId.class);
		if (itemViewId != null) {
			return itemViewId.value();
		} else {
			return null;
		}
	}

	protected abstract <T extends View> void onBindView(int position, SparseArray<T> views);

	public List<T> getBeans() {
		return mBeans;
	}

	@Override
	public final View getView(int position, View convertView, ViewGroup parent) {
		mConvertView = convertView;
		if (mConvertView == null) {
			mViewHolder = new ViewHolder();
			try {
				mConvertView = inflateView();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			int[] itemViewIds = getItemViewIds();
			for (int id : itemViewIds) {
				mViewHolder.get(id);
			}
			mConvertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) mConvertView.getTag();
		}
		onBindView(position, mViewHolder.mViews);
		return mConvertView;
	}

	public class ViewHolder<T extends View> {

		private SparseArray<T> mViews;

		private ViewHolder() {
			mViews = new SparseArray<>();
		}

		private T get(int id) {
			View view = mViews.get(id);
			if (view == null) {
				view = mConvertView.findViewById(id);
				mViews.put(id, (T) view);
			}
			return (T) view;
		}
	}
}
