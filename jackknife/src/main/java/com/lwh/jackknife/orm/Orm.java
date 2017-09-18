package com.lwh.jackknife.orm;

import android.content.Context;

import com.lwh.jackknife.app.Application;
import com.lwh.jackknife.orm.helper.OrmSQLiteOpenHelper;
import com.lwh.jackknife.orm.table.OrmTable;

import java.util.List;

/**
 * 如果使用了此类，需要继承{@link Application}。
 */
public class Orm {

    public synchronized static void init(Context context, String databaseName){
        OrmSQLiteOpenHelper helper = new OrmSQLiteOpenHelper(context, databaseName, 1, null);
        Application.getInstance().attach(helper);
    }

    public synchronized static void init(Context context, OrmConfig config) {
        String name = config.getDatabaseName();
        int versionCode = config.getVersionCode();
        List<Class<? extends OrmTable>> tableClasses = config.getTableClasses();
        OrmSQLiteOpenHelper helper = new OrmSQLiteOpenHelper(context, name, versionCode, tableClasses);
        Application.getInstance().attach(helper);
    }
}
