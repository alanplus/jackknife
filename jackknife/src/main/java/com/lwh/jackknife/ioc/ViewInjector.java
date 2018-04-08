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
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lwh.jackknife.ioc.exception.IllegalViewClassNameException;
import com.lwh.jackknife.ioc.exception.InjectException;
import com.lwh.jackknife.ioc.exception.ViewTypeException;

public class ViewInjector {

    private final String METHOD_SET_CONTENT_VIEW = "setContentView";
    private final String METHOD_FIND_VIEW_BY_ID = "findViewById";
    private final String METHOD_INFLATE = "inflate";
    private final String METHOD_VALUE = "value";
    private final String UNDERLINE = "_";
    private final String R_ID = ".R$id";
    private final String R_LAYOUT = ".R$layout";
    private final int A_INDEX = 'A';
    private final int Z_INDEX = 'Z';
    private final String VIEW_TYPE_ERROR = "The viewInjected must be an activity or a fragment.";
    private final String VIEW_CLASS_NAME_ERROR = "Class name is not ends with \'Activity\' or " +
            "\'Fragment\'.";

    public enum ViewType {
        UNDECLARED,
        Activity,
        Fragment,
        Dialog
    }

    private static ViewInjector sInstance;

    private ViewInjector() {
    }

    private static ViewInjector getInstance() {
        if (sInstance == null) {
            synchronized (ViewInjector.class) {
                if (sInstance == null) {
                    sInstance = new ViewInjector();
                }
            }
        }
        return new ViewInjector();
    }

    public static void inject(SupportV v) {
        if (v instanceof SupportActivity || v instanceof SupportDialog) {
            injectLayout(v);
            injectViews(v);
            injectEvents(v);
        } else if (v instanceof SupportFragment) {
            injectViews(v);
            injectEvents(v);
        }
    }

    protected SupportActivity getSupportActivity(SupportV v) {
        ViewType viewType = getViewType(v);
        SupportActivity activity = null;
        if (viewType == ViewType.Activity) {
            activity = (SupportActivity) v;
        } if (viewType == ViewType.Fragment) {
            activity = ((SupportFragment) v).getFragmentActivity();
        } else if (viewType == ViewType.Dialog) {
            activity = ((SupportDialog) v).getDialogActivity();
        } else if (viewType == ViewType.UNDECLARED) {
            throw new ViewTypeException(VIEW_TYPE_ERROR);
        }
        return activity;
    }

    protected String generateLayoutName(SupportV v) {
        ViewType viewType = getViewType(v);
        if (isViewTypeAllowed(viewType)) {
            String suffix = viewType.name();
            StringBuffer sb;
            String layoutName = v.getClass().getSimpleName();
            if (!layoutName.endsWith(suffix)) {
                throw new IllegalViewClassNameException(VIEW_CLASS_NAME_ERROR);
            } else {
                String name = layoutName.substring(0, layoutName.length() - suffix.length());
                sb = new StringBuffer(suffix.toLowerCase(Locale.ENGLISH));
                for (int i = 0; i < name.length(); i++) {
                    if (name.charAt(i) >= A_INDEX && name.charAt(i) <= Z_INDEX || i == 0) {
                        sb.append(UNDERLINE);
                    }
                    sb.append(String.valueOf(name.charAt(i)).toLowerCase(Locale.ENGLISH));
                }
            }
            return sb.toString();
        } else {
            throw new ViewTypeException(VIEW_TYPE_ERROR);
        }
    }

    protected ViewType getViewType(SupportV v) {
        Class<? extends SupportV> viewClass = v.getClass();
        if (SupportActivity.class.isAssignableFrom(viewClass)) {
            return ViewType.Activity;
        } else if (SupportFragment.class.isAssignableFrom(viewClass)) {
            return ViewType.Fragment;
        } else if (SupportDialog.class.isAssignableFrom(viewClass)) {
            return ViewType.Dialog;
        } else {
            return ViewType.UNDECLARED;
        }
    }

    protected boolean isViewTypeAllowed(ViewType viewType){
        return viewType != ViewType.UNDECLARED;
    }

    public static View injectLayout(SupportV v) {
        return getInstance()._injectLayout(v);
    }

    private final View _injectLayout(SupportV v) {
        View view = null;
        ViewType viewType = getViewType(v);
        if (isViewTypeAllowed(viewType)) {
            String layoutName = generateLayoutName(v);
            ContentView contentView = v.getClass().getAnnotation(ContentView.class);
            SupportActivity activity = getSupportActivity(v);
            Class<? extends SupportV> viewClass = v.getClass();
            String packageName = v.getPackageName();
            try {
                Class<?> layoutClass = Class.forName(packageName + R_LAYOUT);
                Field field = layoutClass.getDeclaredField(layoutName);
                int layoutId = field.getInt(v);
                if (contentView != null) {
                    layoutId = contentView.value();
                }
                if (viewType == ViewType.Activity || viewType == ViewType.Dialog) {
                    Method method = viewClass.getMethod(METHOD_SET_CONTENT_VIEW, int.class);
                    method.invoke(activity, layoutId);
                } else if (viewType == ViewType.Fragment) {
                }
                LayoutInflater inflater = LayoutInflater.from((Context) v);
                Class<? extends LayoutInflater> inflaterClass = LayoutInflater.class;
                Method inflateMethod = inflaterClass.getDeclaredMethod(METHOD_INFLATE, int.class,
                        ViewGroup.class);
                view = (View) inflateMethod.invoke(inflater, layoutId, null);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else throw new ViewTypeException(VIEW_TYPE_ERROR);
        return view;
    }

    public static void injectViews(SupportV v) {
        getInstance()._injectViews(v);
    }

    private final void _injectViews(SupportV v) {
        ViewType viewType = getViewType(v);
        if (isViewTypeAllowed(viewType)) {
            Class<?> viewClass = v.getClass();
            Field[] viewFields = viewClass.getDeclaredFields();
            SupportActivity activity = getSupportActivity(v);
            Class<? extends SupportActivity> activityClass = activity.getClass();
            for (Field field : viewFields) {
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                if (View.class.isAssignableFrom(fieldType)) {
                    ViewIgnore viewIgnore = field.getAnnotation(ViewIgnore.class);
                    if (viewIgnore != null) {
                        continue;
                    }
                    ViewId viewId = field.getAnnotation(ViewId.class);
                    int id = View.NO_ID;
                    try {
                        if (viewId != null) {
                            id = viewId.value();
                        } else {
                            String packageName = activity.getPackageName();
                            Class<?> idClass = Class.forName(packageName + R_ID);
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
                            field.set(v, view);
                        } else {
                            throw new InjectException(
                                    fieldType.getName()+" id("+id+") can\'t be injected.");
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            throw new ViewTypeException(VIEW_TYPE_ERROR);
        }
    }

    public static void injectEvents(SupportV v) {
        getInstance()._injectEvents(v);
    }

    private final void _injectEvents(SupportV v) {
        ViewType viewType = getViewType(v);
        if (isViewTypeAllowed(viewType)) {
            Class<?> viewClass = v.getClass();
            Method[] methods = viewClass.getDeclaredMethods();
            SupportActivity activity = getSupportActivity(v);
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
                    try {
                        Method valueMethod = annotationType.getDeclaredMethod(METHOD_VALUE);
                        int[] viewIds = (int[]) valueMethod.invoke(annotation);
                        for (int viewId : viewIds) {
                            View view = activity.findViewById(viewId);
                            if (view == null) {
                                continue;
                            }
                            Method setListenerMethod = view.getClass().getMethod(listenerSetter,
                                    listenerType);
                            HashMap<String, Method> map = new HashMap();
                            map.put(callbackMethod, method);
                            EventInvocationHandler handler = new EventInvocationHandler(map, v);
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
        } else {
            throw new ViewTypeException(VIEW_TYPE_ERROR);
        }
    }

    private class EventInvocationHandler implements InvocationHandler {

        private Map<String, Method> mCallbackMethodMap;
        private SupportV mViewInjected;

        public EventInvocationHandler(HashMap<String, Method> callbackMethodMap, SupportV v) {
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
}
