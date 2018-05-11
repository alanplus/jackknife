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

package com.lwh.jackknife.orm;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.lwh.jackknife.orm.constraint.AssignType;
import com.lwh.jackknife.orm.constraint.Check;
import com.lwh.jackknife.orm.constraint.Default;
import com.lwh.jackknife.orm.constraint.ForeignKey;
import com.lwh.jackknife.orm.constraint.NotNull;
import com.lwh.jackknife.orm.constraint.PrimaryKey;
import com.lwh.jackknife.orm.constraint.Unique;
import com.lwh.jackknife.orm.exception.ConstraintException;
import com.lwh.jackknife.orm.table.Column;
import com.lwh.jackknife.orm.table.Ignore;
import com.lwh.jackknife.orm.table.Table;
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

public class TableManager {

    private static TableManager sInstance;

    private final String TAG = getClass().getSimpleName();

    private final char A = 'A';

    private final char Z = 'Z';

    private final String CREATE_TABLE = "CREATE TABLE";

    private final String ALTER_TABLE = "ALTER TABLE";

    private final String DROP_TABLE = "DROP TABLE";

    private final String IF_NOT_EXISTS = "IF NOT EXISTS";

    private final String ADD_COLUMN = "ADD COLUMN";

    private final String AUTO_INCREMENT = "AUTOINCREMENT";

    private final String SPACE = " ";

    private final String DOT = ".";

    private final String EQUAL_TO = "=";

    private final String SINGLE_QUOTES = "\'";

    private final String UNIQUE = "UNIQUE";

    private final String DEFAULT = "DEFAULT";

    private final String CHECK = "CHECK";

    private final String NOT_NULL = "NOT NULL";

    private final String PRIMARY_KEY = "PRIMARY KEY";

    private final String REFERENCES = "REFERENCES";

    private final String LEFT_PARENTHESIS = "(";

    private final String RIGHT_PARENTHESIS = ")";

    private final String COMMA = ",";

    private final String SEMICOLON = ";";

    private final String UNDERLINE = "_";

    private final String TABLE_NAME_HEADER = "t" + UNDERLINE;

    private TableManager() {
    }

    public static TableManager getInstance() {
        if (sInstance == null) {
            synchronized (TableManager.class) {
                if (sInstance == null) {
                    sInstance = new TableManager();
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

    public String getColumnName(Field field) {
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

    private final BaseDataType matchDataType(Field field) {
        List<BaseDataType> dataTypes = getDeclaredDataTypes();
        for (BaseDataType dataType:dataTypes) {
            if (dataType.matches(field)) {
                return dataType;
            }
        }
        return ByteArrayType.getInstance();
    }

    public static <T extends OrmTable> void createTable(Class<T> tableClass) {
        if (Orm.isPrepared()) {
            getInstance()._createTable(tableClass, Orm.getDatabase());
        }
    }

    /* package */ <T extends OrmTable> void _createTable(Class<T> tableClass, SQLiteDatabase db) {
        String tableName = getTableName(tableClass);
        Field[] fields = tableClass.getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        sb.append(CREATE_TABLE + SPACE + IF_NOT_EXISTS + SPACE + tableName + LEFT_PARENTHESIS);
        List<Annotation> keys = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true);
            StringBuilder fieldBuilder = new StringBuilder();
            Ignore ignore = field.getAnnotation(Ignore.class);
            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
            if (ignore != null) {
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
            Default _default = field.getAnnotation(Default.class);
            if (_default != null) {
                String value = _default.value();
                fieldBuilder.append(SPACE).append(DEFAULT)
                .append(SPACE).append(SINGLE_QUOTES).append(value).append(SINGLE_QUOTES);
            }
            Check check = field.getAnnotation(Check.class);
            if (check != null) {
                String value = check.value();
                fieldBuilder.append(SPACE).append(CHECK).append(SPACE)
                .append(columnName).append(EQUAL_TO).append(SINGLE_QUOTES).append(value).append(SINGLE_QUOTES);
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
                    String foreignKeyTableName = getTableName(foreignKeyTableClass);
                    fieldBuilder.append(SPACE).append(REFERENCES).append(SPACE)
                            .append(foreignKeyTableName).append(LEFT_PARENTHESIS);
                    Field[] foreignKeyTableFields = foreignKeyTableClass.getDeclaredFields();
                    for (Field foreignKeyTableField : foreignKeyTableFields) {
                        PrimaryKey foreignKeyTablePrimaryKey = foreignKeyTableField
                                .getAnnotation(PrimaryKey.class);
                        if (foreignKeyTablePrimaryKey != null) {
                            String foreignKeyTablePrimaryKeyName = getColumnName(foreignKeyTableField);
                            fieldBuilder.append(foreignKeyTablePrimaryKeyName).append(COMMA);
                        }
                    }
                    fieldBuilder.deleteCharAt(fieldBuilder.length() - 1).append(RIGHT_PARENTHESIS);
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
            db.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static <T extends OrmTable> void upgradeTable(Class<T> tableClass) {
        if (Orm.isPrepared()) {
            getInstance()._upgradeTable(tableClass, Orm.getDatabase());
        }
    }

    /* package */ <T extends OrmTable> void _upgradeTable(Class<T> tableClass, SQLiteDatabase db) {
        String tableName = getTableName(tableClass);
        Field[] fields = tableClass.getDeclaredFields();
        List<Annotation> keys = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true);
            StringBuilder sb = new StringBuilder();
            Ignore ignore = field.getAnnotation(Ignore.class);
            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
            if (ignore != null) {
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
            Default _default = field.getAnnotation(Default.class);
            if (_default != null) {
                String value = _default.value();
                sb.append(SPACE).append(DEFAULT)
                        .append(SPACE).append(SINGLE_QUOTES).append(value).append(SINGLE_QUOTES);
            }
            Check check = field.getAnnotation(Check.class);
            if (check != null) {
                String value = check.value();
                sb.append(SPACE).append(CHECK).append(SPACE)
                        .append(columnName).append(EQUAL_TO).append(SINGLE_QUOTES).append(value).append(SINGLE_QUOTES);
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
                    String foreignKeyTableName = getTableName(foreignKeyTableClass);
                    sb.append(SPACE).append(REFERENCES).append(SPACE)
                            .append(foreignKeyTableName).append(LEFT_PARENTHESIS);
                    Field[] foreignKeyTableFields = foreignKeyTableClass.getDeclaredFields();
                    for (Field foreignKeyTableField : foreignKeyTableFields) {
                        PrimaryKey foreignKeyTablePrimaryKey = foreignKeyTableField
                                .getAnnotation(PrimaryKey.class);
                        if (foreignKeyTablePrimaryKey != null) {
                            String foreignKeyTablePrimaryKeyName = getColumnName(foreignKeyTableField);
                            sb.append(foreignKeyTablePrimaryKeyName).append(COMMA);
                        }
                    }
                    sb.deleteCharAt(sb.length() - 1).append(RIGHT_PARENTHESIS);
                }
            }
            try {
                db.execSQL(ALTER_TABLE + SPACE + tableName + SPACE + IF_NOT_EXISTS + SPACE
                        + columnName + SPACE + ADD_COLUMN + SPACE + sb.toString() + SEMICOLON);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /* package */ <T extends OrmTable> void _dropTable(Class<T> tableClass, SQLiteDatabase db) {
        db.execSQL(DROP_TABLE+SPACE+getTableName(tableClass));
    }

    public static <T extends OrmTable> void dropTable(Class<T> tableClass) {
        if (Orm.isPrepared()) {
            getInstance()._dropTable(tableClass, Orm.getDatabase());
        }
    }
}
