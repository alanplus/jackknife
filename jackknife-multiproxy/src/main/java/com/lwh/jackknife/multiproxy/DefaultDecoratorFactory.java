package com.lwh.jackknife.multiproxy;

import com.lwh.jackknife.multiproxy.interfaces.DecoratorFactory;
import com.lwh.jackknife.multiproxy.interfaces.IDifference;

/**
 * 空对象模式。
 */
public class DefaultDecoratorFactory implements DecoratorFactory {

    @Override
    public <C extends IDifference, D extends IDifference>
    D newDecorator(C component, Class<C> componentClazz) {
        return null;
    }

    @Override
    public Class<? extends IDifference> getDecoratorClass() {
        return null;
    }
}
