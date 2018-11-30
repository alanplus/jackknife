package com.lwh.jackknife.xhttp;

public interface DataListener<T> {

    void onSuccess(T data);

    void onError(Exception e);
}
