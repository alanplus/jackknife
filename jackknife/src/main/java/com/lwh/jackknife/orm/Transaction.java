package com.lwh.jackknife.orm;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.lwh.jackknife.util.Logger;

/**
 * 事务。中间发生失败，就不会提交成功。
 */
public class Transaction {

    private static final String TAG = Transaction.class.getName();

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
        }
        return false;
    }

    public interface Worker{
        boolean doTransition(SQLiteDatabase db);
    }
}
