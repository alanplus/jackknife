package com.lwh.jackknife.orm.dao;

import java.util.List;

public interface Dao<T> {

    Long insert(T entity);

    int update(T entity, T where);

    List<T> query(T where);

    int queryCount(T where);

    List<T> query(T where, String orderBy, Integer startIndex, Integer limit);

    int delete(T where);

    boolean deleteAll();
}
