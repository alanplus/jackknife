package com.lwh.jackknife.orm.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lwh.jackknife.app.Application;
import com.lwh.jackknife.orm.annotation.Column;
import com.lwh.jackknife.orm.builder.QueryBuilder;
import com.lwh.jackknife.orm.builder.WhereBuilder;
import com.lwh.jackknife.orm.helper.OrmSQLiteOpenHelper;
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
    private OrmSQLiteOpenHelper mHelper;
    private SQLiteDatabase mDb;
    private final String TAG = getClass().getSimpleName();

    /* package */ OrmDao(Class<T> beanClass){
        this.mBeanClass = beanClass;
        this.mHelper = Application.getInstance().getSQLiteOpenHelper();
        this.mDb = mHelper.getWritableDatabase();
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
                    if (String.class.isAssignableFrom(fieldType)) {
                        values.put(columnName, String.valueOf(field.get(bean)));
                    } else if (boolean.class.isAssignableFrom(fieldType) || Boolean.class.isAssignableFrom(fieldType)) {
                        values.put(columnName, field.getBoolean(bean));
                    } else if (byte.class.isAssignableFrom(fieldType) || Byte.class.isAssignableFrom(fieldType)) {
                        values.put(columnName, field.getByte(bean));
                    } else if (short.class.isAssignableFrom(fieldType) || Short.class.isAssignableFrom(fieldType)) {
                        values.put(columnName, field.getShort(bean));
                    } else if (int.class.isAssignableFrom(fieldType) || Integer.class.isAssignableFrom(fieldType)) {
                        values.put(columnName, field.getInt(bean));
                    } else if (long.class.isAssignableFrom(fieldType) || Long.class.isAssignableFrom(fieldType)) {
                        values.put(columnName, field.getLong(bean));
                    } else if (float.class.isAssignableFrom(fieldType) || Float.class.isAssignableFrom(fieldType)) {
                        values.put(columnName, field.getFloat(bean));
                    } else if (double.class.isAssignableFrom(fieldType) || Double.class.isAssignableFrom(fieldType)) {
                        values.put(columnName, field.getDouble(bean));
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
    public boolean delete(WhereBuilder builder) {
        TableManager manager = TableManager.getInstance();
        String tableName = manager.getTableName(mBeanClass);
        if (mDb.delete(tableName, builder.getWhere(), convertWhereArgs(builder.getWhereArgs())) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean delete() {
        TableManager manager = TableManager.getInstance();
        String tableName = manager.getTableName(mBeanClass);
        if (mDb.delete(tableName, null, null) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean update(WhereBuilder builder, T newBean) {
        TableManager manager = TableManager.getInstance();
        String tableName = manager.getTableName(mBeanClass);
        ContentValues contentValues = getContentValues(newBean);
        if (mDb.update(tableName, contentValues, builder.getWhere(), convertWhereArgs(builder.getWhereArgs())) > 0){
            return true;
        }
        return false;
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
    public boolean update(T newBean) {
        TableManager manager = TableManager.getInstance();
        String tableName = manager.getTableName(mBeanClass);
        ContentValues contentValues = getContentValues(newBean);
        if (mDb.update(tableName, contentValues, null, null) > 0){
            return true;
        }
        return false;
    }

    @Override
    public List<T> select() {
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
    public int selectCount() {
        TableManager manager = TableManager.getInstance();
        String tableName = manager.getTableName(mBeanClass);
        Cursor cursor = mDb.rawQuery("SELECT COUNT(*) FROM TABLE " + tableName, null);
        return cursor.getCount();
    }

    @Override
    public int selectCount(QueryBuilder builder) {
        TableManager manager = TableManager.getInstance();
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
                Field[] fields = mBeanClass.getDeclaredFields();
                for (Field field:fields){
                    field.setAccessible(true);
                    Column column = field.getAnnotation(Column.class);
                    if (column != null){
                        String columnName = column.value();
                        int columnIndex = cursor.getColumnIndex(columnName);
                        Class<?> fieldType = field.getType();
                        if (String.class.isAssignableFrom(fieldType)){
                            field.set(bean, cursor.getString(columnIndex));
                        }else if (boolean.class.isAssignableFrom(fieldType) || Boolean.class.isAssignableFrom(fieldType)){
                            int value = cursor.getInt(columnIndex);
                            switch (value){
                                case 0:
                                    field.set(bean, false);
                                    break;
                                case 1:
                                    field.set(bean, true);
                                    break;
                            }
                        } else if (long.class.isAssignableFrom(fieldType) || Long.class.isAssignableFrom(fieldType)){
                            field.set(bean, cursor.getLong(columnIndex));
                        } else if (int.class.isAssignableFrom(fieldType) || Integer.class.isAssignableFrom(fieldType)){
                            field.set(bean, cursor.getInt(columnIndex));
                        } else if (short.class.isAssignableFrom(fieldType) || Short.class.isAssignableFrom(fieldType)
                                || byte.class.isAssignableFrom(fieldType) || Byte.class.isAssignableFrom(fieldType)){
                            field.set(bean, cursor.getShort(columnIndex));
                        } else if (double.class.isAssignableFrom(fieldType) || Double.class.isAssignableFrom(fieldType)){
                            field.set(bean, cursor.getDouble(columnIndex));
                        } else if (float.class.isAssignableFrom(fieldType) || Float.class.isAssignableFrom(fieldType)) {
                            field.set(bean, cursor.getFloat(columnIndex));
                        } else if (char.class.isAssignableFrom(fieldType) || Character.class.isAssignableFrom(fieldType)) {
                            field.set(bean, cursor.getString(columnIndex));
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
