package com.lwh.jackknife.orm.type;

public class LongType extends BaseDataType {

    private static final LongType mInstance = new LongType();

    public LongType(){
        super(SqlType.INTEGER);
    }

    public LongType(SqlType sqlType) {
        super(sqlType);
    }

    public static LongType getInstance(){
        return mInstance;
    }

    @Override
    public int getDefaultWidth() {
        return 8;
    }
}
