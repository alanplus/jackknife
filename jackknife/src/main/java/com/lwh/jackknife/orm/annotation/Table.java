package com.lwh.jackknife.orm.annotation;

import com.lwh.jackknife.orm.table.OrmTable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来映射表名，和{@link OrmTable}配套使用。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

    String value();
}
