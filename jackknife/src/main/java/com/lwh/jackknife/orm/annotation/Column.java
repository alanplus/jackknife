package com.lwh.jackknife.orm.annotation;

import com.lwh.jackknife.orm.table.OrmTable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 映射表的列所必须的注解，{@link OrmTable}的实现类中，要映射的属性使用此注解。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    String value();
}
