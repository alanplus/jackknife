/*
 * Copyright (C) 2017 The JackKnife Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lwh.jackknife.orm.dao;

import com.lwh.jackknife.orm.OrmTable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DaoFactory {

    private static Map<Class<? extends OrmTable>, OrmDao> sDaoMap = new ConcurrentHashMap<>();

    public synchronized static <T extends OrmTable> OrmDao<T> getDao(Class<T> beanClass) {
        if (sDaoMap.containsKey(beanClass)) {
            return sDaoMap.get(beanClass);
        } else {
            OrmDao<T> dao = new OrmDao<>(beanClass);
            sDaoMap.put(beanClass, dao);
            return dao;
        }
    }
}
