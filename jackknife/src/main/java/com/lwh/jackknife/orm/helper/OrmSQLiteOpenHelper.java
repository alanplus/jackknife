package com.lwh.jackknife.orm.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lwh.jackknife.orm.Transaction;
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
        Transaction.execute(db, new Transaction.Worker() {
            @Override
            public boolean doTransition(SQLiteDatabase db) {
                for (Class<? extends OrmTable> tableClass:mTableClasses){//遍历所有要创建的表的对象类型
                    mTableManager.createTable(tableClass);
                }
                return true;
            }
        });
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
