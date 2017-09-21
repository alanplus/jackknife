package com.lwh.demo;

import android.view.View;

import com.lwh.jackknife.ioc.EventBase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 放在真实要回调的回调方法上面，它是onItemClick的被代理的方法。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventBase(listenerSetter = "setOnClickListener",
        listenerType = View.OnClickListener.class,
    callbackMethod = "onClick")
public @interface OnClick {

    int[] value();
}
