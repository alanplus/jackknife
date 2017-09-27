package com.lwh.jackknife.orm.table;

import com.lwh.jackknife.orm.annotation.Column;
import com.lwh.jackknife.orm.annotation.Table;

/**
 * 要映射的实体类需要实现此接口，以及配置{@link Table}和{@link Column}等，{@link Table}可以不配置，但此接口
 * 一定要实现。
 */
public interface OrmTable {
}
