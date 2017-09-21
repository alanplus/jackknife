package com.lwh.jackknife.orm.type;

/**
 * Created by liuwenhao on 2017/9/16.
 */

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
        return 0;
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
