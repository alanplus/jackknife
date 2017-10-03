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

package com.lwh.jackknife.orm.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lwh.jackknife.app.Application;
import com.lwh.jackknife.orm.Transaction;
import com.lwh.jackknife.orm.annotation.Column;
import com.lwh.jackknife.orm.builder.QueryBuilder;
import com.lwh.jackknife.orm.builder.WhereBuilder;
import com.lwh.jackknife.orm.table.OrmTable;
import com.lwh.jackknife.orm.table.TableManager;
import com.lwh.jackknife.util.Logger;
import com.lwh.jackknife.util.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 万能DAO。
 *
 * @param <T> 数据实体类。
 */
public class OrmDao<T extends OrmTable> implements Dao<T> {

    private Class<T> mBeanClass;
    private SQLiteOpenHelper mHelper;
    private SQLiteDatabase mDb;
    private final String TAG = getClass().getSimpleName();
    private final String SELECT_COUNT = "SELECT COUNT(*) FROM ";

    /* package */ OrmDao(Class<T> beanClass){
        this.mBeanClass = beanClass;
        this.mHelper = Application.getInstance().getSQLiteOpenHelper();
        this.mDb = mHelper.getWritableDatabase();
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

    private boolean isAssignableFromCharacter(Class<?> fieldType){
        if (char.class.isAssignableFrom(fieldType) || Character.class.isAssignableFrom(fieldType)){
            return true;
        }
        return false;
    }

    private boolean isAssignableFromCharSequence(Class<?> fieldType){
        if (CharSequence.class.isAssignableFrom(fieldType)){
            return true;
        }
        return false;
    }

    private boolean isAssignableFromClass(Class<?> fieldType){
        if (Class.class.isAssignableFrom(fieldType)){
            return true;
        }
        return false;
    }

    public ContentValues getContentValues(T bean) {
        ContentValues values = new ContentValues();
        Field[] fields = mBeanClass.getDeclaredFields();
        for (Field field:fields){
            field.setAccessible(true);
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                String columnName = column.value();
                Class<?> fieldType = field.getType();
                try {
                    if (isAssignableFromCharSequence(fieldType)) {
                        values.put(columnName, String.valueOf(field.get(bean)));
                    } else if (isAssignableFromBoolean(fieldType)) {
                        values.put(columnName, field.getBoolean(bean));
                    } else if (isAssignableFromByte(fieldType)) {
                        values.put(columnName, field.getByte(bean));
                    } else if (isAssignableFromShort(fieldType)) {
                        values.put(columnName, field.getShort(bean));
                    } else if (isAssignableFromInteger(fieldType)) {
                        values.put(columnName, field.getInt(bean));
                    } else if (isAssignableFromLong(fieldType)) {
                        values.put(columnName, field.getLong(bean));
                    } else if (isAssignableFromFloat(fieldType)) {
                        values.put(columnName, field.getFloat(bean));
                    } else if (isAssignableFromDouble(fieldType)) {
                        values.put(columnName, field.getDouble(bean));
                    } else if (Class.class.isAssignableFrom(fieldType)) {
                        values.put(columnName, ((Class)field.get(bean)).getName());
                    } else {
                        values.put(columnName, (byte[]) field.get(bean));
                    }
                }catch (IllegalAccessException e){
                    e.printStackTrace();
                }
            }
        }
        return values;
    }

    private String getColumns(){
        StringBuilder sb = new StringBuilder();
        Field[] fields = mBeanClass.getDeclaredFields();
        for (Field field:fields){
            field.setAccessible(true);
            String name = field.getName();
            sb.append(name).append(",");
        }
        return sb.substring(0, sb.length()-1);
    }

    @Override
    public boolean insert(T bean) {
        TableManager manager = TableManager.getInstance();
        String tableName = manager.getTableName(mBeanClass);
        ContentValues contentValues = getContentValues(bean);
        if(mDb.insert(tableName, getColumns(), contentValues) > 0){
            Logger.error(TAG, "insert success");
            return true;
        }
        return false;
    }

    @Override
    public boolean insert(List<T> beans) {
        int count = 0;
        for (T bean:beans){
            boolean isOk = insert(bean);
            if (isOk){
                count++;
            }
        }
        if (count == beans.size()){
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(final WhereBuilder builder) {
        return Transaction.execute(mDb, new Transaction.Worker() {
            @Override
            public boolean doTransition(SQLiteDatabase db) {
                TableManager manager = TableManager.getInstance();
                String tableName = manager.getTableName(mBeanClass);
                return mDb.delete(tableName, builder.getWhere(), convertWhereArgs(builder.getWhereArgs())) > 0;
            }
        });
    }

    @Override
    public boolean deleteAll() {
        return Transaction.execute(mDb, new Transaction.Worker() {
            @Override
            public boolean doTransition(SQLiteDatabase db) {
                TableManager manager = TableManager.getInstance();
                String tableName = manager.getTableName(mBeanClass);
                return mDb.delete(tableName, null, null) > 0;
            }
        });
    }

    public String[] convertWhereArgs(Object[] objects){
        List<String> result = new ArrayList<>();
        for (Object obj:objects){
            if (obj instanceof Number){
                result.add(String.valueOf(obj));
            }else if (obj instanceof String){
                result.add(obj.toString());
            }
        }
        return result.toArray(new String[]{});
    }

    @Override
    public boolean update(final WhereBuilder builder, final T newBean) {
        return Transaction.execute(mDb, new Transaction.Worker() {
            @Override
            public boolean doTransition(SQLiteDatabase db) {
                TableManager manager = TableManager.getInstance();
                String tableName = manager.getTableName(mBeanClass);
                ContentValues contentValues = getContentValues(newBean);
                return mDb.update(tableName, contentValues, builder.getWhere(), convertWhereArgs
                        (builder.getWhereArgs())) > 0;
            }
        });
    }

    @Override
    public boolean updateAll(final T newBean) {
        return Transaction.execute(mDb, new Transaction.Worker() {
            @Override
            public boolean doTransition(SQLiteDatabase db) {
                TableManager manager = TableManager.getInstance();
                String tableName = manager.getTableName(mBeanClass);
                ContentValues contentValues = getContentValues(newBean);
                return mDb.update(tableName, contentValues, null, null) > 0;
            }
        });
    }

    @Override
    public List<T> selectAll() {
        TableManager manager = TableManager.getInstance();
        String tableName = manager.getTableName(mBeanClass);
        Cursor cursor = mDb.query(tableName, null, null, null, null, null, null);
        return getResult(cursor);
    }

    @Override
    public List<T> select(QueryBuilder builder) {
        TableManager manager = TableManager.getInstance();
        String tableName = manager.getTableName(mBeanClass);
        String[] columns = builder.getColumns();
        String group = builder.getGroup();
        String having = builder.getHaving();
        String order = builder.getOrder();
        String limit = builder.getLimit();
        WhereBuilder whereBuilder = builder.getWhereBuilder();
        String where = whereBuilder.getWhere();
        String[] whereArgs = convertWhereArgs(whereBuilder.getWhereArgs());
        Cursor cursor = mDb.query(tableName, columns, where, whereArgs, group, having, order);
        List<T> result = getResult(cursor);
        if (TextUtils.isNotEmpty(limit)) {
            if (limit.contains(",")) {
                String[] limitPart = limit.split(",");
                return getSpecifiedBeans(result, Integer.valueOf(limitPart[0]), Integer.valueOf(limitPart[1]));
            } else {
                return getSpecifiedBeans(result, Integer.valueOf(limit));
            }
        }
        return result;
    }

    public List<T> getSpecifiedBeans(List<T> beans, int start, int length){
        List<T> newBeans = new ArrayList<>();
        for (int i=start;i<length;i++) {
            newBeans.add(beans.get(i));
        }
        return newBeans;
    }

    public List<T> getSpecifiedBeans(List<T> beans, int limit){
        return getSpecifiedBeans(beans, 0, limit);
    }

    @Override
    public int selectAllCount() {
        TableManager manager = TableManager.getInstance();
        String tableName = manager.getTableName(mBeanClass);
        Cursor cursor = mDb.rawQuery(SELECT_COUNT + tableName, null);
        return cursor.getCount();
    }

    @Override
    public int selectCount(QueryBuilder builder) {
        TableManager manager = TableManager.getInstance();
        String tableName = manager.getTableName(mBeanClass);
        WhereBuilder whereBuilder = builder.getWhereBuilder();
        String sql = SELECT_COUNT + tableName;
        sql += whereBuilder.getSQL();
        if (TextUtils.isNotEmpty(builder.getGroup())){
            sql += QueryBuilder.GROUP_BY + builder.getGroup();
        }
        if (TextUtils.isNotEmpty(builder.getHaving())){
            sql += QueryBuilder.HAVING + builder.getHaving();
        }
        if (TextUtils.isNotEmpty(builder.getOrder())){
            sql += QueryBuilder.ORDER_BY + builder.getOrder();
        }
        if (TextUtils.isNotEmpty(builder.getLimit())){
            sql += QueryBuilder.LIMIT + builder.getLimit();
        }
        Cursor cursor = mDb.rawQuery(sql, null);
        return cursor.getCount();
    }

    /**
     * 获取查询结果，<b>OrmTable的实现类必须提供空的构造方法</b>。
     *
     * @param cursor 游标。
     * @return 查询出来的数据。
     */
    public List<T> getResult(Cursor cursor){
        List<T> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            try {
                T bean = mBeanClass.newInstance();
                Field[] fields = mBeanClass.getDeclaredFields();
                for (Field field:fields){
                    field.setAccessible(true);
                    Column column = field.getAnnotation(Column.class);
                    if (column != null){
                        String columnName = column.value();
                        int columnIndex = cursor.getColumnIndex(columnName);
                        Class<?> fieldType = field.getType();
                        if (isAssignableFromCharSequence(fieldType)){
                            field.set(bean, cursor.getString(columnIndex));
                        }else if (isAssignableFromBoolean(fieldType)){
                            int value = cursor.getInt(columnIndex);
                            switch (value){
                                case 0:
                                    field.set(bean, false);
                                    break;
                                case 1:
                                    field.set(bean, true);
                                    break;
                            }
                        } else if (isAssignableFromLong(fieldType)){
                            field.set(bean, cursor.getLong(columnIndex));
                        } else if (isAssignableFromInteger(fieldType)){
                            field.set(bean, cursor.getInt(columnIndex));
                        } else if (isAssignableFromShort(fieldType)
                                || isAssignableFromByte(fieldType)){
                            field.set(bean, cursor.getShort(columnIndex));
                        } else if (isAssignableFromDouble(fieldType)){
                            field.set(bean, cursor.getDouble(columnIndex));
                        } else if (isAssignableFromFloat(fieldType)) {
                            field.set(bean, cursor.getFloat(columnIndex));
                        } else if (isAssignableFromCharacter(fieldType)) {
                            field.set(bean, cursor.getString(columnIndex));
                        } else if (isAssignableFromClass(fieldType)) {
                            try {
                                field.set(bean, Class.forName(cursor.getString(columnIndex)));
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else {
                            field.set(bean, cursor.getBlob(columnIndex));
                        }
                    }
                }
                result.add(bean);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
