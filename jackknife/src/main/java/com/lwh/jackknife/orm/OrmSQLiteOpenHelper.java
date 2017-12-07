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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class OrmSQLiteOpenHelper extends SQLiteOpenHelper {

    private List<Class<? extends OrmTable>> mTableClasses;

    public OrmSQLiteOpenHelper(Context context, String name, int version, Class<? extends OrmTable>[] tables) {
        super(context, name, null, version);
        if (tables != null) {
            mTableClasses = Arrays.asList(tables);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (mTableClasses != null && !mTableClasses.isEmpty()) {
            Iterator<Class<? extends OrmTable>> iterator = mTableClasses.iterator();
            while (iterator.hasNext()) {
                Class<? extends OrmTable> tableClass = iterator.next();
                TableManager.getInstance().createTableInternal(tableClass, db);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            if (mTableClasses != null && !mTableClasses.isEmpty()) {
                Iterator<Class<? extends OrmTable>> iterator = mTableClasses.iterator();
                while (iterator.hasNext()) {
                    Class<? extends OrmTable> tableClass = iterator.next();
                    TableManager.getInstance().upgradeTableInternal(tableClass, db);
                }
            }
        }
    }
}
