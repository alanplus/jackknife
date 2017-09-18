package com.lwh.jackknife.orm.dao;

import com.lwh.jackknife.orm.builder.QueryBuilder;
import com.lwh.jackknife.orm.builder.WhereBuilder;

import java.util.List;

/**
 * 直接对数据库进行增删改查的接口。
 *
 * @param <T> bean数据。
 */
public interface Dao<T> {

    boolean insert(T bean);//插入一个数据
    boolean insert(List<T> beans);//插入一堆数据
    boolean delete(WhereBuilder builder);//按条件删除数据
    boolean delete();//删除所有数据
    boolean update(WhereBuilder builder, T newBean);//按条件更新数据
    boolean update(T newBean);//更新所有数据，此方法一般不会被用到
    List<T> select();//查询所有数据
    List<T> select(QueryBuilder builder);//按条件查询数据
    int selectCount();//获取所有数据的条数
    int selectCount(QueryBuilder builder);//按条件获取数据的条数
}
