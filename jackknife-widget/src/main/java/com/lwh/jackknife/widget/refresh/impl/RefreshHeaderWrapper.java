package com.lwh.jackknife.widget.refresh.impl;

import android.annotation.SuppressLint;
import android.view.View;

import com.lwh.jackknife.widget.refresh.api.RefreshHeader;
import com.lwh.jackknife.widget.refresh.internal.InternalAbstract;

/**
 * 刷新头部包装
 * Created by scwang on 2017/5/26.
 */
@SuppressLint("ViewConstructor")
public class RefreshHeaderWrapper extends InternalAbstract implements RefreshHeader/*, InvocationHandler*/ {

    public RefreshHeaderWrapper(View wrapper) {
        super(wrapper);
    }

}
