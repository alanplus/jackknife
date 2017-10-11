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

package com.lwh.jackknife.orm.table;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lwh.jackknife.app.Application;
import com.lwh.jackknife.orm.AssignType;
import com.lwh.jackknife.orm.annotation.Column;
import com.lwh.jackknife.orm.annotation.ForeignKey;
import com.lwh.jackknife.orm.annotation.NonColumn;
import com.lwh.jackknife.orm.annotation.NotNull;
import com.lwh.jackknife.orm.annotation.PrimaryKey;
import com.lwh.jackknife.orm.annotation.Table;
import com.lwh.jackknife.orm.annotation.Unique;
import com.lwh.jackknife.orm.builder.QueryBuilder;
import com.lwh.jackknife.orm.builder.WhereBuilder;
import com.lwh.jackknife.orm.dao.DaoFactory;
import com.lwh.jackknife.orm.dao.OrmDao;
import com.lwh.jackknife.orm.exception.ConstraintException;
import com.lwh.jackknife.orm.type.BaseDataType;
import com.lwh.jackknife.orm.type.BooleanType;
import com.lwh.jackknife.orm.type.ByteArrayType;
import com.lwh.jackknife.orm.type.ByteType;
import com.lwh.jackknife.orm.type.CharType;
import com.lwh.jackknife.orm.type.ClassType;
import com.lwh.jackknife.orm.type.DoubleType;
import com.lwh.jackknife.orm.type.FloatType;
import com.lwh.jackknife.orm.type.IntType;
import com.lwh.jackknife.orm.type.LongType;
import com.lwh.jackknife.orm.type.ShortType;
import com.lwh.jackknife.orm.type.SqlType;
import com.lwh.jackknife.orm.type.StringType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OrmTable表的管理器。
 */
public class TableManager {

    /**
     * 表管理器的单例。
     */
    private static TableManager sInstance;

    /**
     * 数据库操作类。
     */
    private static SQLiteDatabase sDatabase;

    /**
     * 创建一个存放映射关系的Map。
     */
    private Map<Class<? extends OrmTable>, String> mTableNameMap = new ConcurrentHashMap<>();
    private final char A = 'A';
    private final char Z = 'Z';
    private final String CREATE_TABLE = "CREATE TABLE";
    private final String AUTO_INCREMENT = "AUTO INCREMENT";
    private final String TABLE_NAME = "table_name";
    private final String SPACE = " ";
    private final String UNIQUE = "UNIQUE";
    private final String NOT_NULL = "NOT NULL";
    private final String PRIMARY_KEY = "PRIMARY KEY";
    private final String REFERENCES = "REFERENCES";
    private final String LEFT_PARENTHESIS = "(";
    private final String RIGHT_PARENTHESIS = ")";
    private final String COMMA = ",";
    private final String SEMICOLON = ";";
    private final String UNDERLINE = "_";
    private final String TABLE_NAME_HEADER = "t" + UNDERLINE;
    private OrmDao<TableName> mDao;

    private TableManager(SQLiteDatabase db){
        this.sDatabase = db;
        mDao = DaoFactory.getDao(TableName.class);
    }

    public void installTables(){
        createTable(TableName.class);
        List<TableName> tables = mDao.selectAll();

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
                        sInstance.installTables();
                    }
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取表名。
     *
     * @param tableClass 实现OrmTable接口的类的类型。
     * @param <T> 实现OrmTable接口的类。
     * @return 一个实现OrmTable接口的表名。
     */
    public <T extends OrmTable> String getTableName(Class<T> tableClass){
        Table table = tableClass.getAnnotation(Table.class);//获取到表的Table注解
        String tableName;
        if (table != null) {//不存在Table注解的情况
            tableName = table.value();//按注解指定的表名来
        } else {//存在Table注解的情况
            String className = tableClass.getSimpleName();
            tableName = generateTableName(className);//按对象的类名和表名的映射规则生成默认的
        }
        return tableName;
    }

    public <T extends OrmTable> String getColumnName(Field field){
        String columnName;
        Column column = field.getAnnotation(Column.class);
        if (column != null){
            columnName = column.value();
        }else {
            String fieldName = field.getName();
            columnName = generateColumnName(fieldName);//按对象的属性名和列表的映射规则生成默认的
        }
        return columnName;
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

    /**
     * 获取声明的可映射的数据类型。
     *
     * @return 你要映射的所有数据类型。
     */
    protected List<BaseDataType> getDeclaredDataTypes(){
        List<BaseDataType> dataTypes = new ArrayList<>();
        dataTypes.add(BooleanType.getInstance());
        dataTypes.add(ByteType.getInstance());
        dataTypes.add(ShortType.getInstance());
        dataTypes.add(IntType.getInstance());
        dataTypes.add(LongType.getInstance());
        dataTypes.add(FloatType.getInstance());
        dataTypes.add(DoubleType.getInstance());
        dataTypes.add(CharType.getInstance());
        dataTypes.add(StringType.getInstance());
        dataTypes.add(ClassType.getInstance());
        return dataTypes;
    }

    private BaseDataType matchDataType(Field field){
        List<BaseDataType> dataTypes = getDeclaredDataTypes();
        for (BaseDataType dataType:dataTypes) {
            if (dataType.matches(field)){ //匹配到合适的数据类型。
                return dataType;
            }
        }
        return ByteArrayType.getInstance();
    }

    public <T extends OrmTable> void createTable(Class<T> tableClass){
        String tableName = getTableName(tableClass);//获取到表名
        Field[] fields = tableClass.getDeclaredFields();//拿到这个表的所有字段
        StringBuilder sb = new StringBuilder();
        sb.append(CREATE_TABLE + SPACE + tableName + LEFT_PARENTHESIS);//sql:CREATE TABLE ${tableName} (
        List<Annotation> keys = new ArrayList<>();//记录该表有没有主键或外键
        for (Field field:fields){//遍历表的字段
            field.setAccessible(true);
            NonColumn nonColumn = field.getAnnotation(NonColumn.class);//不需要映射的列
            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);//主键
            ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);//外键
            if (nonColumn != null){//跳过不需要映射的字段
                continue;
            }
            if(primaryKey != null) {
                keys.add(primaryKey);//将主键加入集合
            }
            if (foreignKey != null) {
                keys.add(foreignKey);//将外键加入集合
            }
            BaseDataType dataType = matchDataType(field);//解析并映射数据类型
            SqlType sqlType = dataType.getSqlType();
            String columnType = sqlType.name();//列类型
            String columnName = getColumnName(field);//列名
            sb.append(columnName + SPACE + columnType);//sql:${columnName} ${columnType}
            Unique unique = field.getAnnotation(Unique.class);
            if (unique != null) {
                sb.append(SPACE).append(UNIQUE);//sql: UNIQUE
            }
            NotNull notNull = field.getAnnotation(NotNull.class);
            if (notNull != null) {
                sb.append(SPACE).append(NOT_NULL);//sql: NOT NULL
            }
            if (primaryKey != null) {
                sb.append(SPACE).append(PRIMARY_KEY);//sql: PRIMARY KEY
                AssignType assignType = primaryKey.value();//分配类型
                if (assignType.equals(AssignType.BY_MYSELF)) {
                } else if (assignType.equals(AssignType.AUTO_INCREMENT)) {
                    sb.append(SPACE).append(AUTO_INCREMENT);
                }
            }
            if (foreignKey != null){
                Class<? extends OrmTable> foreignKeyTableClass = foreignKey.value();
                if (!foreignKeyTableClass.equals(tableClass)){//建立外键的条件，不能建立在自己的主键基础上
                    String foreignKeyTableName = foreignKeyTableClass.getName();//外键表的表名
                    sb.append(SPACE).append(REFERENCES).append(SPACE).append(foreignKeyTableName)
                            .append(LEFT_PARENTHESIS);//sql: REFERENCES ${foreignKeyTableName} (
                    Field[] foreignKeyTableFields = foreignKeyTableClass.getDeclaredFields();//拿到外键所指向表的所有列
                    for (Field foreignKeyTableField:foreignKeyTableFields){//遍历复合主键，如果有
                        PrimaryKey foreignKeyTablePrimaryKey = foreignKeyTableField.getAnnotation(PrimaryKey.class);
                        if (foreignKeyTablePrimaryKey != null){
                            String foreignKeyTablePrimaryKeyName = field.getName();//外键表的某个主键的名字
                            sb.append(foreignKeyTablePrimaryKeyName).append(COMMA);//sql:${foreignKeyTablePrimaryKeyName},
                        }
                    }
                    sb.deleteCharAt(sb.length()-1).append(RIGHT_PARENTHESIS);//删掉最后一个逗号
                }
            }
            sb.append(COMMA);
        }
        if (keys.size() == 0){
            throw new ConstraintException("请至少指定一个有效的主键或外键");
        }
        try {
            String sql = sb.deleteCharAt(sb.length()-1).append(RIGHT_PARENTHESIS).append(SEMICOLON)
                    .toString();//删除最后一个逗号并加上右括号
            sDatabase.execSQL(sql);
            mTableNameMap.put(tableClass, tableName);//将新创建出来的表加入缓存
            WhereBuilder whereBuilder = new WhereBuilder()
                    .addWhereEqualTo(TABLE_NAME, tableName);
            QueryBuilder queryBuilder = new QueryBuilder().where(whereBuilder);
            int count = mDao.selectCount(queryBuilder);
            if (count == 0) {//没有从表名表中查询到此表
                TableName nameTable = new TableName(tableClass, tableName);//存放Orm框架创建的表的系统表
                mDao.insert(nameTable);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
