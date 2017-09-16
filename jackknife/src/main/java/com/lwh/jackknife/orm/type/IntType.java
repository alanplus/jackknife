package com.lwh.jackknife.orm.type;

/**
 * Created by liuwenhao on 2017/9/16.
 */

public class IntType extends BaseDataType {

    private static final IntType mInstance = new IntType();

    public IntType(){
        super(SqlType.INTEGER);
    }

    public IntType(SqlType sqlType) {
        super(sqlType);
    }

    public static IntType getInstance(){
        return mInstance;
    }


    @Override
    public int getDefaultWidth() {
        return 0;
    }
}
