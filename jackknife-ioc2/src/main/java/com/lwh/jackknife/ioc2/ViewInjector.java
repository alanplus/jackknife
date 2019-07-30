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

package com.lwh.jackknife.ioc2;

import com.lwh.jackknife.ioc2.adapter.InjectAdapter;
import com.lwh.jackknife.ioc2.adapter.NullAdapter;
import com.lwh.jackknife.ioc2.annotation.EventBase2;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class ViewInjector {

    static Map<Class<?>, InjectAdapter> mInjectCache = new HashMap<>();

    public static String SUFFIX = "$InjectAdapter";
    public static String METHOD_VALUE = "value";

    public static Object injectWithBindingEvent(Ioc2EventSupport target) {
        Object obj = inject(target);
        injectEvents(target);
        return obj;
    }

    public static Object inject(Object target) {
        return getViewAdapter(target.getClass()).inject(target);
    }

    public static void injectEvents(Ioc2EventSupport target) {
        Class<?> viewClass = target.getClass();
        Method[] methods = viewClass.getDeclaredMethods();
        for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
                EventBase2 eventBase = annotationType.getAnnotation(EventBase2.class);
                if (eventBase == null) {
                    continue;
                }
                String listenerSetter = eventBase.listenerSetter();
                Class<?> listenerType = eventBase.listenerType();
                String callbackMethod = eventBase.callbackMethod();
                try {
                    Method valueMethod = annotationType.getDeclaredMethod(METHOD_VALUE);
                    int[] viewIds = (int[]) valueMethod.invoke(annotation);
                    for (int viewId : viewIds) {
                        Object view = target.getView(viewId);
                        if (view == null) {
                            continue;
                        }
                        Method setListenerMethod = view.getClass().getMethod(listenerSetter,
                                listenerType);
                        HashMap<String, Method> map = new HashMap<>();
                        map.put(callbackMethod, method);
                        EventInvocationHandler handler = new EventInvocationHandler(map, target);
                        Object proxy = Proxy.newProxyInstance(listenerType.getClassLoader(),
                                new Class<?>[]{listenerType}, handler);
                        setListenerMethod.invoke(view, proxy);
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class EventInvocationHandler implements InvocationHandler {

        private Map<String, Method> mCallbackMethodMap;
        private Object mViewInjected;

        public EventInvocationHandler(HashMap<String, Method> callbackMethodMap, Ioc2EventSupport v) {
            this.mCallbackMethodMap = callbackMethodMap;
            this.mViewInjected = v;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            Method callbackMethod = mCallbackMethodMap.get(name);
            if (callbackMethod != null) {
                return callbackMethod.invoke(mViewInjected, args);
            }
            return method.invoke(proxy, args);
        }
    }

    private static InjectAdapter getViewAdapter(Class<?> viewClass) {
        InjectAdapter adapter = mInjectCache.get(viewClass);
        if (adapter != null) {
            return adapter;
        }
        String $className = viewClass.getName() + SUFFIX;
        try {
            Class<?> adapterClass = Class.forName($className);
            adapter = (InjectAdapter) adapterClass.newInstance();
            mInjectCache.put(viewClass, adapter);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return adapter == null ? new NullAdapter() : adapter;
    }
}
