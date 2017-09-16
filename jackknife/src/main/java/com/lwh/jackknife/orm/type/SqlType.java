package com.lwh.jackknife.orm.type;

/**
 * SQLite数据库的sql的类型枚举。
 */
public enum SqlType {
    NULL,//值是空值。
    INTEGER,//值是有符号整数，根据值的大小以1，2，3，4，6 或8字节存储。
    REAL,//值是浮点数，以8字节 IEEE 浮点数存储。
    TEXT,//值是文本字符串，使用数据库编码（UTF-8, UTF-16BE 或 UTF-16LE）进行存储。
    BLOB//值是一个数据块，按它的输入原样存储。
}
