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

import com.lwh.jackknife.orm.Condition;

public class WhereBuilder {

    private static final String WHERE = " WHERE ";

    private static final String EQUAL_HOLDER = "=?";

    private static final String NOT_EQUAL_HOLDER = "!=?";

    private static final String GREATER_THAN_HOLDER = ">?";

    private static final String LESS_THAN_HOLDER = "<?";

    private static final String GREATER_THAN_OR_EQUAL_TO_HOLDER = ">=?";

    private static final String LESS_THAN_OR_EQUAL_TO_HOLDER = "<=?";

    private static final String COMMA_HOLDER = ",?";

    private static final String HOLDER = "?";

    private static final String AND = " AND ";

    private static final String OR = " OR ";

    private static final String NOT = " NOT ";

    private static final String IN = " IN ";

    private static final String PARENTHESES_LEFT = "(";

    private static final String PARENTHESES_RIGHT = ")";

    private Condition mCondition;

    private WhereBuilder(){
        mCondition = new Condition();
    }

    private WhereBuilder (Condition condition){
        this.mCondition = condition;
    }

    public static WhereBuilder create(){
        return new WhereBuilder();
    }

    public static WhereBuilder create(Condition condition){
        return new WhereBuilder(condition);
    }

    public WhereBuilder and(){
        mCondition.appendSelection(AND);
        return this;
    }

    public WhereBuilder or(){
        mCondition.appendSelection(OR);
        return this;
    }

    public WhereBuilder not(){
        mCondition.appendSelection(AND);
        return this;
    }

    public WhereBuilder parenthesesLeft(){
        mCondition.appendSelection(PARENTHESES_LEFT);
        return this;
    }

    public WhereBuilder parenthesesRight(){
        if (!mCondition.hasSelection()) {
            throw new RuntimeException("The right parenthesis cannot be the start of an SQL " +
                    "statement.");
        } else {
           mCondition.appendSelection(PARENTHESES_RIGHT);
        }
        return this;
    }

    public WhereBuilder append(String connect, String whereClause, String... whereArgs){
        if (!mCondition.hasSelection()) {
            mCondition.setSelection(whereClause);
            mCondition.setSelectionArgs(whereArgs);
        } else {
            if (connect != null){
                mCondition.appendSelection(connect);
            }
            mCondition.appendSelection(whereClause);
            String[] newWhereArgs = new String[mCondition.getSelection().length()+whereArgs.length];
            System.arraycopy(mCondition.getSelection(), 0, newWhereArgs, 0, mCondition.getSelection().length());
            System.arraycopy(whereArgs, 0, newWhereArgs, mCondition.getSelection().length(), whereArgs.length);
            mCondition.setSelectionArgs(newWhereArgs);
        }
        return this;
    }

    public String buildWhereIn(String column, int num){
        StringBuilder sb = new StringBuilder(column).append(IN).append(PARENTHESES_LEFT)
                .append(HOLDER);
        for (int i=0;i<num-1;i++){
            sb.append(COMMA_HOLDER);
        }
        return sb.append(PARENTHESES_RIGHT).toString();
    }

    public WhereBuilder addWhereEqualTo(String column, Object value){
        return append(null, column + EQUAL_HOLDER, new String[]{String.valueOf(value)});
    }

    public WhereBuilder addWhereNotEqualTo(String column, Object value){
        return append(null, column + NOT_EQUAL_HOLDER, new String[]{String.valueOf(value)});
    }

    public WhereBuilder addWhereGreaterThan(String column, Object value){
        return append(null, column + GREATER_THAN_HOLDER, new String[]{String.valueOf(value)});
    }

    public WhereBuilder addWhereGreaterThanOrEqualTo(String column, Object value){
        return append(null, column + GREATER_THAN_OR_EQUAL_TO_HOLDER, new String[]{String.valueOf(value)});
    }

    public WhereBuilder addWhereLessThan(String column, Object value){
        return append(null, column + LESS_THAN_HOLDER, new String[]{String.valueOf(value)});
    }

    public WhereBuilder addWhereLessThanOrEqualTo(String column, Object value){
        return append(null, column + LESS_THAN_OR_EQUAL_TO_HOLDER, new String[]{String.valueOf(value)});
    }

    public WhereBuilder addWhereIn(String column, Object[] values){
        String[] strVals = new String[values.length];
        for (int i=0;i<strVals.length;i++) {
            strVals[i] = String.valueOf(values[i]);
        }
        return append(null, column + IN, strVals);
    }

    public Condition build(){
        return mCondition;
    }

    public String getWhereClause() {
        return mCondition.getSelection();
    }

    public Object[] getWhereArgs() {
        return mCondition.getSelectionArgs();
    }

    public WhereBuilder where(Condition condition){
        mCondition = condition;
        return this;
    }
}
