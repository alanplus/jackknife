package com.lwh.jackknife.mvp;

import java.lang.reflect.ParameterizedType;

public abstract class XHttpResponse<T> implements HttpResponse<T> {

    @Override
    public Class<T> getResponseType() {
        if (this.getClass().getGenericSuperclass() instanceof ParameterizedType &&
                ((ParameterizedType) (this.getClass().getGenericSuperclass())).getActualTypeArguments().length > 0) {
            Class tClass = (Class) ((ParameterizedType) (this.getClass()
                    .getGenericSuperclass())).getActualTypeArguments()[0];
            return tClass;
        }
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
