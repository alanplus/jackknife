package com.lwh.jackknife.mvp;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 遵循MVP（Model-View-Presenter）设计理念。使用需要注意以下要点：
 * <ol>
 *     <li>提供UI交互。</li>
 *     <li>在Presenter的控制下修改UI。</li>
 *     <li>业务事件在Presenter中处理。Note: V层不要存储数据，且不要直接和M层交互。</li>
 * </ol>
 *
 * @author lwh
 */
public abstract class BaseModel<T>{

    private List<T> mBeans;
    protected String mOrderBy;
    protected String mHaving;
    protected String mGroupBy;
    protected int mLimit = Integer.MAX_VALUE;

    public BaseModel(){
        mBeans = new ArrayList<>();//创建一个集合用来存储数据
        mBeans.addAll(initBeans());
    }

    public interface OnLoadListener<T> {
        void onLoad(List<T> beans);
    }

    public List<T> getBeans() {
        return mBeans;
    }

    public int getBeanCount(){
        return mBeans.size();
    }

    public void add(T bean){
        mBeans.add(bean);
    }

    public void add(List<T> beans){
        mBeans.addAll(beans);
    }

    public void clear(){
        mBeans.clear();
    }

    protected abstract List<T> initBeans();
}
