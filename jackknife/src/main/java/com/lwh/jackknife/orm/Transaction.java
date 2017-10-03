package com.lwh.jackknife.orm;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.lwh.jackknife.util.Logger;

/**
 * 事务提交。提交过程中发生错误，就不会提交成功。
 */
public class Transaction {

    private static final String TAG = Transaction.class.getName();

    private Transaction(){
    }

    public static boolean execute(SQLiteDatabase db, Worker worker){
        db.beginTransaction();
        Logger.info(TAG, "begin transaction");
        try {
            boolean isOk = worker.doTransition(db);
            if (isOk) {
                db.setTransactionSuccessful();
            }
            return isOk;
        } catch(SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            Logger.info(TAG, "end transaction");
        }
        return false;
    }

    public interface Worker{
        boolean doTransition(SQLiteDatabase db);
    }
}
