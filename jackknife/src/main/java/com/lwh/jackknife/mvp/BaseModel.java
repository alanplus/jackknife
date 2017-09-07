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

    public List<T> getResult(WhereBuilder<T> builder) throws IllegalAccessException {
        List<T> result = new ArrayList<>();
        Map<String, Object> conditions = builder.getQueryConditions();
        if (mBeans.size() == 0){
            return null;
        }
        if (builder == null || conditions.size() == 0){
            return mBeans;
        }
        Set<String> keySet = conditions.keySet();
        Iterator<String> iterator = keySet.iterator();
        for (T bean:mBeans) {
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object obj = conditions.get(key);
                Class<?> beanClass = bean.getClass();//拿到bean的类型
                Field[] fields = beanClass.getFields();
                for (Field field:fields){
                    String[] keyPart = key.split(" ");
                    if (keyPart[1].equals(WhereBuilder.KEY_EQUAL_TO)){
                        if (keyPart[0].equals(field.getName()) &&
                                field.get(bean).equals(obj)){
                            result.add(bean);
                        }
                    } else if (keyPart[1].equals(WhereBuilder.KEY_NOT_EQUAL_TO)){
                        if (keyPart[0].equals(field.getName()) &&
                                !field.get(bean).equals(obj)){
                            result.add(bean);
                        }
                    } else if (keyPart[1].equals(WhereBuilder.KEY_GREATOR_THAN)){
                        if (keyPart[0].equals(field.getName()) &&
                                Float.parseFloat((String) field.get(bean)) > Float.parseFloat((String) obj)){
                            result.add(bean);
                        }
                    } else if (keyPart[1].equals(WhereBuilder.KEY_LESS_THAN)){
                        if (keyPart[0].equals(field.getName()) &&
                                Float.parseFloat((String) field.get(bean)) < Float.parseFloat((String) obj)){
                            result.add(bean);
                        }
                    } else if (keyPart[1].equals(WhereBuilder.KEY_GREATOR_THAN_OR_EQUAL_TO)){
                        if (keyPart[0].equals(field.getName()) &&
                                Float.parseFloat((String) field.get(bean)) >= Float.parseFloat((String) obj)){
                            result.add(bean);
                        }
                    } else if (keyPart[1].equals(WhereBuilder.KEY_LESS_THAN_OR_EQUAL_TO)){
                        if (keyPart[0].equals(field.getName()) &&
                                Float.parseFloat((String) field.get(bean)) <= Float.parseFloat((String) obj)){
                            result.add(bean);
                        }
                    }
                }
            }
        }
        return result;
    }

    public class WhereBuilder<T> {

        private static final String KEY_EQUAL_TO = " =?";
        private static final String KEY_NOT_EQUAL_TO = " !=?";
        private static final String KEY_GREATOR_THAN = " >?";
        private static final String KEY_LESS_THAN = " <?";
        private static final String KEY_GREATOR_THAN_OR_EQUAL_TO = " >=?";
        private static final String KEY_LESS_THAN_OR_EQUAL_TO = " <=?";
        private Map<String, Object> mQueryConditions;

        public WhereBuilder(){
            mQueryConditions = new ConcurrentHashMap<>();
        }

        public Map<String, Object> getQueryConditions() {
            return mQueryConditions;
        }

        public WhereBuilder<T> addWhereEqualTo(String key, Object value){
            mQueryConditions.put(key+KEY_EQUAL_TO, value);
            return this;
        }

        public WhereBuilder<T> addWhereNotEqualTo(String key, Object value){
            mQueryConditions.put(key+KEY_NOT_EQUAL_TO, value);
            return this;
        }

        public WhereBuilder<T> addWhereGreaterThan(String key, Object value){
            mQueryConditions.put(key+KEY_GREATOR_THAN, value);
            return this;
        }

        public WhereBuilder<T> addWhereLessThan(String key, Object value){
            mQueryConditions.put(key+KEY_LESS_THAN, value);
            return this;
        }

        public WhereBuilder<T> addWhereGreaterThanOrEqualTo(String key, Object value){
            mQueryConditions.put(key+KEY_GREATOR_THAN_OR_EQUAL_TO, value);
            return this;
        }

        public WhereBuilder<T> addWhereLessThanOrEqualTo(String key, Object value){
            mQueryConditions.put(key+KEY_LESS_THAN_OR_EQUAL_TO, value);
            return this;
        }
    }
}
