package com.lwh.jackknife.orm.type;

public class StringType extends BaseDataType {

    private static final StringType mInstance = new StringType();

    public StringType(){
        super(SqlType.TEXT);
    }

    public StringType(SqlType sqlType) {
        super(sqlType);
    }

    public static StringType getInstance(){
        return mInstance;
    }

    @Override
    public int getDefaultWidth() {
        return 255;
    }
}
