/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
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
