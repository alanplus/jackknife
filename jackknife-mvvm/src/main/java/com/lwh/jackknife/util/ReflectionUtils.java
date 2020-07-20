/*
 * Copyright (C) 2018 The JackKnife Open Source Project
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

package com.lwh.jackknife.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    public static Class<?> newClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object newInstance(String className) {
        Class<?> clazz = newClass(className);
        if (clazz != null) {
            return newInstance(clazz);
        }
        return null;
    }


    public static <T> T newInstance(Class<T> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> c : constructors) {
            c.setAccessible(true);
            Class[] cls = c.getParameterTypes();
            if (cls.length == 0) {
                try {
                    return (T) c.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                Object[] objs = new Object[cls.length];
                for (int i = 0; i < cls.length; i++) {
                    objs[i] = getPrimitiveDefaultValue(cls[i]);
                }
                try {
                    return (T) c.newInstance(objs);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static Object getPrimitiveDefaultValue(Class clazz) {
        if (clazz.isPrimitive()) {
            return clazz == boolean.class ? false : 0;
        }
        return null;
    }

    public static Class<?> getGenericType(Object obj) {
        if(obj.getClass().getGenericSuperclass() instanceof ParameterizedType &&
                ((ParameterizedType) (obj.getClass().getGenericSuperclass())).getActualTypeArguments().length > 0) {
            Class tClass = (Class) ((ParameterizedType) (obj.getClass()
                    .getGenericSuperclass())).getActualTypeArguments()[0];
            return tClass;
        }
        return (Class<?>) ((ParameterizedType)obj.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public static Class<?> getGenericType(Field field) {
        Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (type instanceof Class<?>) {
                return (Class<?>) type;
            }
        } else if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        return null;
    }

    public static Class<?> getNestedGenericType(Field f, int genericTypeIndex) {
        Type type = f.getGenericType();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            type = parameterizedType.getActualTypeArguments()[genericTypeIndex];
            return (Class<?>) type;
        }
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        return null;
    }

    public static boolean isNumber(Class<?> numberCls) {
        return numberCls == long.class
                || numberCls == Long.class
                || numberCls == int.class
                || numberCls == Integer.class
                || numberCls == short.class
                || numberCls == Short.class
                || numberCls == byte.class
                || numberCls == Byte.class;
    }

    public static Object getFieldValue(Field field, Object obj) {
        field.setAccessible(true);
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setFieldValue(Field field, Object value) {
        field.setAccessible(true);
        try {
            field.set(field, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Object getStaticFieldValue(Field field) {
        field.setAccessible(true);
        if (isStaticField(field)) {
            try {
                return field.get(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Field is not static.");
    }

    public static void setStaticFieldValue(Field field, Object value) {
        field.setAccessible(true);
        if (isStaticField(field)) {
            try {
                field.set(null, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Field is not static.");
    }

    // <editor-folder desc="判断属性是否有某关键字">

    public static boolean isStaticField(Field f) {
        return Modifier.isStatic(f.getModifiers());
    }

    public static boolean isFinalField(Field f) {
        return Modifier.isFinal(f.getModifiers());
    }

    public static boolean isSynchronizedField(Field f) {
        return Modifier.isSynchronized(f.getModifiers());
    }

    public static boolean isAbstract(Field f) {
        return Modifier.isAbstract(f.getModifiers());
    }

    public static boolean isNative(Field f) {
        return Modifier.isNative(f.getModifiers());
    }

    public static boolean isVolatile(Field f) {
        return Modifier.isVolatile(f.getModifiers());
    }

    public static boolean isTransient(Field f) {
        return Modifier.isTransient(f.getModifiers());
    }

    // </editor-folder>
}
