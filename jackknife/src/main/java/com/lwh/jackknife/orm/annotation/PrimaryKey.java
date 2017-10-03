package com.lwh.jackknife.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 主键，配置了该注解的属性，其哈希码不能重复，且外键必须要建立在主键的基础上。任何时候只允许有一个主键，不支持复合
 * 主键。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryKey {
}
