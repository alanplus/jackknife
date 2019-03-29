package com.lwh.jackknife.multiproxy;

import com.lwh.jackknife.multiproxy.interfaces.DecoratorFactory;
import com.lwh.jackknife.multiproxy.interfaces.IDifference;
import com.lwh.jackknife.multiproxy.loader.FactoryProducer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

public class MultiProxy {

    private static Holder sDecoratorHolder;

    private MultiProxy() {
    }

    public static String getPackageName(ProcessingEnvironment env, Element element) {
        return env.getElementUtils().getPackageOf(element).getQualifiedName().toString();
    }

    public static class Holder {
        ConcurrentMap<String, IDifference> mDecoratorMap;

        Holder() {
            mDecoratorMap = new ConcurrentHashMap();
        }

        public boolean checkDecorator(String name) {
            return mDecoratorMap.containsKey(name);
        }

        public IDifference addDecorator(IDifference difference) {
            String name = difference.getClass().getName();
            if (!checkDecorator(name)) {
                mDecoratorMap.put(name, difference);
                return difference;
            } else {
                return mDecoratorMap.get(name);
            }
        }
    }

    public static Holder getHolder() {
        if (sDecoratorHolder == null) {
            synchronized (Holder.class) {
                if (sDecoratorHolder == null) {
                    sDecoratorHolder = new Holder();
                }
            }
        }
        return sDecoratorHolder;
    }

    public static <M extends IDifference, D extends IDifference> D getDecorator(M module, Class<D> differenceClazz) {
        Class<? extends IDifference> moduleClazz = module.getClass();
        DecoratorFactory factory = FactoryProducer.getFactory(moduleClazz);
        D decorator = factory.newDecorator((D) module, differenceClazz);
        return (D) getHolder().addDecorator(decorator);
    }
}
