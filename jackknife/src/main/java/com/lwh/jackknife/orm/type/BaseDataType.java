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

package com.lwh.jackknife.orm.type;

import com.lwh.jackknife.orm.helper.BaseFieldConverter;
import com.lwh.jackknife.orm.helper.DataPersister;

public abstract class BaseDataType extends BaseFieldConverter implements DataPersister {

    private final SqlType mSqlType;

    public BaseDataType(SqlType sqlType){
        this.mSqlType = sqlType;
    }

    public SqlType getSqlType() {
        return mSqlType;
    }

    @Override
    public Object toJavaData(Object sql) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object toSqlData(Object java) {
        throw new UnsupportedOperationException();
    }
}
