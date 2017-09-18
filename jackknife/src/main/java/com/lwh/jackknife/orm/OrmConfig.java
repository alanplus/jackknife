package com.lwh.jackknife.orm;

import com.lwh.jackknife.orm.table.OrmTable;

import java.util.ArrayList;
import java.util.List;

public class OrmConfig {

    private String mDbName;
    private int mVersionCode;
    private List<Class<OrmTable>> mTableClasses;

    private OrmConfig(Builder builder){
        mDbName = builder.mDbName;
        mVersionCode = builder.mVersionCode;
        mTableClasses = builder.mTableClasses;
    }

    public String getDatabaseName() {
        return mDbName;
    }

    public int getVersionCode() {
        return mVersionCode;
    }

    public List<Class<OrmTable>> getTableClasses() {
        return mTableClasses;
    }

    public static class Builder{

        private String mDbName;
        private int mVersionCode = 1;
        private List<Class<OrmTable>> mTableClasses;

        public Builder tables(Class<OrmTable>... tableClasses){
            mTableClasses = new ArrayList<>();
            for (Class<OrmTable> tableClass : tableClasses){
                mTableClasses.add(tableClass);
            }
            return this;
        }

        public Builder database(String name){
            mDbName = name;
            return this;
        }

        public Builder version(int versionCode){
            mVersionCode = versionCode;
            return this;
        }

        public OrmConfig build(){
            if (mDbName != null) {
                return new OrmConfig(this);
            }
            return null;
        }
    }
}
