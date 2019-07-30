package com.lwh.jackknife.ioc2;

import com.lwh.jackknife.ioc2.adapter.InjectAdapter;
import com.squareup.javapoet.TypeVariableName;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

public abstract class Context {

    protected ProcessingEnvironment mEnv;
    protected Filer mFiler;
    protected TypeVariableName mT = TypeVariableName.get("T", InjectAdapter.class);
    protected final String CLASS_LAYOUT_INFLATER = "android.view.LayoutInflater";
    protected final String CLASS_VIEW = "android.view.View";
    protected final String CLASS_ACTIVITY = "android.app.Activity";

    public Context(ProcessingEnvironment env, Filer filer) {
        this.mEnv = env;
        this.mFiler = filer;
    }

    public abstract void write(Element element);
}
