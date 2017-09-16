package com.lwh.jackknife.orm.type;

/**
 * Created by liuwenhao on 2017/9/16.
 */

public class ByteArrayType extends BaseDataType {

    private static final ByteType mInstance = new ByteType();

    public ByteArrayType(){
        super(SqlType.BLOB);
    }

    public ByteArrayType(SqlType sqlType) {
        super(sqlType);
    }

    public static ByteType getInstance(){
        return mInstance;
    }


    @Override
    public int getDefaultWidth() {
        return 0;
    }
}
