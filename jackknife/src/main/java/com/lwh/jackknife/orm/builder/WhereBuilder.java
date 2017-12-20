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

    private WhereBuilder(String whereClause, String[] whereArgs) {
        this.mSelection = whereClause;
        this.mSelectionArgs = whereArgs;
    }

    public static WhereBuilder create(){
        return new WhereBuilder();
    }

    public static WhereBuilder create(String whereClause, String[] whereArgs) {
        return new WhereBuilder(whereClause, whereArgs);
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

    public WhereBuilder and(String whereClause, String... whereArgs) {
        return append(AND, whereClause, whereArgs);
    }

    public WhereBuilder or(String whereClause, String... whereArgs) {
        return append(OR, whereClause, whereArgs);
    }

    public WhereBuilder not(String whereClause, String... whereArgs) {
        return not().parenthesesLeft().append(null, whereClause, whereArgs).parenthesesRight();
    }

    public WhereBuilder andNot(String whereClause, String... whereArgs) {
        return and(not(whereClause, whereArgs));
    }

    public WhereBuilder orNot(String whereClause, String... whereArgs) {
        return or(not(whereClause, whereArgs));
    }

    public WhereBuilder and(WhereBuilder builder) {
        String selection = builder.getSelection();
        String[] selectionArgs = builder.getSelectionArgs();
        return and(selection, selectionArgs);
    }

    public WhereBuilder or(WhereBuilder builder) {
        String selection = builder.getSelection();
        String[] selectionArgs = builder.getSelectionArgs();
        return or(selection, selectionArgs);
    }

    public WhereBuilder not(WhereBuilder builder) {
        String selection = builder.getSelection();
        String[] selectionArgs = builder.getSelectionArgs();
        return not(selection, selectionArgs);
    }

    public WhereBuilder andNot(WhereBuilder builder) {
        return and(not(builder));
    }

    public WhereBuilder orNot(WhereBuilder builder) {
        return or(not(builder));
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
        }
        return this;
    }

    public WhereBuilder addWhereEqualTo(String column, Object value){
        return append(null, column + EQUAL_HOLDER, String.valueOf(value));
    }

    public WhereBuilder addWhereNotEqualTo(String column, Object value){
        return append(null, column + NOT_EQUAL_HOLDER, String.valueOf(value));
    }

    public WhereBuilder addWhereGreaterThan(String column, Object value){
        return append(null, column + GREATER_THAN_HOLDER, String.valueOf(value));
    }

    public WhereBuilder addWhereGreaterThanOrEqualTo(String column, Object value){
        return append(null, column + GREATER_THAN_OR_EQUAL_TO_HOLDER, String.valueOf(value));
    }

    public WhereBuilder addWhereLessThan(String column, Object value){
        return append(null, column + LESS_THAN_HOLDER, String.valueOf(value));
    }

    public WhereBuilder addWhereLessThanOrEqualTo(String column, Object value){
        return append(null, column + LESS_THAN_OR_EQUAL_TO_HOLDER, String.valueOf(value));
    }

    public WhereBuilder addWhereIn(String column, Object... values) {
        return appendWhereIn(null, column, values);
    }

    public WhereBuilder andWhereEqualTo(String column, Object value) {
        return append(AND, column + EQUAL_HOLDER, String.valueOf(value));
    }

    public WhereBuilder andWhereNotEqualTo(String column, Object value) {
        return append(AND, column + NOT_EQUAL_HOLDER, String.valueOf(value));
    }

    public WhereBuilder andWhereGreatorThan(String column, Object value) {
        return append(AND, column + GREATER_THAN_HOLDER, String.valueOf(value));
    }

    public WhereBuilder andWhereGreatorThanOrEqualTo(String column, Object value) {
        return append(AND, column + GREATER_THAN_OR_EQUAL_TO_HOLDER, String.valueOf(value));
    }

    public WhereBuilder andWhereLessThan(String column, Object value) {
        return append(AND, column + LESS_THAN_HOLDER, String.valueOf(value));
    }

    public WhereBuilder andWhereLessThanOrEqualTo(String column, Object value) {
        return append(AND, column + LESS_THAN_OR_EQUAL_TO_HOLDER, String.valueOf(value));
    }

    public WhereBuilder andWhereIn(String column, Object... values) {
        return appendWhereIn(AND, column, values);
    }

    public WhereBuilder orWhereEqualTo(String column, Object value) {
        return append(OR, column + EQUAL_HOLDER, String.valueOf(value));
    }

    public WhereBuilder orWhereNotEqualTo(String column, Object value) {
        return append(OR, column + NOT_EQUAL_HOLDER, String.valueOf(value));
    }
    public WhereBuilder orWhereGreatorThan(String column, Object value) {
        return append(OR, column + GREATER_THAN_HOLDER, String.valueOf(value));
    }
    public WhereBuilder orWhereGreatorThanOrEqualTo(String column, Object value) {
        return append(OR, column + GREATER_THAN_OR_EQUAL_TO_HOLDER, String.valueOf(value));
    }
    public WhereBuilder orWhereLessThan(String column, Object value) {
        return append(OR, column + LESS_THAN_HOLDER, String.valueOf(value));
    }
    public WhereBuilder orWhereLessThanOrEqualTo(String column, Object value) {
        return append(OR, column + LESS_THAN_OR_EQUAL_TO_HOLDER, String.valueOf(value));
    }

    public WhereBuilder orWhereIn(String column, Object... values) {
        return appendWhereIn(OR, column, values);
    }

    private WhereBuilder append(String connect, String whereClause, String... whereArgs){
        if (mSelection == null) {
            mSelection = whereClause;
            String[] selectionArgs = new String[whereArgs.length];
            System.arraycopy(whereArgs, 0, selectionArgs, 0, whereArgs.length);
            mSelectionArgs = selectionArgs;
        } else {
            if (connect != null) {
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

    private WhereBuilder appendWhereIn(String connect, String column, Object[] values){
        String whereIn = buildWhereIn(column, values.length);
        String[] tempValues = new String[values.length];
        for (int i=0;i<tempValues.length;i++) {
            tempValues[i] = String.valueOf(values[i]);
        }
        return append(connect, whereIn, tempValues);
    }

    private String buildWhereIn(String column, int num) {
        StringBuilder sb = new StringBuilder(column).append(SPACE).append(IN).append(PARENTHESES_LEFT)
                .append(HOLDER);
        for (int i=0;i<num-1;i++) {
            sb.append(COMMA_HOLDER);
        }
        return sb.append(PARENTHESES_RIGHT).toString();
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
