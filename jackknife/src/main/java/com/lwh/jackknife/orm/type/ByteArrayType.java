package com.lwh.jackknife.orm.type;

public class ByteArrayType extends BaseDataType {

    private static final ByteArrayType mInstance = new ByteArrayType();

    public ByteArrayType(){
        super(SqlType.BLOB);
    }

    public ByteArrayType(SqlType sqlType) {
        super(sqlType);
    }

    public static ByteArrayType getInstance(){
        return mInstance;
    }

    @Override
    public int getDefaultWidth() {
        return 0;
    }
}
