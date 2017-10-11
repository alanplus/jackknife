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
import android.database.sqlite.SQLiteOpenHelper;

import com.lwh.jackknife.app.Application;

/**
 * 如果你使用了此类，你将需要继承{@link Application}。
 */
public class Orm {

    public synchronized static void init(Context context, String databaseName){
        SQLiteOpenHelper helper = new OrmSQLiteOpenHelper(context, databaseName, 1);
        Application.getInstance().attach(helper);
    }

    public synchronized static void init(Context context, OrmConfig config) {
        String name = config.getDatabaseName();
        int versionCode = config.getVersionCode();
        SQLiteOpenHelper helper = new OrmSQLiteOpenHelper(context, name, versionCode);
        Application.getInstance().attach(helper);
    }
}
