package com.lwh.jackknife.orm.type;

import com.lwh.jackknife.orm.helper.DataPersister;

public enum DataType {

    STRING(StringType.getInstance()),
    BOOLEAN(BooleanType.getInstance()),
    CHAR(CharType.getInstance()),
    BYTE(ByteType.getInstance()),
    SHORT(ShortType.getInstance()),
    INT(IntType.getInstance()),
    LONG(LongType.getInstance()),
    FLOAT(FloatType.getInstance()),
    DOUBLE(DoubleType.getInstance()),
    OTHER(ByteArrayType.getInstance());

    private final DataPersister mPersister;

    /* package */ DataType(DataPersister persister){
        mPersister = persister;
    }

    public DataPersister getPersister() {
        return mPersister;
    }
}
