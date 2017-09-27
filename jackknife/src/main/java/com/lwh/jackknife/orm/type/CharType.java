package com.lwh.jackknife.orm.type;

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
    public Object toSqlData(Object java) {
        Character character = (Character) java;
        if (character == null || character == 0){
            return null;
        }
        return character;
    }

    @Override
    public Object toJavaData(Object sql) {
        return super.toJavaData(sql);
    }

    @Override
    public int getDefaultWidth() {
        return 0;
    }
}
