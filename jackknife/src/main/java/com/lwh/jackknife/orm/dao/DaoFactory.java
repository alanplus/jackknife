package com.lwh.jackknife.orm.dao;

import java.util.Map;

public class DaoFactory {

    private static Map<Class<?>,OrmDao> sDaoMap;

    public synchronized static <T> OrmDao<T> getDao(Class<T> beanClass) {
        if (sDaoMap.containsKey(beanClass)){
            return sDaoMap.get(beanClass);
        }else{
            OrmDao<T> dao = new OrmDao<>(beanClass);
            sDaoMap.put(beanClass, dao);
            return dao;
        }
    }
}
