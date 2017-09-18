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

/**
 * 适配器基类，继承此类可以适配任何类型的Bean。
 *
 * @author lwh
 * @param <T> 数据模型。
 */
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
		bindDataSet(beans);
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

	/**
	 * Binds data set for this adapter.You can't invoke the method when the
	 * adapter has data set.It is called in the Constructor commonly.
	 * 为本是适配器绑定数据集。当适配器有数据的时候，你不能调用这个方法。它通常在构造方法中被调用。
	 */
	public void bindDataSet(List<T> beans) {
		if (mBeans == null) {
			mBeans = beans;
			notifyDataSetChanged();
		} else
			throw new IllegalStateException("Data set is already binded.");
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

	/**
	 * 添加一个条目
	 */
	public void addItem(T data) {
		mBeans.add(data);
		notifyDataSetChanged();
	}

	/**
	 * 在指定位置添加一个条目
	 */
	public void addItem(int position, T bean) {
		mBeans.add(position, bean);
		notifyDataSetChanged();
	}

	/**
	 * 在适配器末尾添加一堆条目
	 */
	public void addItems(List<T> beans) {
		mBeans.addAll(beans);
		notifyDataSetChanged();
	}

	/**
	 * 从适配器的指定位置开始，添加一堆条目
	 */
	public void addItems(int start, List<T> beans) {
		mBeans.addAll(start, beans);
		notifyDataSetChanged();
	}

	/**
	 * 替换指定位置的条目
	 */
	public void replaceItem(int position, T bean) {
		mBeans.set(position, bean);
		notifyDataSetChanged();
	}

	/**
	 * 从指定位置开始替换一堆条目
	 */
	public void replaceItems(int start, List<T> beans) {
		for (T bean : beans) {
			mBeans.set(start, bean);
			start++;
		}
	}

	/**
	 * Replaces all data in BaseAdapter whether the data is empty or not.
	 * 替换适配器中所有条目，无论数据是否为空
	 */
	public void replace(List<T> beans) {
		mBeans = beans;
		notifyDataSetInvalidated();
	}
	
	public void removeItem(T bean){
		mBeans.remove(bean);
		notifyDataSetChanged();
	}

	/**
	 * 从指定位置移除一个条目
	 */
	public void removeItem(int position) {
		mBeans.remove(position);
		notifyDataSetChanged();
	}

	/**
	 * 移除连续下标的一堆条目
	 * @param start 开始的下标
	 * @param end 结束的下标
	 */
	public void removeItems(int start, int end) {
		for (int i = start; i <= end; i++) {
			mBeans.remove(i);
		}
		notifyDataSetChanged();
	}

	/**
	 * 清空所有条目
	 */
	public void clear() {
		mBeans.clear();
		notifyDataSetChanged();
	}

	/**
	 * 加载布局
	 * @hide
	 */
	private View inflateView()
			throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Class<?> adapterClass = getClass();
		ItemLayoutId itemLayoutId = adapterClass.getAnnotation(ItemLayoutId.class);
		int layoutId = itemLayoutId.value();
		Class<?> inflaterClass = LayoutInflater.class;
		Method inflateMethod = inflaterClass.getMethod(METHOD_INFLATE, int.class, ViewGroup.class);
		return (View) inflateMethod.invoke(mInflater, layoutId, null);
	}

	/**
	 * 获取布局文件中条目的id
	 * @hide
	 */
	private int[] getItemViewIds() {
		Class<?> adapterClass = getClass();
		ItemViewId itemViewId = adapterClass.getAnnotation(ItemViewId.class);
		if (itemViewId != null) {
			return itemViewId.value();
		} else {
			return null;
		}
	}

	/**
	 * 给条目的子控件绑定数据
	 * @param position 位置
	 * @param views 存放了条目中的控件，通过get(int id)拿
	 */
	protected abstract <T extends View> void onBindView(int position, SparseArray<T> views);

	/**
	 * 获取所有的Bean
	 */
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
