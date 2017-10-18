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

public abstract class BaseModel<T>{

    protected List<T> mBeans;

    protected Class<T> mBeanClass;

    private int mLimit = -1;

    private int mSkip;

    private String mSortBy = "";

    protected final String TAG = getClass().getName();

    public BaseModel(Class<T> beanClass){
        if (beanClass == null) {
            throw new IllegalArgumentException("beanClass is null.");
        }
        mBeans = new ArrayList<>();//创建一个集合用来存储数据
        mBeans.addAll(initBeans());
        mBeanClass = beanClass;
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

    public interface OnLoadListener<T> {
        void onLoad(List<T> beans);
    }

    public interface OnExtractListener<E>{
        void onExtract(String elementName, List<E> elements);
    }

    public BaseModel limit(int limit) {
        this.mLimit = limit;
        return this;
    }

    public BaseModel sortBy(String sortBy) {
        this.mSortBy = sortBy;
        return this;
    }

    public BaseModel skip(int skip){
        this.mSkip = skip;
        return this;
    }

    private void restore(){
        this.mLimit = -1;
        this.mSkip = 0;
        this.mSortBy = "";
    }

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
    protected <E> List<E> extractElement(Selector selector, String elementName) throws
            IllegalAccessException, NoSuchFieldException {
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
    protected List<T> findObjects(Selector selector) throws IllegalAccessException,
            NoSuchFieldException {
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
                if (matchCondition(key, leftValue, rightValue)) {
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

    private class ModelComparator implements Comparator<T> {

        private String mName;
        private boolean mDesc;
        private final String PLUS = "+";
        private final String MINUS = "-";

        /* package */ ModelComparator(String sortBy){
            if (checkSortBy(sortBy)) {
                String symbol = sortBy.substring(0, 1);
                if (symbol.equals(PLUS)) {
                    mDesc = true;
                } else if (symbol.equals(MINUS)) {
                    mDesc = false;
                }
                mName = sortBy.substring(1);
            } else {
                throw new IllegalArgumentException("sortBy format error.");
            }
        }

        /* package */ ModelComparator(String name, boolean isDesc) {
            this.mName = name;
            this.mDesc = isDesc;
        }

        private boolean checkSortBy(String sortBy) {
            if (sortBy.startsWith(MINUS) || sortBy.startsWith(MINUS)) {
                return true;
            }
            return false;
        }

        @Override
        public int compare(T bean1, T bean2) {
            try {
                int revise = 1;
                if (mDesc) {
                     revise = -revise;
                }
                Field nameField = mBeanClass.getDeclaredField(mName);
                nameField.setAccessible(true);
                Class<?> type = nameField.getType();
                if (isAssignableFromString(type)) {
                    String s1 = (String) nameField.get(bean1);
                    String s2 = (String) nameField.get(bean2);
                    String p1 = TextUtils.getPinyinFromSentence(s1);
                    String p2 = TextUtils.getPinyinFromSentence(s2);
                    return p1.compareTo(p2) * revise;
                } else {
                    Object o1 = nameField.get(bean1);
                    Object o2 = nameField.get(bean2);
                    if (o1.hashCode() == o2.hashCode()) {
                        return 0;
                    }
                    return o1.hashCode() > o2.hashCode() ? revise:-revise;
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return 0;
        }
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

    private boolean isAssignableFromNumber(Class<?> fieldType) {
        if (isAssignableFromByte(fieldType) ||
                isAssignableFromShort(fieldType) ||
                isAssignableFromInteger(fieldType) ||
                isAssignableFromLong(fieldType) ||
                isAssignableFromFloat(fieldType) ||
                isAssignableFromDouble(fieldType)) {
            return true;
        }
        return false;
    }

    private boolean isAssignableFromString(Class<?> fieldType) {
        if (String.class.isAssignableFrom(fieldType)) {
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

    private boolean matchEqualTo(Object requiredValue, Object actualValue) {
        if (requiredValue.equals(actualValue)) {
            return true;
        }
        return false;
    }

    private boolean matchNotEqualTo(Object requiredValue, Object actualValue) {
        if (!requiredValue.equals(actualValue)) {
            return true;
        }
        return false;
    }

    private boolean matchGreatorThan(Object requiredValue, Object actualValue) {
        Number n1 = (Number) requiredValue;
        Number n2 = (Number) actualValue;
        double d1 = n1.doubleValue();
        double d2 = n2.doubleValue();
        if (d1 > d2){
            return true;
        }
        return false;
    }

    private boolean matchLessThan(Object requiredValue, Object actualValue) {
        Number n1 = (Number) requiredValue;
        Number n2 = (Number) actualValue;
        double d1 = n1.doubleValue();
        double d2 = n2.doubleValue();
        if (d1 < d2){
            return true;
        }
        return false;
    }

    private boolean matchGreatorThanOrEqualTo(Object requiredValue, Object actualValue) {
        Number n1 = (Number) requiredValue;
        Number n2 = (Number) actualValue;
        double d1 = n1.doubleValue();
        double d2 = n2.doubleValue();
        if (d1 >= d2){
            return true;
        }
        return false;
    }

    private boolean matchLessThanOrEqualTo(Object requiredValue, Object actualValue) {
        Number n1 = (Number) requiredValue;
        Number n2 = (Number) actualValue;
        double d1 = n1.doubleValue();
        double d2 = n2.doubleValue();
        if (d1 <= d2){
            return true;
        }
        return false;
    }

    private boolean matchExists(Object actualValue) {
        if (actualValue != null){
            return true;
        }
        return false;
    }

    private boolean matchContains(Object requiredValue, Object actualValue) {
        String lhs = (String) requiredValue;
        String rhs = (String) actualValue;
        if (rhs.contains(lhs)){
            return true;
        }
        return false;
    }

    private boolean matchStartsWith(Object requiredValue, Object actualValue) {
        String lhs = (String) requiredValue;
        String rhs = (String) actualValue;
        if (rhs.startsWith(lhs)){
            return true;
        }
        return false;
    }

    private boolean matchEndsWith(Object requiredValue, Object actualValue) {
        String lhs = (String) requiredValue;
        String rhs = (String) actualValue;
        if (rhs.endsWith(lhs)){
            return true;
        }
        return false;
    }

    private boolean matchRegex(Object requiredValue, Object actualValue){
        Pattern p = Pattern.compile((String) requiredValue);
        Matcher m = p.matcher((String) actualValue);
        if (m.matches()) {
            return true;
        }
        return false;
    }

    private boolean matchCondition(String key, Object requiredValue, Object actualValue)
            throws IllegalAccessException, NoSuchFieldException {
        String[] keyPart = key.split(Selector.SPACE);
        String elementName = keyPart[0];
        String condition = keyPart[1];
        Field field = mBeanClass.getDeclaredField(elementName);
        field.setAccessible(true);
        Class<?> fieldType = requiredValue.getClass();
        if (condition.equals(Selector.EQUAL_TO_HOLDER)) {//等于
            return matchEqualTo(requiredValue, actualValue);
        } else if (condition.equals(Selector.NOT_EQUAL_TO_HOLDER)){//不等于
            return matchNotEqualTo(requiredValue, actualValue);
        } else if (condition.equals(Selector.GREATOR_THAN_HOLDER)
                && isAssignableFromNumber(fieldType)) {
            return matchGreatorThan(requiredValue, actualValue);
        } else if (condition.equals(Selector.LESS_THAN_HOLDER)
                && isAssignableFromNumber(fieldType)) {
            return matchLessThan(requiredValue, actualValue);
        } else if (condition.equals(Selector.GREATOR_THAN_OR_EQUAL_TO_HOLDER)
                && isAssignableFromNumber(fieldType)) {
            return matchGreatorThanOrEqualTo(requiredValue, actualValue);
        } else if (condition.equals(Selector.LESS_THAN_OR_EQUAL_TO_HOLDER)
                && isAssignableFromNumber(fieldType)) {
            return matchLessThanOrEqualTo(requiredValue, actualValue);
        } else if (condition.equals(Selector.EXISTS_HOLDER)) {
            return matchExists(actualValue);
        } else if (condition.equals(Selector.CONTAINS_HOLDER)
                && isAssinableFromCharSequence(fieldType)) {
            return matchContains(requiredValue, actualValue);
        } else if (condition.equals(Selector.STARTS_WITH_HOLDER)
                && isAssinableFromCharSequence(fieldType)) {
            return matchStartsWith(requiredValue, actualValue);
        } else if (condition.equals(Selector.ENDS_WITH_HOLDER)
                && isAssinableFromCharSequence(fieldType)) {
            return matchEndsWith(requiredValue, actualValue);
        } else if (condition.equals(Selector.MATCHES_HOLDER)
                && isAssinableFromCharSequence(fieldType)) {
            return matchRegex(requiredValue, actualValue);
        } else {
            throw new UndeclaredExpressionException("condition key is illegal.");
        }
    }

    public static class UndeclaredExpressionException extends RuntimeException {
        public UndeclaredExpressionException() {
        }

        public UndeclaredExpressionException(String detailMessage) {
            super(detailMessage);
        }
    }

    public static class Selector{

        private Map<String, Object> mConditionMap;

        private static final String SPACE = " ";

        private static final String EQUAL_TO_HOLDER = "=?";

        private static final String NOT_EQUAL_TO_HOLDER = "!=?";

        private static final String GREATOR_THAN_HOLDER = ">?";

        private static final String LESS_THAN_HOLDER = "<?";

        private static final String GREATOR_THAN_OR_EQUAL_TO_HOLDER = ">=?";

        private static final String LESS_THAN_OR_EQUAL_TO_HOLDER = "<=?";

        private static final String EXISTS_HOLDER = "exists?";

        private static final String CONTAINS_HOLDER = "contains?";

        private static final String STARTS_WITH_HOLDER = "startswith?";

        private static final String ENDS_WITH_HOLDER = "endswith?";

        private static final String MATCHES_HOLDER = "matches?";

        private Selector(){
            mConditionMap = new ConcurrentHashMap<>();
        }

        public static Selector create() {
            return new Selector();
        }

        protected Map<String, Object> getConditionMap() {
            return mConditionMap;
        }

        public Selector addWhereEqualTo(String elementName, Object value){
            String key = elementName + SPACE + EQUAL_TO_HOLDER;
            mConditionMap.put(key, value);
            return this;
        }

        public Selector addWhereNotEqualTo(String elementName, Object value){
            String key = elementName + SPACE + NOT_EQUAL_TO_HOLDER;
            mConditionMap.put(key, value);
            return this;
        }

        public Selector addWhereGreatorThan(String elementName, Number value){
            String key = elementName + SPACE + GREATOR_THAN_HOLDER;
            mConditionMap.put(key, value);
            return this;
        }

        public Selector addWhereLessThan(String elementName, Number value){
            String key = elementName + SPACE + LESS_THAN_HOLDER;
            mConditionMap.put(key, value);
            return this;
        }

        public Selector addWhereGreatorThanOrEqualTo(String elementName, Number value){
            String key = elementName + SPACE + GREATOR_THAN_OR_EQUAL_TO_HOLDER;
            mConditionMap.put(key, value);
            return this;
        }

        public Selector addWhereLessThanOrEqualTo(String elementName, Number value){
            String key = elementName + SPACE + LESS_THAN_OR_EQUAL_TO_HOLDER;
            mConditionMap.put(key, value);
            return this;
        }

        public Selector addWhereExists(String elementName){
            String key = elementName + SPACE + EXISTS_HOLDER;
            mConditionMap.put(key, null);
            return this;
        }

        public Selector addWhereContains(String elementName, String value){
            String key = elementName + SPACE + CONTAINS_HOLDER;
            mConditionMap.put(key, value);
            return this;
        }

        public Selector addWhereStartsWith(String elementName, String prefix){
            String key = elementName + SPACE + STARTS_WITH_HOLDER;
            mConditionMap.put(key, prefix);
            return this;
        }

        public Selector addWhereEndsWith(String elementName, String suffix){
            String key = elementName + SPACE + ENDS_WITH_HOLDER;
            mConditionMap.put(key, suffix);
            return this;
        }

        public Selector addWhereMatches(String elementName, String regex){
            String key = elementName + SPACE + MATCHES_HOLDER;
            mConditionMap.put(key, regex);
            return this;
        }
    }
}
