package com.lwh.jackknife.mvp;

import android.support.annotation.NonNull;

import com.lwh.jackknife.util.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * 缓存的所有的bean对象。
     */
    protected List<T> mBeans;

    /**
     * bean对象的类型。
     */
    protected Class<T> mBeanClass;

    /**
     * 要检索的结果的限制条数。
     */
    protected int mCount = Integer.MAX_VALUE;

    /**
     * 跳过前面多少条？
     */
    protected int mSkip;

    /**
     * 按该bean的哪个属性排序，比如按name升序，则为"name"，按name降序，则为"-name";
     */
    protected String mSortBy = "";

    public BaseModel(){
        mBeans = new ArrayList<>();//创建一个集合用来存储数据
        mBeans.addAll(initBeans());
    }

    /**
     * 添加一个bean。
     *
     * @param bean 数据。
     */
    public void add(T bean){
        mBeans.add(bean);
    }

    /**
     * 添加一堆bean。
     *
     * @param beans 数据集合。
     */
    public void add(List<T> beans){
        mBeans.addAll(beans);
    }

    /**
     * 清除所有的bean。
     */
    public void clear(){
        mBeans.clear();
    }

    public void setCount(int count) {
        this.mCount = count;
    }

    public void setSkip(int skip) {
        this.mSkip = skip;
    }

    public void setSortBy(String sortBy){
        this.mSortBy = sortBy;
    }

    /**
     * 加载数据的回调接口。
     *
     * @param <T> bean数据。
     */
    public interface OnLoadListener<T> {
        void onLoad(List<T> beans);
    }

    /**
     * 提取数据的回调接口。
     *
     * @param <E> bean数据的属性。
     */
    public interface OnExtractListener<E>{
        void onExtract(String elementName, List<E> elements);
    }

    /**
     * 获取到所有的bean。
     *
     * @hide
     * @return bean数据。
     */
    public List<T> getBeans() {
        return mBeans;
    }

    public int getCount(){
        return mBeans.size();
    }

    public int getSkip() {
        return mSkip;
    }

    public String getSortBy() {
        return mSortBy;
    }

    /**
     * 初始化Bean。
     *
     * @return bean数据。
     */
    protected abstract List<T> initBeans();

    /**
     * 提取bean对象的某个属性组成新的集合。
     *
     * @param selector 选择器。
     * @param elementClass bean数据某个属性的类型。
     * @param elementName bean数据某个属性的名称。
     * @param <E> bean数据某个属性。
     * @return bean数据某个属性的集合。
     * @throws IllegalAccessException 非法访问异常。
     * @throws NoSuchFieldException 没有这样一个属性的异常。
     */
    protected <E> List<E> extractElement(Selector selector, @NonNull Class<E> elementClass,
                                         @NonNull String elementName) throws IllegalAccessException,
            NoSuchFieldException {
        List<E> elements = new ArrayList<>();
        List<T> beans = findObjects(selector);
        if (beans.size() > 0) {
            for (T bean : beans) {
                Field[] fields = elementClass.getFields();
                for (Field field : fields) {
                    if (field.getName().equals(elementName)) {
                        E element = (E) field.get(bean);
                        elements.add(element);
                    }
                }
            }
        }
        return elements;
    }

    /**
     * 查询出所有的bean。
     *
     * @param selector 选择器。
     * @return bean数据的集合。
     * @throws IllegalAccessException 非法访问异常。
     * @throws NoSuchFieldException 没有这样一个属性的异常。
     */
    protected List<T> findObjects(Selector selector) throws IllegalAccessException, NoSuchFieldException {
        if (selector == null){
            return mBeans;
        }
        List<T> temp = new ArrayList<>();
        Map<String, Object> map = selector.getConditionMap();
        Set<String> keys = map.keySet();
        for (T bean : mBeans) {
            Field[] fields = mBeanClass.getFields();//逻辑上的属性
            for (Field field:fields) {
                Iterator<String> iterator = keys.iterator();
                int conditionMetCount = 0;
                while (iterator.hasNext()){
                    String key = iterator.next();
                    if (conditionMatches(key, bean, field)) {
                        conditionMetCount++;
                    }
                }
                if (conditionMetCount == keys.size()){
                    temp.add(bean);
                }
            }
        }
        List<T> result = new ArrayList<>();
        if (mSortBy != null){//需要排序就排序
            Collections.sort(temp, new ModelComparator(mSortBy));
        }
        for (int i=mSkip;i<mSkip+mCount;i++){
            result.add(temp.get(i));
        }
        return result;
    }

    /**
     * bean数据的比较器。
     */
    private class ModelComparator implements Comparator<T>{

        private String mSortBy;

        public ModelComparator(String sortBy){
            this.mSortBy = sortBy;
        }

        @Override
        public int compare(T o1, T o2) {
            if (mSortBy.startsWith("-") || mSortBy.startsWith("+")){
                String name = mSortBy.substring(1);
                try {
                    Field field = mBeanClass.getField(name);
                    Class<?> fieldType = field.getType();
                    int revise = 1;//修正
                    if (mSortBy.startsWith("-")){
                        revise = -revise;//取相反数
                    }
                    if (Number.class.isAssignableFrom(fieldType)) {
                        if (Float.class.isAssignableFrom(fieldType) || Double.class.isAssignableFrom(fieldType)) {
                            double d1 = field.getDouble(o1);
                            double d2 = field.getDouble(o2);

                            if (d1 > d2) {
                                return 1 * revise;
                            } else if (d1 < d2) {
                                return -1 * revise;
                            } else {
                                return 0;
                            }
                        } else {
                            long l1 = field.getLong(o1);
                            long l2 = field.getLong(o2);
                            if (l1 > l2) {
                                return 1 * revise;
                            } else if (l1 < l2) {
                                return -1 * revise;
                            } else {
                                return 0;
                            }
                        }
                    }else if (String.class.isAssignableFrom(fieldType)){
                        String s1 = (String) field.get(o1);
                        String s2 = (String) field.get(o2);
                        String pinyin1 = TextUtils.getPinyinFromSentence(s1);
                        String pinyin2 = TextUtils.getPinyinFromSentence(s2);
                        return pinyin1.compareTo(pinyin2);
                    }else{
                        if (o1.hashCode() > o2.hashCode()){
                            return 1 * revise;
                        }else if (o1.hashCode() < o2.hashCode()){
                            return -1 * revise;
                        }else {
                            return 0;
                        }
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }else {
                try {
                    Field field = mBeanClass.getField(mSortBy);
                    Class<?> fieldType = field.getType();
                    if (Number.class.isAssignableFrom(fieldType)) {
                        if (Float.class.isAssignableFrom(fieldType) || Double.class.isAssignableFrom(fieldType)) {
                            double d1 = field.getDouble(o1);
                            double d2 = field.getDouble(o2);

                            if (d1 > d2) {
                                return 1;
                            } else if (d1 < d2) {
                                return -1;
                            } else {
                                return 0;
                            }
                        } else {
                            long l1 = field.getLong(o1);
                            long l2 = field.getLong(o2);
                            if (l1 > l2) {
                                return 1;
                            } else if (l1 < l2) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    }else if (String.class.isAssignableFrom(fieldType)){
                        String s1 = (String) field.get(o1);
                        String s2 = (String) field.get(o2);
                        String pinyin1 = TextUtils.getPinyinFromSentence(s1);
                        String pinyin2 = TextUtils.getPinyinFromSentence(s2);
                        return pinyin1.compareTo(pinyin2);
                    }else{
                        if (o1.hashCode() > o2.hashCode()){
                            return 1;
                        }else if (o1.hashCode() < o2.hashCode()){
                            return -1;
                        }else {
                            return 0;
                        }
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            throw new IllegalStateException("比较两个对象值时发生未知错误");
        }
    }

    /**
     * 是否匹配条件。
     *
     * @param key 要判断的条件列表的当前的键。
     * @param bean 要判断的当前的bean对象。
     * @param field 要判断的当前bean类（不是对象）的属性。
     * @return 是否匹配。
     * @throws IllegalAccessException 非法访问异常。
     * @throws NoSuchFieldException 没有这样一个属性的异常。
     */
    public boolean conditionMatches(String key, T bean, Field field) throws IllegalAccessException, NoSuchFieldException {
        String[] keyPart = key.split(Selector.SPACE);
        String elementName = keyPart[0];
        String condition = keyPart[1];
        field.setAccessible(true);
        Class<?> fieldType = field.getType();
        if (condition.equals(Selector.EQUAL_HOLDER) && elementName.equals(field.getName())) {//等于
            Object o = field.get("");
            if (bean.equals(o)){
                return true;
            }
        }else if (condition.equals(Selector.NOT_EQUAL_HOLDER) && elementName.equals(field.getName())){//不等于
            Object o = field.get("");
            if (!bean.equals(o)){
                return true;
            }
        }else if (condition.equals(Selector.GREATOR_THAN_HOLDER) && elementName.equals(field.getName())
                && Number.class.isAssignableFrom(fieldType)) {
            if (Float.class.isAssignableFrom(fieldType) || Double.class.isAssignableFrom(fieldType)){
                Field beanField = bean.getClass().getField(elementName);
                double lhs = beanField.getDouble(bean);
                double rhs = field.getDouble("");
                if (lhs > rhs){
                    return true;
                }
            } else {
                Field beanField = bean.getClass().getField(elementName);
                double lhs = beanField.getLong(bean);
                double rhs = field.getLong("");
                if (lhs > rhs){
                    return true;
                }
            }
        }else if (condition.equals(Selector.LESS_THAN_HOLDER) && elementName.equals(field.getName())
                && Number.class.isAssignableFrom(fieldType)) {
            if (Float.class.isAssignableFrom(fieldType) || Double.class.isAssignableFrom(fieldType)){
                Field beanField = bean.getClass().getField(elementName);
                double lhs = beanField.getDouble(bean);
                double rhs = field.getDouble("");
                if (lhs < rhs){
                    return true;
                }
            } else {
                Field beanField = bean.getClass().getField(elementName);
                double lhs = beanField.getLong(bean);
                double rhs = field.getLong("");
                if (lhs < rhs){
                    return true;
                }
            }
        }else if (condition.equals(Selector.GREATOR_THAN_OR_EQUAL_TO_HOLDER) && elementName.equals(field.getName())
                && Number.class.isAssignableFrom(fieldType)) {
            if (Float.class.isAssignableFrom(fieldType) || Double.class.isAssignableFrom(fieldType)){
                Field beanField = bean.getClass().getField(elementName);
                double lhs = beanField.getDouble(bean);
                double rhs = field.getDouble("");
                if (lhs >= rhs){
                    return true;
                }
            } else {
                Field beanField = bean.getClass().getField(elementName);
                double lhs = beanField.getLong(bean);
                double rhs = field.getLong("");
                if (lhs >= rhs){
                    return true;
                }
            }
        }else if (condition.equals(Selector.LESS_THAN_OR_EQUAL_TO_HOLDER) && elementName.equals(field.getName())
                && Number.class.isAssignableFrom(fieldType)) {
            if (Float.class.isAssignableFrom(fieldType) || Double.class.isAssignableFrom(fieldType)){
                Field beanField = bean.getClass().getField(elementName);
                double lhs = beanField.getDouble(bean);
                double rhs = field.getDouble("");
                if (lhs <= rhs){
                    return true;
                }
            } else {
                Field beanField = bean.getClass().getField(elementName);
                double lhs = beanField.getLong(bean);
                double rhs = field.getLong("");
                if (lhs <= rhs){
                    return true;
                }
            }
        }else if (condition.equals(Selector.EXISTS_HOLDER) && elementName.equals(field.getName())) {
            Field beanField = bean.getClass().getField(elementName);
            Object o = beanField.get(bean);
            if (o != null){
                return true;
            }
        }else if (condition.equals(Selector.CONTAINS_HOLDER) && elementName.equals(field.getName())
                && String.class.isAssignableFrom(fieldType)) {
            String value = String.valueOf(field.get(""));
            Field realField = bean.getClass().getField(elementName);
            String s = String.valueOf(realField.get(bean));
            if (s.contains(value)){
                return true;
            }
        }else if (condition.equals(Selector.STARTS_WITH_HOLDER) && elementName.equals(field.getName())
                && String.class.isAssignableFrom(fieldType)) {
            String prefix = String.valueOf(field.get(""));
            Field realField = bean.getClass().getField(elementName);
            String s = String.valueOf(realField.get(bean));
            if (s.startsWith(prefix)){
                return true;
            }
        }else if (condition.equals(Selector.ENDS_WITH_HOLDER) && elementName.equals(field.getName())
                && String.class.isAssignableFrom(fieldType)) {
            String suffix = String.valueOf(field.get(""));
            Field realField = bean.getClass().getField(elementName);
            String s = String.valueOf(realField.get(bean));
            if (s.endsWith(suffix)){
                return true;
            }
        }else if (condition.equals(Selector.MATCHES_HOLDER) && elementName.equals(field.getName())
                && String.class.isAssignableFrom(fieldType)) {
            String regex = String.valueOf(field.get(""));
            Field realField = bean.getClass().getField(elementName);
            String s = String.valueOf(realField.get(bean));
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(s);
            if (m.matches()){
                return true;
            }
        }
        return false;
    }

    /**
     * 得到满足条件的bean对象的数量。
     *
     * @param selector 选择器。
     * @param countIgnore 要不要忽略设置的限制条数。
     * @return bean的数量。
     */
    protected int getCount(Selector selector, boolean countIgnore){
        List<T> objects = null;
        try {
            objects = findObjects(selector);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if(objects != null){
            if (countIgnore) {
                return objects.size();
            }else{
                return objects.size() > mCount ? mCount : objects.size();
            }
        }
        return 0;
    }

    /**
     * 索引数据的选择器，它用来筛选bean集合中的数据。
     */
    public static class Selector{

        /**
         * 条件列表，所有条件均为与（AND）运算。
         */
        private Map<String, Object> mConditionMap;

        /**
         * 空格。
         */
        private static final String SPACE = " ";

        /**
         * 等于的条件。
         */
        private static final String EQUAL_HOLDER = "=?";

        /**
         * 不等于的条件。
         */
        private static final String NOT_EQUAL_HOLDER = "!=?";

        /**
         * 大于的条件。
         */
        private static final String GREATOR_THAN_HOLDER = ">?";

        /**
         * 小于的条件。
         */
        private static final String LESS_THAN_HOLDER = "<?";

        /**
         * 大于等于的条件。
         */
        private static final String GREATOR_THAN_OR_EQUAL_TO_HOLDER = ">=?";

        /**
         * 小于等于的条件。
         */
        private static final String LESS_THAN_OR_EQUAL_TO_HOLDER = "<=?";

        /**
         * 某个属性不为null值的条件。
         */
        private static final String EXISTS_HOLDER = "exists?";

        /**
         * 包含某个字符串的条件。
         */
        private static final String CONTAINS_HOLDER = "contains?";

        /**
         * 以某个字符串开始的条件。
         */
        private static final String STARTS_WITH_HOLDER = "startswith?";

        /**
         * 以某个字符串结尾的条件。
         */
        private static final String ENDS_WITH_HOLDER = "endswith?";

        /**
         * 匹配某个正则表达式的条件。
         */
        private static final String MATCHES_HOLDER = "matches?";

        public Selector(){
            mConditionMap = new ConcurrentHashMap<>();
        }

        /**
         * 得到条件Map。
         *
         * @return 条件Map。
         */
        protected Map<String, Object> getConditionMap() {
            return mConditionMap;
        }

        /**
         * 添加等于的条件。
         *
         * @param elementName bean的属性的名称。
         * @param value 条件值。
         * @return 选择器。
         */
        protected Selector addWhereEqualTo(String elementName, Object value){
            String key = elementName + SPACE + EQUAL_HOLDER;
            mConditionMap.put(key, value);
            return this;
        }

        /**
         * 添加不等于的条件。
         *
         * @param elementName bean的属性的名称。
         * @param value 条件值。
         * @return 选择器。
         */
        protected Selector addWhereNotEqualTo(String elementName, Object value){
            String key = elementName + SPACE + NOT_EQUAL_HOLDER;
            mConditionMap.put(key, value);
            return this;
        }

        /**
         * 添加大于的条件。
         *
         * @param elementName bean的属性的名称。
         * @param value 条件值。
         * @return 选择器。
         */
        protected Selector addWhereGreatorThan(String elementName, Number value){
            String key = elementName + SPACE + GREATOR_THAN_HOLDER;
            mConditionMap.put(key, value);
            return this;
        }

        /**
         * 添加小于的条件。
         *
         * @param elementName bean的属性的名称。
         * @param value 条件值。
         * @return 选择器。
         */
        protected Selector addWhereLessThan(String elementName, Number value){
            String key = elementName + SPACE + LESS_THAN_HOLDER;
            mConditionMap.put(key, value);
            return this;
        }

        /**
         * 添加大于等于的条件。
         *
         * @param elementName bean的属性的名称。
         * @param value 条件值。
         * @return 选择器。
         */
        protected Selector addWhereGreatorThanOrEqualTo(String elementName, Number value){
            String key = elementName + SPACE + GREATOR_THAN_OR_EQUAL_TO_HOLDER;
            mConditionMap.put(key, value);
            return this;
        }

        /**
         * 添加小于等于的条件。
         *
         * @param elementName bean的属性的名称。
         * @param value 条件值。
         * @return 选择器。
         */
        protected Selector addWhereLessThanOrEqualTo(String elementName, Number value){
            String key = elementName + SPACE + LESS_THAN_OR_EQUAL_TO_HOLDER;
            mConditionMap.put(key, value);
            return this;
        }

        /**
         * 添加bean的某个属性的值不为空的条件。
         *
         * @param elementName bean的属性的名称。
         * @return 选择器。
         */
        protected Selector addWhereExists(String elementName){
            String key = elementName + SPACE + EXISTS_HOLDER;
            mConditionMap.put(key, null);
            return this;
        }

        /**
         * 添加包含某个字符串的条件。
         *
         * @param elementName bean的属性的名称。
         * @param value 包含的字符串。
         * @return 选择器。
         */
        protected Selector addWhereContains(String elementName, String value){
            String key = elementName + SPACE + CONTAINS_HOLDER;
            mConditionMap.put(key, value);
            return this;
        }

        /**
         * 添加以前缀字符开始的条件。
         *
         * @param elementName bean的属性的名称。
         * @param prefix 前缀。
         * @return 选择器。
         */
        protected Selector addWhereStartsWith(String elementName, String prefix){
            String key = elementName + SPACE + STARTS_WITH_HOLDER;
            mConditionMap.put(key, prefix);
            return this;
        }

        /**
         * 添加以后缀字符结尾的条件。
         *
         * @param elementName bean的属性的名称。
         * @param suffix 后缀。
         * @return 选择器。
         */
        protected Selector addWhereEndsWith(String elementName, String suffix){
            String key = elementName + SPACE + ENDS_WITH_HOLDER;
            mConditionMap.put(key, suffix);
            return this;
        }

        /**
         * 添加匹配正则表达式的条件。
         *
         * @param elementName bean的属性的名称。
         * @param regex 正则表达式。
         * @return 选择器。
         */
        protected Selector addWhereMatches(String elementName, String regex){
            String key = elementName + SPACE + MATCHES_HOLDER;
            mConditionMap.put(key, regex);
            return this;
        }
    }
}
