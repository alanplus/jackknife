package com.lwh.jackknife.multiproxy.interfaces;

public interface IDifference<T> {

    /**
     * 获取差异的装饰器对象。
     */
    T getDecorator();
}
