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
import com.lwh.jackknife.orm.table.OrmTable;

import java.util.List;

/**
 * 直接对数据库进行增删改查的接口。
 *
 * @param <T> bean数据。
 */
public interface Dao<T extends OrmTable> {

    /**
     * 插入一个bean数据。
     *
     * @param bean 数据。
     * @return 是否插入成功。
     */
    boolean insert(T bean);

    /**
     * 插入一堆bean数据。
     *
     * @param beans 数据集合。
     * @return 是否插入成功。
     */
    boolean insert(List<T> beans);

    /**
     * 按条件删除数据。
     *
     * @param builder 条件。
     * @return 是否删除成功。
     */
    boolean delete(WhereBuilder builder);

    /**
     * 删除所有数据。
     *
     * @return 是否删除成功。
     */
    boolean deleteAll();

    /**
     * 按条件更新数据。
     *
     * @param builder 条件。
     * @param newBean 要修改成什么样的数据？
     * @return 是否修改成功。
     */
    boolean update(WhereBuilder builder, T newBean);

    /**
     * 更新所有数据。
     *
     * @param newBean 要修改成什么样的数据？
     * @return 是否修改成功。
     */
    boolean updateAll(T newBean);

    /**
     * 查询所有数据。
     *
     * @return 查询出的数据集合。
     */
    List<T> selectAll();

    /**
     * 按查询条件查询数据。
     *
     * @param builder 查询条件。
     * @return 查询出的数据集合。
     */
    List<T> select(QueryBuilder builder);

    /**
     * 获取所有数据的条数。
     *
     * @return 所有数据的条数。
     */
    int selectAllCount();

    /**
     * 按查询条件获取数据的条数。
     *
     * @param builder 查询条件。
     * @return 查询出的数据的条数。
     */
    int selectCount(QueryBuilder builder);
}
