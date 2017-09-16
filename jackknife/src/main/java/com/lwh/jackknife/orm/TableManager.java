package com.lwh.jackknife.orm;

import android.database.sqlite.SQLiteDatabase;

import com.lwh.jackknife.orm.annotation.Column;
import com.lwh.jackknife.orm.annotation.Table;
import com.lwh.jackknife.orm.type.BaseDataType;
import com.lwh.jackknife.orm.type.BooleanType;
import com.lwh.jackknife.orm.type.ByteArrayType;
import com.lwh.jackknife.orm.type.ByteType;
import com.lwh.jackknife.orm.type.CharType;
import com.lwh.jackknife.orm.type.DoubleType;
import com.lwh.jackknife.orm.type.FloatType;
import com.lwh.jackknife.orm.type.IntType;
import com.lwh.jackknife.orm.type.LongType;
import com.lwh.jackknife.orm.type.ShortType;
import com.lwh.jackknife.orm.type.SqlType;
import com.lwh.jackknife.orm.type.StringType;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TableManager {

    private static TableManager sInstance;
    private static SQLiteDatabase sDatabase;
    private Map<Class<OrmTable>,String> mTableNameMap;

    public TableManager(SQLiteDatabase db){
        this.sDatabase = db;
        mTableNameMap = new ConcurrentHashMap<>();
    }

    public static TableManager getInstance(SQLiteDatabase db){
        if (sInstance == null){
            synchronized (TableManager.class){
                if (sInstance == null) {
                    sInstance = new TableManager(db);
                }
            }
        }
        return sInstance;
    }

    public <T> String getTableName(Class<T> tableClass){
        if (mTableNameMap.containsKey(tableClass)){
            return mTableNameMap.get(tableClass);
        }
        return "";
    }

    public void createTable(Class<OrmTable> tableClass){
        Table table = tableClass.getAnnotation(Table.class);
        String tableName;
        if (table != null) {
            tableName = table.value();//按注解指定的表名来
        } else {
            String className = tableClass.getSimpleName();
            tableName = generateTableName(className);//按对象的类名和表名的映射规则生成默认的
        }
        Field[] fields = tableClass.getFields();
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE "+tableName + "(");
        for (Field field:fields){
            String columnName;
            String colunmType;
            Column column = field.getAnnotation(Column.class);
            if (column != null){
                columnName = column.value();
            }else {
                String fieldName = field.getName();
                columnName = generateColumnName(fieldName);//按对象的属性名和列表的映射规则生成默认的
            }
            Class<?> fieldType = field.getType();
            BaseDataType dataType;
            if (Boolean.class.isAssignableFrom(fieldType)){
                dataType = new BooleanType();
            }else if (Byte.class.isAssignableFrom(fieldType)) {
                dataType = new ByteType();
            }else if (Short.class.isAssignableFrom(fieldType)){
                dataType = new ShortType();
            }else if (Integer.class.isAssignableFrom(fieldType)){
                dataType = new IntType();
            }else if (Long.class.isAssignableFrom(fieldType)){
                dataType = new LongType();
            }else if (Float.class.isAssignableFrom(fieldType)) {
                dataType = new FloatType();
            }else if (Double.class.isAssignableFrom(fieldType)){
                dataType = new DoubleType();
            }else if (Character.class.isAssignableFrom(fieldType)){
                dataType = new CharType();
            }else if (String.class.isAssignableFrom(fieldType)){
                dataType = new StringType();
            }else{//数组或Object
                dataType = new ByteArrayType();
            }
            SqlType sqlType = dataType.getSqlType();
            colunmType = sqlType.name();
            sb.append(columnName + " " + colunmType).append(",");//添加一个表的列的sql语句
        }
        String sql = sb.substring(0, sb.length()-2)+");";//删除最后一个逗号并加上又括号
        sDatabase.execSQL(sql);
    }

    private String generateTableName(String className){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < className.length(); i++) {
            if (className.charAt(i) >= 65 && className.charAt(i) <= 90 || i == 0) {
                sb.append("_");
            }
            sb.append(String.valueOf(className.charAt(i)).toLowerCase(Locale.ENGLISH));
        }
        return "t_" + sb.toString().toLowerCase();
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

}
