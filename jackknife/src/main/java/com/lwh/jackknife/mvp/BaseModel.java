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

    public List<T> getResult(QueryBuilder<T> builder) throws IllegalAccessException {
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
                    if (keyPart[1].equals(QueryBuilder.KEY_EQUAL_TO)){
                        if (keyPart[0].equals(field.getName()) &&
                                field.get(bean).equals(obj)){
                            result.add(bean);
                        }
                    } else if (keyPart[1].equals(QueryBuilder.KEY_NOT_EQUAL_TO)){
                        if (keyPart[0].equals(field.getName()) &&
                                !field.get(bean).equals(obj)){
                            result.add(bean);
                        }
                    } else if (keyPart[1].equals(QueryBuilder.KEY_GREATOR_THAN)){
                        if (keyPart[0].equals(field.getName()) &&
                                Float.parseFloat((String) field.get(bean)) > Float.parseFloat((String) obj)){
                            result.add(bean);
                        }
                    } else if (keyPart[1].equals(QueryBuilder.KEY_LESS_THAN)){
                        if (keyPart[0].equals(field.getName()) &&
                                Float.parseFloat((String) field.get(bean)) < Float.parseFloat((String) obj)){
                            result.add(bean);
                        }
                    } else if (keyPart[1].equals(QueryBuilder.KEY_GREATOR_THAN_OR_EQUAL_TO)){
                        if (keyPart[0].equals(field.getName()) &&
                                Float.parseFloat((String) field.get(bean)) >= Float.parseFloat((String) obj)){
                            result.add(bean);
                        }
                    } else if (keyPart[1].equals(QueryBuilder.KEY_LESS_THAN_OR_EQUAL_TO)){
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

    public static class WhereBuilder {

        private String where;
        private Object[] whereArgs;

        private WhereBuilder(Class beanClass){

        }

        public static WhereBuilder create(Class beanClass){
            return new WhereBuilder(beanClass);
        }

        public WhereBuilder and(String where, Object[] whereArgs){
            return this;
        }

        public WhereBuilder append(String connect, String where, Object... value){
            return this;
        }
    }

    public class QueryBuilder<T> {

        private static final String KEY_EQUAL_TO = " =?";
        private static final String KEY_NOT_EQUAL_TO = " !=?";
        private static final String KEY_GREATOR_THAN = " >?";
        private static final String KEY_LESS_THAN = " <?";
        private static final String KEY_GREATOR_THAN_OR_EQUAL_TO = " >=?";
        private static final String KEY_LESS_THAN_OR_EQUAL_TO = " <=?";

        private static final String ASC = "ASC";
        private static final String DESC = "DESC";
        private static final String AND = "AND ";
        private static final String OR = "OR ";
        private static final String GROUP_BY = "GROUP BY ";
        private static final String HAVING = "HAVING ";
        private static final String ORDER_BY = "ORDER BY ";
        private static final String LIMIT = "LIMIT ";
        private static final String SELECT_COUNT = "SELECT COUNT(*) FROM ";
        private static final String DISTINCT = "DISTINCT";
        private static final String ASTERISK = "*";
        private static final String FROM = " FROM ";
        private static final String EQUAL_HOLDER = "=?";
        private static final String COMMA_HOLDER = ",?";
        private boolean distinct;
        private String group;
        private String having;
        private String order;
        private int limit;


        private Map<String, Object> mQueryConditions;

        public QueryBuilder(){
            mQueryConditions = new ConcurrentHashMap<>();
        }

        public Map<String, Object> getQueryConditions() {
            return mQueryConditions;
        }

        public QueryBuilder<T> addWhereEqualTo(String key, Object value){
            mQueryConditions.put(key+KEY_EQUAL_TO, value);
            return this;
        }

        public QueryBuilder<T> addWhereNotEqualTo(String key, Object value){
            mQueryConditions.put(key+KEY_NOT_EQUAL_TO, value);
            return this;
        }

        public QueryBuilder<T> addWhereGreaterThan(String key, Object value){
            mQueryConditions.put(key+KEY_GREATOR_THAN, value);
            return this;
        }

        public QueryBuilder<T> addWhereLessThan(String key, Object value){
            mQueryConditions.put(key+KEY_LESS_THAN, value);
            return this;
        }

        public QueryBuilder<T> addWhereGreaterThanOrEqualTo(String key, Object value){
            mQueryConditions.put(key+KEY_GREATOR_THAN_OR_EQUAL_TO, value);
            return this;
        }

        public QueryBuilder<T> addWhereLessThanOrEqualTo(String key, Object value){
            mQueryConditions.put(key+KEY_LESS_THAN_OR_EQUAL_TO, value);
            return this;
        }
    }
}
