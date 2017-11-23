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
import android.util.Log;

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

public class TableManager {

    private static TableManager sInstance;

    private static SQLiteDatabase sDatabase;

    private final String TAG = getClass().getSimpleName();

    private Map<Class<? extends OrmTable>, String> mTableNameMap = new ConcurrentHashMap<>();

    private final char A = 'A';

    private final char Z = 'Z';

    private final String CREATE_TABLE = "CREATE TABLE";

    private final String ALTER_TABLE = "ALTER TABLE";

    private final String IF_NOT_EXISTS = "IF NOT EXISTS";

    private final String ADD_COLUMN = "ADD COLUMN";

    private final String AUTO_INCREMENT = "AUTOINCREMENT";

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

    private TableManager(SQLiteDatabase db) {
        this.sDatabase = db;
        mDao = DaoFactory.getDao(TableName.class);
    }

    public void installTables() {
        createTable(TableName.class);
        List<TableName> tables = mDao.selectAll();
        for (TableName table:tables) {
            String tableName = table.getTableName();
            Class<? extends OrmTable> tableClass = table.getTableClass();
            mTableNameMap.put(tableClass, tableName);
        }
    }

    public static TableManager getInstance() {
        if (sInstance == null) {
            synchronized (TableManager.class) {
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

    public <T extends OrmTable> String getTableName(Class<T> tableClass) {
        Table table = tableClass.getAnnotation(Table.class);
        String tableName;
        if (table != null) {
            tableName = table.value();
        } else {
            String className = tableClass.getSimpleName();
            tableName = generateTableName(className);
        }
        return tableName;
    }

    public String getColumnName(Field field){
        String columnName;
        Column column = field.getAnnotation(Column.class);
        if (column != null) {
            columnName = column.value();
        } else {
            String fieldName = field.getName();
            columnName = generateColumnName(fieldName);
        }
        return columnName;
    }

    public String generateTableName(String className) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < className.length(); i++) {
            if (className.charAt(i) >= A && className.charAt(i) <= Z && i != 0) {
                sb.append(UNDERLINE);
            }
            sb.append(String.valueOf(className.charAt(i)).toLowerCase(Locale.ENGLISH));
        }
        return TABLE_NAME_HEADER + sb.toString().toLowerCase();
    }

    public String generateColumnName(String fieldName) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fieldName.length(); i++) {
            if (fieldName.charAt(i) >= A && fieldName.charAt(i) <= Z && i != 0) {
                sb.append(UNDERLINE);
            }
            sb.append(String.valueOf(fieldName.charAt(i)).toLowerCase(Locale.ENGLISH));
        }
        return sb.toString().toLowerCase();
    }

    protected List<BaseDataType> getDeclaredDataTypes() {
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

    private BaseDataType matchDataType(Field field) {
        List<BaseDataType> dataTypes = getDeclaredDataTypes();
        for (BaseDataType dataType:dataTypes) {
            if (dataType.matches(field)) {
                return dataType;
            }
        }
        return ByteArrayType.getInstance();
    }

    public <T extends OrmTable> void createTable(Class<T> tableClass) {
        String tableName = getTableName(tableClass);
        Field[] fields = tableClass.getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        sb.append(CREATE_TABLE + SPACE + IF_NOT_EXISTS + SPACE + tableName + LEFT_PARENTHESIS);
        List<Annotation> keys = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true);
            StringBuilder fieldBuilder = new StringBuilder();
            NonColumn nonColumn = field.getAnnotation(NonColumn.class);
            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
            if (nonColumn != null) {
                continue;
            }
            if (primaryKey != null) {
                keys.add(primaryKey);
            }
            if (foreignKey != null) {
                keys.add(foreignKey);
            }
            BaseDataType dataType = matchDataType(field);
            SqlType sqlType = dataType.getSqlType();
            String columnType = sqlType.name();
            String columnName = getColumnName(field);
            fieldBuilder.append(columnName + SPACE + columnType);
            Unique unique = field.getAnnotation(Unique.class);
            if (unique != null) {
                fieldBuilder.append(SPACE).append(UNIQUE);
            }
            NotNull notNull = field.getAnnotation(NotNull.class);
            if (notNull != null) {
                fieldBuilder.append(SPACE).append(NOT_NULL);
            }
            if (primaryKey != null) {
                fieldBuilder.append(SPACE).append(PRIMARY_KEY);
                AssignType assignType = primaryKey.value();
                if (assignType.equals(AssignType.BY_MYSELF)) {
                } else if (assignType.equals(AssignType.AUTO_INCREMENT)) {
                    fieldBuilder.append(SPACE).append(AUTO_INCREMENT);
                }
            }
            if (foreignKey != null) {
                Class<? extends OrmTable> foreignKeyTableClass = foreignKey.value();
                if (!foreignKeyTableClass.equals(tableClass)) {
                    String foreignKeyTableName = foreignKeyTableClass.getName();
                    fieldBuilder.append(SPACE).append(REFERENCES).append(SPACE)
                            .append(foreignKeyTableName).append(LEFT_PARENTHESIS);
                    Field[] foreignKeyTableFields = foreignKeyTableClass.getDeclaredFields();
                    for (Field foreignKeyTableField : foreignKeyTableFields) {
                        PrimaryKey foreignKeyTablePrimaryKey = foreignKeyTableField
                                .getAnnotation(PrimaryKey.class);
                        if (foreignKeyTablePrimaryKey != null) {
                            String foreignKeyTablePrimaryKeyName = field.getName();
                            fieldBuilder.append(foreignKeyTablePrimaryKeyName).append(COMMA);
                        }
                    }
                    fieldBuilder.deleteCharAt(sb.length() - 1).append(RIGHT_PARENTHESIS);
                }
            }
            fieldBuilder.append(COMMA);
            sb.append(fieldBuilder);
        }
        if (keys.size() == 0) {
            throw new ConstraintException("Please specify at least one valid primary or foreign key.");
        }
        try {
            String sql = sb.deleteCharAt(sb.length() - 1).append(RIGHT_PARENTHESIS)
                    .append(SEMICOLON).toString();
            Log.i(TAG, "execute create table sql:" + sql);
            sDatabase.execSQL(sql);
            mTableNameMap.put(tableClass, tableName);
            WhereBuilder whereBuilder = WhereBuilder.create()
                    .addWhereEqualTo(TABLE_NAME, tableName);
            QueryBuilder queryBuilder = QueryBuilder.create().where(whereBuilder);
            int count = mDao.selectCount(queryBuilder);
            if (count == 0) {
                TableName nameTable = new TableName(tableClass, tableName);
                mDao.insert(nameTable);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public <T extends OrmTable> void upgradeTable(Class<T> tableClass) {
        TableName nameTable = mDao.selectOne(QueryBuilder.create().where(WhereBuilder.create()
                .addWhereEqualTo("table_class", tableClass)));
        if (nameTable != null) {
            String tableName = getTableName(tableClass);
            Field[] fields = tableClass.getDeclaredFields();
            StringBuilder sb = new StringBuilder();
            List<Annotation> keys = new ArrayList<>();
            for (Field field : fields) {
                field.setAccessible(true);
                NonColumn nonColumn = field.getAnnotation(NonColumn.class);
                PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
                if (nonColumn != null) {
                    continue;
                }
                if (primaryKey != null) {
                    keys.add(primaryKey);
                }
                if (foreignKey != null) {
                    keys.add(foreignKey);
                }
                BaseDataType dataType = matchDataType(field);
                SqlType sqlType = dataType.getSqlType();
                String columnType = sqlType.name();
                String columnName = getColumnName(field);
                sb.append(columnName + SPACE + columnType);
                Unique unique = field.getAnnotation(Unique.class);
                if (unique != null) {
                    sb.append(SPACE).append(UNIQUE);
                }
                NotNull notNull = field.getAnnotation(NotNull.class);
                if (notNull != null) {
                    sb.append(SPACE).append(NOT_NULL);
                }
                if (primaryKey != null) {
                    sb.append(SPACE).append(PRIMARY_KEY);
                    AssignType assignType = primaryKey.value();
                    if (assignType.equals(AssignType.BY_MYSELF)) {
                    } else if (assignType.equals(AssignType.AUTO_INCREMENT)) {
                        sb.append(SPACE).append(AUTO_INCREMENT);
                    }
                }
                if (foreignKey != null) {
                    Class<? extends OrmTable> foreignKeyTableClass = foreignKey.value();
                    if (!foreignKeyTableClass.equals(tableClass)) {
                        String foreignKeyTableName = foreignKeyTableClass.getName();
                        sb.append(SPACE).append(REFERENCES).append(SPACE)
                                .append(foreignKeyTableName).append(LEFT_PARENTHESIS);
                        Field[] foreignKeyTableFields = foreignKeyTableClass.getDeclaredFields();
                        for (Field foreignKeyTableField : foreignKeyTableFields) {
                            PrimaryKey foreignKeyTablePrimaryKey = foreignKeyTableField
                                    .getAnnotation(PrimaryKey.class);
                            if (foreignKeyTablePrimaryKey != null) {
                                String foreignKeyTablePrimaryKeyName = field.getName();
                                sb.append(foreignKeyTablePrimaryKeyName).append(COMMA);
                            }
                        }
                        sb.deleteCharAt(sb.length() - 1).append(RIGHT_PARENTHESIS);
                    }
                }
            }
            try {
                sDatabase.execSQL(ALTER_TABLE + SPACE + tableName + SPACE + ADD_COLUMN + SPACE +
                        IF_NOT_EXISTS + SPACE + sb.toString() + SEMICOLON);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
