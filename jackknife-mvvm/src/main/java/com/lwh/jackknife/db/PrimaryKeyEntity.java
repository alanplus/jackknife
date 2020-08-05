/*
 * Copyright (C) 2020 The JackKnife Open Source Project
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

package com.lwh.jackknife.db;

public class PrimaryKeyEntity {

    private String name;
    private String value;

    public PrimaryKeyEntity(String primaryKeyName, Number primaryKeyValue) {
        this.name = primaryKeyName;
        this.value = String.valueOf(primaryKeyValue);
    }

    public PrimaryKeyEntity(String primaryKeyName, String primaryKeyValue) {
        this.name = primaryKeyName;
        this.value = primaryKeyValue;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
