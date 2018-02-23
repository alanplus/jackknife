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

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

public class ReflectionUtils {

    public static <T> T newInstance(Class<T> clazz)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> c : constructors) {
            c.setAccessible(true);
            Class[] cls = c.getParameterTypes();
            if (cls.length == 0) {
                return (T) c.newInstance();
            } else {
                Object[] objs = new Object[cls.length];
                for (int i = 0; i < cls.length; i++) {
                    objs[i] = getDefaultPrimitiveValue(cls[i]);
                }
                return (T) c.newInstance(objs);
            }
        }
        return null;
    }

    public static Object getDefaultPrimitiveValue(Class clazz) {
        if (clazz.isPrimitive()) {
            return clazz == boolean.class ? false : 0;
        }
        return null;
    }

    public static boolean isNumber(Class<?> clazz) {
        return clazz == long.class
                || clazz == Long.class
                || clazz == int.class
                || clazz == Integer.class
                || clazz == short.class
                || clazz == Short.class
                || clazz == byte.class
                || clazz == Byte.class;
    }

    public static void setNumber(Object o, Field field, long n) throws IllegalAccessException {
        field.setAccessible(true);
        Class clazz = field.getType();
        if (clazz == long.class) {
            field.setLong(o, n);
        } else if (clazz == int.class) {
            field.setInt(o, (int) n);
        } else if (clazz == short.class) {
            field.setShort(o, (short) n);
        } else if (clazz == byte.class) {
            field.setByte(o, (byte) n);
        } else if (clazz == Long.class) {
            field.set(o, new Long(n));
        } else if (clazz == Integer.class) {
            field.set(o, new Integer((int) n));
        } else if (clazz == Short.class) {
            field.set(o, new Short((short) n));
        } else if (clazz == Byte.class) {
            field.set(o, new Byte((byte) n));
        } else {
            throw new RuntimeException("field is not a number class");
        }
    }

    public static boolean isCollection(Class clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }

    public static boolean isArray(Class clazz) {
        return clazz.isArray();
    }

    public static boolean isSerializable(Field field) {
        Class<?>[] cls = field.getType().getInterfaces();
        for (Class<?> c : cls) {
            if (Serializable.class == c) { return true; }
        }
        return false;
    }

    public static Class<?> getComponentType(Field field) {
        return field.getType().getComponentType();
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

    public static void set(Field field, Object obj, Object value) throws IllegalArgumentException,
            IllegalAccessException {
        field.setAccessible(true);
        field.set(obj, value);
    }

    public static Object get(Field field, Object obj) throws IllegalArgumentException,
            IllegalAccessException {
        field.setAccessible(true);
        return field.get(obj);
    }
}
