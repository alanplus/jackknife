package com.lwh.jackknife.orm.type;

public class FloatType extends BaseDataType {

    private static final FloatType mInstance = new FloatType();

    public FloatType(){
        super(SqlType.REAL);
    }

    public FloatType(SqlType sqlType) {
        super(sqlType);
    }

    public static FloatType getInstance(){
        return mInstance;
    }

    @Override
    public int getDefaultWidth() {
        return 4;
    }
}
