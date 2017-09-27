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
    public Object toJavaData(Object sql) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object toSqlData(Object java) {
        throw new UnsupportedOperationException();
    }
}
