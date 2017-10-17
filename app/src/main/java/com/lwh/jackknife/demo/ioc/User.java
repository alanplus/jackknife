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

package com.lwh.jackknife.demo.ioc;

import com.lwh.jackknife.orm.AssignType;
import com.lwh.jackknife.orm.annotation.Column;
import com.lwh.jackknife.orm.annotation.NotNull;
import com.lwh.jackknife.orm.annotation.PrimaryKey;
import com.lwh.jackknife.orm.annotation.Table;
import com.lwh.jackknife.orm.table.OrmTable;

@Table("user")
public class User implements OrmTable{

    @PrimaryKey(AssignType.BY_MYSELF)
    @Column("name")
    private String name;

    @NotNull
    @Column("age")
    private int age;

    public User() {
    }

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
