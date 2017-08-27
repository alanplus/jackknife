package com.lwh.jackknife.orm.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.lwh.jackknife.orm.Column;
import com.lwh.jackknife.orm.Table;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BaseDao<T> implements Dao<T> {

    private SQLiteDatabase mDatabase;

    private boolean mTableCreated;

    private String mTableName;

    private Class<T> mDataClass;

    private Map<String, Field> mRelationMap;

    public synchronized boolean init(Class<T> dataClass, SQLiteDatabase db) {
        if (!mTableCreated) {
            this.mDatabase = db;
            this.mTableName = dataClass.getAnnotation(Table.class).value();
            this.mDataClass = dataClass;
            if (!mDatabase.isOpen()) {
                return false;
            }
            if (!TextUtils.isEmpty(createTable())) {
                mDatabase.execSQL(createTable());
            }
            mRelationMap = new HashMap<>();
            initRelationMap();
            mTableCreated = true;
        }
        return true;
    }

    /**
     * 初始化关系映射。
     */
    private void initRelationMap() {
        String sql = "SELECT * FROM "+this.mTableName+" LIMIT 1, 0";//查询出这个表的所有列。
        Cursor cursor = null;
        try {
            cursor = this.mDatabase.rawQuery(sql,null);
            String[] columnNames = cursor.getColumnNames();
            Field[] columnFields = mDataClass.getFields();
            for (Field field:columnFields) {
                field.setAccessible(true);
            }
            for (String columnName:columnNames) {
                Field colmunToFiled = null;
                //开始找对应关系
                for (Field field:columnFields) {
                    String fieldName;
                    if (field.getAnnotation(Column.class) != null) {
                        fieldName = field.getAnnotation(Column.class).value();
                    } else {
                        fieldName = field.getName();
                    }
                    if (columnName.equals(fieldName)) {
                        colmunToFiled=field;
                        break;
                    }
                }
                if (colmunToFiled != null) {
                    mRelationMap.put(columnName, colmunToFiled);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    /**
     * 创建表。
     *
     * @return 创表语句。
     */
    public abstract String createTable();

    @Override
    public Long insert(T data) {
        Map<String, String> map = getValues(data);
        ContentValues contentValues = getContentValues(map);
        Long result = mDatabase.insert(mTableName, null, contentValues);
        return result;
    }

    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Set<String> keys = map.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = map.get(key);
            if (value != null) {
                contentValues.put(key,value);
            }
        }
        return contentValues;
    }

    private Map<String, String> getValues(T data) {
        Map<String, String> result = new HashMap<>();
        Iterator<Field> iterator = mRelationMap.values().iterator();
        while (iterator.hasNext()) {
            Field columnFiled = iterator.next();
            String relationKey;
            String relationValue = null;
            Column column = columnFiled.getAnnotation(Column.class);
            if (column != null) {
                relationKey = column.value();
            } else {
                relationKey = columnFiled.getName();
            }
            try {
                if (columnFiled.get(data) == null) {
                    continue;
                }
                relationValue = columnFiled.get(data).toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            result.put(relationKey,relationValue);
        }
        return result;
    }

    @Override
    public int update(T data, T where) {
        int result;
        Map<String, String> values = getValues(data);
        Condition condition = new Condition(getValues(where));
        result = mDatabase.update(mTableName,getContentValues(values),condition.mWhereClause,condition.mWhereArgs);
        return result;
    }

    @Override
    public List<T> query(T where) {
        return query(where, null, null, null);
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        Map<String, String> values = getValues(where);
        String limitStr = null;
        if(startIndex != null && limit != null) {
            limitStr = startIndex+" , "+limit;
        }
        Condition condition = new Condition(values);
        Cursor cursor;
        cursor = mDatabase.query(mTableName, null, condition.getWhereClause(),
                condition.getWhereArgs(), null, null, orderBy, limitStr);
        List<T> result = getReslut(cursor, where);
        cursor.close();
        return result;
    }

    @Override
    public int queryCount(T where){
        List<T> datas = query(where);
        if (datas != null){
            return datas.size();
        }
        return 0;
    }

    /**
     * 获取查询结果。
     *
     * @param cursor 游标。
     * @param where 条件对象。
     * @return 查询结果。
     */
    private List<T> getReslut(Cursor cursor, T where) {
        List list = new ArrayList();
        Object item;
        while (cursor.moveToNext()) {
            Class<?> whereClass = where.getClass();
            try {
                item = whereClass.newInstance();
                Iterator<Map.Entry<String, Field>> iterator = mRelationMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Field> entry = iterator.next();
                    String columnName = entry.getKey();
                    Field field = entry.getValue();
                    Integer colmunIndex = cursor.getColumnIndex(columnName);
                    Class type = field.getType();
                    if (colmunIndex != -1) {
                        if (type == String.class) {
                            field.set(item,cursor.getString(colmunIndex));
                        }else if(type == Integer.class) {
                            field.set(item,cursor.getInt(colmunIndex));
                        }else  if(type == Long.class) {
                            field.set(item,cursor.getLong(colmunIndex));
                        }else if(type == Double.class) {
                            field.set(item,cursor.getDouble(colmunIndex));
                        }else if(type == byte[].class) {
                            field.set(item,cursor.getBlob(colmunIndex));
                        } else {
                            continue;
                        }
                    }
                }
                list.add(item);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    @Override
    public int delete(T where) {
        Map<String, String> values = getValues(where);
        Condition condition = new Condition(values);
        int result = mDatabase.delete(mTableName, condition.getWhereClause(), condition.getWhereArgs());
        return result;
    }

    @Override
    public boolean deleteAll(){
        return true;
    }

    /**
     * 查询条件。
     */
    private class Condition {

        /**
         * where子句。
         */
        private String mWhereClause;

        /**
         * where参数。
         */
        private String[] mWhereArgs;

        public Condition(Map<String, String> map) {
            List<String> list = new ArrayList();
            StringBuilder builder = new StringBuilder();
            builder.append(" 1=1 ");
            Set<String> keys = map.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = map.get(key);
                if (value != null) {
                    builder.append(" and "+key+" =? ");
                    list.add(value);
                }
            }
            this.mWhereClause = builder.toString();
            this.mWhereArgs = list.toArray(new String[list.size()]);
        }

        public String getWhereClause() {
            return mWhereClause;
        }

        public String[] getWhereArgs() {
            return mWhereArgs;
        }
    }
}
