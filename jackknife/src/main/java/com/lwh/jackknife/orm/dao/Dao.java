package com.lwh.jackknife.orm.dao;

import java.util.List;

public interface Dao<T> {

    boolean insert(T data);
    boolean insert(List<T> datas);
    boolean delete(T where);
    boolean deleteAll();
    boolean update(T newData, T where);
    List<T> queryAll();
    List<T> query(T where);
    T queryOnly(T where);
}
