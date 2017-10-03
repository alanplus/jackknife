/*
 * Copyright (C) 2017 The JackKnife Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
