package com.lwh.jackknife.orm.dao;

import com.lwh.jackknife.orm.table.OrmTable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DaoFactory {

    private static Map<Class<? extends OrmTable>,OrmDao> sDaoMap = new ConcurrentHashMap<>();

    public synchronized static <T extends OrmTable> OrmDao<T> getDao(Class<T> beanClass) {
        if (sDaoMap.containsKey(beanClass)){
            return sDaoMap.get(beanClass);
        }else{
            OrmDao<T> dao = new OrmDao<>(beanClass);
            sDaoMap.put(beanClass, dao);
            return dao;
        }
    }
}
