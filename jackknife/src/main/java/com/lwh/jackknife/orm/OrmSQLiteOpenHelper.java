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

import com.lwh.jackknife.orm.dao.DaoFactory;
import com.lwh.jackknife.orm.dao.OrmDao;
import com.lwh.jackknife.orm.table.OrmTable;
import com.lwh.jackknife.orm.table.TableManager;

import java.lang.reflect.Field;
import java.util.List;

public class OrmSQLiteOpenHelper extends SQLiteOpenHelper {

    public OrmSQLiteOpenHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            try {
                Class<? extends OrmTable> tableNameClass = (Class<? extends OrmTable>)
                        Class.forName("com.lwh.jackknife.orm.table.TableName");
                OrmDao<?> dao = DaoFactory.getDao(tableNameClass);
                List<?> tables = dao.selectAll();
                for (Object table : tables) {
                    Field field = tableNameClass.getDeclaredField("tableClass");
                    field.setAccessible(true);
                    Class<? extends OrmTable> tableClass =
                            (Class<? extends OrmTable>) field.get(table);
                    TableManager.getInstance().upgradeTable(tableClass);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
