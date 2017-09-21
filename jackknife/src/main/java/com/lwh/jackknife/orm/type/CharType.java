package com.lwh.jackknife.orm.type;

/**
 * Created by liuwenhao on 2017/9/16.
 */

public class CharType extends BaseDataType {

    private static final CharType mInstance = new CharType();

    public CharType(){
        super(SqlType.TEXT);
    }

    public CharType(SqlType sqlType) {
        super(sqlType);
    }

    public static CharType getInstance(){
        return mInstance;
    }

    @Override
    public Object java2sql(Object jobject) {
        Character character = (Character) jobject;
        if (character == null || character == 0){
            return null;
        }
        return character;
    }

    @Override
    public Object sql2java(Object sqlData) {
        return null;
    }

    @Override
    public int getDefaultWidth() {
        return 0;
    }
}
