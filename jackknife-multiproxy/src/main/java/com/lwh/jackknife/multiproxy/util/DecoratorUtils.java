package com.lwh.jackknife.multiproxy.util;

import com.lwh.jackknife.multiproxy.annotation.Autowired;
import com.lwh.jackknife.multiproxy.interfaces.DecoratorFactory;
import com.lwh.jackknife.multiproxy.interfaces.IDifference;
import com.lwh.jackknife.multiproxy.loader.FactoryProducer;

import java.lang.reflect.Field;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

public class DecoratorUtils {

    private DecoratorUtils() {
    }

    public static String getPackageName(ProcessingEnvironment env, Element element) {
        return env.getElementUtils().getPackageOf(element).getQualifiedName().toString();
    }

    public <D extends IDifference, M extends IDifference> D getDecorator(
            M module, Class<M> moduleClazz,
                                                  Class<D> differenceClazz) {
        Field[] fields = moduleClazz.getDeclaredFields();
        for (Field field : fields) {
            Autowired autowired = field.getAnnotation(Autowired.class);
            if (autowired == null) {
                continue;
            }
            try {
                field.setAccessible(true);
                if (field.get(module) == null) {
                    synchronized (this) {
                        if (field.get(module) == null) {
                            DecoratorFactory factory = FactoryProducer.getFactory(moduleClazz);
                            field.set(module, factory.newDecorator((D) module, differenceClazz));
                        }
                        return (D) (field.get(module) == null ? module : field.get(module));
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        throw new IllegalStateException("注入模块失败");
    }
}
