package com.lwh.jackknife.orm.table;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lwh.jackknife.app.Application;
import com.lwh.jackknife.orm.annotation.Column;
import com.lwh.jackknife.orm.annotation.Ignore;
import com.lwh.jackknife.orm.annotation.Table;
import com.lwh.jackknife.orm.dao.DaoFactory;
import com.lwh.jackknife.orm.dao.OrmDao;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 表管理器。
 */
public class TableManager {

    private static TableManager sInstance;
    private static SQLiteDatabase sDatabase;
    private Map<Class<? extends OrmTable>, String> mTableNameMap;
    private final char A = 'A';
    private final char Z = 'Z';
    private final String CREATE_TABLE = "CREATE TABLE";
    private final String SPACE = " ";
    private final String LEFT_PARENTHESIS = "(";
    private final String RIGHT_PARENTHESIS = ")";
    private final String COMMA = ",";
    private final String SEMICOLON = ";";
    private final String UNDERLINE = "_";
    private final String TABLE_NAME_HEADER = "t" + UNDERLINE;
    private OrmDao<TableName> mDao;

    private TableManager(SQLiteDatabase db){
        this.sDatabase = db;
        createTable(TableName.class);
        mTableNameMap = new ConcurrentHashMap<>();
        mDao = DaoFactory.getDao(TableName.class);
        List<TableName> tables = mDao.select();
        for (TableName table:tables) {
            String tableName = table.getTableName();
            Class<? extends OrmTable> tableClass = table.getTableClass();
            mTableNameMap.put(tableClass, tableName);
        }
    }

    public static TableManager getInstance(){
        if (sInstance == null){
            synchronized (TableManager.class){
                if (sInstance == null) {
                    if (Application.getInstance() instanceof Application) {
                        SQLiteOpenHelper helper = Application.getInstance().getSQLiteOpenHelper();
                        sDatabase = helper.getWritableDatabase();
                        sInstance = new TableManager(sDatabase);
                    }
                }
            }
        }
        return sInstance;
    }

    public <T extends OrmTable> String getTableName(Class<T> tableClass){
        Table table = tableClass.getAnnotation(Table.class);
        return table.value();
    }

    public <T extends OrmTable> void createTable(Class<T> tableClass){
        Table table = tableClass.getAnnotation(Table.class);
        String tableName;
        if (table != null) {
            tableName = table.value();//按注解指定的表名来
        } else {
            String className = tableClass.getSimpleName();
            tableName = generateTableName(className);//按对象的类名和表名的映射规则生成默认的
        }
        Field[] fields = tableClass.getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        sb.append(CREATE_TABLE + SPACE + tableName + LEFT_PARENTHESIS);
        for (Field field:fields){
            field.setAccessible(true);
            Ignore ignore = field.getAnnotation(Ignore.class);
            if (ignore != null){
                continue;
            }
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
            if (boolean.class.isAssignableFrom(fieldType) || Boolean.class.isAssignableFrom(fieldType)){
                dataType = new BooleanType();
            }else if (byte.class.isAssignableFrom(fieldType) || Byte.class.isAssignableFrom(fieldType)) {
                dataType = new ByteType();
            }else if (short.class.isAssignableFrom(fieldType) || Short.class.isAssignableFrom(fieldType)){
                dataType = new ShortType();
            }else if (int.class.isAssignableFrom(fieldType) || Integer.class.isAssignableFrom(fieldType)){
                dataType = new IntType();
            }else if (long.class.isAssignableFrom(fieldType) || Long.class.isAssignableFrom(fieldType)){
                dataType = new LongType();
            }else if (float.class.isAssignableFrom(fieldType) || Float.class.isAssignableFrom(fieldType)) {
                dataType = new FloatType();
            }else if (double.class.isAssignableFrom(fieldType) || Double.class.isAssignableFrom(fieldType)){
                dataType = new DoubleType();
            }else if (char.class.isAssignableFrom(fieldType) || Character.class.isAssignableFrom(fieldType)){
                dataType = new CharType();
            }else if (String.class.isAssignableFrom(fieldType)){
                dataType = new StringType();
            }else{//数组或Object
                dataType = new ByteArrayType();
            }
            SqlType sqlType = dataType.getSqlType();
            colunmType = sqlType.name();
            sb.append(columnName + SPACE + colunmType).append(COMMA);//添加一个表的列的sql语句
        }
        String sql = sb.substring(0, sb.length()-1) + RIGHT_PARENTHESIS + SEMICOLON;//删除最后一个逗号并加上右括号
        try {
            sDatabase.execSQL(sql);
            mTableNameMap.put(tableClass, tableName);
            TableName nameTable = new TableName(tableClass, tableName);
            mDao.insert(nameTable);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private String generateTableName(String className){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < className.length(); i++) {
            if (className.charAt(i) >= A && className.charAt(i) <= Z || i == 0) {
                sb.append(UNDERLINE);
            }
            sb.append(String.valueOf(className.charAt(i)).toLowerCase(Locale.ENGLISH));
        }
        return TABLE_NAME_HEADER + sb.toString().toLowerCase();
    }

    private String generateColumnName(String fieldName){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fieldName.length(); i++) {
            if (fieldName.charAt(i) >= A && fieldName.charAt(i) <= Z || i == 0) {
                sb.append(UNDERLINE);
            }
            sb.append(String.valueOf(fieldName.charAt(i)).toLowerCase(Locale.ENGLISH));
        }
        return sb.toString().toLowerCase();
    }
}
