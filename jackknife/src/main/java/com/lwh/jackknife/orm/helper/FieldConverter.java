package com.lwh.jackknife.orm.helper;

/**
 * 属性转换器。
 */
public interface FieldConverter {

    Object toSqlData(Object java);
    Object toJavaData(Object sql);
}
