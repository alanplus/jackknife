package com.lwh.jackknife.orm.helper;

public interface DataPersister extends FieldConverter{

    /**
     * 返回和这个类型相关的默认的宽度，如果不写，就是0。
     *
     * @return 默认的宽度。
     */
    int getDefaultWidth();
}
