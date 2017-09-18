package com.lwh.jackknife.orm.builder;

public class QueryBuilder<T> {

    public static final String ASC = " ASC";
    public static final String DESC = " DESC";
    public static final String GROUP_BY = " GROUP BY ";
    public static final String HAVING = " HAVING ";
    public static final String ORDER_BY = " ORDER BY ";
    public static final String LIMIT = " LIMIT ";
    public static final String SELECT_COUNT = "SELECT COUNT(*) FROM ";
    public static final String SELECT = "SELECT ";
    public static final String DISTINCT = " DISTINCT ";
    public static final String ASTERISK = "*";
    public static final String FROM = " FROM ";
    public static final String COMMA = ",";

    protected String[] mColumns;
    protected boolean mDistinct;
    protected String mGroup;
    protected String mHaving;
    protected String mOrder;
    protected String mLimit;
    protected WhereBuilder mWhereBuilder;

    public QueryBuilder(){
        mWhereBuilder = new WhereBuilder();
    }

    public QueryBuilder where(WhereBuilder builder){
        mWhereBuilder = builder;
        return this;
    }

    public QueryBuilder where(String where, Object[] whereArgs){
        mWhereBuilder.where(where, whereArgs);
        return this;
    }

    public static QueryBuilder create(){
        return new QueryBuilder<>();
    }


    public QueryBuilder distinct(boolean distinct){
        mDistinct = distinct;
        return this;
    }

    public QueryBuilder columns(String[] columns){
        mColumns = columns;
        return this;
    }

    public QueryBuilder having(String having){
        mHaving = HAVING + having;
        return this;
    }

    public QueryBuilder orderBy(String order){
        mOrder = ORDER_BY + order;
        return this;
    }

    public QueryBuilder groupBy(String group){
        mGroup = GROUP_BY + group;
        return this;
    }

    public QueryBuilder limit(String limit){
        mLimit = LIMIT + limit;
        return this;
    }

    public QueryBuilder limit(int start, int length){
        mLimit = LIMIT + start + COMMA + length;
        return this;
    }

    public WhereBuilder getWhereBuilder() {
        return mWhereBuilder;
    }

    public String getHaving() {
        return mHaving;
    }

    public String getOrder() {
        return mOrder;
    }

    public String getGroup() {
        return mGroup;
    }

    public String getLimit() {
        return mLimit;
    }

    public String[] getColumns() {
        return mColumns;
    }
}
