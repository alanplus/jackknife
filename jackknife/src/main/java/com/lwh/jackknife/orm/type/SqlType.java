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

package com.lwh.jackknife.orm.type;

/**
 * SQLite数据库的sql的类型枚举，所有其他数据库的SQL语句的数据类型最终会转为以下五种。
 */
public enum SqlType {
    NULL,//值是空值。
    INTEGER,//值是有符号整数，根据值的大小以1，2，3，4，6 或8字节存储。
    REAL,//值是浮点数，以8字节 IEEE 浮点数存储。
    TEXT,//值是文本字符串，使用数据库编码（UTF-8, UTF-16BE 或 UTF-16LE）进行存储。
    BLOB//值是一个数据块，按它的输入原样存储。
}
