package com.lwh.jackknife.orm.dao;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;

public class DaoFactory {

    private String path;

    private SQLiteDatabase mDatabase;
    
    private static DaoFactory sInstance;

    private DaoFactory(File file) {
        this.path = file.getAbsolutePath();
        openDatabase();
    }

    public static DaoFactory getInstance(String dbName) {
        if (sInstance == null) {
            synchronized (DaoFactory.class) {
                if (sInstance == null) {
                    sInstance = new DaoFactory(new File(Environment.getExternalStorageDirectory(),
                            dbName + ".db"));
                }
            }
        }
        return sInstance;
    }

    public synchronized <T extends BaseDao<M>,M> T getDao(Class<T> daoClass, Class<M> dataClass) {
        BaseDao dao = null;
        try {
            dao = daoClass.newInstance();
            dao.init(dataClass, mDatabase);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) dao;
    }

    private void openDatabase() {
        this.mDatabase = SQLiteDatabase.openOrCreateDatabase(path, null);
    }
}
