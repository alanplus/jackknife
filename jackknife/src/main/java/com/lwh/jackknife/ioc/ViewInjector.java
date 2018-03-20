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

public class ViewInjector<V extends SupportV> {

    private final String METHOD_SET_CONTENT_VIEW = "setContentView";
    private final String METHOD_FIND_VIEW_BY_ID = "findViewById";
    private final String METHOD_INFLATE = "inflate";
    private final String METHOD_VALUE = "value";
    private final String UNDERLINE = "_";
    private final String ID = ".R$id";
    private final String LAYOUT = ".R$layout";
    private final int A = 'A';
    private final int Z = 'Z';
    private final String VIEW_TYPE_ERROR = "The viewInjected must be an activity or a fragment.";
    private final String VIEW_CLASS_NAME_ERROR = "Class name is not ends with \'Activity\' or " +
            "\'Fragment\'.";

    enum ViewType {
        Activity,
        Fragment,
        Dialog,
        View,
        UNDECLARED
    }

    private ViewInjector() {
    }

    public static ViewInjector create() {
        return new ViewInjector();
    }

    public void inject(V viewInjected) {
        if (viewInjected instanceof SupportActivity || viewInjected instanceof SupportDialog) {
            injectLayout(viewInjected);
            injectViews(viewInjected);
            injectEvents(viewInjected);
        } else if (viewInjected instanceof SupportFragment || viewInjected instanceof SupportView) {
            injectViews(viewInjected);
            injectEvents(viewInjected);
        }
    }

    protected SupportContextV getContextV(V viewInjected) {
        ViewType viewType = getViewType(viewInjected);
        SupportContextV v = null;
        if (viewType == ViewType.Activity) {
            v = (SupportActivity) viewInjected;
        } if (viewType == ViewType.Fragment) {
            v = ((SupportFragment) viewInjected).getFragmentActivity();
        } else if (viewType == ViewType.Dialog) {
            v = ((SupportDialog) viewInjected).getDialogActivity();
        } else if (viewType == ViewType.View) {
            v = (SupportView)viewInjected;
        } else if (viewType == ViewType.UNDECLARED) {
            throw new ViewTypeException(VIEW_TYPE_ERROR);
        }
        return v;
    }

    protected String generateLayoutName(V viewInjected) {
        ViewType viewType = getViewType(viewInjected);
        if (isViewTypeAllowed(viewType)) {
            String suffix = viewType.name();
            StringBuffer sb;
            String layoutName = viewInjected.getClass().getSimpleName();
            if (!layoutName.endsWith(suffix)) {
                throw new IllegalViewClassNameException(VIEW_CLASS_NAME_ERROR);
            } else {
                String name = layoutName.substring(0, layoutName.length() - suffix.length());
                sb = new StringBuffer(suffix.toLowerCase(Locale.ENGLISH));
                for (int i = 0; i < name.length(); i++) {
                    if (name.charAt(i) >= A && name.charAt(i) <= Z || i == 0) {
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

    protected ViewType getViewType(V viewInjected) {
        Class<V> viewClass = (Class<V>) viewInjected.getClass();
        if (SupportActivity.class.isAssignableFrom(viewClass)) {
            return ViewType.Activity;
        } else if (SupportFragment.class.isAssignableFrom(viewClass)) {
            return ViewType.Fragment;
        } else if (SupportDialog.class.isAssignableFrom(viewClass)) {
            return ViewType.Dialog;
        } else if (SupportView.class.isAssignableFrom(viewClass)) {
            return ViewType.View;
        } else {
            return ViewType.UNDECLARED;
        }
    }

    protected boolean isViewTypeAllowed(ViewType viewType){
        return viewType != ViewType.UNDECLARED;
    }

    public final View injectLayout(V viewInjected) {
        View view = null;
        ViewType viewType = getViewType(viewInjected);
        if (isViewTypeAllowed(viewType)) {
            String layoutName = generateLayoutName(viewInjected);
            ContentView contentView = viewInjected.getClass().getAnnotation(ContentView.class);
            viewInjected = (V) getContextV(viewInjected);
            Class<?> viewClass = viewInjected.getClass();
            String packageName = ((SupportContextV) viewInjected).getPackageName();
            try {
                Class<?> layoutClass = Class.forName(packageName + LAYOUT);
                Field field = layoutClass.getDeclaredField(layoutName);
                int layoutId = field.getInt(viewInjected);
                if (contentView != null) {
                    layoutId = contentView.value();
                }
                if (viewType == ViewType.Activity || viewType == ViewType.Dialog) {
                    Method method = viewClass.getMethod(METHOD_SET_CONTENT_VIEW, int.class);
                    method.invoke(viewInjected, layoutId);
                } else if (viewType == ViewType.Fragment || viewType == ViewType.View) {
                }
                LayoutInflater inflater = LayoutInflater.from((Context) viewInjected);
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

    public final void injectViews(V viewInjected) {
        ViewType viewType = getViewType(viewInjected);
        if (isViewTypeAllowed(viewType)) {
            Class<?> viewClass = viewInjected.getClass();
            Field[] viewFields = viewClass.getDeclaredFields();
            SupportContextV contextV = getContextV(viewInjected);
            Class<? extends SupportContextV> contextVClass = contextV.getClass();
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
                            String packageName = contextV.getPackageName();
                            Class<?> idClass = Class.forName(packageName + ID);
                            Field idField = idClass.getDeclaredField(field.getName());
                            try {
                                id = idField.getInt(idField);
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            }
                        }
                        Method findViewByIdMethod = contextVClass.getMethod(METHOD_FIND_VIEW_BY_ID,
                                int.class);
                        Object view = findViewByIdMethod.invoke(contextV, id);
                        if (view != null) {
                            field.set(viewInjected, view);
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

    public final void injectEvents(V viewInjected) {
        ViewType viewType = getViewType(viewInjected);
        if (isViewTypeAllowed(viewType)) {
            Class<?> viewClass = viewInjected.getClass();
            Method[] methods = viewClass.getDeclaredMethods();
            SupportContextV contextV = getContextV(viewInjected);
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
                            View view = contextV.findViewById(viewId);
                            if (view == null) {
                                continue;
                            }
                            Method setListenerMethod = view.getClass().getMethod(listenerSetter,
                                    listenerType);
                            HashMap<String, Method> map = new HashMap();
                            map.put(callbackMethod, method);
                            EventInvocationHandler handler = new EventInvocationHandler(map,
                                    viewInjected);
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
        private V mViewInjected;

        public EventInvocationHandler(HashMap<String, Method> callbackMethodMap, V view) {
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
