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

import com.lwh.jackknife.orm.builder.QueryBuilder;
import com.lwh.jackknife.orm.builder.WhereBuilder;
import com.lwh.jackknife.orm.OrmTable;

import java.util.List;

public interface Dao<T extends OrmTable> {

    boolean insert(T bean);

    boolean insert(List<T> beans);

    boolean delete(WhereBuilder builder);

    boolean deleteAll();

    boolean update(WhereBuilder builder, T newBean);

    boolean updateAll(T newBean);

    List<T> selectAll();

    List<T> select(QueryBuilder builder);

    T selectOne();

    T selectOne(QueryBuilder builder);

    long selectCount();

    long selectCount(QueryBuilder builder);
}
