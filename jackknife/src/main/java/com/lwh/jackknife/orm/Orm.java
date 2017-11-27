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

package com.lwh.jackknife.orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lwh.jackknife.app.Application;
import com.lwh.jackknife.orm.table.OrmTable;

public class Orm {

    public synchronized static void init(Context context, String databaseName) {
        SQLiteOpenHelper helper = new OrmSQLiteOpenHelper(context, databaseName, 1, null);
        SQLiteDatabase database = helper.getWritableDatabase();
        Application.getInstance().attach(database);
    }

    public synchronized static void init(Context context, OrmConfig config) {
        String name = config.getDatabaseName();
        int versionCode = config.getVersionCode();
        Class<? extends OrmTable>[] tables = config.getTables();
        SQLiteOpenHelper helper = new OrmSQLiteOpenHelper(context, name, versionCode, tables);
        SQLiteDatabase database = helper.getWritableDatabase();
        Application.getInstance().attach(database);
    }
}
