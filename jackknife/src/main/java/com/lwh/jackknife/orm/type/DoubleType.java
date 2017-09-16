package com.lwh.jackknife.orm.type;

/**
 * Created by liuwenhao on 2017/9/16.
 */

public class DoubleType extends BaseDataType {

    private static final DoubleType mInstance = new DoubleType();

    public DoubleType(){
        super(SqlType.REAL);
    }

    public DoubleType(SqlType sqlType) {
        super(sqlType);
    }

    public static DoubleType getInstance(){
        return mInstance;
    }


    @Override
    public int getDefaultWidth() {
        return 0;
    }
}
