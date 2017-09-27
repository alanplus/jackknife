package com.lwh.jackknife.orm;

import android.content.Context;

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
        OrmSQLiteOpenHelper helper = new OrmSQLiteOpenHelper(context, databaseName, version, null);
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
