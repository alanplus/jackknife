package com.lwh.jackknife.orm.type;

public class ByteType extends BaseDataType {

    private static final ByteType mInstance = new ByteType();

    public ByteType(){
        super(SqlType.INTEGER);
    }

    public ByteType(SqlType sqlType) {
        super(sqlType);
    }

    public static ByteType getInstance(){
        return mInstance;
    }

    @Override
    public int getDefaultWidth() {
        return 1;
    }
}
