package com.lwh.jackknife.orm.type;

/**
 * Created by liuwenhao on 2017/9/16.
 */

public class ShortType extends BaseDataType {

    private static final ShortType mInstance = new ShortType();

    public ShortType(){
        super(SqlType.INTEGER);
    }

    public ShortType(SqlType sqlType) {
        super(sqlType);
    }

    public static ShortType getInstance(){
        return mInstance;
    }


    @Override
    public int getDefaultWidth() {
        return 0;
    }
}
