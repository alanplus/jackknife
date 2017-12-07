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

public abstract class BaseModel<BEAN>{

    protected List<BEAN> mDatas;

    protected Class<BEAN> mDataClass;

    private Comparator<BEAN> mComparator;

    public BaseModel(Class<BEAN> dataClass){
        if (dataClass == null) {
            throw new IllegalArgumentException("Unknown bean type.");
        }
        mDatas = new ArrayList<>();
        mDatas.addAll(initBeans());
        mDataClass = dataClass;
    }

    public enum SortStrategy {
        ASC, DESC
    }

    public BaseModel add(BEAN datas){
        mDatas.add(datas);
        return this;
    }

    public BaseModel add(List<BEAN> beans){
        mDatas.addAll(beans);
        return this;
    }

    public BaseModel clear(){
        mDatas.clear();
        return this;
    }

    public BaseModel sort(final String sortKey, final SortStrategy strategy) {
        mComparator = new Comparator<BEAN>() {

            @Override
            public int compare(BEAN lhs, BEAN rhs) {
                int reverse = 1;
                if (strategy == SortStrategy.DESC) {
                    reverse = -reverse;
                }
                try {
                    Field field = mDataClass.getDeclaredField(sortKey);
                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();
                    if (String.class.isAssignableFrom(fieldType)) {
                        return (TextUtils.getPinyinFromSentence((String) field.get(lhs)).compareTo
                                (TextUtils.getPinyinFromSentence((String) field.get(rhs)))) * reverse;
                    } else {
                        return (field.get(rhs).hashCode() - field.get(lhs).hashCode()) * reverse;
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                throw new RuntimeException("Unknown condition.");
            }

            @Override
            public boolean equals(Object object) {
                return false;
            }
        };
        Collections.sort(mDatas, mComparator);
        return this;
    }

    public BaseModel sort(String sortKey) {
        return sort(sortKey, SortStrategy.ASC);
    }

    public interface OnLoadListener<BEAN> {
        void onLoad(List<BEAN> beans);
    }

    public interface OnExtractListener<ELEMENT>{
        void onExtract(String elementName, List<ELEMENT> elements);
    }

    public List<BEAN> getDatas() {
        return mDatas;
    }

    public int getCount(){
        return mDatas.size();
    }

    protected abstract List<BEAN> initBeans();

    protected int countObjects(Selector selector) {
        List<BEAN> objects = findObjects(selector);
        if (objects != null) {
            return objects.size();
        }
        return 0;
    }

    protected <ELEMENT> List<ELEMENT> extractElement(String elementName) {
        return extractElement(null, elementName);
    }

    protected <ELEMENT> List<ELEMENT> extractElement(Selector selector, String elementName) {
        List<ELEMENT> elements = new ArrayList<>();
        List<BEAN> datas = findObjects(selector);
        if (datas.size() > 0) {
            for (BEAN bean : datas) {
                Field[] fields = mDataClass.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (field.getName().equals(elementName)) {
                        ELEMENT element = null;
                        try {
                            element = (ELEMENT) field.get(bean);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        elements.add(element);
                    }
                }
            }
        }
        return elements;
    }

    protected List<BEAN> findObjects() {
        return findObjects(null);
    }

    protected List<BEAN> findObjects(Selector selector) {
        if (selector == null){
            return mDatas;
        }
        List<BEAN> result = new ArrayList<>();
        Map<String, Object> map = selector.getConditionMap();
        Set<String> keys = map.keySet();
        for (int i=0;i<mDatas.size();i++) {
            int matchesCount = 0;
            BEAN bean = mDatas.get(i);
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                String[] keyPart = key.split(Selector.SPACE);
                String elementName = keyPart[0];
                Field targetField;
                try {
                    targetField = mDataClass.getDeclaredField(elementName);
                    targetField.setAccessible(true);
                    Object leftValue = map.get(key);
                    Object rightValue = targetField.get(bean);
                    if (matchCondition(key, leftValue, rightValue)) {
                        matchesCount++;
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (matchesCount == keys.size()){
                result.add(bean);
            }
        }
        return result;
    }

    private boolean isAssignableFromByte(Class<?> fieldType){
        return byte.class.isAssignableFrom(fieldType) || Byte.class.isAssignableFrom(fieldType);
    }

    private boolean isAssignableFromNumber(Class<?> fieldType) {
        return isAssignableFromByte(fieldType) ||
                isAssignableFromShort(fieldType) ||
                isAssignableFromInteger(fieldType) ||
                isAssignableFromLong(fieldType) ||
                isAssignableFromFloat(fieldType) ||
                isAssignableFromDouble(fieldType);
    }

    private boolean isAssignableFromShort(Class<?> fieldType){
        return short.class.isAssignableFrom(fieldType) || Short.class.isAssignableFrom(fieldType);
    }

    private boolean isAssignableFromInteger(Class<?> fieldType){
        return int.class.isAssignableFrom(fieldType) || Integer.class.isAssignableFrom(fieldType);
    }

    private boolean isAssignableFromLong(Class<?> fieldType){
        return long.class.isAssignableFrom(fieldType) || Long.class.isAssignableFrom(fieldType);
    }

    private boolean isAssignableFromFloat(Class<?> fieldType){
        return float.class.isAssignableFrom(fieldType) || Float.class.isAssignableFrom(fieldType);
    }

    private boolean isAssignableFromDouble(Class<?> fieldType){
        return double.class.isAssignableFrom(fieldType) || Double.class.isAssignableFrom(fieldType);
    }

    private boolean isAssinableFromCharSequence(Class<?> fieldType){
        return CharSequence.class.isAssignableFrom(fieldType);
    }

    private boolean matchEqualTo(Object requiredValue, Object actualValue) {
        return requiredValue.equals(actualValue);
    }

    private boolean matchNotEqualTo(Object requiredValue, Object actualValue) {
        return !requiredValue.equals(actualValue);
    }

    private boolean matchGreatorThan(Number requiredValue, Number actualValue) {
        return requiredValue.doubleValue() < actualValue.doubleValue();
    }

    private boolean matchLessThan(Number requiredValue, Number actualValue) {
        return requiredValue.doubleValue() > actualValue.doubleValue();
    }

    private boolean matchGreatorThanOrEqualTo(Number requiredValue, Number actualValue) {
        return requiredValue.doubleValue() <= actualValue.doubleValue();
    }

    private boolean matchLessThanOrEqualTo(Number requiredValue, Number actualValue) {
        return requiredValue.doubleValue() >= actualValue.doubleValue();
    }

    private boolean matchContains(String requiredValue, String actualValue) {
        return actualValue.contains(requiredValue);
    }

    private boolean matchStartsWith(String requiredValue, String actualValue) {
        return actualValue.startsWith(requiredValue);
    }

    private boolean matchEndsWith(String requiredValue, String actualValue) {
        return actualValue.endsWith(requiredValue);
    }

    private boolean matchCondition(String key, Object requiredValue, Object actualValue)
            throws IllegalAccessException, NoSuchFieldException {
        String[] keyPart = key.split(Selector.SPACE);
        String elementName = keyPart[0];
        String condition = keyPart[1];
        Field field = mDataClass.getDeclaredField(elementName);
        field.setAccessible(true);
        Class<?> fieldType = requiredValue.getClass();
        if (condition.equals(Selector.EQUAL_TO_HOLDER)) {
            return matchEqualTo(requiredValue, actualValue);
        }
        if (condition.equals(Selector.NOT_EQUAL_TO_HOLDER)) {
            return matchNotEqualTo(requiredValue, actualValue);
        }
        if (condition.equals(Selector.GREATOR_THAN_HOLDER)
                && isAssignableFromNumber(fieldType)) {
            return matchGreatorThan((Number)requiredValue, (Number)actualValue);
        }
        if (condition.equals(Selector.LESS_THAN_HOLDER)
                && isAssignableFromNumber(fieldType)) {
            return matchLessThan((Number)requiredValue, (Number)actualValue);
        }
        if (condition.equals(Selector.GREATOR_THAN_OR_EQUAL_TO_HOLDER)
                && isAssignableFromNumber(fieldType)) {
            return matchGreatorThanOrEqualTo((Number)requiredValue, (Number)actualValue);
        }
        if (condition.equals(Selector.LESS_THAN_OR_EQUAL_TO_HOLDER)
                && isAssignableFromNumber(fieldType)) {
            return matchLessThanOrEqualTo((Number)requiredValue, (Number)actualValue);
        }
        if (condition.equals(Selector.CONTAINS_HOLDER)
                && isAssinableFromCharSequence(fieldType)) {
            return matchContains(requiredValue.toString(), actualValue.toString());
        }
        if (condition.equals(Selector.STARTS_WITH_HOLDER)
                && isAssinableFromCharSequence(fieldType)) {
            return matchStartsWith(requiredValue.toString(), actualValue.toString());
        }
        if (condition.equals(Selector.ENDS_WITH_HOLDER)
                && isAssinableFromCharSequence(fieldType)) {
            return matchEndsWith(requiredValue.toString(), actualValue.toString());
        }
        throw new UndeclaredExpressionException("Condition key is illegal.");
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

        private static final String CONTAINS_HOLDER = "contains?";

        private static final String STARTS_WITH_HOLDER = "startswith?";

        private static final String ENDS_WITH_HOLDER = "endswith?";

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
    }
}
