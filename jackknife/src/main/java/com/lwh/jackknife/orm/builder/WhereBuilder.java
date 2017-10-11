/*
 * Copyright (C) 2017 The JackKnife Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lwh.jackknife.orm.builder;

import com.lwh.jackknife.util.TextUtils;

public class WhereBuilder {

    public static final String WHERE = " WHERE ";
    public static final String EQUAL_HOLDER = "=?";
    public static final String NOT_EQUAL_HOLDER = "!=?";
    public static final String GREATER_THAN_HOLDER = ">?";
    public static final String LESS_THAN_HOLDER = "<?";
    public static final String GREATER_THAN_OR_EQUAL_TO_HOLDER = ">=?";
    public static final String LESS_THAN_OR_EQUAL_TO_HOLDER = "<=?";
    public static final String COMMA_HOLDER = ",?";
    public static final String HOLDER = "?";
    public static final String AND = " AND ";
    public static final String OR = " OR ";
    public static final String NOT = " NOT ";
    public static final String IN = " IN ";
    public static final String PARENTHESES_LEFT = "(";
    public static final String PARENTHESES_RIGHT = ")";
    protected String mWhere;
    protected Object[] mWhereArgs;

    public WhereBuilder(){
    }

    public WhereBuilder (String where, Object[] whereArgs){
        this.mWhere = where;
        this.mWhereArgs = whereArgs;
    }

    public static WhereBuilder create(){
        return new WhereBuilder();
    }

    public static WhereBuilder create(String where, Object[] whereArgs){
        return new WhereBuilder(where, whereArgs);
    }

    public WhereBuilder and(){
        if (mWhere != null) {
            mWhere += AND;
        }
        return this;
    }

    public WhereBuilder or(){
        if (mWhere != null) {
            mWhere += OR;
        }
        return this;
    }

    public WhereBuilder not(){
        if (mWhere != null) {
            mWhere += NOT;
        }
        return this;
    }

    public WhereBuilder parenthesesLeft(){
        if (mWhere == null){
            mWhere = PARENTHESES_LEFT;
        }else{
            mWhere += PARENTHESES_LEFT;
        }
        return this;
    }

    public WhereBuilder parenthesesRight(){
        if (mWhere == null){
            throw new RuntimeException("右括号不能为SQL语句的开始。");
        } else {
            mWhere += PARENTHESES_RIGHT;
        }
        return this;
    }

    public WhereBuilder append(String connect, String where, Object... whereArgs){
        if (mWhere == null) {
            mWhere = where;
            mWhereArgs = whereArgs;
        } else {
            if (connect != null){
                mWhere += connect;
            }
            mWhere += where;
            Object[] newWhereArgs = new Object[mWhereArgs.length+whereArgs.length];
            System.arraycopy(mWhereArgs, 0, newWhereArgs, 0, mWhereArgs.length);//把已有的whereArgs复制到新的数组中
            System.arraycopy(whereArgs, 0, newWhereArgs, mWhereArgs.length, whereArgs.length);//把传入的whereArgs复制到新的数组中
            mWhereArgs = newWhereArgs;
        }
        return this;
    }

    public String buildWhereIn(String column, int num){
        StringBuilder sb = new StringBuilder(column).append(IN).append(PARENTHESES_LEFT).append(HOLDER);
        for (int i=0;i<num-1;i++){
            sb.append(COMMA_HOLDER);
        }
        return sb.append(PARENTHESES_RIGHT).toString();
    }

    public WhereBuilder addWhereEqualTo(String column, Object value){
        return append(null, column + EQUAL_HOLDER, new Object[]{value});
    }

    public WhereBuilder addWhereNotEqualTo(String column, Object value){
        return append(null, column + NOT_EQUAL_HOLDER, new Object[]{value});
    }

    public WhereBuilder addWhereGreaterThan(String column, Object value){
        return append(null, column + GREATER_THAN_HOLDER, new Object[]{value});
    }

    public WhereBuilder addWhereGreaterThanOrEqualTo(String column, Object value){
        return append(null, column + GREATER_THAN_OR_EQUAL_TO_HOLDER, new Object[]{value});
    }

    public WhereBuilder addWhereLessThan(String column, Object value){
        return append(null, column + LESS_THAN_HOLDER, new Object[]{value});
    }

    public WhereBuilder addWhereLessThanOrEqualTo(String column, Object value){
        return append(null, column + LESS_THAN_OR_EQUAL_TO_HOLDER, new Object[]{value});
    }

    public WhereBuilder addWhereIn(String column, Object[] values){
        return append(null, column + IN, values);
    }

    public String build(){
        return mWhere != null ? WHERE + mWhere:"";
    }

    public String getWhere() {
        return mWhere;
    }

    public Object[] getWhereArgs() {
        return mWhereArgs;
    }

    public WhereBuilder where(String where, Object[] whereArgs){
        setWhereAndArgs(where, whereArgs);
        return this;
    }

    private void setWhereAndArgs(String where, Object[] whereArgs) {
        if (TextUtils.isNotEmpty(where) || whereArgs != null) {
            mWhere = where;
            mWhereArgs = whereArgs;
        }
    }

    public String getSQL(){
        int position = 0;
        for (int i = 0; i < mWhere.length(); i++) {
            char c = mWhere.charAt(i);
            if (c == '?'){
                String arg;
                if (mWhereArgs[position] instanceof Number) {
                    arg = String.valueOf(mWhereArgs[position]);
                }else if (mWhereArgs[position] instanceof String){
                    arg = mWhereArgs[position].toString();
                }else {
                    throw new RuntimeException("mWhereArgs类型不为Number和String");
                }
                mWhere.replaceFirst("^?$", arg);
                position++;
            }
        }
        return WHERE + mWhere;
    }
}
