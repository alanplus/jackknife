package com.lwh.jackknife.orm;

/**
 * Created by liuwenhao on 2017/9/16.
 */

public interface FieldConverter {

    Object java2sql(Object jobject);
    Object sql2java(Object sqlData);
}
