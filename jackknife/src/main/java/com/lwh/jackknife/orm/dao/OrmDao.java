package com.lwh.jackknife.orm.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lwh.jackknife.app.Application;
import com.lwh.jackknife.orm.helper.OrmSQLiteOpenHelper;
import com.lwh.jackknife.orm.annotation.Column;
import com.lwh.jackknife.orm.builder.QueryBuilder;
import com.lwh.jackknife.orm.table.TableManager;
import com.lwh.jackknife.orm.builder.WhereBuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 万能DAO。
 *
 * @param <T> 数据实体类。
 */
public class OrmDao<T> implements Dao<T> {

    private Class<T> mBeanClass;
    private OrmSQLiteOpenHelper mHelper;
    private SQLiteDatabase mDb;

    public OrmDao(Class<T> beanClass){
        this.mBeanClass = beanClass;
        this.mHelper = Application.getInstance().getSQLiteOpenHelper();
        this.mDb = mHelper.getWritableDatabase();
    }

    public ContentValues getContentValues(T bean) throws IllegalAccessException {
        ContentValues values = new ContentValues();
        Field[] fields = mBeanClass.getFields();
        for (Field field:fields){
            field.setAccessible(true);
            Column column = field.getAnnotation(Column.class);
            String columnName;
            if (column != null){
                columnName = column.value();
            } else {
                columnName = generateColumnName(field.getName());
            }
            Class<?> fieldType = field.getType();
            if (String.class.isAssignableFrom(fieldType)){
                values.put(columnName, String.valueOf(field.get("")));
            }else if (Boolean.class.isAssignableFrom(fieldType)){
                values.put(columnName, field.getBoolean(bean));
            }else if(Byte.class.isAssignableFrom(fieldType)){
                values.put(columnName, field.getByte(bean));
            }else if (Short.class.isAssignableFrom(fieldType)){
                values.put(columnName, field.getShort(bean));
            }else if (Integer.class.isAssignableFrom(fieldType)){
                values.put(columnName, field.getInt(bean));
            }else if (Long.class.isAssignableFrom(fieldType)){
                values.put(columnName, field.getLong(bean));
            }else if (Float.class.isAssignableFrom(fieldType)){
                values.put(columnName, field.getFloat(bean));
            }else if (Double.class.isAssignableFrom(fieldType)){
                values.put(columnName, field.getDouble(bean));
            }else {
                values.put(columnName, (byte[]) field.get(""));
            }
        }
        return values;
    }

    private String generateColumnName(String fieldName){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fieldName.length(); i++) {
            if (fieldName.charAt(i) >= 65 && fieldName.charAt(i) <= 90 || i == 0) {
                sb.append("_");
            }
            sb.append(String.valueOf(fieldName.charAt(i)).toLowerCase(Locale.ENGLISH));
        }
        return sb.toString().toLowerCase();
    }

    @Override
    public boolean insert(T bean) {
        try {
            TableManager manager = TableManager.getInstance(mDb);
            String tableName = manager.getTableName(mBeanClass);
            ContentValues contentValues = getContentValues(bean);
            if(mDb.insert(tableName, null, contentValues) > 0){
                return true;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
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
    public boolean delete(WhereBuilder builder) {
        TableManager manager = TableManager.getInstance(mDb);
        String tableName = manager.getTableName(mBeanClass);
        if (mDb.delete(tableName, builder.getWhere(), (String[]) builder.getWhereArgs()) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean delete() {
        TableManager manager = TableManager.getInstance(mDb);
        String tableName = manager.getTableName(mBeanClass);
        if (mDb.delete(tableName, null, null) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean update(WhereBuilder builder, T newBean) {
        TableManager manager = TableManager.getInstance(mDb);
        String tableName = manager.getTableName(mBeanClass);
        try {
            ContentValues contentValues = getContentValues(newBean);
            if (mDb.update(tableName, contentValues, builder.getWhere(), (String[]) builder.getWhereArgs()) > 0){
                return true;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(T newBean) {
        TableManager manager = TableManager.getInstance(mDb);
        String tableName = manager.getTableName(mBeanClass);
        try {
            ContentValues contentValues = getContentValues(newBean);
            if (mDb.update(tableName, contentValues, null, null) > 0){
                return true;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<T> select() {
        TableManager manager = TableManager.getInstance(mDb);
        String tableName = manager.getTableName(mBeanClass);
        Cursor cursor = mDb.query(tableName, null, null, null, null, null, null);
        return getResult(cursor);
    }

    @Override
    public List<T> select(QueryBuilder builder) {
        TableManager manager = TableManager.getInstance(mDb);
        String tableName = manager.getTableName(mBeanClass);
        String[] columns = builder.getColumns();
        String group = builder.getGroup();
        String having = builder.getHaving();
        String order = builder.getOrder();
        String limit = builder.getLimit();
        WhereBuilder whereBuilder = builder.getWhereBuilder();
        String where = whereBuilder.getWhere();
        String[] whereArgs = (String[]) whereBuilder.getWhereArgs();
        Cursor cursor = mDb.query(tableName, columns, where, whereArgs, group, having, order);
        List<T> result = getResult(cursor);
        if (limit.contains(",")){
            String[] limitPart = limit.split(",");
            return getSpecifiedBeans(result, Integer.valueOf(limitPart[0]), Integer.valueOf(limitPart[1]));
        }else{
            return getSpecifiedBeans(result, Integer.valueOf(limit));
        }
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
    public int selectCount() {
        TableManager manager = TableManager.getInstance(mDb);
        String tableName = manager.getTableName(mBeanClass);
        Cursor cursor = mDb.rawQuery("SELECT COUNT(*) FROM TABLE " + tableName, null);
        return cursor.getCount();
    }

    @Override
    public int selectCount(QueryBuilder builder) {
        TableManager manager = TableManager.getInstance(mDb);
        String tableName = manager.getTableName(mBeanClass);
        WhereBuilder whereBuilder = builder.getWhereBuilder();
        String sql = "SELECT COUNT(*) FROM TABLE " + tableName;
        sql += whereBuilder.getSQL();
        if (builder.getGroup() != null){
            sql += QueryBuilder.GROUP_BY + builder.getGroup();
        }
        if (builder.getHaving() != null){
            sql += QueryBuilder.HAVING + builder.getHaving();
        }
        if (builder.getOrder() != null){
            sql += QueryBuilder.ORDER_BY + builder.getOrder();
        }
        if (builder.getLimit() != null){
            sql += QueryBuilder.LIMIT + builder.getLimit();
        }
        Cursor cursor = mDb.rawQuery(sql, null);
        return cursor.getCount();
    }

    public List<T> getResult(Cursor cursor){
        List<T> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            try {
                T bean = mBeanClass.newInstance();
                Field[] fields = mBeanClass.getFields();
                for (Field field:fields){
                    field.setAccessible(true);
                    //这个肯定不是第一次，TableManager早在SQLiteHelper就会创建。
                    TableManager manager = TableManager.getInstance(null);
                    field.set(bean, cursor.getColumnIndex(manager.getTableName(mBeanClass)));
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
