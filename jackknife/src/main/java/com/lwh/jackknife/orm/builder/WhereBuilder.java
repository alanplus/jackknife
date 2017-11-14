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

    private static final String SPACE = "";

    private String mSelection;

    private String[] mSelectionArgs;

    private WhereBuilder(){
    }

    private WhereBuilder(Condition condition){
        this.mSelection = condition.getSelection();
        this.mSelectionArgs = condition.getSelectionArgs();
    }

    public static WhereBuilder create(){
        return new WhereBuilder();
    }

    public static WhereBuilder create(Condition condition){
        return new WhereBuilder(condition);
    }

    public WhereBuilder and(){
        if (mSelection != null) {
            mSelection += AND;
        }
        return this;
    }

    public WhereBuilder or(){
        if (mSelection != null) {
            mSelection += OR;
        }
        return this;
    }

    public WhereBuilder not(){
        if (mSelection != null) {
            mSelection += NOT;
        } else {
            mSelection = NOT;
        }
        return this;
    }

    public WhereBuilder parenthesesLeft(){
        if (mSelection != null) {
            mSelection += PARENTHESES_LEFT;
        } else {
            mSelection = PARENTHESES_LEFT;
        }
        return this;
    }

    public WhereBuilder parenthesesRight(){
        if (mSelection != null) {
            mSelection += PARENTHESES_RIGHT;
        } else {
            mSelection = PARENTHESES_RIGHT;
        }
        return this;
    }

    public WhereBuilder append(String connect, String whereClause, String... whereArgs){
        if (mSelection == null) {
            mSelection = whereClause;
            mSelectionArgs = whereArgs;
        } else {
            if (connect != null){
                mSelection += connect;
            }
            mSelection += whereClause;
            String[] selectionArgs = new String[mSelection.length()+whereArgs.length];
            System.arraycopy(mSelection, 0, selectionArgs, 0, mSelection.length());
            System.arraycopy(whereArgs, 0, selectionArgs, mSelection.length(), whereArgs.length);
            mSelectionArgs = selectionArgs;
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

    public String build() {
        return mSelection != null ? WHERE + mSelection:SPACE;
    }

    public String getSelection() {
        return mSelection;
    }

    public String[] getSelectionArgs() {
        return mSelectionArgs;
    }

    public WhereBuilder where(Condition condition){
        mSelection = condition.getSelection();
        mSelectionArgs = condition.getSelectionArgs();
        return this;
    }
}
