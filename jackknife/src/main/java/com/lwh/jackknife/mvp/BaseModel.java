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

package com.lwh.jackknife.mvp;

import android.support.annotation.NonNull;

import com.lwh.jackknife.util.Logger;
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
     * 要检索的结果的限制条数，-1表示不限制。
     */
    private int mLimit = -1;

    /**
     * 跳过前面多少条？
     */
    private int mSkip;

    /**
     * 按该bean的哪个属性排序，比如按name升序，则为"name"，按name降序，则为"-name";
     */
    private String mSortBy = "";

    private final String TAG = getClass().getName();

    public BaseModel(Class<T> beanClass){
        mBeans = new ArrayList<>();//创建一个集合用来存储数据
        mBeans.addAll(initBeans());
        mBeanClass = beanClass;
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

    public void limit(int limit) {
        this.mLimit = limit;
    }

    public void sortBy(String sortBy) {
        this.mSortBy = sortBy;
    }

    public void skip(int skip){
        this.mSkip = skip;
    }

    private void restore(){
        this.mLimit = -1;
        this.mSkip = 0;
        this.mSortBy = "";
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
     * 得到满足条件的bean对象的数量。
     *
     * @param selector 选择器。
     * @param ignoreLimit true表示拿到实际的数据数量，false表示拿到limit后的数据数量，当实际数据数量小于limit时，不影响结果。
     * @return bean的数量。
     */
    protected int getCount(Selector selector, boolean ignoreLimit){
        List<T> objects = null;
        try {
            objects = findObjects(selector);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if(objects != null){
            if (ignoreLimit) {
                return objects.size();
            }else{
                return objects.size() > mLimit ? mLimit : objects.size();
            }
        }
        return 0;
    }

    /**
     * 提取bean对象的某个属性组成新的集合。
     *
     * @param selector 选择器。
     * @param elementName bean数据某个属性的名称。
     * @param <E> bean数据某个属性。
     * @return bean数据某个属性的集合。
     * @throws IllegalAccessException 非法访问异常。
     * @throws NoSuchFieldException 没有这样一个属性的异常。
     */
    protected <E> List<E> extractElement(Selector selector, @NonNull String elementName) throws IllegalAccessException,
            NoSuchFieldException {
        List<E> elements = new ArrayList<>();
        List<T> beans = findObjects(selector);
        if (beans.size() > 0) {
            for (T bean : beans) {
                Field[] fields = mBeanClass.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
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
        for (int i=0;i<mBeans.size();i++) {
            int matchesCount = 0;
            T bean = mBeans.get(i);//存储在内存中的真实的数据
            Iterator<String> iterator = keys.iterator();//拿到所有的条件key
            while (iterator.hasNext()){//遍历条件key
                String key = iterator.next();
                String[] keyPart = key.split(Selector.SPACE);
                String elementName = keyPart[0];
                Field targetField = mBeanClass.getDeclaredField(elementName);//要检测的属性
                targetField.setAccessible(true);
                Object leftValue = map.get(key);
                Object rightValue = targetField.get(bean);
                if (conditionMatches(key, leftValue, rightValue)) {
                    matchesCount++;
                }
            }
            if (matchesCount == keys.size()){
                temp.add(bean);
            }
        }
        List<T> result = new ArrayList<>();
        if (temp.size() > 0) {
            if (TextUtils.isNotEmpty(mSortBy)) {//需要排序就排序
                Collections.sort(temp, new ModelComparator(mSortBy));
            }
            if (mLimit < -1){
                throw new RuntimeException("限制数量不合法");
            }else if (mLimit != -1) {
                for (int j = mSkip; j < mSkip + mLimit; j++) {
                    result.add(temp.get(j));
                }
            }else{
                result.addAll(temp);
            }
        }
        restore();
        return result;
    }

    private boolean isAssignableFromBoolean(Class<?> fieldType){
        if (boolean.class.isAssignableFrom(fieldType) || Boolean.class.isAssignableFrom(fieldType)){
            return true;
        }
        return false;
    }

    private boolean isAssignableFromByte(Class<?> fieldType){
        if (byte.class.isAssignableFrom(fieldType) || Byte.class.isAssignableFrom(fieldType)){
            return true;
        }
        return false;
    }

    private boolean isAssignableFromShort(Class<?> fieldType){
        if (short.class.isAssignableFrom(fieldType) || Short.class.isAssignableFrom(fieldType)){
            return true;
        }
        return false;
    }

    private boolean isAssignableFromInteger(Class<?> fieldType){
        if (int.class.isAssignableFrom(fieldType) || Integer.class.isAssignableFrom(fieldType)){
            return true;
        }
        return false;
    }

    private boolean isAssignableFromLong(Class<?> fieldType){
        if (long.class.isAssignableFrom(fieldType) || Long.class.isAssignableFrom(fieldType)){
            return true;
        }
        return false;
    }

    private boolean isAssignableFromFloat(Class<?> fieldType){
        if (float.class.isAssignableFrom(fieldType) || Float.class.isAssignableFrom(fieldType)){
            return true;
        }
        return false;
    }

    private boolean isAssignableFromDouble(Class<?> fieldType){
        if (double.class.isAssignableFrom(fieldType) || Double.class.isAssignableFrom(fieldType)){
            return true;
        }
        return false;
    }

    private boolean isAssinableFromCharacter(Class<?> fieldType){
        if (char.class.isAssignableFrom(fieldType) || Character.class.isAssignableFrom(fieldType)){
            return true;
        }
        return false;
    }

    private boolean isAssinableFromCharSequence(Class<?> fieldType){
        if (CharSequence.class.isAssignableFrom(fieldType)){
            return true;
        }
        return false;
    }

    /**
     * 是否匹配条件。
     *
     * @param key 要判断的条件列表的当前的键。
     * @param rightValue model中缓存的bean对象的指定属性值。
     * @return 是否匹配。
     * @throws IllegalAccessException 非法访问异常。
     * @throws NoSuchFieldException 没有这样一个属性的异常。
     */
    public boolean conditionMatches(String key, Object leftValue, Object rightValue) throws IllegalAccessException, NoSuchFieldException {
        String[] keyPart = key.split(Selector.SPACE);
        String elementName = keyPart[0];
        String condition = keyPart[1];
        Field field = mBeanClass.getDeclaredField(elementName);
        field.setAccessible(true);
        Class<?> fieldType = leftValue.getClass();
        if (condition.equals(Selector.EQUAL_HOLDER)) {//等于
            if (Number.class.isAssignableFrom(fieldType)){
                Number n1 = (Number)leftValue;
                Number n2 = (Number) rightValue;
                if (n1.equals(n2)){
                    return true;
                }
            }else if (isAssinableFromCharSequence(fieldType)){
                CharSequence s1 = (CharSequence) leftValue;
                CharSequence s2 = (CharSequence) rightValue;
                if (s1.equals(s2)){
                    return true;
                }
            }else if (isAssignableFromBoolean(fieldType)){
                boolean b1 = (boolean) leftValue;
                boolean b2 = (boolean) rightValue;
                if (b1 == b2){
                    return true;
                }
            }else {
                if (leftValue.equals(rightValue)){
                    return true;
                }
            }
            return false;
        }else if (condition.equals(Selector.NOT_EQUAL_HOLDER)){//不等于
            if (Number.class.isAssignableFrom(fieldType)){
                Number n1 = (Number)leftValue;
                Number n2 = (Number) rightValue;
                if (!n1.equals(n2)){
                    return true;
                }
            }else if (CharSequence.class.isAssignableFrom(fieldType)){
                CharSequence s1 = (CharSequence) leftValue;
                CharSequence s2 = (CharSequence) rightValue;
                if (!s1.equals(s2)){
                    return true;
                }
            }else if (Boolean.class.isAssignableFrom(fieldType)){
                boolean b1 = (boolean) leftValue;
                boolean b2 = (boolean) rightValue;
                if (b1 != b2){
                    return true;
                }
            }else {
                if (!leftValue.equals(rightValue)){
                    return true;
                }
            }
            return false;
        }else if (condition.equals(Selector.GREATOR_THAN_HOLDER)
                && Number.class.isAssignableFrom(fieldType)) {
            Number n1 = (Number) leftValue;
            Number n2 = (Number) rightValue;
            double d1 = n1.doubleValue();
            double d2 = n2.doubleValue();
            if (d1 > d2){
                return true;
            }
            return false;
        }else if (condition.equals(Selector.LESS_THAN_HOLDER)
                && Number.class.isAssignableFrom(fieldType)) {
            Number n1 = (Number) leftValue;
            Number n2 = (Number) rightValue;
            double d1 = n1.doubleValue();
            double d2 = n2.doubleValue();
            if (d1 < d2){
                return true;
            }
            return false;
        }else if (condition.equals(Selector.GREATOR_THAN_OR_EQUAL_TO_HOLDER)
                && Number.class.isAssignableFrom(fieldType)) {
            Number n1 = (Number) leftValue;
            Number n2 = (Number) rightValue;
            double d1 = n1.doubleValue();
            double d2 = n2.doubleValue();
            if (d1 >= d2){
                return true;
            }
            return false;
        }else if (condition.equals(Selector.LESS_THAN_OR_EQUAL_TO_HOLDER)
                && Number.class.isAssignableFrom(fieldType)) {
            Number n1 = (Number) leftValue;
            Number n2 = (Number) rightValue;
            double d1 = n1.doubleValue();
            double d2 = n2.doubleValue();
            if (d1 <= d2){
                return true;
            }
            return false;
        }else if (condition.equals(Selector.EXISTS_HOLDER)) {
            if (rightValue != null){
                return true;
            }
            return false;
        }else if (condition.equals(Selector.CONTAINS_HOLDER)
                && isAssinableFromCharSequence(fieldType)) {
            String lhs = (String) leftValue;
            String rhs = (String) rightValue;
            if (rhs.contains(lhs)){
                return true;
            }
            return false;
        }else if (condition.equals(Selector.STARTS_WITH_HOLDER)
                && isAssinableFromCharSequence(fieldType)) {
            String lhs = (String) leftValue;
            String rhs = (String) rightValue;
            if (rhs.startsWith(lhs)){
                return true;
            }
            return false;
        }else if (condition.equals(Selector.ENDS_WITH_HOLDER)
                && isAssinableFromCharSequence(fieldType)) {
            String lhs = (String) leftValue;
            String rhs = (String) rightValue;
            if (rhs.endsWith(lhs)){
                return true;
            }
            return false;
        }else if (condition.equals(Selector.MATCHES_HOLDER)
                && isAssinableFromCharSequence(fieldType)) {
            String regex = (String) leftValue;
            String value = (String) rightValue;
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(value);
            if (m.matches()) {
                return true;
            }
            return false;
        }else {
            throw new RuntimeException("未定义的条件表达式");
        }
    }


    /**
     * bean数据的比较器。
     */
    private class ModelComparator implements Comparator<T> {

        private String mSortBy;

        public ModelComparator(String sortBy){
            this.mSortBy = sortBy;
        }

        @Override
        public int compare(T o1, T o2) {
            if (mSortBy.startsWith("-") || mSortBy.startsWith("+")){
                String name = mSortBy.substring(1);
                try {
                    Field field = mBeanClass.getDeclaredField(name);
                    field.setAccessible(true);
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
            }
            throw new IllegalStateException("比较两个对象值时发生未知错误");
        }
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
        public Selector addWhereEqualTo(String elementName, Object value){
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
        public Selector addWhereNotEqualTo(String elementName, Object value){
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
        public Selector addWhereGreatorThan(String elementName, Number value){
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
        public Selector addWhereLessThan(String elementName, Number value){
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
        public Selector addWhereGreatorThanOrEqualTo(String elementName, Number value){
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
        public Selector addWhereLessThanOrEqualTo(String elementName, Number value){
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
        public Selector addWhereExists(String elementName){
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
        public Selector addWhereContains(String elementName, String value){
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
        public Selector addWhereStartsWith(String elementName, String prefix){
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
        public Selector addWhereEndsWith(String elementName, String suffix){
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
        public Selector addWhereMatches(String elementName, String regex){
            String key = elementName + SPACE + MATCHES_HOLDER;
            mConditionMap.put(key, regex);
            return this;
        }
    }
}
