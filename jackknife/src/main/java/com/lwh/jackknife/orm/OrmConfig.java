package com.lwh.jackknife.orm;

import com.lwh.jackknife.orm.table.OrmTable;
import com.lwh.jackknife.util.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class OrmConfig {

    private String mDatabaseName;
    private int mVersionCode;
    private List<Class<? extends OrmTable>> mTableClasses;

    private OrmConfig(Builder builder){
        mDatabaseName = builder.mDatabaseName;
        mVersionCode = builder.mVersionCode;
        mTableClasses = builder.mTableClasses;
    }

    public String getDatabaseName() {
        return mDatabaseName;
    }

    public int getVersionCode() {
        return mVersionCode;
    }

    public List<Class<? extends OrmTable>> getTableClasses() {
        return mTableClasses;
    }

    public static class Builder{

        private String mDatabaseName;
        private int mVersionCode = 1;
        private List<Class<? extends OrmTable>> mTableClasses;

        public Builder tables(Class<? extends OrmTable>... tableClasses){
            mTableClasses = new ArrayList<>();
            for (Class<? extends OrmTable> tableClass : tableClasses){
                mTableClasses.add(tableClass);
            }
            return this;
        }

        public Builder database(String name){
            mDatabaseName = name;
            return this;
        }

        public Builder version(int code){
            mVersionCode = code;
            return this;
        }

        public OrmConfig build(){
            if (TextUtils.isNotEmpty(mDatabaseName)) {
                return new OrmConfig(this);
            }
            return null;
        }
    }
}
