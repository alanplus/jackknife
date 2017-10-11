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

import com.lwh.jackknife.orm.AssignType;
import com.lwh.jackknife.orm.annotation.Column;
import com.lwh.jackknife.orm.annotation.NotNull;
import com.lwh.jackknife.orm.annotation.PrimaryKey;
import com.lwh.jackknife.orm.annotation.Table;
import com.lwh.jackknife.orm.annotation.Unique;

/**
 * 保存表名的表。
 *
 * @hide
 */
@Table("jackknife_table")
class TableName implements OrmTable {

    @Unique
    @NotNull
    @Column("table_class")
    private Class<? extends OrmTable> tableClass;

    @PrimaryKey(AssignType.BY_MYSELF)
    @Column("table_name")
    private String tableName;

    TableName(Class<? extends OrmTable> tableClass, String tableName) {
        this.tableClass = tableClass;
        this.tableName = tableName;
    }

    public Class<? extends OrmTable> getTableClass() {
        return tableClass;
    }

    public String getTableName() {
        return tableName;
    }
}
