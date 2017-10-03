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

package com.lwh.jackknife.orm.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lwh.jackknife.orm.table.OrmTable;
import com.lwh.jackknife.orm.table.TableManager;

import java.util.List;

public class OrmSQLiteOpenHelper extends SQLiteOpenHelper{

    private List<Class<? extends OrmTable>> mTableClasses;
    private TableManager mTableManager;

    public OrmSQLiteOpenHelper(Context context, String name, int version, List<Class<? extends OrmTable>> tableClasses) {
        super(context, name, null, version);
        this.mTableClasses = tableClasses;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (mTableClasses != null) {//有表创建
            autoCreateTable(db);//自动创表
        }
    }

    private void autoCreateTable(SQLiteDatabase db){
        mTableManager  = TableManager.getInstance();
        for (Class<? extends OrmTable> tableClass:mTableClasses){//遍历所有要创建的表的对象类型
            mTableManager.createTable(tableClass);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
