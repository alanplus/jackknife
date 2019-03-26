package com.lwh.jackknife.multiproxy.loader;

import com.lwh.jackknife.multiproxy.DefaultDecoratorFactory;
import com.lwh.jackknife.multiproxy.interfaces.DecoratorFactory;

import java.util.HashMap;
import java.util.Map;

public class FactoryProducer {

    private static Map<String, DecoratorFactory> sInjectCache = new HashMap<>();

    public static DecoratorFactory getFactory(Class<?> targetClazz) {
        String $className = targetClazz.getName() + "$Factory";
        DecoratorFactory factory = sInjectCache.get($className);
        if (factory != null) {
            return factory;
        }
        try {
            Class<?> factoryClazz = Class.forName($className);
            factory = (DecoratorFactory) factoryClazz.newInstance();
            sInjectCache.put($className, factory);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return factory == null ? new DefaultDecoratorFactory() : factory;
    }
}
