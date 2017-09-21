package com.lwh.jackknife.orm.type;

public class BooleanType extends BaseDataType {

    private static final BooleanType mInstance = new BooleanType();

    public BooleanType(){
        super(SqlType.INTEGER);
    }

    public BooleanType(SqlType sqlType) {
        super(sqlType);
    }

    public static BooleanType getInstance(){
        return mInstance;
    }


    @Override
    public int getDefaultWidth() {
        return 1;
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
