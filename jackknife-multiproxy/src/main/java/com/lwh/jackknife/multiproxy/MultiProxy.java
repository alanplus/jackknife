package com.lwh.jackknife.multiproxy;

import com.lwh.jackknife.multiproxy.annotation.Autowired;
import com.lwh.jackknife.multiproxy.interfaces.DecoratorFactory;
import com.lwh.jackknife.multiproxy.interfaces.IDifference;
import com.lwh.jackknife.multiproxy.loader.FactoryProducer;

import java.lang.reflect.Field;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

public class MultiProxy {

    private MultiProxy() {
    }

    public static String getPackageName(ProcessingEnvironment env, Element element) {
        return env.getElementUtils().getPackageOf(element).getQualifiedName().toString();
    }

    public static <M extends IDifference, D extends IDifference> D
        getDecorator(M module, Class<D> differenceClazz) {
        Class<? extends IDifference> moduleClazz = module.getClass();
        Field[] fields = moduleClazz.getDeclaredFields();
        for (Field field : fields) {
            Autowired autowired = field.getAnnotation(Autowired.class);
            if (autowired == null) {
                continue;
            }
            try {
                field.setAccessible(true);
                Object o = field.get(module);
                if (o == null) {
                    synchronized (moduleClazz) {
                        if (o == null) {
                            DecoratorFactory factory = FactoryProducer.getFactory(moduleClazz);
                            field.set(module, factory.newDecorator((D) module, differenceClazz));
                        }
                        return (D) (o == null ? module : o);
                    }
                }
                return (D) o;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        throw new IllegalStateException("注入模块失败");
    }
}
