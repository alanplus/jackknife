package com.lwh.jackknife.orm.table;

import com.lwh.jackknife.orm.annotation.Column;
import com.lwh.jackknife.orm.annotation.Table;

/**
 * 保存表名的表。
 *
 * @hide
 */
@Table("jackknife_table")
class TableName implements OrmTable {

    @Column("table_class")
    private Class<? extends OrmTable> tableClass;

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
