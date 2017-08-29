package com.lwh.jackknife.orm.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lwh.jackknife.orm.Column;
import com.lwh.jackknife.orm.Table;
import com.lwh.jackknife.util.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 万能DAO。
 *
 * @param <T> 数据实体类。
 */
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
            if (TextUtils.isNotEmpty(createTable())) {
                mDatabase.execSQL(createTable());//创表
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
        String sql = "SELECT * FROM " + mTableName + " LIMIT 1";
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

    /**
     * 创建表。
     *
     * @return 创表语句。
     */
    protected abstract String createTable();

    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues values = new ContentValues();
        Set<String> keys = map.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = map.get(key);
            if(value != null){
                values.put(key,value);
            }
        }
        return values;
    }

    @Override
    public boolean insert(T data) {
        Map<String, String> map = getValues(data);
        ContentValues values = getContentValues(map);
        long rowID = mDatabase.insert(mTableName, null, values);
        if (rowID != -1){
            return true;
        }
        return false;
    }

    @Override
    public boolean insert(List<T> datas) {
        if (datas == null){
            return false;
        }
        int count = 0;
        for (T data:datas){
            if(insert(data)){
                count++;
            }
        }
        if (count == datas.size()){
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(T where) {
        Map<String, String> map = getValues(where);
        Condition condition = new Condition(map);
        int rowsOfAffected = mDatabase.delete(mTableName, condition.getWhereClause(), condition.getWhereArgs());
        if (rowsOfAffected > 0){
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteAll(){
        int rowsOfAffected = mDatabase.delete(mTableName, null, null);
        if (rowsOfAffected > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean update(T newData, T where) {
        Map<String, String> map = getValues(where);
        Condition condition = new Condition(map);
        int rowsOfAffected = mDatabase.update(mTableName, getContentValues(map), condition.getWhereClause(),
                condition.getWhereArgs());
        if (rowsOfAffected > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<T> query(T where) {
        Map<String, String> map = getValues(where);
        Condition condition = new Condition(map);
        Cursor cursor = mDatabase.query(mTableName, null, condition.getWhereClause(),
                condition.getWhereArgs(), null, null, null);
        return getResult(cursor, where);
    }

    @Override
    public T queryOnly(T where) {
        if (query(where) != null) {
            return query(where).get(0);
        }
        return null;
    }

    /**
     * 获取查询结果。
     *
     * @param cursor 游标。
     * @param where 条件对象。
     * @return 查询结果。
     */
    private List<T> getResult(Cursor cursor, T where) {
        List<T> datas = new ArrayList<>();
        T data;
        while (cursor.moveToNext()) {
            Class<?> whereClass = where.getClass();
            try {
                data = (T) whereClass.newInstance();
                Iterator<Map.Entry<String, Field>> iterator = mRelationMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Field> entry = iterator.next();
                    String columnName = entry.getKey();
                    Field field = entry.getValue();
                    Integer columnIndex = cursor.getColumnIndex(columnName);
                    Class type = field.getType();
                    if (columnIndex != -1) {
                        if (type == String.class) {
                            field.set(data,cursor.getString(columnIndex));
                        }else if(type == Integer.class) {
                            field.set(data,cursor.getInt(columnIndex));
                        }else  if(type == Long.class) {
                            field.set(data,cursor.getLong(columnIndex));
                        }else if(type == Double.class) {
                            field.set(data,cursor.getDouble(columnIndex));
                        }else if(type == byte[].class) {
                            field.set(data,cursor.getBlob(columnIndex));
                        } else {
                            continue;
                        }
                    }
                }
                datas.add(data);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return datas;
    }

    private class Condition {

        private String mWhereClause;
        private String[] mWhereArgs;
        private String AND = " AND ";

        public Condition(Map<String, String> map){
            List<String> list = new ArrayList<>();
            StringBuilder builder = new StringBuilder();
            Set<String> keys = map.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = map.get(key);
                if (value != null) {
                    builder.append(AND+key+" =? ");
                    list.add(value);
                }
            }
            this.mWhereClause = builder.substring(AND.length());
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
