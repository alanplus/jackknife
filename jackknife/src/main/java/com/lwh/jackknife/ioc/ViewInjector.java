/*
 * Copyright (C) 2017 The JackKnife Open Source Project
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

package com.lwh.jackknife.ioc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ViewInjector {

    private final String METHOD_SET_CONTENT_VIEW = "setContentView";
    private final String METHOD_FIND_VIEW_BY_ID = "findViewById";
    private final String METHOD_INFLATE = "inflate";
    private final String METHOD_VALUE = "value";
    private final String TYPE_NAME_ACTIVITY = Activity.class.getSimpleName();
    private final String TYPE_NAME_FRAGMENT = Fragment.class.getSimpleName();
    private final int VIEW_TYPE_UNDECLARED = -1;
    private final int VIEW_TYPE_ACTIVITY = 0;
    private final int VIEW_TYPE_FRAGMENT = 1;

    public void inject(Activity activity) throws InvocationTargetException,
            NoSuchMethodException, ClassNotFoundException, NoSuchFieldException,
            IllegalAccessException {
        injectLayout(activity);
        injectViews(activity);
        injectEvents(activity);
    }

    public void inject(Fragment fragment) throws InvocationTargetException,
            NoSuchMethodException, ClassNotFoundException, IllegalAccessException,
            NoSuchFieldException {
        injectViews(fragment);
        injectEvents(fragment);
    }

    protected Activity getActivity(Object viewInjected) {
        int type = getViewType(viewInjected.getClass());
        if (type == VIEW_TYPE_FRAGMENT) {
            viewInjected = ((Fragment) viewInjected).getActivity();
        }else if (type == VIEW_TYPE_UNDECLARED){
            throw new IllegalArgumentException("viewInjected must be an Activity or a fragment.");
        }
        return (Activity) viewInjected;
    }

    protected String generateLayoutName(Object viewInjected) {
        int type = getViewType(viewInjected.getClass());
        String suffix = TYPE_NAME_ACTIVITY;
        if (type == VIEW_TYPE_FRAGMENT) {
            suffix = TYPE_NAME_FRAGMENT;
        }
        StringBuffer sb;
        String layoutName = viewInjected.getClass().getSimpleName();
        if (!layoutName.endsWith(suffix)) {
            throw new IllegalArgumentException("viewInjected must be an Activity or a fragment.");
        } else {
            String name = layoutName.substring(0, layoutName.length() - suffix.length());
            sb = new StringBuffer(suffix.toLowerCase(Locale.ENGLISH));
            for (int i = 0; i < name.length(); i++) {
                if (name.charAt(i) >= 65 && name.charAt(i) <= 90 || i == 0) {
                    sb.append("_");
                }
                sb.append(String.valueOf(name.charAt(i)).toLowerCase(Locale.ENGLISH));
            }
        }
        return sb.toString();
    }

    protected int getViewType(Class<?> viewClass) {
        if (Activity.class.isAssignableFrom(viewClass)) {
            return VIEW_TYPE_ACTIVITY;
        } else if (Fragment.class.isAssignableFrom(viewClass)) {
            return VIEW_TYPE_FRAGMENT;
        } else {
            return VIEW_TYPE_UNDECLARED;
        }
    }

    protected boolean isViewTypeAllowed(int type){
        if (type != VIEW_TYPE_UNDECLARED){
            return true;
        }
        return false;
    }

    public final View injectLayout(Object viewInjected) throws NoSuchMethodException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            ClassNotFoundException, NoSuchFieldException {
        int type = getViewType(viewInjected.getClass());
        if (isViewTypeAllowed(type)) {
            String layoutName = generateLayoutName(viewInjected);
            viewInjected = getActivity(viewInjected);
            Class<?> viewClass = viewInjected.getClass();
            String packageName = ((Activity) viewInjected).getPackageName();
            Class<?> layoutClass = Class.forName(packageName + ".R$layout");
            Field field = layoutClass.getDeclaredField(layoutName);
            int layoutId = field.getInt(viewInjected);
            if (type == VIEW_TYPE_ACTIVITY) {
                Method method = viewClass.getMethod(METHOD_SET_CONTENT_VIEW, int.class);
                method.invoke(viewInjected, layoutId);
            }
            LayoutInflater inflater = LayoutInflater.from((Context) viewInjected);
            Class<? extends LayoutInflater> inflaterClass = LayoutInflater.class;
            Method inflateMethod = inflaterClass.getDeclaredMethod(METHOD_INFLATE, int.class,
                    ViewGroup.class);
            return (View) inflateMethod.invoke(inflater, layoutId, null);
        }else{
            throw new IllegalArgumentException("viewInjected must be an Activity or a fragment.");
        }
    }

    public final void injectViews(Object viewInjected) throws NoSuchMethodException,
            ClassNotFoundException, InvocationTargetException, IllegalAccessException,
            NoSuchFieldException {
        int type = getViewType(viewInjected.getClass());
        if (isViewTypeAllowed(type)) {
            Class<?> viewClass = viewInjected.getClass();
            Field[] viewFields = viewClass.getDeclaredFields();
            Activity activity = getActivity(viewInjected);
            Class<? extends Activity> activityClass = activity.getClass();
            for (Field field : viewFields) {
                field.setAccessible(true);
                Class<?> viewType = field.getType();
                if (View.class.isAssignableFrom(viewType)) {
                    ViewIgnore viewIgnore = field.getAnnotation(ViewIgnore.class);
                    if (viewIgnore != null) {
                        continue;
                    }
                    ViewId viewId = field.getAnnotation(ViewId.class);
                    int id = View.NO_ID;
                    if (viewId != null) {
                        id = viewId.value();
                    } else {
                        String packageName = activity.getPackageName();
                        Class<?> idClass = Class.forName(packageName + ".R$id");
                        Field idField = idClass.getDeclaredField(field.getName());
                        try {
                            id = idField.getInt(idField);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                    Method findViewByIdMethod = activityClass.getMethod(METHOD_FIND_VIEW_BY_ID,
                            int.class);
                    Object view = findViewByIdMethod.invoke(activity, id);
                    if (view != null) {
                        field.set(viewInjected, view);
                    }
                }
            }
        }else{
            throw new IllegalArgumentException("viewInjected must be an Activity or a fragment.");
        }
    }

    public final void injectEvents(Object viewInjected)
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        int type = getViewType(viewInjected.getClass());
        if (isViewTypeAllowed(type)) {
            Class<?> viewClass = viewInjected.getClass();
            Method[] methods = viewClass.getDeclaredMethods();
            Activity activity = getActivity(viewInjected);
            for (Method method : methods) {
                Annotation[] annotations = method.getAnnotations();
                for (Annotation annotation : annotations) {
                    Class<? extends Annotation> annotationType = annotation.annotationType();
                    EventBase eventBase = annotationType.getAnnotation(EventBase.class);
                    if (eventBase == null) {
                        continue;
                    }
                    String listenerSetter = eventBase.listenerSetter();
                    Class<?> listenerType = eventBase.listenerType();
                    String callbackMethod = eventBase.callbackMethod();
                    Method valueMethod = annotationType.getDeclaredMethod(METHOD_VALUE);
                    int[] viewIds = (int[]) valueMethod.invoke(annotation);
                    for (int viewId : viewIds) {
                        View view = activity.findViewById(viewId);
                        if (view == null) {
                            continue;
                        }
                        Method setListenerMethod = view.getClass().getMethod(listenerSetter, listenerType);
                        HashMap<String, Method> map = new HashMap();
                        map.put(callbackMethod, method);
                        EventInvocationHandler handler = new EventInvocationHandler(map,
                                viewInjected);
                        Object proxy = Proxy.newProxyInstance(listenerType.getClassLoader(),
                                new Class<?>[]{listenerType}, handler);
                        setListenerMethod.invoke(view, proxy);
                    }
                }
            }
        }else{
            throw new IllegalArgumentException("viewInjected must be an Activity or a fragment.");
        }
    }

    private class EventInvocationHandler implements InvocationHandler {

        private HashMap<String, Method> mCallbackMethodMap;
        private Object mViewInjected;

        public EventInvocationHandler(HashMap<String, Method> callbackMethodMap, Object view) {
            this.mCallbackMethodMap = callbackMethodMap;
            this.mViewInjected = view;
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
}
