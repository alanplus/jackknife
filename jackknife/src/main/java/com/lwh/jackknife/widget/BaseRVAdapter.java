package com.lwh.jackknife.widget;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

/**
 * 万能的{@link RecyclerView}适配器。
 *
 * @author lwh
 * @param <T> 适配的数据类型。
 */
public abstract class BaseRVAdapter<T> extends RecyclerView.Adapter<BaseRVAdapter.ViewHolder> {

    /**
     * Bean的集合。
     */
    private List<T> mBeans;

    /**
     * 上下文。
     */
    private Context mContext;

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ItemViewId{
        int[] value();
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ItemLayoutId{
        int value();
    }

    /**
     * 替换模式。
     *
     * @author lwh
     */
    enum ReplaceMode{
        COVER_ONLY,//只覆盖已有数据
        COVER_APPEND,//覆盖已有数据，超出的部分也扩展
        SHOW_ERROR//显示错误
    }

    public BaseRVAdapter(Context context){
        this.mContext = context;
        mBeans = new ArrayList<>();
    }

    public BaseRVAdapter(List<T> beans, Context context){
        this(context);
        bindDataSet(beans);
    }

    private void bindDataSet(List<T> beans) {
        mBeans.addAll(beans);
        notifyDataSetChanged();
    }

    /**
     * 获取上下文。
     *
     * @return 上下文。
     */
    public Context getContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemLayoutId itemLayoutId = getClass().getAnnotation(ItemLayoutId.class);
        View view = null;
        if (itemLayoutId != null) {
            int id = itemLayoutId.value();
            view = LayoutInflater.from(mContext).inflate(id, parent, false);
        }
        return new ViewHolder(view);
    }

    /**
     * 添加一个条目到适配器末尾。
     *
     * @param bean 条目数据。
     */
    public void addItem(T bean) {
        mBeans.add(bean);
        notifyDataSetChanged();
    }

    /**
     * 在指定位置添加一个条目。
     *
     * @param index 插入的位置。
     * @param bean 条目数据。
     */
    public void addItem(int index, T bean) {
        if (index < 0 || index >= getCount()) {
            return;
        }
        mBeans.add(index, bean);
        notifyDataSetChanged();
    }

    /**
     * 在适配器末尾添加一堆条目。
     *
     * @param beans 条目数据的集合。
     */
    public void addItems(List<T> beans) {
        mBeans.addAll(beans);
        notifyDataSetChanged();
    }

    /**
     * 从适配器的指定位置开始，添加一堆条目。
     *
     * @param startIndex 开始的索引位置。
     * @param beans 条目数据的集合。
     */
    public void addItems(int startIndex, List<T> beans) {
        if (startIndex < 0 || startIndex >= getCount()) {
            return;
        }
        mBeans.addAll(startIndex, beans);
        notifyDataSetChanged();
    }

    /**
     * 替换指定位置的条目。
     *
     * @param index 替换的位置。
     * @param bean 条目数据。
     */
    public void setItem(int index, T bean) {
        if (index < 0 || index >= getCount()){
            return;
        }
        mBeans.set(index, bean);
        notifyDataSetChanged();
    }

    /**
     * 从指定位置开始替换一堆条目。
     *
     * @param start 开始替换条目的位置。
     * @param beans Bean数据集合。
     * @param mode 替换模式。
     */
    public void setItems(int start, List<T> beans, ReplaceMode mode) {
        if (isGonnaOver(start, beans.size())){
            int limit = 0;
            switch (mode){
                case COVER_ONLY:
                    limit = getCount() - start - 1;
                    break;
                case COVER_APPEND:
                    limit = beans.size();
                    break;
                case SHOW_ERROR:
                    throw new RuntimeException("没有足够的可替换的数据。");
            }
            for (int i=0;i < limit;i++){
                mBeans.set(i, beans.get(i));
            }
        }
    }

    /**
     * 替换时将要超出吗？
     *
     * @param start 替换的开始位置。
     * @param count 替换的数量。
     * @return 是否会超出范围。
     */
    private boolean isGonnaOver(int start, int count){
        if (start + count > getCount()){
            return true;
        }
        return false;
    }

    /**
     * 移除一些条目。
     *
     * @param beans 条目数据集合。
     */
    public void replaceItem(List<T> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    /**
     * 移除一个条目。
     *
     * @param bean 条目数据。
     */
    public void removeItem(T bean){
        mBeans.remove(bean);
        notifyDataSetChanged();
    }

    /**
     * 从指定位置移除一个条目。
     *
     * @param index 要移除的索引位置。
     */
    public void removeItem(int index) {
        mBeans.remove(index);
        notifyDataSetChanged();
    }

    /**
     * 移除连续下标的一堆条目。
     *
     * @param startIndex 开始的下标。
     * @param endIndex 结束的下标。
     */
    public void removeItems(int startIndex, int endIndex) {
        if (startIndex <= endIndex && startIndex >=0 && endIndex < getCount()) {
            for (int i = startIndex; i <= endIndex; i++) {
                mBeans.remove(i);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 清空所有条目。
     */
    public void clearItem() {
        mBeans.clear();
        notifyDataSetChanged();
    }

    /**
     * 置顶指定条目。
     *
     * @param position 需要置顶的条目的位置。
     */
    public void stickItem(int position){
        T bean = mBeans.get(position);
        mBeans.remove(position);
        mBeans.add(0, bean);
        notifyDataSetChanged();
    }

    /**
     * 获取Bean数据的个数。
     *
     * @return Bean数据的个数。
     */
    public int getCount(){
        return mBeans.size();
    }

    /**
     * 获取所有的Bean数据。
     *
     * @return Bean数据集合。
     */
    public List<T> getBeans() {
        return mBeans;
    }

    /**
     * 获取指定位置的Bean数据。
     *
     * @param position 要获取的Bean数据的位置。
     * @return Bean数据。
     */
    public T getChildAt(int position){
        return mBeans.get(position);
    }

    @Override
    public int getItemCount() {
        if (mBeans != null) {
            return mBeans.size();
        }
        return -1;
    }

    /**
     * 主要用作控件的缓存优化。
     *
     * @param <V> 要缓存的控件。
     */
    public final class ViewHolder<V extends View> extends RecyclerView.ViewHolder{

        /**
         * 缓存控件的稀疏数组。
         */
        private SparseArray<V> mViews;

        public ViewHolder(View itemView) {
            super(itemView);
            mViews = new SparseArray<>();
            Class<? extends BaseRVAdapter> adapterClass = BaseRVAdapter.class;
            ItemViewId itemViewId = adapterClass.getAnnotation(ItemViewId.class);
            if (itemViewId != null) {
                int[] ids = itemViewId.value();
                for (int id : ids) {
                    mViews.put(id, findViewById(id));
                }
            }
        }

        /**
         * 如果没有缓存，则创建一个以传入id为key的缓存控件；如果有缓存，则获取key等于传入id的缓存控件。
         *
         * @param id 用作缓存key的id。
         * @return 缓存的控件。
         */
        public V findViewById(@IdRes int id){
            View view = mViews.get(id);
            if (view == null){
                view = itemView.findViewById(id);
                mViews.put(id, (V) view);
            }
            return (V) view;
        }
    }
}
