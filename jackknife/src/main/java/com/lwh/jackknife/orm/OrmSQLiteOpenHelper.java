package com.lwh.jackknife.orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

public class OrmSQLiteOpenHelper extends SQLiteOpenHelper{

    private List<Class<OrmTable>> mTableClasses;
    private TableManager mTableManager;

    public OrmSQLiteOpenHelper(Context context, String name, int version, List<Class<OrmTable>> tableClasses) {
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
        mTableManager  = TableManager.getInstance(db);
        Transaction.execute(db, new Transaction.Worker() {
            @Override
            public boolean doTransition(SQLiteDatabase db) {
                for (Class<OrmTable> tableClass:mTableClasses){//遍历所有要创建的表的对象类型
                    mTableManager.createTable(tableClass);
                }
                return true;
            }
        });
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            // FIXME: 2017/9/16 
        }
    }
}
