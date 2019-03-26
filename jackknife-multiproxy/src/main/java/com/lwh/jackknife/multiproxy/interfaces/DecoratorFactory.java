package com.lwh.jackknife.multiproxy.interfaces;

public interface DecoratorFactory {

    <C extends IDifference, D extends IDifference>
    D newDecorator(C component, Class<C> componentClazz);

    Class<? extends IDifference> getDecoratorClass();
}
