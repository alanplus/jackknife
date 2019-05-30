package com.lwh.jackknife.orm;

public class PrimaryKeyValue {

    private String mRealValue;

    public PrimaryKeyValue(Number number) {
        this.mRealValue = String.valueOf(number);
    }

    public PrimaryKeyValue(String str) {
        this.mRealValue = str;
    }

    public String get() {
        return mRealValue;
    }
}
