/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.lwh.jackknife.orm;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.lwh.jackknife.app.Application;
import com.lwh.jackknife.orm.helper.OrmSQLiteOpenHelper;
import com.lwh.jackknife.orm.table.OrmTable;

import java.util.List;

/**
 * 如果你使用了此类，你将需要继承{@link Application}。
 */
public class Orm {

    public synchronized static void init(Context context, String databaseName){
        init(context, databaseName, 1);
    }

    public synchronized static void init(Context context, String databaseName, int version){
        SQLiteOpenHelper helper = new OrmSQLiteOpenHelper(context, databaseName, version, null);
        Application.getInstance().attach(helper);
    }

    public synchronized static void init(Context context, OrmConfig config) {
        String name = config.getDatabaseName();
        int versionCode = config.getVersionCode();
        List<Class<? extends OrmTable>> tableClasses = config.getTableClasses();
        SQLiteOpenHelper helper = new OrmSQLiteOpenHelper(context, name, versionCode, tableClasses);
        Application.getInstance().attach(helper);
    }
}
