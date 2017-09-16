package com.lwh.jackknife.orm;

import android.content.Context;

import java.util.List;

public class Orm {

    static OrmSQLiteOpenHelper sHelper;

    public synchronized static void init(Context context, String databaseName){
        sHelper = new OrmSQLiteOpenHelper(context, databaseName, 1, null);
    }

    public synchronized static void init(Context context, OrmConfig config) {
        String name = config.getDatabaseName();
        int versionCode = config.getVersionCode();
        List<Class<OrmTable>> tableClasses = config.getTableClasses();
        sHelper = new OrmSQLiteOpenHelper(context, name, versionCode, tableClasses);
    }
}
