package com.lwh.jackknife.mvp;

public interface HttpResponse<T> {

    void onSuccess(T t);
    void onError(int code,String msg);
    Class<T> getResponseType();
}
