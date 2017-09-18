package com.lwh.jackknife.orm.type;

import com.lwh.jackknife.orm.helper.BaseFieldConverter;
import com.lwh.jackknife.orm.helper.DataPersister;

public abstract class BaseDataType extends BaseFieldConverter implements DataPersister {

    private final SqlType mSqlType;

    public BaseDataType(SqlType sqlType){
        this.mSqlType = sqlType;
    }

    public SqlType getSqlType() {
        return mSqlType;
    }

    @Override
    public Object java2sql(Object jobject) {
        return null;
    }

    @Override
    public Object sql2java(Object sqlData) {
        return null;
    }
}
