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

import com.lwh.jackknife.ioc.ViewInjector;
import com.lwh.jackknife.util.ReflectionUtils;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class OrmSQLiteOpenHelper extends SQLiteOpenHelper {

    private Class<? extends OrmTable>[] mTables;

    public OrmSQLiteOpenHelper(Context context, String name, int version, Class<? extends OrmTable>[] tables) {
        super(context, name, null, version);
        this.mTables = tables;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (mTables != null && mTables.length > 0) {
            for (Class<? extends OrmTable> table:mTables) {
                TableManager.getInstance()._createTable(table, db);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (mTables != null && mTables.length > 0 && newVersion > oldVersion) {
            for (int i=0;i<mTables.length;i++) {
                Class<? extends OrmTable> table = mTables[i];
                OrmTable ormTable = ReflectionUtils.newInstance(mTables[i]);
                boolean isRecreated = ormTable.isUpgradeRecreated();
                if (isRecreated) {
                    TableManager.getInstance()._dropTable(table, db);
                    TableManager.getInstance()._createTable(table, db);
                } else {
                    TableManager.getInstance()._upgradeTable(table, db);
                }
            }
        }
    }
}
