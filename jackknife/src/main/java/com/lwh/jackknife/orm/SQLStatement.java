package com.lwh.jackknife.orm;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.lwh.jackknife.util.IoUtils;

import java.io.Serializable;
import java.sql.Date;

public class SQLStatement {

    private final String TAG = this.getClass().getName();
    private String mSQL;
    private Object[] mArgs;
    private SQLiteStatement mStatement;

    public SQLStatement(){
    }

    public SQLStatement(String sql, Object[] args){
        this.mSQL = sql;
        this.mArgs = args;
    }

    public void bind(int position, Object obj){
        if (obj == null){
            mStatement.bindNull(position);
        } else if (obj instanceof CharSequence || obj instanceof Boolean || obj instanceof Character){
            mStatement.bindString(position, String.valueOf(obj));
        } else {
            if (obj instanceof Float || obj instanceof Double){
                mStatement.bindDouble(position, ((Number)obj).doubleValue());
            }else if (obj instanceof Number){
                mStatement.bindLong(position, ((Number) obj).longValue());
            }else if (obj instanceof Date){
                mStatement.bindLong(position, ((Date)obj).getTime());
            }else if (obj instanceof byte[]){
                mStatement.bindBlob(position, (byte[]) obj);
            }else if (obj instanceof Serializable){
                mStatement.bindBlob(position, IoUtils.bytes(obj));
            }else{
                mStatement.bindNull(position);
            }
        }
    }

    public boolean execute(SQLiteDatabase db){
        mStatement = db.compileStatement(mSQL);
        return false;
    }

    public void print(){
//        Logger.info(TAG, );
    }
}
